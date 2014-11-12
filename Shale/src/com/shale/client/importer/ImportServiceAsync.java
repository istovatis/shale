package com.shale.client.importer;

import java.util.ArrayList;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ImportServiceAsync {
	// import metadata from the concept map
	public void importMetadata(String fileName, String dir, AsyncCallback <String> callback);
	public void importDiagram(String user, String title, String org, AsyncCallback <String> callback);
	public void importDiagram(String fileName, String dir, AsyncCallback <String> callback);
	public void getFileText(String dir, String fileName, String occasion, AsyncCallback <String> callback) ;
	public void fileFinder(String username, String organisation, AsyncCallback <String []> callback);
	//get the vertices from the server
	void getVertices(String fileName, String dir, int graph, int currentGraphs, boolean increase,
			AsyncCallback<ArrayList<ArrayList<Integer>>> callback);
	public void getRestart(AsyncCallback<Boolean> callback);
}
