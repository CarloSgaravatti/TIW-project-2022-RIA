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

import it.polimi.tiw.beans.User;
import it.polimi.tiw.daos.DirectoryDAO;
import it.polimi.tiw.utils.ConnectionCreator;

@WebServlet("/CreateDirectory")
@MultipartConfig
public class CreateDirectoryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
    public CreateDirectoryServlet() {
        super();
    }
    
    @Override
    public void init() throws ServletException {
    	ServletContext servletContext = getServletContext();
    	connection = ConnectionCreator.newConnection(servletContext);
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String directoryName = StringEscapeUtils.escapeJava(request.getParameter("name"));
		if (directoryName == null || directoryName.isBlank()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Missing directory name");
			return;
		}
		Date creationDate = (Date) request.getAttribute("creationDate");
		DirectoryDAO directoryDAO = new DirectoryDAO(connection);
		try {
			directoryDAO.createDirectory(directoryName, creationDate, ((User) request.getSession().getAttribute("user")).getUserId());
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, please retry later");
			return;
		}
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(directoryName);
	}
	
	@Override
	public void destroy() {
		ConnectionCreator.closeConnection(connection);
	}
}
