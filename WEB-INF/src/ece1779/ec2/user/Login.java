package ece1779.ec2.user;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import ece1779.ec2.models.User;

public class Login extends HttpServlet {

	private static final long serialVersionUID = 622584957466026027L;
	String error = null;

	Connection con = null;
	String dbDriver = null;
	String dbURL = null;
	String dbUser = null;
	String dbPassword = null;

	public void init(ServletConfig config) {
		try {
			super.init(config);
			ServletContext context = config.getServletContext();

			dbDriver = context.getInitParameter("dbDriver");
			dbURL = context.getInitParameter("dbURL");
			dbUser = context.getInitParameter("dbUser");
			dbPassword = context.getInitParameter("dbPassword");

		} catch (ServletException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// password = context.getInitParameter("adminPassword");

	}

	// password = context.getInitParameter("adminPassword");

	public void doPost(HttpServletRequest request, HttpServletResponse response) {

		HttpSession session = request.getSession();

		String username = null;
		String password = null;

		try {
			username = request.getParameter("username");
			password = request.getParameter("password");

			int id = validate(username, password);
			if (id != 0) {
				User user = new User();
				user.setId(id);
				user.setUsername(username);

				session.setAttribute("user", user);

				response.sendRedirect(response.encodeRedirectURL("../Welcome"));
			} else {
				session.setAttribute("error-message",
						"Username or Passowrd not correct"+getServletContext().getAttribute("dbDriver"));
				RequestDispatcher view = request
						.getRequestDispatcher("login.jsp");
				view.forward(request, response);
			}
		} catch (Exception e) {
			getServletContext().log(e.getMessage());
		}
	}

	public int validate(String username, String password) {

		int id = 0;

		try {

			// Get DB connection from pool
			// DataSource dbcp = (DataSource) this.getServletContext()
			// .getAttribute("dbpool");
			// con = dbcp.getConnection();
			Class.forName(this.dbDriver);
			con = DriverManager.getConnection(
					this.dbURL, this.dbUser,
					this.dbPassword);
			// Execute SQL query
			Statement stmt = con.createStatement();
			String sql = "SELECT * FROM users WHERE login = " + "'" + username
					+ "'" + " AND password = " + "'" + password + "'";

			ResultSet rs = stmt.executeQuery(sql);
			rs.next();
			id = rs.getInt("id");

		} catch (Exception ex) {
			getServletContext().log(ex.getMessage());
			error = ex.getMessage();

		} finally {
			try {
				con.close();
			} catch (Exception e) {
				getServletContext().log(e.getMessage());
			}
		}

		return id;
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();

		if (session.isNew() || session.getAttribute("user") == null) {
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
