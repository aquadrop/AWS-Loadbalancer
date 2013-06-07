package ece1779.ec2;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class Logout extends HttpServlet {

	private static final long serialVersionUID = -8826456069879952439L;

	public void doGet(HttpServletRequest request, HttpServletResponse response) {

		HttpSession session = request.getSession();

		if (session.getAttribute("admin") != null) {
			session.removeAttribute("admin");
		}
		
		if (session.getAttribute("user") != null) {
			session.removeAttribute("user");
		}
		
		if (session.getAttribute("Detour")!=null)
			session.removeAttribute("Detour");
		
		
//		if (session!=null)
//			session.invalidate();
//		session = request.getSession(true);
		try {
			response.sendRedirect(response.encodeRedirectURL("user/Login"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
