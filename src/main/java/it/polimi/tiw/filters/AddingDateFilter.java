package it.polimi.tiw.filters;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;

public class AddingDateFilter extends HttpFilter implements Filter {
	private static final long serialVersionUID = 1L;
	
    public AddingDateFilter() {
        super();
    }

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		java.util.Date date = java.util.Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
		java.sql.Date creationDate = new java.sql.Date(date.getTime());
		HttpServletRequest req = (HttpServletRequest) request;
		req.setAttribute("creationDate", creationDate);

		chain.doFilter(request, response);
	}
}
