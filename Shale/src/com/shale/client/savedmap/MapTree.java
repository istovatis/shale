package com.shale.client.savedmap;

public interface MapTree {
	/**
	 * Initialises the fileTree. Gets all the files created by user, in the
	 * form: "username-title.cxl" Keeps only the title and adds the title to the
	 * tree.
	 * 
	 * @param files
	 */

	public void initTree(String[] files);

	/**
	 * Add "Your Files" and "Admin" Root Nodes
	 */
	public void addRoots();
	
	public void showMyMaps();
	
	public String getOnlyTitle(String fullName);

}
