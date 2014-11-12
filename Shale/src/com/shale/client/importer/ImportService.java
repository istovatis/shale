package com.shale.client.importer;

import java.util.ArrayList;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("ImportService")
public interface ImportService extends RemoteService {

	public String importDiagram(String user, String title, String org);
	// New adaptation, only give the filename and the directory
	public String importDiagram(String fileName, String dir);
	public String getFileText(String dir, String fileName, String occasion);
	public String[] fileFinder(String useranme, String organisation);
	String importMetadata(String fileName, String dir);
	//this is the arraylist contains arraylist of vertices
	ArrayList<ArrayList<Integer>> getVertices(String fileName, String dir,
			int graph, int currentGraphs, boolean increase);
	public boolean getRestart();
	
	/**
	 * Utility class for simplifying access to the instance of async service.
	 */
	public static class Util {
		private static ImportServiceAsync instance;

		public static ImportServiceAsync getInstance() {
			if (instance == null) {
				instance = GWT.create(ImportService.class);
			}
			return instance;
		}
	}
}
