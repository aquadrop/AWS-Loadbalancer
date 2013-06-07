package ece1779.ec2.user;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

public class Register extends HttpServlet {

	private static final long serialVersionUID = -2102892057927543835L;

	public void doPost(HttpServletRequest request, HttpServletResponse response) {

		HttpSession session = request.getSession();
		
		String username = null;
		String password = null;
		String password_confirm = null;
		
		username = request.getParameter("username");
		password = request.getParameter("password");
		password_confirm = request.getParameter("password_confirm");
		
		Connection con = null;
		try {
			if (username == null || password == null) {
				session.setAttribute("error-message", "Username and Password can't be empty");
				RequestDispatcher view = request.getRequestDispatcher("register.jsp");
				view.forward(request, response);
				return;
			} else if (!password.equals(password_confirm)) {
				session.setAttribute("error-message", "Password does not match");
				RequestDispatcher view = request.getRequestDispatcher("register.jsp");
				view.forward(request, response);
				return;
			}

			// Get DB connection from pool
			DataSource dbcp = (DataSource) this.getServletContext().getAttribute("dbpool");
			con = dbcp.getConnection();
			
			// Execute SQL query
			Statement stmt = con.createStatement();
			String sql = "INSERT INTO users (login, password) " + " VALUES (" + "'" + username + "','" + password + "')";
			stmt.execute(sql);

		} catch (Exception e) {
			getServletContext().log(e.getMessage());
		} finally {
			try {
				con.close();
			} catch (Exception e) {
				getServletContext().log(e.getMessage());
			}
		}
		
		try {
			response.sendRedirect(response.encodeRedirectURL("Login"));
		} catch (IOException e) {
			getServletContext().log(e.getMessage());
		}
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		try {
			RequestDispatcher view = request.getRequestDispatcher("register.jsp");
			view.forward(request, response);
		} catch (Exception e) {
			
		}
	}
}
