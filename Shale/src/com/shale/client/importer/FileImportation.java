package com.shale.client.importer;

import com.google.gwt.core.client.GWT;
import com.shale.client.conceptmap.MainPlace;
import com.shale.client.user.Student;
import com.shale.client.user.Teacher;
import com.shale.client.user.User;

/**
 * Imports a file given the selected username and directory to search/
 * @author Istovatis -- istovatis@gmail.com --
 *
 */
public abstract class FileImportation {

	private ImportServiceAsync importSvc = GWT.create(ImportService.class);
	public static String fileName;
	protected String directory;

	private static String text;

	public abstract void importFile(String fileName, String directory);

	public static String getFileName() { return fileName; }

	public String getDirectory() { return directory; }
	public void setDirecotory(String directory) { this.directory = directory; }
	
	public abstract void setFileName();

	public void setCurrentDirectory() {
		directory = "";
	}

	public static void setText(String txt) { text = txt; }
	public static String getText() { return text; }

	public String removeFileType(String file) {
		return file.replace(".cxl", "");
	}

	/**
	 * Update the name of the file imported
	 */
	public static void updateFileName() {
		// get MainView tokens.
		String[] tokens = MainPlace.getMainTokens();
		User.setAllData();
		// Map author was set as tokens[0] at menuView. It is not
		// User.getUserName() because teacher may open his/her student's map and
		// not his own
		String author = tokens[0];
		String group = User.getGroup();
		// File name depends on user's group
		if (group.equals("teacher")) {
			Teacher admin = new Teacher();
			// If author of the map is the teacher, set file as
			// admin-user-title.cxl
			if (author.equals(User.getUsername())) {
				admin.setFileName();
			}
			// if author is not this teacher, set file as user-title.cxl
			else {
				admin.setFileName(author);
			}
			fileName = admin.getFileName();
		} else if (group.equals("student")) {
			Student student = new Student();
			student.setFileName();
			fileName = student.getFileName();
		}
	}
	
	/**
	 * Set the fileName for a teacher that creates a new map.
	 */
	public static void fileName4NewMap(){
		Teacher admin = new Teacher();
		admin.setFileName();
	}

}
