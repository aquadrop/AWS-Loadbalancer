package ece1779.ec2;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * 
 */
public class WelcomePage extends HttpServlet {

	private static final long serialVersionUID = -6085045202556946766L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		HttpSession session = request.getSession();
		
		if (session.isNew() || (session.getAttribute("user") == null && session.getAttribute("admin") == null)) {
			try {
				response.sendRedirect(response.encodeRedirectURL("user/Login"));
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			try {
				RequestDispatcher view = request.getRequestDispatcher("index.jsp");
				view.forward(request, response);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
}
