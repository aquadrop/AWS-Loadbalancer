package ece1779.ec2.manager;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.swing.text.html.HTMLDocument.Iterator;

import quicktime.app.spaces.Collection;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.autoscaling.model.Instance;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import com.amazonaws.services.cloudwatch.model.ListMetricsRequest;
import com.amazonaws.services.cloudwatch.model.ListMetricsResult;
import com.amazonaws.services.cloudwatch.model.Metric;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusRequest;
import com.amazonaws.services.ec2.model.DescribeInstanceStatusResult;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.InstanceStatus;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.CreateLoadBalancerResult;
import com.amazonaws.services.elasticloadbalancing.model.Listener;
import com.amazonaws.services.elasticloadbalancing.model.RegisterInstancesWithLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.RegisterInstancesWithLoadBalancerResult;
import com.sun.xml.internal.bind.v2.runtime.reflect.ListIterator;

import ece1779.ec2.models.CloudWatchStats;
import ece1779.ec2.models.InstanceMonitor;

public class LoadManager extends HttpServlet {

	private static final long serialVersionUID = -4663386790374393372L;

	private BasicAWSCredentials awsCredential;

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();

		if (session.isNew() || session.getAttribute("admin") == null) {
			try {
				response.sendRedirect(response.encodeRedirectURL("Login"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				// startNewInstance(request, response);
				cloudWatch(request, response);
				RequestDispatcher view = request
						.getRequestDispatcher("loadManager.jsp");
				view.forward(request, response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void cloudWatch(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		awsCredential = (BasicAWSCredentials) this.getServletContext()
				.getAttribute("AWSCredentials");
		ServletContext context = this.getServletContext();
		HttpSession session = request.getSession();
		boolean debug = true;
		if (!debug) {
			PrintWriter out = response.getWriter();
			AmazonCloudWatch cw = new AmazonCloudWatchClient(awsCredential);
			AmazonEC2Client ec2 = new AmazonEC2Client(awsCredential);
			List<CloudWatchStats> cloudWatchStats = new ArrayList<CloudWatchStats>();

			try {
				ListMetricsRequest listMetricsRequest = new ListMetricsRequest();
				listMetricsRequest.setMetricName("CPUUtilization");
				listMetricsRequest.setNamespace("AWS/EC2");

				ListMetricsResult result = cw.listMetrics(listMetricsRequest);
				List<Metric> metrics = result.getMetrics();

				InstanceMonitor instanceMonitor = new InstanceMonitor();
				DescribeInstancesRequest direquest = new DescribeInstancesRequest();
				DescribeInstancesResult ec2Result = ec2
						.describeInstances(direquest);
				List<Reservation> reservations = ec2Result.getReservations();
				List<String> runninginstanceIds = new ArrayList<String>();
				for (Reservation rv : reservations) {
					List<com.amazonaws.services.ec2.model.Instance> insts = rv
							.getInstances();
					for (com.amazonaws.services.ec2.model.Instance i : insts) {
						if (i.getState().getName().equals("running"))
							runninginstanceIds.add(i.getInstanceId());
					}
				}
				instanceMonitor.setRunningInstanceIds(runninginstanceIds);
				session.setAttribute("InstanceMonitor", instanceMonitor);
				for (Metric metric : metrics) {
					List<Dimension> dimensions = metric.getDimensions();
					if (!runninginstanceIds.contains(dimensions.get(0)
							.getValue()))
						continue;
					String namespace = metric.getNamespace();
					String metricName = metric.getMetricName();

					GetMetricStatisticsRequest statisticsRequest = new GetMetricStatisticsRequest();
					statisticsRequest.setNamespace(namespace);
					statisticsRequest.setMetricName(metricName);
					statisticsRequest.setDimensions(dimensions);

					Date endTime = new Date();
					Date startTime = new Date();
					startTime.setTime(endTime.getTime() - 1200000);
					statisticsRequest.setStartTime(startTime);
					statisticsRequest.setEndTime(endTime);
					statisticsRequest.setPeriod(60);

					Vector<String> statistics = new Vector<String>();
					statistics.add("Maximum");
					statisticsRequest.setStatistics(statistics);
					GetMetricStatisticsResult stats = cw
							.getMetricStatistics(statisticsRequest);

					// skip terminated instances that have no CPU report

					if (stats.getDatapoints().size() == 0)
						continue;

					CloudWatchStats cws = new CloudWatchStats();
					cws.setNameSpace(namespace);
					cws.setMetricName(metricName);
					cws.setDimensions(dimensions);
					cws.setStats(stats);

					cloudWatchStats.add(cws);
				}

				request.setAttribute("cloudWatchStats", cloudWatchStats);
				session.setAttribute("cloudWatchStatsSession", cloudWatchStats);

			} catch (AmazonServiceException ase) {

			} catch (AmazonClientException ace) {
				out.println(ace.getMessage());
			}

			// Load Balance if exceeds threshold but avoid button action
			Boolean Detour = (Boolean) session.getAttribute("Detour");
			if (Detour) {
				Thread t = new Thread(new LoadBalancer(awsCredential,
						cloudWatchStats));
				t.run();
			}

			session.setAttribute("Detour", true);
		}
		if (debug) {
			Boolean do_Thread = (Boolean) session.getAttribute("Thread");
			if (session.getAttribute("loadBalance") == null) {
				if (context.getAttribute("backgroundThread") == null)
					session.setAttribute("loadBalance", new LoadBalancer(
							session, awsCredential));
				else {
					session.setAttribute("backgroundloadBalance", true);
				}
				if (do_Thread) {
					Thread t = new Thread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							while (true) {
								List<CloudWatchStats> cloudWatchStats = LoadBalancer
										.getCloudWatchList();
								try {
									Thread.sleep(2000);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
								try {
									if (cloudWatchStats.size() != 0)
										break;
								} catch (NullPointerException e) {
								}

							}
						}

					});
					t.start();
					try {
						t.join();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			session.setAttribute("Thread", false);

			List<CloudWatchStats> cloudWatchStats = LoadBalancer
					.getCloudWatchList();
			// (List<CloudWatchStats>) session
			// .getAttribute("cloudWatchStatsSession");
			session.setAttribute("cloudWatchStatsSession", cloudWatchStats);

			request.setAttribute("cloudWatchStats", cloudWatchStats);
		}

	}

}
