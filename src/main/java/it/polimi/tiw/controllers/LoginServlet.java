package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

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

@WebServlet("/Login")
@MultipartConfig
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    public LoginServlet() {
        super();
    }
    
    @Override
    public void init() throws ServletException {
    	connection = ConnectionCreator.newConnection(getServletContext());
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String username = StringEscapeUtils.escapeJava(request.getParameter("username"));
		String password = StringEscapeUtils.escapeJava(request.getParameter("password"));
		if (username == null || password == null || username.isBlank() || password.isBlank()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Missing parameters");
			return;
		}
		UsersDAO usersDAO = new UsersDAO(connection);
		User user = null;
		try {
			user = usersDAO.checkCredentials(username, password);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, please retry later");
			return;
		}
		if (user == null) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.getWriter().println("User not found: username or password are not correct");
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
