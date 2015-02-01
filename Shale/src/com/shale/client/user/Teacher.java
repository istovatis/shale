package com.shale.client.user;

import java.util.ArrayList;
import java.util.HashSet;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;
import com.shale.client.exporter.DiagramExportation;
import com.shale.client.exporter.ExportService;
import com.shale.client.exporter.ExportServiceAsync;
import com.shale.client.importer.DiagramImportation;
import com.shale.client.importer.FileImportation;
import com.shale.client.importer.ImportService;
import com.shale.client.importer.ImportServiceAsync;
import com.shale.client.menu.MenuPlace;
import com.shale.client.menu.MenuView;
import com.shale.client.savedmap.Description;
import com.shale.client.user.insert.InsertView;
import com.shale.client.utils.Languages;

public class Teacher extends User {
	private static String teacherList;
	private static ArrayList<String> myStudents = new ArrayList<String>(0);
	private static HashSet<String> otherStudents = new HashSet<String>(0);		// Students belong to other teachers

	private ImportServiceAsync importSvc = GWT.create(ImportService.class);
	private ExportServiceAsync exportSvc = GWT.create(ExportService.class);
	private DiagramImportation diagram;
	private static String selectedTeacher;
	private Student student;
	// new name edited by teacher @InsertView.
	private String name;
	private static int numTeachers;		//Number of teachers at teachers.cxl

	public static int getNumTeachers() {return numTeachers; }
	public static void setNumTeachers(int numTeachers) { Teacher.numTeachers = numTeachers; }

	Dictionary dict = Languages.getDictionary();

	public Teacher(String name) {
		setDescription(name);
	}
	public void chooseMap() {}
	public Teacher() { }

	public void setName(String name) { this.name = name; }
	public String getName() { return name; }

	public static void setSelectedTeacher(String tech) { selectedTeacher = tech; }
	public static String getSelectedTeacher() { return selectedTeacher; }
	
	public static void setTeacherList(String teach) { teacherList = teach; }
	public static String getTeacherList() { return teacherList; }

	/**
	 * Get the whole context of the students.cxl file. Keep only my students.
	 */
	public void loadAllStudents() {
		// get the whole context of the students.cxl file
		String importedStudents = Student.getStudentList();
		setStudentStatus(importedStudents);
	}

	public void initUsersListBox() {
		MenuView.myStudentsList.addItem(User.getUsername());
		loadAllStudents();
	}

	/**
	 * Set myStudents, students count and next student Id.
	 * 
	 * Read importedText and check the attribute "name" of tag "teacher".If the
	 * name is mine, the nested students are also mine. Firstly store myself at
	 * this list. Then Store all these students in myStudents arrayList.
	 * 
	 * @param importedText
	 */
	public void setStudentStatus(String importedText) {
		myStudents.clear();
		otherStudents.clear();
		//rearrangeIds();	// reset students ids
		Document doc = XMLParser.parse(importedText);
		Element root = doc.getDocumentElement();
		NodeList nodeList = root.getElementsByTagName("teacher");
		String curTeacher = getUsername();
		int i = 0;
		int maxId = 0;
		Student.clearStudents();
		int students = Student.getStudentsCount();
		for (i = 0; i < nodeList.getLength(); i++) {
			Element teach = (Element) nodeList.item(i);
			String teacherName = teach.getAttribute("name");
			if (curTeacher.equals(teacherName)) {
				NodeList studentList = teach.getElementsByTagName("student");
				for (int j = 0; j < studentList.getLength(); j++) {
					Element student = (Element) studentList.item(j);
					String studentName = student.getAttribute("name");
					//depricated code
//					if (!studentExist(studentName)) {
//						myStudents.add(studentName);
//					}
					myStudents.add(studentName);
					String id = student.getAttribute("id");
					maxId = findMaxId(id, maxId);
					students++;
				}
			} else {
				NodeList studentList = teach.getElementsByTagName("student");
				for (int j = 0; j < studentList.getLength(); j++) {
					Element student = (Element) studentList.item(j);
					String id = student.getAttribute("id");
					maxId = findMaxId(id, maxId);
					students++;
					String studentName = student.getAttribute("name");
					otherStudents.add(studentName);
				}
			}
		}
		maxId++;
		Student.setNextId(maxId);
		Student.setStudentsCount(students);
		System.out.println(students + " students loaded. Next id: " + maxId);
	}
	
	/**
	 * Find the students max id stored in students.cxl
	 * @param studentId
	 * @param maxId
	 * @return
	 */
	public int findMaxId(String studentId, int maxId) {
		Integer curId = Integer.valueOf(studentId);
		if (maxId < curId)
			maxId = curId;
		return maxId;
	}

	/**
	 * 
	 * Check if student belongs to my students. If not, list him now by
	 * appending him to students.cxl file and making rpc request.
	 */
	public void newStudent(String name, String pass) {
		String importedStudents = Student.getStudentList();
		if (!(studentExist(name))) {
			// set new student's name
			student = new Student();
			student.setName(name);
			// all students, including the new one
			String allStudents = appendStudent(importedStudents, name, pass);
			// make rpc request and save students to students.cxl
			addStudentRPC(allStudents);
		} else
			Window.alert(Languages.getMsgs().get("oldStudent"));
	}

	/**
	 * Search students.cxl file for student and remove him from students.cxl
	 * file and myStudents arraylist
	 * 
	 * @param name
	 *            The student's name
	 */
	public void removeStudent(String name) {
		String text = Student.getStudentList();
		String me = User.getUsername();
		Document doc = XMLParser.parse(text);
		Element root = doc.getDocumentElement();
		// firstly search for current teacher tag
		NodeList teacherList = root.getElementsByTagName("teacher");
		for (int i = 0; i < teacherList.getLength(); i++) {
			Element teach = (Element) teacherList.item(i);
			String teacherName = teach.getAttribute("name");
			// found teacher? Then search for student.
			if (teacherName.equals(me)) {
				NodeList studentList = teach.getElementsByTagName("student");
				for (int j = 0; j < studentList.getLength(); j++) {
					Element st = (Element) studentList.item(j);
					String student = st.getAttribute("name");
					if (student.equals(name))
						teach.removeChild(st);
				}
			}
		}
		// remove student from Teacher's StudentList
		for (int i = 0; i < myStudents.size(); i++) {
			String student = myStudents.get(i);
			if (student.equals(name)) {
				myStudents.remove(i);
			}
		}
		String newText = doc.toString();
		// make rpc request and save New students.cxl
		removeStudentRPC(newText);
	}

	public boolean studentExist(String student) {
		boolean check = false;
		for (String st : myStudents) {
			if (st.equals(student))
				return true;
		}
		for(String st : otherStudents){
			if (st.equals(student))
				return true;
		}
		return false;
	}

	/**
	 * Append student to the teacher list and return the doc with all
	 * nodes(including the new one)
	 * 
	 * @param text
	 * @param student
	 * @return
	 */
	public String appendStudent(String text, String name, String pass) {
		String thisTeacher = getUsername();
		int id = Student.getStudentsCount() + getNumTeachers() + 1;
		Student.setStudentsCount(id);

		Document doc = XMLParser.parse(text);
		Element root = doc.getDocumentElement();
		Element studentEl = doc.createElement("student");
		// set students attributes
		studentEl.setAttribute("id", id + "");
		studentEl.setAttribute("name", name);
		studentEl.setAttribute("password", pass);

		NodeList teacherList = root.getElementsByTagName("teacher");
		for (int i = 0; i < teacherList.getLength(); i++) {
			Element teach = (Element) teacherList.item(i);
			String teacherName = teach.getAttribute("name");
			if (teacherName.equals(thisTeacher))
				teach.appendChild(studentEl);
		}
		return doc.toString();
	}

	/**
	 * Check if description is saved in descriptions.cxl. If not, save it now.
	 */
	public void newDescription() {
		String importedDescs = Description.getText();
		String newDescription = getDescription();
		String author = getUsername();

		Description desc = new Description();

		boolean exist = desc.isDescriptionExists(importedDescs, newDescription,
				author);
		if (!exist) {
			String newCXL = desc.reconstructDescriptionFile(importedDescs,
					newDescription, author);
			addDescriptionRPC(newCXL);
			// Create new file at descriptions folder
			newFileDescriptionRPC();
		} else
			Window.alert(Languages.getMsgs().get("oldDesc"));
	}

	/**
	 * RPC call to add a new student. On success, add student at both myStudents
	 * list and InsertView.MyStudentList Box
	 * 
	 * @param allStudents
	 */
	public void addStudentRPC(String allStudents) {
		// Initialize the service proxy.
		if (exportSvc == null) {
			exportSvc = GWT.create(ExportService.class);
		}

		// Set up the callback object.
		AsyncCallback<ExportService> callback = new AsyncCallback<ExportService>() {
			public void onFailure(Throwable caught) {
				Window.alert("Could not add new student. Caused by: "
						+ caught.getMessage());
			}

			public void onSuccess(ExportService result) {
				refreshMyStudentLists();
				Window.alert(Languages.getMsgs().get("newStudent"));
			}
		};
		// Set the current dir
		String dir = "";
		String descFile = "students";
		String occasion = "addStudent";

		exportSvc.saveFile(allStudents, dir, descFile, occasion, callback);
	}

	/**
	 * Add new student to myStudents list and myStudentList @InsertView
	 */
	public void refreshMyStudentLists() {
		String name = student.getName();
		InsertView.myStudentList.addItem(name);
		myStudents.add(name);
	}

	/**
	 * RPC to add a new students.cxl file, without removed student. On success,
	 * remove student from myStudentListBox @InsertView
	 * 
	 * @param allStudents
	 */
	public void removeStudentRPC(String allStudents) {
		// Initialize the service proxy.
		if (exportSvc == null) {
			exportSvc = GWT.create(ExportService.class);
		}

		// Set up the callback object.
		AsyncCallback<ExportService> callback = new AsyncCallback<ExportService>() {
			public void onFailure(Throwable caught) {
				Window.alert("Could not add new student. Caused by: "
						+ caught.getMessage());
			}

			public void onSuccess(ExportService result) {
				// remove myStudentListBox
				int index = InsertView.myStudentList.getSelectedIndex();
				InsertView.myStudentList.removeItem(index);
				Window.alert(Languages.getMsgs().get("delStudent"));
			}
		};
		// Set the current dir
		String dir = "";
		String descFile = "students";
		String occasion = "addStudent";

		exportSvc.saveFile(allStudents, dir, descFile, occasion, callback);
	}

	public void addDescriptionRPC(String newCXL) {
		// Initialize the service proxy.
		if (exportSvc == null) {
			exportSvc = GWT.create(ExportService.class);
		}

		// Set up the callback object.
		AsyncCallback<ExportService> callback = new AsyncCallback<ExportService>() {
			public void onFailure(Throwable caught) {
				Window.alert("Could not add this description. Caused by: "
						+ caught.getMessage());
			}

			public void onSuccess(ExportService result) {
				Window.alert(Languages.getMsgs().get("newDesc"));
			}
		};
		// Set the current dir
		String dir = "";
		String descFile = "descriptions";
		String occasion = "addDesc";

		exportSvc.saveFile(newCXL, dir, descFile, occasion, callback);
	}

	/**
	 * Construct a new file with name: teacher-descrption.cxl in both tei and
	 * anatolia folders. This file contains only the xml header.
	 */
	public void newFileDescriptionRPC() {
		String author = getUsername();
		String title = author + "-" + getDescription();
		String header = DiagramExportation.createXmlHeader("");
		// Initialize the service proxy.
		if (exportSvc == null) {
			exportSvc = GWT.create(ExportService.class);
		}

		// Set up the callback object.
		AsyncCallback<ExportService> callback = new AsyncCallback<ExportService>() {
			public void onFailure(Throwable caught) {
				Window.alert("Could not add this description. Caused by: "
						+ caught.getMessage());
			}

			public void onSuccess(ExportService result) {
				// Nothing at the moment
			}
		};
		String occasion = "newDescFile";
		exportSvc.saveFile(header, "anatolia", title, occasion, callback);
		exportSvc.saveFile(header, "tei", title, occasion, callback);
	}

	public void editStudent(String oldName, String newName, String newPass) {
		// save old name
		setName(oldName);
		String text = Student.getStudentList();
		String me = User.getUsername();
		Document doc = XMLParser.parse(text);
		Element root = doc.getDocumentElement();
		// firstly search for current teacher tag
		NodeList teacherList = root.getElementsByTagName("teacher");
		for (int i = 0; i < teacherList.getLength(); i++) {
			Element teacher = (Element) teacherList.item(i);
			String teacherName = teacher.getAttribute("name");
			// found teacher? Then search for student.
			if (teacherName.equals(me)) {
				NodeList studentList = teacher.getElementsByTagName("student");
				for (int j = 0; j < studentList.getLength(); j++) {
					Element student = (Element) studentList.item(j);
					String studentName = student.getAttribute("name");
					if (studentName.equals(oldName)) {
						student.setAttribute("name", newName);
						student.setAttribute("password", newPass);
						// teacher.appendChild(student);
					}
				}
			}
		}
		// edit Student name @ Teacher's StudentList
		for (int i = 0; i < myStudents.size(); i++) {
			String student = myStudents.get(i);
			if (student.equals(oldName))
				myStudents.set(i, newName);
		}

		// set student's new name
		student = new Student();
		student.setName(newName);

		String newText = doc.toString();
		// make rpc request and save New students.cxl
		editStudentRPC(newText);

		// If name changed, rename all file created by this user
		if (!oldName.equals(newName))
			getUserFilesRPC(oldName);
	}

	/**
	 * Rearrange ids. Start from 0 and every time increase by one.
	 */
	public void rearrangeIds() {
		int id=getNumTeachers();
		String text = Student.getStudentList();
		Document doc = XMLParser.parse(text);
		Element root = doc.getDocumentElement();
		// firstly search for current teacher tag
		NodeList teacherList = root.getElementsByTagName("teacher");
		for (int i = 0; i < teacherList.getLength(); i++) {
			Element teacher = (Element) teacherList.item(i);
			// found teacher? Then search for student.
			NodeList studentList = teacher.getElementsByTagName("student");
			for (int j = 0; j < studentList.getLength(); j++) {
				Element student = (Element) studentList.item(j);
				id++;
				student.setAttribute("id", String.valueOf(id));
			}
		}
		String newText = doc.toString();
		// make rpc request and save New students.cxl
		addStudentRPC(newText);

	}

	public void getUserFilesRPC(String user) {
		// Initialize the service proxy.
		if (importSvc == null) {
			importSvc = GWT.create(ImportService.class);
		}

		// Set up the callback object.
		AsyncCallback<String[]> callback = new AsyncCallback<String[]>() {
			public void onFailure(Throwable caught) {
				Window.alert("Cannot load maps. Caused by: "
						+ caught.getMessage());
			}

			public void onSuccess(String[] result) {
				if (result == null) {
					// this user has not created maps yet.
				} else {
					renameFiles(result);
				}
			}
		};
		// pass username and organisation to the server
		String org = User.getOrganisation();
		importSvc.fileFinder(user, org, callback);
	}

	public void renameFiles(String[] result) {
		for (String file : result) {
			String title = getOnlyTitle(file);
			renameFilesRPC(title);
		}
	}

	public void renameFilesRPC(String title) {
		// Initialize the service proxy.
		if (exportSvc == null) {
			exportSvc = GWT.create(ExportService.class);
		}

		// Set up the callback object.
		AsyncCallback<ExportService> callback = new AsyncCallback<ExportService>() {
			public void onFailure(Throwable caught) {
				Window.alert("Could rename file. Caused by: "
						+ caught.getMessage());
			}

			public void onSuccess(ExportService result) {

			}
		};
		String dir = User.getOrganisation();
		String newName = student.getName();
		String oldName = getName();
		exportSvc.renameFile(dir, oldName, newName, title, callback);
	}

	public void editStudentRPC(String allStudents) {
		// Initialize the service proxy.
		if (exportSvc == null) {
			exportSvc = GWT.create(ExportService.class);
		}

		// Set up the callback object.
		AsyncCallback<ExportService> callback = new AsyncCallback<ExportService>() {
			public void onFailure(Throwable caught) {
				Window.alert("Could not add new student. Caused by: "
						+ caught.getMessage());
			}

			public void onSuccess(ExportService result) {
				// remove myStudentListBox
				int index = InsertView.myStudentList.getSelectedIndex();
				String name = student.getName();
				InsertView.myStudentList.setItemText(index, name);
				Window.alert(Languages.getMsgs().get("editStudent"));
			}
		};
		// Set the current dir
		String dir = "";
		String descFile = "students";
		String occasion = "addStudent";

		exportSvc.saveFile(allStudents, dir, descFile, occasion, callback);
	}

	/**
	 * Set fileName as: admin-"username"-"title"
	 * 
	 */
	public void setFileName() {
		String user = User.getUsername();
		String title = User.getTitle();
		String mapName = "admin" + "-" + user + "-" + title;
		FileImportation.fileName = mapName;
	}

	/**
	 * Set FileName for a specific author. (Author in this application is a
	 * student)
	 * 
	 * @param author
	 */
	public void setFileName(String author) {
		String title = User.getTitle();
		String mapName = author + "-" + title;
		FileImportation.fileName = mapName;
	}

	public void initTree(String[] files) {

		for (String file : files) {
			TreeItem fileNode = new TreeItem();
			String fileName = removeFileType(file);
			String user = User.getUsername();
			String admin = "admin" + "-" + user + "-";
			if (fileName.startsWith(admin)) {
				String title = fileName.replace(admin, "");
			}
			fileNode.setStyleName("files");
		}
		/*
		 * In case of node selection, show the file name and import the specific
		 * diagram.
		 */
		MenuView.myStudentsTree
				.addSelectionHandler(new SelectionHandler<TreeItem>() {
					@Override
					public void onSelection(SelectionEvent event) {

						diagram = new DiagramImportation();

						TreeItem item = (TreeItem) event.getSelectedItem();
						if (item.getParentItem() != null) {
							if (item.getParentItem().getText()
									.equals(dict.get("myFiles"))) {
								String title = getOnlyTitle(item.getText());
								User.setTitle(title);
								boolean isThisFromAdmin = true;
								// diagram.diagramRPC(tokens, isThisFromAdmin);
							} else if (item.getParentItem().getText()
									.equals(dict.get("studentFiles"))) {
								String title = getOnlyTitle(item.getText());
								String[] tokens = MenuPlace.getMenuTokens();
								tokens[3] = title;
								boolean isThisFromAdmin = false;
								// diagram.diagramRPC(tokens, isThisFromAdmin);
							}
						}
					}
				});
	}

	/**
	 * RPC call to obtain all the fileNames created by user.
	 * 
	 */
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
					// initTree(result);s
					initMyMapsListBox(result);
				}
			}
		};
		// pass username and organisation to the server
		String username = "admin" + "-" + User.getUsername();
		String org = User.getOrganisation();
		importSvc.fileFinder(username, org, callback);
	}

	/**
	 * Dedicated to MenuView: Initializes the list box containing all students
	 * by current teacher.
	 */
	public void initMyStudentsForMenu() {
		for (String student : myStudents)
			MenuView.myStudentsList.addItem(student);
	}

	/**
	 * Dedicated to InsertView: Initializes the list box containing all students
	 * by current teacher.
	 */
	public void initMyStudentsForInsert() {
		for (String student : myStudents)
			InsertView.myStudentList.addItem(student);
	}

	/**
	 * Get teachers from teachers.cxl RPC to retrieve data from teachers file.
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
					setTeacherList(result);
				}
			}
		};
		String occasion = "users";
		importSvc.getFileText(dir, fileName, occasion, callback);
	}

	/**
	 * Get all tags of teachers.cxl and search "teacher" tag. Find the specific
	 * user and return his password. Don't forget to set the organisation!
	 * 
	 */
	public String[] getUserData(String user) {
		// array containing password and organisation
		String[] data = new String[2];
		String text = getTeacherList();
		Document doc = XMLParser.parse(text);
		Element root = doc.getDocumentElement();
		NodeList nodeList = root.getElementsByTagName("teacher");
		String pass = null;
		int i;
		for (i = 0; i < nodeList.getLength(); i++) {
			Element element = (Element) nodeList.item(i);
			String storedUser = element.getAttribute("name");
			String password = element.getAttribute("password");
			String org = element.getAttribute("organisation");
			if (user.equals(storedUser)) {
				// set the organisation
				data[0] = password;
				data[1] = org;
			}
		}
		setNumTeachers(i);
		System.out.println(i +" Teachers stored.");
		return data;
	}

	public void setUsersFile() {
		this.fileName = "teachers";
	}

	/**
	 * Check if student is listed in students.cxl in teacher's node. If not,
	 * save student now.
	 */
	public void addStudent() {
		String importedDescs = Description.getText();
		String newDescription = getDescription();
		String author = getUsername();

		Description desc = new Description();

		boolean exist = desc.isDescriptionExists(importedDescs, newDescription,
				author);
		if (!exist) {
			String newCXL = desc.reconstructDescriptionFile(importedDescs,
					newDescription, author);
			addDescriptionRPC(newCXL);
			// Create new file at descriptions folder
			newFileDescriptionRPC();
		} else
			Window.alert(Languages.getMsgs().get("oldDesc"));
	}

	/**
	 * Validate user. Set group as "teacher"
	 * 
	 */
	public boolean validateUser(String user, String password) {
		String[] data = new String[2];
		data = getUserData(user);
		String storedPassword = data[0];
		String org = data[1];
		if (password.equals(storedPassword)) {
			User.setOrganisation(org);
			User.setGroup("teacher");
			return true;
		} else 
			return false;
	}

}
