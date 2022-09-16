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

import com.google.gson.Gson;

import it.polimi.tiw.beans.Document;
import it.polimi.tiw.beans.Subdirectory;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.daos.DirectoryDAO;
import it.polimi.tiw.daos.DocumentDAO;
import it.polimi.tiw.utils.ConnectionCreator;
import it.polimi.tiw.utils.Pair;

@WebServlet("/MoveDocument")
public class MoveDocumentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
	
    public MoveDocumentServlet() {
        super();
    }
    
    @Override
    public void init() throws ServletException {
    	ServletContext servletContext = getServletContext();
    	connection = ConnectionCreator.newConnection(servletContext);
    }
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int documentId, subdirectoryId;
		try {
			documentId = Integer.parseInt(request.getParameter("documentId"));
			subdirectoryId = Integer.parseInt(request.getParameter("subdirectoryId"));
		} catch (NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Wrong parameters format");
			return;
		}
		DocumentDAO documentDAO = new DocumentDAO(connection);
		DirectoryDAO directoryDAO = new DirectoryDAO(connection);
		Subdirectory subdirectory;
		Subdirectory previousSubdirectory;
		try {
			subdirectory = (Subdirectory) directoryDAO.findDirectoryById(subdirectoryId);
			Pair<Document, Subdirectory> pair = documentDAO.findDocumentAndSubdirectory(documentId);
			previousSubdirectory = pair.getSecondElement();
			if (subdirectory.getUserId() != ((User) request.getSession().getAttribute("user")).getUserId()
					|| previousSubdirectory.getUserId() != ((User) request.getSession().getAttribute("user")).getUserId()) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("You are not allowed to move this document because you are not the owner");
				return;
			}
			documentDAO.moveDocument(documentId, subdirectoryId);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, please retry later");
			return;
		} catch (ClassCastException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Not a subdirectory");
			return;
		}
		Gson gson = new Gson();
		String subdirectoryJson = gson.toJson(subdirectory);
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(subdirectoryJson);
	}
	
	@Override
	public void destroy() {
		ConnectionCreator.closeConnection(connection);
	}
}
