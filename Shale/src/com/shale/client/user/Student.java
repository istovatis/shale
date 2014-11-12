package com.shale.client.user;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.i18n.client.TimeZone;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;
import com.shale.client.conceptmap.MainView;
import com.shale.client.exporter.DiagramExportation;
import com.shale.client.exporter.ExportService;
import com.shale.client.exporter.ExportServiceAsync;
import com.shale.client.importer.DiagramImportation;
import com.shale.client.importer.ImportService;
import com.shale.client.importer.ImportServiceAsync;
import com.shale.client.menu.MenuView;
import com.shale.client.savedmap.Description;
import com.shale.client.utils.Languages;

public class Student extends User {

	private static String myTeacher;
	private static String studentList;		//xml-like student list
	private String name;
	
	private static int studentsCount = 0;		//total count of students stored at students.cxl file
	private static int nextId = 0;		//next student id to be saved at students.cxl file
	
	private TreeItem myNode;
	private TreeItem adminNode;
	Dictionary dict = Languages.getDictionary();
	private DiagramImportation diagram;
	private ExportServiceAsync exportSvc = GWT.create(ExportService.class);
	private ImportServiceAsync importSvc = GWT.create(ImportService.class);

	public String getName(){
		return name;
	}
	
	public static void clearStudents(){
		studentsCount = 0;
		nextId = 0;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public static int getNextId() {
		return nextId;
	}

	public static void setNextId(int nextId) {
		Student.nextId = nextId;
	}
	
	public static String getMyTeacher() {
		return myTeacher;
	}

	public static void setMyTeacher(String teacher) {
		myTeacher = teacher;
	}

	public static void setStudentList(String st) {
		studentList = st;
	}

	public static String getStudentList() {
		return studentList;
	}
	
	public static int getStudentsCount() {
		return studentsCount;
	}

	public static void setStudentsCount(int studentsCount) {
		Student.studentsCount = studentsCount;
	}
	
	public void initUsersListBox(){
		MenuView.myStudentsList.addItem(User.getUsername());
		MenuView.myStudentsList.addItem(Student.getMyTeacher());
	}

	/**
	 * Check if the user is listed in tag "student" If listed, check if map
	 * exists.If not, add a map.
	 * 
	 * @return true if user exists. or else false
	 */
	public void listStudent() {
		boolean check = false;
		boolean mapCheck = false;

		String descFile = User.getDescription();
		importDescNameFile(descFile);
		String txt = getText();
		Document doc = XMLParser.parse(txt);
		Element root = doc.getDocumentElement();
		String username = User.getUsername();
		String title = User.getTitle();

		NodeList nodeList = root.getElementsByTagName("student");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element node = (Element) nodeList.item(i);
			String name = node.getAttribute("name");
			if (name.equals(username)) {
				check = true;
				// check if map exists. If not, add the map
				NodeList mapList = root.getElementsByTagName("map");
				for (int j = 0; j < mapList.getLength(); j++) {
					Element mapNode = (Element) nodeList.item(j);
					String tit = mapNode.getAttribute("title");
					if (title.equals(tit)) {
						mapCheck = true;
					}
				}
				if (mapCheck == false) {
					Node add = oldStudentForCXL();
					root.appendChild(add);
				}
			}
		}
		// If student has not listed, list him now
		if (check == false) {
			Node add = newStudentForCXL();
			doc.appendChild(add);
		}
	}

	/**
	 * Import text from "username"-"title" file from organisation directory RPC
	 * to retrive data.
	 */
	public void importFile(String fileName) {
		// Initialize the service proxy.
		if (importSvc == null) {
			importSvc = GWT.create(ImportService.class);
		}
		// Set up the callback object.
		AsyncCallback<String> callback = new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				Window.alert("Cannot load data. Caused by: "
						+ caught.getMessage() + "Trace:"
						+ caught.getStackTrace() + "also: " + caught.getClass());
			}

			public void onSuccess(String result) {
				if (result == null) {
					// usename-title.cxl file is empty
					Window.alert("This file is empty");
				} else {
					setStudentList(result);
				}
			}
		};
		// Locate tei or anatolia directory
		String dir = getOrganisation();
		String occasion = "userlist";
		importSvc.getFileText(dir, getFileName(), occasion, callback);
	}

	/**
	 * Call this method when a student wants to access a description file to
	 * determine if he is listed. Import text from "username"-"title" file from
	 * organisation directory RPC to retrive data.
	 */
	public void importDescNameFile(String fileName) {
		// Initialize the service proxy.
		if (importSvc == null) {
			importSvc = GWT.create(ImportService.class);
		}
		// Set up the callback object.
		AsyncCallback<String> callback = new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				Window.alert("Cannot load data. Caused by: "
						+ caught.getMessage() + "Trace:"
						+ caught.getStackTrace() + "also: " + caught.getClass());
			}

			public void onSuccess(String result) {
				if (result == null) {
					// file is empty
					Window.alert("This description file is empty");
				} else {
					setText(result);
				}
			}
		};
		// Locate tei or anatolia directory
		String dir = getOrganisation();
		String occasion = "descName";
		importSvc.getFileText(dir, fileName, occasion, callback);
	}

	/**
	 * The fileName as: "user"-"title"
	 * 
	 */
	public void setFileName() {
		String user = User.getUsername();
		String title = User.getTitle();
		String dir = User.getOrganisation();
		String mapName = user + "-" + title;
		this.fileName = mapName;
	}
	
	public void showMyMaps() {
		// Initialize the service proxy.
		if (importSvc == null) {
			importSvc = GWT.create(ImportService.class);
		}

		// Set up the callback object.
		AsyncCallback<String[]> callback = new AsyncCallback<String[]>() {
			public void onFailure(Throwable caught) {
				Window.alert("Cannot load existing concepts. Caused by: "
						+ caught.getMessage());
			}

			public void onSuccess(String[] result) {
				if (result == null) {
					Window.alert("This is your first concept map");
				} else {
					MainView.fileTree.setVisible(true);
				}
			}
		};
		String username = User.getUsername();
		String org = User.getOrganisation();
		importSvc.fileFinder(username, org, callback);
	}

	/**
	 * Adds the student to description file.
	 * 
	 * @param user
	 * @param descFile
	 */
	public void listStudentRPC(String export) {
		String dir = getOrganisation();
		String teacher = Teacher.getSelectedTeacher();
		String file = teacher + "-" + getDescription();
		String header = DiagramExportation.createXmlHeader("");
		// Initialize the service proxy.
		if (exportSvc == null) {
			exportSvc = GWT.create(ExportService.class);
		}

		// Set up the callback object.
		AsyncCallback<ExportService> callback = new AsyncCallback<ExportService>() {
			public void onFailure(Throwable caught) {
				Window.alert("Could not add student to this description. Caused by: "
						+ caught.getMessage());
			}

			public void onSuccess(ExportService result) {
				// Nothing at the moment
			}
		};
		String occasion = "addStudent";
		exportSvc.saveFile(export, dir, file, occasion, callback);
	}

	/**
	 * For student that has not listed in description_name file yet. Format a
	 * student tag for cxl, containing name, title and the current time.
	 * 
	 * @return
	 */
	public Node newStudentForCXL() {
		Document doc = XMLParser.createDocument();
		Element user = doc.createElement("student");
		Element map = doc.createElement("map");
		// set the current time
		Date date = new Date();
		DateTimeFormat dtf = DateTimeFormat.getFormat("yyyyMMddHHmmss");
		String now = dtf.format(date, TimeZone.createTimeZone(0));
		String student = getUsername();
		String title = getTitle();
		user.setAttribute("name", student);
		map.setAttribute("title", title);
		map.setAttribute("date", now);

		user.appendChild(map);
		doc.appendChild(user);

		return doc;
	}

	/**
	 * For student that has already listed in description_name file. Just add a
	 * new node in student_name tag.
	 * 
	 */
	public Node oldStudentForCXL() {
		Document doc = XMLParser.createDocument();
		Element map = doc.createElement("map");
		// set the current time
		Date date = new Date();
		DateTimeFormat dtf = DateTimeFormat.getFormat("yyyyMMddHHmmss");
		String now = dtf.format(date, TimeZone.createTimeZone(0));
		map.setAttribute("title", title);
		map.setAttribute("date", now);

		doc.appendChild(map);
		return doc;
	}

	/**
	 * Get students from students.cxl RPC to retrieve data from students file.
	 */
	public void importFile(String fileName, String dir) {
		// Initialize the service proxy.
		if (importSvc == null) {
			importSvc = GWT.create(ImportService.class);
		}
		// Set up the callback object.
		AsyncCallback<String> callback = new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				Window.alert("Cannot load data. Caused by: "
						+ caught.getMessage() + "Trace:"
						+ caught.getStackTrace() + "also: " + caught.getClass());
			}

			public void onSuccess(String result) {
				if (result == null) {
					Window.alert("Database contains no users");
				} else {
					setStudentList(result);
				}
			}
		};
		String occasion = "users";
		importSvc.getFileText(dir, fileName, occasion, callback);
	}

	/**
	 * Parse a text and search for student. First find his teacher. Search
	 * teacher node for the specific student and return his password and
	 * organisation.
	 */
	public String[] getUserData(String user) {
		String text = getStudentList();
		Document doc = XMLParser.parse(text);
		Element root = doc.getDocumentElement();
		NodeList nodeList = root.getElementsByTagName("teacher");
		String pass = null;
		// array containing password and organisation
		String[] data = new String[2];

		for (int i = 0; i < nodeList.getLength(); i++) {
			Element teach = (Element) nodeList.item(i);
			String teacherName = teach.getAttribute("name");
			String org = teach.getAttribute("organisation");
			NodeList studentList = teach.getElementsByTagName("student");
			for (int j = 0; j < studentList.getLength(); j++) {
				Element stu = (Element) studentList.item(j);
				String storedUser = stu.getAttribute("name");
				String password = stu.getAttribute("password");
				// System.out.println("name:"+storedUser+" "+"pass:"+password+"Teacher:"+getMyTeacher());
				if (user.equals(storedUser)) {
					data[0] = password;
					data[1] = org;
					setOrganisation(org);
					setMyTeacher(teacherName);
				}
			}
		}
		return data;
	}

	public void setUsersFile() {
		this.fileName = "students";
	}

	/**
	 * Set user as "admin"+MyTeacher and get his maps.
	 * 
	 */
	public void showMyTeacherMaps() {
		// Initialize the service proxy.
		if (importSvc == null) {
			importSvc = GWT.create(ImportService.class);
		}

		// Set up the callback object.
		AsyncCallback<String[]> callback = new AsyncCallback<String[]>() {
			public void onFailure(Throwable caught) {
				Window.alert("Cannot load existing concepts. Caused by: "
						+ caught.getMessage());
			}

			public void onSuccess(String[] result) {
				if (result == null) {
					Window.alert("This is your first concept map");
				} else {
					initMyMapsListBox(result);
				}
			}
		};
		// get my teacher. Load his maps
		String username = "admin" + "-" + getMyTeacher();
		String org = User.getOrganisation();
		importSvc.fileFinder(username, org, callback);
	}

	/**
	 * Validate user. Set group as "student"
	 * 
	 */
	public boolean validateUser(String user, String password) {
		String[] data = new String[2];
		data = getUserData(user);
		String storedPassword = data[0];
		String org = data[1];
		if (password.equals(storedPassword)) {
			User.setOrganisation(org);
			User.setGroup("student");
			return true;
		} else {
			return false;
		}
	}

	public void setMyTeachersDescription() {
		if (User.getDescription().isEmpty()) {
			getMyTeachersDescription();
		}
	}

	/**
	 * Set user as "admin"-"Myteacher", and import description from file
	 * "admin"-"Myteacher"-"title".cxl from dir org
	 */
	public void getMyTeachersDescription() {
		String user = "admin" + "-" + Student.getMyTeacher();
		String title = User.getTitle();
		String org = User.getOrganisation();
		Description desc = new Description();
		desc.importDescRPC(user, title, org);
	}
	
	public void initMyDescription(){
		if(User.getDescription().isEmpty()){
			setMyTeachersDescription();
		}
	}
}
