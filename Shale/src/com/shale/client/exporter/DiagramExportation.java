package com.shale.client.exporter;

import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.regexp.shared.RegExp;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Text;
import com.google.gwt.xml.client.XMLParser;
import com.orange.links.client.save.DiagramSerializationService;
import com.orange.links.client.save.FunctionModel;
import com.shale.client.clustering.Clustering;
import com.shale.client.clustering.SystemClustering;
import com.shale.client.element.LinkingPhraseModel;
import com.shale.client.importer.FileImportation;
import com.shale.client.savedmap.Metadata;
import com.shale.client.user.User;
import com.shale.client.utils.Languages;
import com.shale.client.utils.MyDiagramModel;

/**
 * Export the concept map by representing the created Concept Map into an XML
 * formatted document. This document is then stored to server.
 * 
 * @author Istovatis -- istovatis@gmail.com --
 *
 */
public class DiagramExportation extends DiagramSerializationService {

	String diagram;

	private ExportServiceAsync exportSvc = GWT.create(ExportService.class);

	public static String exportDiagram(MyDiagramModel diagramRepresentation) {

		Document doc = XMLParser.createDocument();

		// Diagram Properties
		Element cmap = doc.createElement("cmap");
		Element diagramRoot = doc.createElement("map");
		diagramRoot
				.setAttribute("width", diagramRepresentation.getWidth() + "");
		diagramRoot.setAttribute("heigth", diagramRepresentation.getHeight()
				+ "");
		doc.appendChild(cmap);

		Element conceptList = doc.createElement("concept-list");
		Element linkingPhrase = doc.createElement("linking-phrase-list");
		Element connectionList = doc.createElement("connection-list");
		Element conceptAppearance = doc
				.createElement("concept-appearance-list");
		Element linkingPhraseAppearance = doc
				.createElement("linking-phrase-appearance-list");
		Element resMeta = userData(doc);

		cmap.appendChild(resMeta);
		cmap.appendChild(diagramRoot);
		diagramRoot.appendChild(conceptList);
		diagramRoot.appendChild(linkingPhrase);
		diagramRoot.appendChild(connectionList);
		diagramRoot.appendChild(conceptAppearance);
		diagramRoot.appendChild(linkingPhraseAppearance);

		// Concepts computation
		Set<FunctionModel> functionSet = diagramRepresentation
				.getFunctionRepresentationSet();
		for (FunctionModel function : functionSet) {
			if (function.identifier.equals("concept")) {
				Element functionElement = doc.createElement("concept");
				functionElement.setAttribute("id", function.id);
				System.out.println("@ExportD " + function.id + " "
						+ function.content);
				functionElement.setAttribute("label", function.content);
				conceptList.appendChild(functionElement);
			}
		}

		// Linking phrase computation
		Set<LinkingPhraseModel> linkSet = diagramRepresentation
				.getLinkRepresentationSet();
		for (LinkingPhraseModel link : linkSet) {
			Element linkElement = doc.createElement("linking-phrase");
			linkElement.setAttribute("label", link.decoration.content + "");
			linkElement.setAttribute("id", link.getId());
			linkingPhrase.appendChild(linkElement);
		}

		// connection list first part (from start to link)computation
		for (LinkingPhraseModel link : linkSet) {
			Element linkElement = doc.createElement("connection");
			linkElement
					.setAttribute("id", link.hashCode() + "-" + link.startId);
			linkElement.setAttribute("from-id", link.startId);
			linkElement.setAttribute("to-id", link.getId());
			connectionList.appendChild(linkElement);
		}

		// connection list second part (from link to end)computation
		for (LinkingPhraseModel link : linkSet) {
			Element linkElement = doc.createElement("connection");
			linkElement.setAttribute("id", link.hashCode() + "-" + link.endId);
			linkElement.setAttribute("from-id", link.getId());
			linkElement.setAttribute("to-id", link.endId);
			connectionList.appendChild(linkElement);
		}

		// concept-appearance-list
		for (FunctionModel function : functionSet) {
			Element functionElement = doc.createElement("concept-appearance");
			if (function.identifier.equals("concept")) {
				functionElement.setAttribute("id", function.id);
				functionElement.setAttribute("x", function.left + "");
				functionElement.setAttribute("y", function.top + "");
				conceptAppearance.appendChild(functionElement);
			}
		}

		/*
		 * If diagramController.addWidget(LinkingPhrase) called, then a linking
		 * phrase functionElement is created. All thease elements hava a
		 * "linking phrase" identifier. Export them with the following command
		 * block.
		 */
		/*
		 * for (FunctionModel function : functionSet) { Element functionElement
		 * = doc .createElement("linking-phrase-appearance"); if
		 * (function.identifier.equals("linking phrase")) {
		 * functionElement.setAttribute("id", function.id);
		 * functionElement.setAttribute("x", function.left + "");
		 * functionElement.setAttribute("y", function.top + "");
		 * linkingPhraseAppearance.appendChild(functionElement); } }
		 */

		for (LinkingPhraseModel link : linkSet) {
			Element linkElement = doc
					.createElement("linking-phrase-appearance");
			linkElement.setAttribute("id", link.getId());
			linkElement.setAttribute("x", link.getLeft() + "");
			linkElement.setAttribute("y", link.getTop() + "");
			for (int[] p : link.pointList) {
				linkElement.setAttribute("x", p[0] + "");
				linkElement.setAttribute("y", p[0] + "");
			}
			linkingPhraseAppearance.appendChild(linkElement);
		}

		String out = doc.toString();
		out = format(out);
		out = createXmlHeader(out);
		return out;
	}

	public static String createXmlHeader(String xml) {
		return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n\n" + xml;
	}

	public static String format(String xml) {
		String output = "";
		RegExp r = RegExp.compile("(<[^<]*>|<[^<]*/>|</[^<]*>)", "g");
		output = r.replace(xml, "$1\n");
		return output;
	}

	/**
	 * 
	 * @param Document
	 * @return nodes with inserted user data
	 * 
	 * 
	 */
	private static Element userData(Document doc) {
		Element root = doc.createElement("res-meta");
		Element creator = doc.createElement("dc:creator");
		Element mail = doc.createElement("vcard:EMAIL");
		Element description = doc.createElement("dc:description");
		Element title = doc.createElement("dc:title");
		Element org = doc.createElement("vcard:ORG");
		Element orgName = doc.createElement("vcard:Orgname");

		Text email = doc.createTextNode(User.getMail());
		Text texTitle = doc.createTextNode(User.getTitle());
		Text desc = doc.createTextNode(User.getDescription());
		Text textOrganisation = doc.createTextNode(User.getOrganisation());
		mail.appendChild(email);
		description.appendChild(desc);
		title.appendChild(texTitle);
		orgName.appendChild(textOrganisation);

		root.appendChild(creator);
		creator.appendChild(mail);
		root.appendChild(title);
		root.appendChild(org);
		org.appendChild(orgName);
		root.appendChild(description);
		return root;
	}

	/**
	 * RPC call to save file. Filename depends on the group of user. Teachers
	 * save files as admin-"user"-"title" where author is the teacher. In other
	 * cases, file is saved as user-title.cxl. Students save the file as
	 * "user"-"title"
	 * 
	 * @param diagram
	 */
	public void diagramRPC(String diagram) {
		setDiagram(diagram);
		// Initialize the service proxy.
		if (exportSvc == null) {
			exportSvc = GWT.create(ExportService.class);
		}

		// Set up the callback object.
		AsyncCallback<ExportService> callback = new AsyncCallback<ExportService>() {
			public void onFailure(Throwable caught) {
				Window.alert("Save Concept Map failed. Caused by: "
						+ caught.getMessage());
			}

			public void onSuccess(ExportService result) {
				// reload the diagram
				// MainView.diagramController.clearDiagram();
				// Load Diagram that was selected @MenuView
				// DiagramImportation diagram = new DiagramImportation();

				// diagram.diagramRPC(tokens[0], tokens[1], tokens[2]);
				// MainView.diagramController.importDiagram(getDiagram(),
				// new DiagramSaveFactoryImpl());

				// Load metadata
				Metadata.get().load();
				String saved = Languages.getMsgs().get("mapSaved");
				if (!Clustering.isChanged) { //show msg only when user wants it
					Window.alert(saved);
				}
				else{
					System.out.println("Concept map changed. Saving...");
					SystemClustering.get().getClustersRPC();
				}
				Clustering.isChanged = false;
			}
		};
		
		exportSvc.saveFile(diagram, User.getOrganisation(),
				FileImportation.fileName, "newMap", callback);
	}

	public String getDiagram() {
		return diagram;
	}

	public void setDiagram(String diagram) {
		this.diagram = diagram;
	}

}
