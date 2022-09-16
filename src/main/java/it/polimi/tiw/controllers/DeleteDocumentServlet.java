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
import it.polimi.tiw.daos.DocumentDAO;
import it.polimi.tiw.utils.ConnectionCreator;
import it.polimi.tiw.utils.Pair;

@WebServlet("/DeleteDocument")
public class DeleteDocumentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    public DeleteDocumentServlet() {
        super();
    }
    
    @Override
    public void init() throws ServletException {
    	ServletContext servletContext = getServletContext();
    	connection = ConnectionCreator.newConnection(servletContext);
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int documentId;
		try {
			documentId = Integer.parseInt(request.getParameter("documentId"));
		} catch (NumberFormatException | NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Wrong parameters format");
			return;
		}
		DocumentDAO documentDAO = new DocumentDAO(connection);
		Subdirectory directory;
		try {
			Pair<Document, Subdirectory> pair = documentDAO.findDocumentAndSubdirectory(documentId);
			directory = pair.getSecondElement();
			if (directory.getUserId() != ((User) request.getSession().getAttribute("user")).getUserId()) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				response.getWriter().println("You are not allowed to delete this directory");
				return;
			}
			documentDAO.deleteDocument(documentId);
		} catch (SQLException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, please retry later");
			return;
		} catch (NullPointerException e) {
			e.printStackTrace();
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Document not found");
			return;
		}
		Gson gson = new Gson();
		String subdirectoryJson = gson.toJson(directory);
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
