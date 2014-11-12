package com.shale.client.exporter;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface ExportServiceAsync {

	public void saveFile(String export,String dir, String fileName,String occasion, AsyncCallback callback);
	
	public void renameFile(String dir, String oldUser, String newUser, String title, AsyncCallback callback);
	
	public void copyDirectory(String sourceLocation , String targetLocation, AsyncCallback callback);
}
