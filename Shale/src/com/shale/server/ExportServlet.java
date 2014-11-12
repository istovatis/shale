package com.shale.server;

/*
 * 
 *Zombie file. This file has replaced by ExportService. 
 *
 */

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ExportServlet extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String export = request.getParameter("diagram");
		File exportFile = new File("exportDiagrams.xml");
		try {
			FileWriter writer = new FileWriter("exportDiagrams.xml");
			BufferedWriter out = new BufferedWriter(writer);
			out.write(export);
			out.close();
			// String url = GWT.getHostPageBaseURL();
			// Window.Location.replace(url);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// response.getWriter().write("Hello " + name +
		// ", Welcome in My Servlet");
	}
}
