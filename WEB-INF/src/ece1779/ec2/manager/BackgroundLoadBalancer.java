package ece1779.ec2.manager;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.dbcp.cpdsadapter.DriverAdapterCPDS;

import com.amazonaws.auth.BasicAWSCredentials;

public class BackgroundLoadBalancer implements ServletContextListener {

	private LoadBalancer t = null;
	private BasicAWSCredentials awsCredentials;

	public void contextInitialized(ServletContextEvent sce) {

		// String accessKey = "AKIAJTMZ2QYOOVJFXUPQ";
		// String secretKey = "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";
		// awsCredential = new BasicAWSCredentials(accessKey, secretKey);
		ServletContext context = sce.getServletContext();
		String accessKey = context.getInitParameter("AWSaccessKey");
		String secretKey = context.getInitParameter("AWSsecretKey");
		awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
		context.setAttribute("AWSCredentials", awsCredentials);

		String dbDriver = context.getInitParameter("dbDriver");
		String dbURL = context.getInitParameter("dbURL");
		String dbUser = context.getInitParameter("dbUser");
		String dbPassword = context.getInitParameter("dbPassword");
		context.setAttribute("dbDriver", "Hello");
		DriverAdapterCPDS ds = new DriverAdapterCPDS();
		try {
			if (context.getAttribute("database") == null) {
				ds.setDriver(dbDriver);
				ds.setUrl(dbURL);
				ds.setUser(dbUser);
				ds.setPassword(dbPassword);
				context.setAttribute("database", "database");
			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// if ((t == null) || (!t.isAlive())) {
		// t = new LoadBalancer(sce.getServletContext(), awsCredential);
		// t.start();
		// sce.getServletContext().setAttribute("backgroundThread",
		// "backgroundThread");
		// }
	}

	@SuppressWarnings("deprecation")
	public void contextDestroyed(ServletContextEvent sce) {
		try {
			t.join();
			t.interrupt();
		} catch (Exception ex) {
		}
	}
}
