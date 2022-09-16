package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringEscapeUtils;

import it.polimi.tiw.beans.Directory;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.daos.DirectoryDAO;
import it.polimi.tiw.utils.ConnectionCreator;

@WebServlet("/CreateSubdirectory")
@MultipartConfig
public class CreateSubdirectoryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;

    public CreateSubdirectoryServlet() {
        super();
    }
    
    @Override
    public void init() throws ServletException {
    	ServletContext servletContext = getServletContext();
    	connection = ConnectionCreator.newConnection(servletContext);
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String subdirectoryName = StringEscapeUtils.escapeJava(request.getParameter("name"));
		if (subdirectoryName == null || subdirectoryName.isBlank()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Missing directory name");
			return;
		}
		int directoryId;
		try {
			directoryId = Integer.parseInt(StringEscapeUtils.escapeJava(request.getParameter("directoryId")));
		} catch(NumberFormatException | NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Wrong parameters format");
			return;
		}
		Date creationDate = (Date) request.getAttribute("creationDate");
		DirectoryDAO directoryDAO = new DirectoryDAO(connection);
		try {
			Directory directory = directoryDAO.findDirectoryById(directoryId);
			int userId = ((User) request.getSession().getAttribute("user")).getUserId();
			if (directory.getUserId() != userId) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().println("You are not allowed to access this directory");
				return;
			}
			directoryDAO.createSubdirectory(subdirectoryName, creationDate, directoryId, ((User) request.getSession().getAttribute("user")).getUserId());
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, please retry later");
			return;
		} catch (NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Directory not found in your directory tree");
			return;
		}
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(subdirectoryName);
	}
	
	@Override
	public void destroy() {
		ConnectionCreator.closeConnection(connection);
	}
}
