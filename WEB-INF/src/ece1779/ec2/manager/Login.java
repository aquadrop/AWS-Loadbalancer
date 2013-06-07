package ece1779.ec2.manager;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.amazonaws.auth.BasicAWSCredentials;

import ece1779.ec2.models.User;

public class Login extends HttpServlet {

	private static final long serialVersionUID = 622584957466026027L;

	private String username;

	private String password;

	String error = null;

	BasicAWSCredentials awsCredential;

	public void init(ServletConfig config) {
		ServletContext context = config.getServletContext();

		username = context.getInitParameter("adminUsername");
		password = context.getInitParameter("adminPassword");

	}

	public void doPost(HttpServletRequest request, HttpServletResponse response) {

		HttpSession session = request.getSession();
		// BasicAWSCredentials awsCredential = (BasicAWSCredentials)
		// this.getServletContext().getAttribute("AWSCredentials");
		String username = null;
		String password = null;

		try {
			username = request.getParameter("username");
			password = request.getParameter("password");

			if (validate(username, password)) {
				User user = new User();
				user.setUsername(username);

				session.setAttribute("admin", user);
				session.setAttribute("Detour", false);
				session.setAttribute("Thread", true);
				// session.setAttribute("loadBalance", new LoadBalancer(session,
				// awsCredential));
				response.sendRedirect(response.encodeRedirectURL("../Welcome"));
			} else {
				session.setAttribute("error-message",
						"Username or Passowrd not correct");
				RequestDispatcher view = request
						.getRequestDispatcher("login.jsp");
				view.forward(request, response);
			}
		} catch (Exception e) {
			getServletContext().log(e.getMessage());
		}
	}

	public boolean validate(String username, String password) {
		if (username.equals(this.username) && password.equals(this.password)) {
			return true;
		}

		return false;
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		// awsCredential = (BasicAWSCredentials)
		// getServletContext().getAttribute("AWSCredentials");
		if (session.isNew() || session.getAttribute("admin") == null) {
			try {
				RequestDispatcher view = request
						.getRequestDispatcher("login.jsp");
				view.forward(request, response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try {
				response.sendRedirect(response.encodeRedirectURL("../Welcome"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
