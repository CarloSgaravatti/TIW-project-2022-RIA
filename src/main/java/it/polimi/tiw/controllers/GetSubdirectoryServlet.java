package it.polimi.tiw.controllers;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

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

@WebServlet("/GetSubdirectory")
public class GetSubdirectoryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    public GetSubdirectoryServlet() {
        super();
    }
    
    @Override
    public void init() throws ServletException {
    	ServletContext servletContext = getServletContext();
    	connection = ConnectionCreator.newConnection(servletContext);
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		int subdirectoryId;
		try {
			subdirectoryId = Integer.parseInt(request.getParameter("subdirectoryId"));
		} catch (NumberFormatException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Wrong parameters format");
			return;
		}
		DirectoryDAO directoryDAO = new DirectoryDAO(connection);
		Subdirectory subdirectory;
		List<Document> documents = null;
		DocumentDAO documentDAO = new DocumentDAO(connection);
		try {
			subdirectory = (Subdirectory) directoryDAO.findDirectoryById(subdirectoryId);
			if (subdirectory.getUserId() != ((User) request.getSession().getAttribute("user")).getUserId()) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				response.getWriter().println("You are not allowed to access this subdirectory because you are not the owner.");
				return;
			}
			documents = documentDAO.findAlldocumentsOfSubdirectory(subdirectoryId);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, please retry later");
			return;
		} catch (ClassCastException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Not a subdirectory");
			return;
		} catch (NullPointerException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Subdirectory not found");
			return;
		}
		Gson gson = new Gson();
		String documentsJson = gson.toJson(documents);
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(documentsJson);
	}
	
	@Override
	public void destroy() {
		ConnectionCreator.closeConnection(connection);
	}
}
