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

import it.polimi.tiw.beans.Subdirectory;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.daos.DirectoryDAO;
import it.polimi.tiw.daos.DocumentDAO;
import it.polimi.tiw.utils.ConnectionCreator;

@WebServlet("/CreateDocument")
@MultipartConfig
public class CreateDocumentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    public CreateDocumentServlet() {
        super();
    }
    
    @Override
    public void init() throws ServletException {
    	ServletContext servletContext = getServletContext();
    	connection = ConnectionCreator.newConnection(servletContext);
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String documentName = StringEscapeUtils.escapeJava(request.getParameter("name"));
		String summary = StringEscapeUtils.escapeJava(request.getParameter("summary"));
		String type = StringEscapeUtils.escapeJava(request.getParameter("type"));
		if (documentName == null || summary == null || documentName.isBlank() || summary.isBlank() ||
				type == null || type.isBlank()) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Missing document name, summary or type");
			return;
		}
		if (type.charAt(0) != '.') {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Not a valid document type");
			return;
		}
		int subdirectoryId;
		try {
			subdirectoryId = Integer.parseInt(StringEscapeUtils.escapeJava(request.getParameter("subdirectoryId")));
		} catch(NumberFormatException | NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Wrong parameters format");
			return;
		}
		Date creationDate = (Date) request.getAttribute("creationDate");
		DocumentDAO documentDAO = new DocumentDAO(connection);
		DirectoryDAO directoryDAO = new DirectoryDAO(connection);
		try {
			Subdirectory subdirectory = (Subdirectory) directoryDAO.findDirectoryById(subdirectoryId);
			if (subdirectory.getUserId() != ((User) request.getSession().getAttribute("user")).getUserId()) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().println("You are not allowed to access this subdirectory because you are not the owner");
				return;
			}
			documentDAO.createDocument(documentName, creationDate, summary, subdirectoryId, type);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, please retry later");
			return;
		} catch (NullPointerException | ClassCastException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Subdirectory not found in your directory tree");
			return;
		}
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(subdirectoryId);
	}
	
	@Override
	public void destroy() {
		ConnectionCreator.closeConnection(connection);
	}
}
