package ece1779.ec2.user;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;

import ece1779.ec2.models.Image;

/**
 * this servlet shows all thumbnails of their original images with the UserId
 * that is integer
 * 
 * @author group13
 */
public class ViewImage extends HttpServlet {

	private static final long serialVersionUID = 7363340829032738565L;

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
		Image image = new Image();
		HashMap<String, String> keys = new HashMap<String, String>();
		
		try {
			// Get image ID
			image.setId(Integer.parseInt(request.getParameter("id")));
			
			DataSource dbcp = (DataSource) this.getServletContext().getAttribute("dbpool");
			conn = dbcp.getConnection();
			Statement stmt = conn.createStatement();
			
			String sql = "SELECT * FROM images WHERE Id = " + image.getId();
			ResultSet rs = stmt.executeQuery(sql);

			rs.next();
			keys.put("key1", rs.getString("key1"));
			keys.put("key2", rs.getString("key2"));
			keys.put("key3", rs.getString("key3"));
			keys.put("key4", rs.getString("key4"));
			
			image.setKeys(keys);
			image.setUserId(rs.getInt("userId"));
		
			request.setAttribute("imageObj", image);
			RequestDispatcher view = request.getRequestDispatcher("viewImage.jsp");
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
