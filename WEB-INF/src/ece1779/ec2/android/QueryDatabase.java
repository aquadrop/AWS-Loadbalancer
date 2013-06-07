package ece1779.ec2.android;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import com.google.gson.Gson;
import com.sun.tools.javac.util.List;

import ece1779.ec2.models.User;

public class QueryDatabase extends HttpServlet {

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

		

		try {
			PrintWriter out = response.getWriter();
			String query = request.getParameter("queryimages");

			String json = this.sendbackQueryResult(query, out);
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			response.getWriter().write(json);
			// User user = new User();
			// user.setId(id);
			// user.setUsername(username);
			//
			// session.setAttribute("user", user);
			//
			// response.sendRedirect(response.encodeRedirectURL("../Welcome"));

		} catch (Exception e) {
			getServletContext().log(e.getMessage());
		}
	}

	private String sendbackQueryResult(String mission, PrintWriter out) {
		ArrayList<String> list = new ArrayList<String>();
		// out.print("query");
		try {
			Class.forName(this.dbDriver);
			con = DriverManager.getConnection(this.dbURL, this.dbUser,
					this.dbPassword);
			// Execute SQL query
			Statement stmt = con.createStatement();
			String sql = "SELECT * FROM images WHERE mission = " + "'"
					+ mission + "'";

			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				list.add(rs.getString("key"));
				// list.add(rs.getString("key"));
				// list.add(rs.getString("complete"));

			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (Exception e) {
				getServletContext().log(e.getMessage());
			}
		}

		String json = new Gson().toJson(list);

		return json;
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();

		if (session.isNew() || session.getAttribute("user") == null) {
			try {
				RequestDispatcher view = request
						.getRequestDispatcher("../user/query.jsp");
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
