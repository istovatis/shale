package com.shale.client.user;

import com.google.gwt.core.client.GWT;
import com.shale.client.conceptmap.MainPlace;
import com.shale.client.importer.FileImportation;
import com.shale.client.importer.ImportService;
import com.shale.client.importer.ImportServiceAsync;
import com.shale.client.menu.MenuView;

public abstract class User extends FileImportation {
	private ImportServiceAsync importSvc = GWT.create(ImportService.class);

	protected static String username;

	protected static String password;
	protected static String mail;
	protected static String title;
	protected static String description = "";
	protected static String organisation;
	protected static String group;
	protected static String[] allData = new String[7];

	public User() {
		// Just Do It
	}

	public User(String[] tokens) {
		tokens = MainPlace.getMainTokens();
		username = tokens[0];
		password = tokens[1];
		mail = tokens[2];
		title = tokens[3];
		description = tokens[4];
		organisation = tokens[5];

		setAllData();
	}

	// Gets user password and organisation
	public abstract String[] getUserData(String user);
	
	/**
	 * Firstly add me at listbox. Then add my students if I am teacher, or my teacher if I am student.
	 */
	public abstract void initUsersListBox();
	
	public static void setUsername(String user) { username = user; }
	public static String getUsername() { return username; }

	public static void setPassword(String pass) { password = pass; }
	public static String getPassword() { return password; }

	public static void setMail(String email) { mail = email; }
	public static String getMail() { return mail; }

	public static void setTitle(String tit) { title = tit; }
	public static String getTitle() { return title; }

	public static void setDescription(String desc) { description = desc; }
	public static String getDescription() { return description; }

	public static void setOrganisation(String org) { organisation = org; }
	public static String getOrganisation() { return organisation; }
	
	public static void setGroup(String gr) { group = gr; }
	public static String getGroup() { return group; }

	public static String[] getAllData() {
		return allData;
	}

	public static void setAllData() {
		allData[0] = getUsername();
		allData[1] = getPassword();
		allData[2] = getMail();
		allData[3] = getTitle();
		allData[4] = getDescription();
		allData[5] = getOrganisation();
		allData[6] = getGroup();
	}

	/**
	 * Load students.cxl or teachers.cxl file at current directory.
	 * 
	 */
	public void loadUsersFile() {
		setUsersFile();
		// Locate the current directory
		setCurrentDirectory();
		String currentDir = getDirectory();
		String file = getFileName();
		importFile(file, currentDir);
	}

	/**
	 * Load concept map. Be aware of the setFileName() method. Teachers and
	 * student set their fileName differently.
	 * 
	 */
	public void loadMyMap() {
		setFileName();
		String file = getFileName();
		String dir = getOrganisation();
		importFile(file, dir);
	}

	/**
	 * Gets the user's password stored in .cxl file and compares it with the
	 * given one. If success return true and set Organisation.
	 * 
	 * @param user
	 * @param password
	 * @return
	 */
	public abstract boolean validateUser(String user, String password);

	public void setFileName() {
		// this.fileName = "users";
	}

	public void setUsersFile() {
		// Implemented by Teacher and Student Class
	}

	/**
	 * Adds myMaps at myMaps list box.
	 * 
	 */
	public void initMyMapsListBox(String[] maps) {
		for (String map : maps) {
			String fileName = removeFileType(map);
			String mapTitle = getOnlyTitle(fileName);
			MenuView.myMapsList.addItem(mapTitle);
		}
	}

	/**
	 * Remove the author and the type extenstion from a map name file
	 * In case of "admin"-"teacher"-title.cxl remove "admin" and "teacher".
	 * In case of "student"-title.cxl just remove student. 
	 */
	public String getOnlyTitle(String fullName) {
		String admin = "admin";
		int splitter = fullName.indexOf("-");
		String title = fullName.substring(splitter + 1);
		if(fullName.startsWith(admin)){
			splitter = title.indexOf("-");
			title = title.substring(splitter + 1);
		}
		return title;
	}

}