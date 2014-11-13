package com.shale.client.savedmap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;
import com.shale.client.exporter.DiagramExportation;
import com.shale.client.importer.DiagramImportation;
import com.shale.client.importer.FileImportation;
import com.shale.client.importer.ImportService;
import com.shale.client.importer.ImportServiceAsync;
import com.shale.client.user.Teacher;
import com.shale.client.user.User;

public class Description extends FileImportation {

	private String name;
	private String author;

	Map<String, String> descs = new HashMap<String, String>(0);
	private static Set<String> authors = new HashSet<String>();
	private ImportServiceAsync importSvc = GWT.create(ImportService.class);

	public void setFileName() {
		this.fileName = "descriptions";
	}

	public Description() {}

	public String getName() { return name; }
	public void setName(String name) { this.name = name; }

	public String getAuthor() { return author; }
	public void setAuthor(String author) { this.author = author; }

	/**
	 * Check if description exists in text and return true or false
	 * 
	 * @param text
	 * @param description
	 * @return
	 */
	public boolean isDescriptionExists(String text, String description,
			String author) {
		boolean check = false;
		Document doc = XMLParser.parse(text);
		Element root = doc.getDocumentElement();

		NodeList nodeList = root.getElementsByTagName("description");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element descriptionElement = (Element) nodeList.item(i);
			String desc = descriptionElement.getAttribute("name");
			String auth = descriptionElement.getAttribute("author");
			String org = descriptionElement.getAttribute("organisation");

			if (desc.equals(description) && auth.equals(author)) {
				check = true;
			}
		}
		return check;
	}

	public String[] getAuthorDescriptions(String author) {
		String text = getText();
		Document doc = XMLParser.parse(text);
		Element root = doc.getDocumentElement();
		NodeList nodeList = root.getElementsByTagName("description");
		String[] descriptions = new String[nodeList.getLength()];
		int j = 0;
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element descriptionElement = (Element) nodeList.item(i);
			String desc = descriptionElement.getAttribute("name");
			String teacher = descriptionElement.getAttribute("author");
			if (teacher.equals(author)) {
				descriptions[j] = desc;
				System.out.println(desc + " teacher:" + teacher);
				j++;
			}
		}
		return descriptions;
	}

	public void setAllDescriptions(String text) {
		Document doc = XMLParser.parse(text);
		Element root = doc.getDocumentElement();
		NodeList nodeList = root.getElementsByTagName("description");
		String[][] descriptions = new String[nodeList.getLength()][nodeList
				.getLength()];
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element descriptionElement = (Element) nodeList.item(i);
			String desc = descriptionElement.getAttribute("name");
			String author = descriptionElement.getAttribute("author");
			descs.put(desc, author);
		}
	}

	public void addDescription(String name) {
		Teacher addDesc = new Teacher(name);
		addDesc.newDescription();
	}

	/**
	 * Adds a new descrption "newDesc" by teacher "author" at description-list
	 * file.
	 * 
	 * @param text
	 * @param newDesc
	 * @param author
	 * @return
	 */
	public String reconstructDescriptionFile(String text, String newDesc,
			String author) {
		setAllDescriptions(text);
		Document doc = XMLParser.createDocument();

		Element descList = doc.createElement("description-list");
		doc.appendChild(descList);

		// Add the previously inserted descriptions
		// Get a set of the entries
		Set set = descs.entrySet();
		// Get an iterator
		Iterator i = set.iterator();
		int counter = 0;
		// Display elements
		while (i.hasNext()) {
			counter++;
			Map.Entry description = (Map.Entry) i.next();
			System.out.print(description.getKey() + ": ");
			System.out.println(description.getValue());
			Element desc = doc.createElement("description");
			desc.setAttribute("id", counter + "");
			desc.setAttribute("name", description.getKey().toString());
			desc.setAttribute("author", description.getValue().toString());
			descList.appendChild(desc);
		}

		// Add the new description
		counter++;
		Element desc = doc.createElement("description");
		desc.setAttribute("id", counter + "");
		desc.setAttribute("name", newDesc);
		desc.setAttribute("author", author);
		descList.appendChild(desc);

		String out = doc.toString();

		out = DiagramExportation.createXmlHeader(out);
		return out;
	}

	public void setAllAuthors() {
		String text = getText();
		Document doc = XMLParser.parse(text);
		Element root = doc.getDocumentElement();
		NodeList nodeList = root.getElementsByTagName("description");

		for (int i = 0; i < nodeList.getLength(); i++) {
			Element descriptionElement = (Element) nodeList.item(i);
			String teacher = descriptionElement.getAttribute("author");
			authors.add(teacher);
		}
	}

	public Object[] getAllAuthors() {
		Object[] auth = authors.toArray();
		return auth;
	}

	public String getMapDescription() {
		return null;
	}

	/**
	 * RPC to retrieve data from description file.
	 */
	public void importFile(String fileName, String directory) {
		setFileName();
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
					// No descriptions added
				} else {
					setText(result);
				}
			}
		};
		String occasion = "description_file";
		importSvc.getFileText(directory, fileName, occasion, callback);
	}

	/**
	 * Load descriptions.cxl file at current directory
	 * 
	 */
	public void loadDescriptionsFile() {
		setFileName();
		// Locate the current directory
		setCurrentDirectory();
		String fileName = getFileName();
		String dir = getDirectory();
		importFile(fileName, dir);
	}
	
	
	/**
	 * Import text from user-title.cxl file and parse it to retrieve description
	 * @param user
	 * @param title
	 * @param org
	 */
	public void importDescRPC(String user, String title, String org) {
		// Initialize the service proxy.
		if (importSvc == null) {
			importSvc = GWT.create(ImportService.class);
		}

		// Set up the callback object.
		AsyncCallback<String> callback = new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {
				Window.alert("Load Concept Map failed. Caused by: "
						+ caught.getMessage());
			}

			public void onSuccess(String result) {
				if (result == null) {
					// No diagram retrieved
				} else {
					setDescription(result);
				}
			}

		};

		importSvc.importDiagram(user, title, org, callback);
	}
	
	/**
	 * Format result, parse it and retrieve description. Then set description.
	 * @param result
	 */
	public void setDescription(String result){
		String text = DiagramImportation.format(result);
		Document doc = XMLParser.parse(text);
		String desc = DiagramImportation.retrieveDesc(doc);
		User.setDescription(desc);
	}
}
