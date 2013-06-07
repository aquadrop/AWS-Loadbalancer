package ece1779.ec2.user;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import ece1779.ec2.models.Image;
import ece1779.ec2.models.User;

public class ListImages extends HttpServlet {

	private static final long serialVersionUID = -5197656505348794068L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();

		if (session.isNew() || session.getAttribute("user") == null) {
			try {
				response.sendRedirect(response.encodeRedirectURL("Login"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	
		Connection conn = null;
		User user = (User) session.getAttribute("user");
		ArrayList<Image> images = new ArrayList<Image>();
		
		try {
			DataSource dbcp = (DataSource) this.getServletContext().getAttribute("dbpool");
			conn = dbcp.getConnection();
			Statement stmt = conn.createStatement();
			
			String sql = "SELECT * FROM images WHERE userId = " + user.getId();
			ResultSet rs = stmt.executeQuery(sql);

			while (rs.next()) {
				HashMap<String, String> keys = new HashMap<String, String>();
				keys.put("key1", rs.getString("key1"));
				keys.put("key2", rs.getString("key2"));
				keys.put("key3", rs.getString("key3"));
				keys.put("key4", rs.getString("key4"));
				
				Image image = new Image();
				image.setId(rs.getInt("id"));
				image.setUserId(rs.getInt("userId"));
				image.setKeys(keys);
				
				images.add(image);
			}
			
			request.setAttribute("imageObjs", images);
			RequestDispatcher view = request.getRequestDispatcher("listImages.jsp");
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
