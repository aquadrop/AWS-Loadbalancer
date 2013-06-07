package ece1779.ec2.manager;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancing;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient;
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersRequest;
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersResult;
import com.amazonaws.services.elasticloadbalancing.model.EnableAvailabilityZonesForLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.LoadBalancerDescription;
import com.amazonaws.services.elasticloadbalancing.model.RegisterInstancesWithLoadBalancerRequest;

import ece1779.ec2.models.CloudWatchStats;
import ece1779.ec2.models.InstanceMonitor;

public class StartInstance extends HttpServlet {

	private static final long serialVersionUID = -2827072334623104361L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		response.setContentType("text/html");
		HttpSession session = request.getSession();
		//List<CloudWatchStats> cloudWatchStats = (List<CloudWatchStats>) session.getAttribute("cloudWatchSession");
		
		
			
		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("<head>");
		out.println("<title>Env Variables</title>");
		out.println("</head>");
		out.println("<body><pre>");
//		for (CloudWatchStats cws: cloudWatchStats)
//			out.println(cws.getDimensions().get(0).getName());
		ec2StartInstance(out);
		out.println("</pre></body>");
		out.println("</html>");
		// request.setAttribute("detour", "detour");

		session.setAttribute("Detour", false);
		response.sendRedirect(response.encodeRedirectURL("LoadManager"));
		
	}

	static public void ec2StartInstance(int expand_num,
			BasicAWSCredentials awsCredentials) {
		/*
		 * Important: Be sure to fill in your AWS access credentials in the
		 * AwsCredentials.properties file before you try to run this sample.
		 * http://aws.amazon.com/security-credentials
		 */

		AmazonEC2 ec2 = new AmazonEC2Client(awsCredentials);

		// out.println("===========================================");
		// out.println("Getting Started with Amazon EC2");
		// out.println("===========================================\n");

		try {
			String imageId = "ami-f624ba9f";
			RunInstancesRequest request = new RunInstancesRequest(imageId,
					expand_num, expand_num);

			request.setKeyName("AmazonKey");

			ArrayList<String> securityGroups = new ArrayList<String>();
			securityGroups.add("group13");
			request.setSecurityGroups(securityGroups);

			RunInstancesResult result = ec2.runInstances(request);
			Reservation reservation = result.getReservation();
			List<Instance> instances = reservation.getInstances();
			Instance inst = instances.get(0);
			refreshBalancePool(instances, awsCredentials);

		} catch (AmazonServiceException ase) {

		} catch (AmazonClientException ace) {
		}

	}

	void ec2StartInstance(PrintWriter out) throws IOException {
		/*
		 * Important: Be sure to fill in your AWS access credentials in the
		 * AwsCredentials.properties file before you try to run this sample.
		 * http://aws.amazon.com/security-credentials
		 */

		BasicAWSCredentials awsCredentials = (BasicAWSCredentials) getServletContext()
				.getAttribute("AWSCredentials");

		AmazonEC2 ec2 = new AmazonEC2Client(awsCredentials);

		out.println("===========================================");
		out.println("Normal Getting Started with Amazon EC2");
		out.println("===========================================\n");

		try {
			String imageId = "ami-ebe47382";
			RunInstancesRequest request = new RunInstancesRequest(imageId, 1, 1);

			request.setKeyName("AmazonKey");

			ArrayList<String> securityGroups = new ArrayList<String>();
			securityGroups.add("group13");
			request.setSecurityGroups(securityGroups);

			RunInstancesResult result = ec2.runInstances(request);
			Reservation reservation = result.getReservation();
			List<Instance> instances = reservation.getInstances();
			Instance inst = instances.get(0);

			out.println("Instance Info = " + inst.toString());
			/*
			 * List the buckets in your account
			 */
			out.println("Listing buckets");

			refreshBalancePool(instances, awsCredentials);
			
//			DescribeInstancesRequest direquest = new DescribeInstancesRequest();
//			DescribeInstancesResult ec2Result = ec2.describeInstances(direquest);
//			List<Reservation>reservations =  ec2Result.getReservations();
//			List<String> runninginstanceIds = new ArrayList<String>();
//			for (Reservation rv: reservations)
//			{
//				List<com.amazonaws.services.ec2.model.Instance> insts = rv.getInstances();
//				for (com.amazonaws.services.ec2.model.Instance i: insts)
//				{
//					if (i.getState().getName().equals("running"))
//					{	
//						runninginstanceIds.add(i.getInstanceId());
//						
//					}
//				}
//			}
//			
//			for (String id: runninginstanceIds)
//			{
//				out.println(id);
//			}
			
		} catch (AmazonServiceException ase) {
			out.println("Caught an AmazonServiceException, which means your request made it "
					+ "to Amazon EC2, but was rejected with an error response for some reason.");
			out.println("Error Message:    " + ase.getMessage());
			out.println("HTTP Status Code: " + ase.getStatusCode());
			out.println("AWS Error Code:   " + ase.getErrorCode());
			out.println("Error Type:       " + ase.getErrorType());
			out.println("Request ID:       " + ase.getRequestId());
		} catch (AmazonClientException ace) {
			out.println("Caught an AmazonClientException, which means the client encountered "
					+ "a serious internal problem while trying to communicate with EC2, "
					+ "such as not being able to access the network.");
			out.println("Error Message: " + ace.getMessage());
		}

	}

	public static void refreshBalancePool(List<Instance> instances,
			BasicAWSCredentials awsCredentials) {
		AmazonElasticLoadBalancing elb = new AmazonElasticLoadBalancingClient(
				awsCredentials);
		List<String> loadBalancerNames = new ArrayList<String>();
		String loadBalancer = "loader";
		loadBalancerNames.add(loadBalancer);

		Instance _inst = instances.get(0);

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

			// If the new instance's availability zones is new, add the new zone
			// to load balancer
			if (!availabilityZones.contains(_inst.getPlacement())) {
				availabilityZones.add(_inst.getPlacement()
						.getAvailabilityZone());
			}
			// Add the new instance to load balancer
			for (Instance inst : instances) {
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

	void refreshBalancePool(List<Instance> instances,
			BasicAWSCredentials awsCredentials, PrintWriter out) {

		refreshBalancePool(instances, awsCredentials);

		out.println(instances.size());
	}

}