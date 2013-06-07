package ece1779.ec2;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpSession;

import org.apache.commons.dbcp.cpdsadapter.DriverAdapterCPDS;
import org.apache.commons.dbcp.datasources.SharedPoolDataSource;

import com.amazonaws.auth.BasicAWSCredentials;

public class Initialization implements ServletContextListener {
	private static final long serialVersionUID = 9187318587480356185L;

//	public void init(ServletConfig config) {
//		
//		try {
//			ServletContext context = config.getServletContext();
//			
//			// Initialize connection pool
//			String accessKey = config.getInitParameter("AWSaccessKey");
//			String secretKey = config.getInitParameter("AWSsecretKey");
//			BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
//			
//			context.setAttribute("AWSCredentials", awsCredentials);
////			HttpSession session ;
////			session.setAttribute("AWSCredentials", awsCredentials);
//			// Initialize database
//			String dbDriver = config.getInitParameter("dbDriver");
//			String dbURL = config.getInitParameter("dbURL");
//			String dbUser = config.getInitParameter("dbUser");
//			String dbPassword = config.getInitParameter("dbPassword");
//
//			DriverAdapterCPDS ds = new DriverAdapterCPDS();
//			ds.setDriver(dbDriver);
//			ds.setUrl(dbURL);
//			ds.setUser(dbUser);
//			ds.setPassword(dbPassword);
//
//			SharedPoolDataSource dbcp = new SharedPoolDataSource();
//			dbcp.setConnectionPoolDataSource(ds);
//
//			context.setAttribute("dbpool", dbcp);
//			context.setAttribute("dbDriver", "Hello");
//			
//		} catch (Exception ex) {
//			getServletContext().log("SQLGatewayPool Error: " + ex.getMessage());
//		}
//	}

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contextInitialized(ServletContextEvent sce) {
		try {
			ServletContext context = sce.getServletContext();
			
			// Initialize connection pool
			String accessKey = context.getInitParameter("AWSaccessKey");
			String secretKey = context.getInitParameter("AWSsecretKey");
			BasicAWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
			
			context.setAttribute("AWSCredentials", awsCredentials);
//			HttpSession session ;
//			session.setAttribute("AWSCredentials", awsCredentials);
			// Initialize database
			String dbDriver = context.getInitParameter("dbDriver");
			String dbURL = context.getInitParameter("dbURL");
			String dbUser = context.getInitParameter("dbUser");
			String dbPassword = context.getInitParameter("dbPassword");

			DriverAdapterCPDS ds = new DriverAdapterCPDS();
			ds.setDriver(dbDriver);
			ds.setUrl(dbURL);
			ds.setUser(dbUser);
			ds.setPassword(dbPassword);

			SharedPoolDataSource dbcp = new SharedPoolDataSource();
			dbcp.setConnectionPoolDataSource(ds);

			context.setAttribute("dbpool", dbcp);
			context.setAttribute("dbDriver", "Hello");
			
		} catch (Exception ex) {
			sce.getServletContext().log("SQLGatewayPool Error: " + ex.getMessage());
		}
		
	}
}
