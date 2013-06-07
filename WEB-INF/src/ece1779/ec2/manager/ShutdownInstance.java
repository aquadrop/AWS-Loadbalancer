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
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.RunInstancesRequest;
import com.amazonaws.services.ec2.model.RunInstancesResult;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesRequest;
import com.amazonaws.services.ec2.model.TerminateInstancesResult;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancing;
import com.amazonaws.services.elasticloadbalancing.AmazonElasticLoadBalancingClient;
import com.amazonaws.services.elasticloadbalancing.model.DeregisterInstancesFromLoadBalancerRequest;
import com.amazonaws.services.elasticloadbalancing.model.DeregisterInstancesFromLoadBalancerResult;
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersRequest;
import com.amazonaws.services.elasticloadbalancing.model.DescribeLoadBalancersResult;
import com.amazonaws.services.elasticloadbalancing.model.LoadBalancerDescription;

public class ShutdownInstance extends HttpServlet {

	private static final long serialVersionUID = -2827072334623104361L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		HttpSession session = request.getSession();
		out.println("<html>");
		out.println("<head>");
		out.println("<title>Env Variables</title>");
		out.println("</head>");
		out.println("<body><pre>");

		String instanceId = request.getParameter("ShutdownInstance");
		out.println(instanceId);
		List<String> instanceIds = new ArrayList<String>();
		instanceIds.add(instanceId);
		ec2ShutdownInstance(out, instanceIds);

		out.println("</pre></body>");
		out.println("</html>");
		// response.sendRedirect(response.encodeRedirectURL("LoadManager"));
		// request.setAttribute("detour", "detour");
		session.setAttribute("Detour", false);
		response.sendRedirect(response.encodeRedirectURL("LoadManager"));
	}

	static public void ec2ShutdownInstance(List<String> instanceIds,
			BasicAWSCredentials awsCredentials) throws IOException {
		AmazonEC2 ec2 = new AmazonEC2Client(awsCredentials);
		try {
			TerminateInstancesRequest request = new TerminateInstancesRequest();
			request.setInstanceIds(instanceIds);
			ec2.terminateInstances(request);

		} catch (AmazonServiceException ase) {

		} catch (AmazonClientException ace) {

		}

		// refreshBalancePool(instanceIds, awsCredentials);
	}

	// for button
	void ec2ShutdownInstance(PrintWriter out, List<String> instanceIds)
			throws IOException {
		BasicAWSCredentials awsCredentials = (BasicAWSCredentials) getServletContext()
				.getAttribute("AWSCredentials");
		AmazonEC2 ec2 = new AmazonEC2Client(awsCredentials);
		try {
			TerminateInstancesRequest request = new TerminateInstancesRequest();
			request.setInstanceIds(instanceIds);
			ec2.terminateInstances(request);

		} catch (AmazonServiceException ase) {

		} catch (AmazonClientException ace) {

		}

		refreshBalancePool(instanceIds, awsCredentials);
	}

	public static void refreshBalancePool(List<String> instanceIds,
			BasicAWSCredentials awsCredentials) {
		AmazonEC2 ec2 = new AmazonEC2Client(awsCredentials);

		// Register a loadbalancer with our credential
		AmazonElasticLoadBalancing elb = new AmazonElasticLoadBalancingClient(
				awsCredentials);
		List<String> loadBalancerNames = new ArrayList<String>();
		String loadBalancer = "loader";
		loadBalancerNames.add(loadBalancer); // Our load balancer name

		// Get the detailed configuration information for the specified
		// LoadBalancers.
		DescribeLoadBalancersRequest lbRequest = new DescribeLoadBalancersRequest(
				loadBalancerNames);
		DescribeLoadBalancersResult lbResult = elb
				.describeLoadBalancers(lbRequest);
		List<LoadBalancerDescription> lbDescriptions = lbResult
				.getLoadBalancerDescriptions();

		// Make sure we have entry in load balancer
		if (lbDescriptions.size() > 0) {

			// LoadBalancerDescription description = lbDescriptions.get(0);

			List<com.amazonaws.services.elasticloadbalancing.model.Instance> DeregInstances = new ArrayList<com.amazonaws.services.elasticloadbalancing.model.Instance>();
			com.amazonaws.services.elasticloadbalancing.model.Instance inst = null;
			for (String id : instanceIds) {
				try {
					inst = new com.amazonaws.services.elasticloadbalancing.model.Instance(
							id);
				} catch (Exception e) {
					continue;
				}
				DeregInstances.add(inst);
			}
			DeregisterInstancesFromLoadBalancerRequest diflbr = new DeregisterInstancesFromLoadBalancerRequest(
					loadBalancer, DeregInstances);

			// Deregister the instance from the load balancer
			DeregisterInstancesFromLoadBalancerResult diflbres = elb
					.deregisterInstancesFromLoadBalancer(diflbr);

			// Shut down the instance
			

		}

	}

}