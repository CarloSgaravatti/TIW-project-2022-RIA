package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import it.polimi.tiw.beans.Directory;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.daos.DirectoryDAO;
import it.polimi.tiw.utils.ConnectionCreator;

@WebServlet("/DeleteDirectory")
public class DeleteDirectoryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
    
    public DeleteDirectoryServlet() {
        super();
    }
    
    @Override
    public void init() throws ServletException {
    	ServletContext servletContext = getServletContext();
    	connection = ConnectionCreator.newConnection(servletContext);
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int directoryId;
		try {
			directoryId = Integer.parseInt(request.getParameter("directoryId"));
		} catch (NumberFormatException | NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Wrong parameters format");
			return;
		}
		DirectoryDAO directoryDAO = new DirectoryDAO(connection);
		Directory directory;
		try {
			directory = directoryDAO.findDirectoryById(directoryId);
			if (directory.getUserId() != ((User) request.getSession().getAttribute("user")).getUserId()) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("You are not allowed to delete this directory");
				return;
			}
			directoryDAO.deleteDirectory(directoryId);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, please retry later");
			return;
		}
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
	}
	
	@Override
	public void destroy() {
		ConnectionCreator.closeConnection(connection);
	}
}
