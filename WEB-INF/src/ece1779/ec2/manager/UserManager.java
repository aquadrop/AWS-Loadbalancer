package ece1779.ec2.manager;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import ece1779.ec2.models.User;

public class UserManager extends HttpServlet {

	private static final long serialVersionUID = -4663386790374393372L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();

		if (session.isNew() || session.getAttribute("admin") == null) {
			try {
				response.sendRedirect(response.encodeRedirectURL("Login"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Connection conn = null;
		ArrayList<User> users = new ArrayList<User>();

		try {
			DataSource dbcp = (DataSource) this.getServletContext().getAttribute("dbpool");
			conn = dbcp.getConnection();
			Statement stmt = conn.createStatement();

			String sql = "select users.*, count(images.id) as imageCount from users left join images on users.id = images.userId group by users.id";
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				User user = new User();
				user.setId(rs.getInt("id"));
				user.setUsername(rs.getString("login"));
				user.setImageCount(rs.getInt("imageCount"));
				users.add(user);
			}

			request.setAttribute("users", users);
			RequestDispatcher view = request.getRequestDispatcher("userManager.jsp");
			view.forward(request, response);

		} catch (Exception e) {
			getServletContext().log(e.getMessage());
		} finally {
			try {
				conn.close();
			} catch (Exception e) {
				getServletContext().log(e.getMessage());
			}
		}

	}
}
