package com.shale.server;

import java.io.File;

public class Files {
	protected String organisation;
	protected String fileName;
	protected String directory;
	protected String fullPath;
	
	public Files(){
		//Just Do It
	}
	
	public Files(String fileName, String title, String dir) {
		setFileName(fileName, title);
		setDirectory(dir);
		setFullPath();
	}
	
	public Files(String fileName, String dir) {
		setFileName(fileName);
		setDirectory(dir);
		setFullPath();
	}

	public void setOrganisation(String organisation) {
		this.organisation = organisation;
	}

	public String getOrganisation() {
		return organisation;
	}

	public void setFileName(String fileName, String title) {
		this.fileName = fileName + "-" + title + ".cxl";
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName + ".cxl";
	}

	public String getFileName() {
		return fileName;
	}
	
	/**
	 * Set the directory. If the directory is the current, don't add the file Seperator
	 * @param dir
	 */
	public void setDirectory(String dir) {
		if(dir.isEmpty())
			directory = dir;
		else
		directory = dir + File.separator;	
	}

	public String getDirectory() {
		return directory;
	}
	
	/**
	 * Gets the information that user typed in Login Form. Needed for file
	 * name:tokens[0]:username, tokens[3]:title Needed for directory
	 * tokens[5]:organisation
	 * 
	 * if organisation=tei set path file as tei/fileName.cxl etc etc...
	 * 
	 */
	public void setFullPath() {
		fullPath = getDirectory() + getFileName(); 
	}
	
	public String getFullPath(){
		return fullPath;
	}
}
