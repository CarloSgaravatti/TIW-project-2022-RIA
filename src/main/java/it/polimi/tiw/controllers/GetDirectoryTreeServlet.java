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

import it.polimi.tiw.beans.Directory;
import it.polimi.tiw.beans.User;
import it.polimi.tiw.daos.DirectoryDAO;
import it.polimi.tiw.utils.ConnectionCreator;

@WebServlet("/GetDirectoryTree")
public class GetDirectoryTreeServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private Connection connection = null;
       
    public GetDirectoryTreeServlet() {
        super();
    }
    
    @Override
    public void init() throws ServletException {
    	ServletContext servletContext = getServletContext();
    	connection = ConnectionCreator.newConnection(servletContext);
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		User user = (User) request.getSession().getAttribute("user");
		DirectoryDAO directoryDAO = new DirectoryDAO(connection);
		List<Directory> userDirectoryTree;
		try {
			userDirectoryTree = directoryDAO.findDirectoriesAndSubdirectoriesOfUser(user);
		} catch (SQLException e) {
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			response.getWriter().println("Internal server error, please retry later");
			return;
		}
		Gson gson = new Gson();
		String directoryTreeJson = gson.toJson(userDirectoryTree);
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().println(directoryTreeJson);
	}

	@Override
	public void destroy() {
		ConnectionCreator.closeConnection(connection);
	}
}
