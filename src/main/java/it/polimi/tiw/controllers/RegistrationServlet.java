package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringEscapeUtils;

import it.polimi.tiw.beans.User;
import it.polimi.tiw.daos.UsersDAO;
import it.polimi.tiw.utils.ConnectionCreator;

@WebServlet("/Registration")
@MultipartConfig
public class RegistrationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
    public RegistrationServlet() {
        super();
    }
    
    @Override
    public void init() throws ServletException {
    	connection = ConnectionCreator.newConnection(getServletContext());
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = StringEscapeUtils.escapeJava(request.getParameter("username"));
		String email = StringEscapeUtils.escapeJava(request.getParameter("email"));
		String password = StringEscapeUtils.escapeJava(request.getParameter("password"));
		String repeatedPassword = StringEscapeUtils.escapeJava(request.getParameter("repeatedPassword"));
		Pattern emailPattern = Pattern.compile("^(.+)@(.+)$");
		boolean emailValid = emailPattern.matcher(email).matches();
		boolean isBadRequest = true;
		String badRequestMessage = "";
		if (username == null || password == null || repeatedPassword == null || email == null ||
				username.isBlank() || password.isBlank() || repeatedPassword.isBlank() || email.isBlank()) {
			badRequestMessage = "Missing parameters";
		} else if (!emailValid) {
			badRequestMessage = "Email not valid";
		} else if (!password.equals(repeatedPassword)) {
			badRequestMessage = "Password and repeated password are different";
		} else if (password.length() < 6) {
			badRequestMessage = "Password must be at least of 8 characters";
		} else {
			isBadRequest = false;
		}
		if (isBadRequest) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println(badRequestMessage);
			return;
		}
		UsersDAO usersDAO = new UsersDAO(connection);
		User user = null;
		try {
			user = usersDAO.createUser(username, email, password);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, please retry later");
			return;
		}
		if (user == null) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Username or password are not correct");
			return;
		}
		HttpSession session = request.getSession(true);
		session.setAttribute("user", user);
		session.setAttribute("language", request.getLocale().getLanguage());
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(username);
	}
	
	@Override
	public void destroy() {
		ConnectionCreator.closeConnection(connection);
	}

}
