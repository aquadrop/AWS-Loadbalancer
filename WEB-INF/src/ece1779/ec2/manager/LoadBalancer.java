package ece1779.ec2.manager;

import java.io.IOException;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClient;
import com.amazonaws.services.cloudwatch.model.Datapoint;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsRequest;
import com.amazonaws.services.cloudwatch.model.GetMetricStatisticsResult;
import com.amazonaws.services.cloudwatch.model.ListMetricsRequest;
import com.amazonaws.services.cloudwatch.model.ListMetricsResult;
import com.amazonaws.services.cloudwatch.model.Metric;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancing;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient;
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersRequest;
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersResult;
import com.amazonaws.services.elasticloadbalancing.model.EnableAvailabilityZonesForLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.LoadBalancerDescription;
import com.amazonaws.services.elasticloadbalancing.model.RegisterInstancesWithLoadBalancerRequest;

import ece1779.ec2.models.CloudWatchStats;
import ece1779.ec2.models.InstanceMonitor;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class LoadBalancer extends Thread implements HttpSessionBindingListener {
	HttpServletRequest request;
	HttpServletResponse response;
	ServletContext context;
	HttpSession session;
	static List<CloudWatchStats> cloudWatchStats;
	BasicAWSCredentials awsCredential;
	static boolean sleep = false;
	static boolean firsttime = false;

	static int max_instance = 100;
	static int min_instance = 1;

	boolean selfretrive = false;
	boolean isalive = true;
	// load balancer
	AmazonEC2Client ec2;
	AmazonElasticLoadBalancingClient elb;

	public LoadBalancer(BasicAWSCredentials awsCredential,
			List<CloudWatchStats> cloudWatchStats) {
		this.awsCredential = awsCredential;
		this.cloudWatchStats = cloudWatchStats;
		isalive = false;
		this.selfretrive = false;
	}

	public LoadBalancer(ServletContext context,
			BasicAWSCredentials awsCredential) {
		this.context = context;
		this.awsCredential = awsCredential;
		this.selfretrive = true;
		this.isalive = true;

	}

	public LoadBalancer(HttpSession session, BasicAWSCredentials awsCredential) {
		this.session = session;
		this.awsCredential = awsCredential;
		this.selfretrive = true;
		this.isalive = true;
	}

	static public List<CloudWatchStats> getCloudWatchList() {
		return cloudWatchStats;
	}

	@Override
	public void run() {
		try {
			boolean is = true;
			while (is) {
				loadBalance();

				try {

					Thread.sleep(5000);

				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				is = isalive;
			}

		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void bruteRefreshBalancePool() {
		DescribeInstancesRequest direquest = new DescribeInstancesRequest();
		DescribeInstancesResult ec2Result = ec2.describeInstances(direquest);
		List<Reservation> reservations = ec2Result.getReservations();
		List<Instance> instances = new ArrayList<Instance>();
		for (Reservation rv : reservations) {
			List<com.amazonaws.services.ec2.model.Instance> insts = rv
					.getInstances();
			for (com.amazonaws.services.ec2.model.Instance i : insts) {
				if (i.getState().getName().equals("running")) {
					instances.add(i);

				}
			}
		}

		AmazonElasticLoadBalancing elb = new AmazonElasticLoadBalancingClient(
				awsCredential);
		List<String> loadBalancerNames = new ArrayList<String>();
		String loadBalancer = "loader";
		loadBalancerNames.add(loadBalancer);

		DescribeLoadBalancersRequest lbRequest = new DescribeLoadBalancersRequest(
				loadBalancerNames);
		DescribeLoadBalancersResult lbResult = elb
				.describeLoadBalancers(lbRequest);
		List<LoadBalancerDescription> lbDescriptions = lbResult
				.getLoadBalancerDescriptions();

		if (lbDescriptions.size() > 0) {
			LoadBalancerDescription description = lbDescriptions.get(0);

			// Retrieve the availability zones and instances associated with
			// current load balancer
			List<String> availabilityZones = description.getAvailabilityZones();
			List<com.amazonaws.services.elasticloadbalancing.model.Instance> listInstance = description
					.getInstances();

			// Add the new instance to load balancer
			for (Instance inst : instances) {
				// If the new instance's availability zones is new, add the new
				// zone
				// to load balancer
				if (!availabilityZones.contains(inst.getPlacement())) {
					availabilityZones.add(inst.getPlacement()
							.getAvailabilityZone());
				}
				listInstance
						.add(new com.amazonaws.services.elasticloadbalancing.model.Instance(
								inst.getInstanceId()));
			}

			EnableAvailabilityZonesForLoadBalancerRequest eazReq = new EnableAvailabilityZonesForLoadBalancerRequest(
					loadBalancer, availabilityZones);
			RegisterInstancesWithLoadBalancerRequest riwlbReq = new RegisterInstancesWithLoadBalancerRequest(
					loadBalancer, listInstance);
			elb.enableAvailabilityZonesForLoadBalancer(eazReq);
			elb.registerInstancesWithLoadBalancer(riwlbReq);
		}
	}

	@SuppressWarnings("static-access")
	private void refreshCloudWatch() {
		if (!this.selfretrive)
			return;
		if (firsttime) {
			bruteRefreshBalancePool();
			firsttime = false;
		}
		AmazonCloudWatch cw = new AmazonCloudWatchClient(awsCredential);
		AmazonEC2Client ec2 = new AmazonEC2Client(awsCredential);
		List<CloudWatchStats> cloudWatchStats = new ArrayList<CloudWatchStats>();
		// HttpSession session = request.getSession();
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
			//session.setAttribute("InstanceMonitor", instanceMonitor);
			context.setAttribute("InstanceMonitor", instanceMonitor);
			for (Metric metric : metrics) {
				List<Dimension> dimensions = metric.getDimensions();
				if (!runninginstanceIds.contains(dimensions.get(0).getValue()))
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

			//session.setAttribute("cloudWatchStatsSession", cloudWatchStats);
			context.setAttribute("cloudWatchStatsSession", cloudWatchStats);
			this.cloudWatchStats = cloudWatchStats;
			// session.setAttribute("cloudWatchSession", cloudWatchStats);

		} catch (AmazonServiceException ase) {

		} catch (AmazonClientException ace) {

		}
	}

	private void loadBalance() throws ServletException, IOException {
		refreshCloudWatch();
		double expand_ratio = 2;
		double shrink_ratio = 0.8;
		double cpu_up = 90;
		double cpu_down = 3;
		boolean do_expand = false;
		boolean do_shrink = true;
		for (CloudWatchStats cws : cloudWatchStats) {
			double cpu = 0.0;
			List<Datapoint> datapoints = cws.getStats().getDatapoints();
			for (Datapoint dp : datapoints) {
				cpu = cpu + dp.getMaximum();
			}
			cpu = cpu / datapoints.size();
			if (cpu > cpu_up) {
				do_expand = true;
				do_shrink = false;
				break;
			}
			if (cpu > cpu_down && do_shrink) {
				do_shrink = false;
			}

		}

		if (do_expand) {
			int num_instance_expand = (int) (expand_ratio - 1)
					* cloudWatchStats.size();
			if (num_instance_expand + cloudWatchStats.size() < max_instance)
				StartInstance.ec2StartInstance(num_instance_expand,
						this.awsCredential);
		}

		if (do_shrink) {
			int num_instance_shrink = (int) (shrink_ratio * cloudWatchStats
					.size());
			if (cloudWatchStats.size() - num_instance_shrink > min_instance) {
				List<String> instanceIds = new ArrayList<String>();
				for (int i = 0; i < num_instance_shrink; i++) {
					String instanceId = cloudWatchStats.get(i).getDimensions()
							.get(0).getValue();
					if (instanceId.equals("i-b809dfcb"))
						continue;
					instanceIds.add(instanceId);
				}
				ShutdownInstance.ec2ShutdownInstance(instanceIds,
						this.awsCredential);
			}
		}

	}

	@Override
	public void valueBound(HttpSessionBindingEvent arg0) {
		// TODO Auto-generated method stub
		start();
	}

	@Override
	public void valueUnbound(HttpSessionBindingEvent arg0) {
		// TODO Auto-generated method stub
		interrupt();
	}

}
