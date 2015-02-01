package com.shale.client.savedmap;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;
import com.shale.client.clustering.SystemClustering;
import com.shale.client.clustering.UserClustering;
import com.shale.client.clustering.UserClustering.UserClusterInfo;
import com.shale.client.conceptmap.MainView;
import com.shale.client.element.Concept;
import com.shale.client.exporter.DiagramExportation;
import com.shale.client.exporter.ExportService;
import com.shale.client.exporter.ExportServiceAsync;
import com.shale.client.importer.DiagramImportation;
import com.shale.client.importer.FileImportation;
import com.shale.client.importer.ImportService;
import com.shale.client.importer.ImportServiceAsync;
import com.shale.client.user.User;
import com.shale.client.utils.Languages;

/**
 * Saving and loading metadata of the concept
 * maps. Metadata include information about the system and the user-defined
 * clusters
 * 
 * @author Stergios Bakavos
 * 
 */
public class Metadata {
	private ExportServiceAsync exportSvc = GWT.create(ExportService.class);
	private ImportServiceAsync importSvc = GWT.create(ImportService.class);
	private String metadata;
	// Number of the system clusters
	private int sysCluster = 1;

	private int userCluster = 0;

	private Document doc;

	/**
	 * The singleton instance of the class Metadata
	 */
	private static Metadata instance;

	public static Metadata get() {
		if (instance == null) {
			instance = new Metadata();
		}
		return instance;
	}

	/**
	 * Create metadata as a .cxl file. Metadata include information about the
	 * system and user clusters.
	 */
	public void createMetadata() {

		doc = XMLParser.createDocument();

		// Diagram Properties
		Element meta = doc.createElement("meta");

		// save system clusters information
		sysCluster = MainView.getGraphCounter();
		Element systemClusters = doc.createElement("sys_clusters");
		systemClusters.setAttribute("number", sysCluster + "");
		meta.appendChild(systemClusters);
		
		for(int i=0; i <sysCluster; i++) {
			Element systemCluster = doc.createElement("sys_cluster");
		}
		
		// save user defined clusters
		userCluster = UserClustering.getNumClusters();
		Element userClusters = doc.createElement("user_clusters");
		userClusters.setAttribute("number", userCluster + "");
		meta.appendChild(userClusters);

		if (userCluster > 0) {
			List<UserClusterInfo> clusters = UserClustering.getListDataProvider()
					.getList();
			int index = 0;
			for (UserClusterInfo cluster : clusters) {
				Element userCluster = doc.createElement("cluster");
				userCluster.setAttribute("index", index + "");
				userCluster.setAttribute("id", cluster.getId() + "");
				userCluster.setAttribute("name", cluster.getName());
				ArrayList<Concept> list = cluster.concepts;
				for (Concept concept : list) {
					Element con = doc.createElement("concept");
					con.setAttribute("id", concept.getId() + "");
					con.setAttribute("name", concept.getContent());
					userCluster.appendChild(con);
				}
				userClusters.appendChild(userCluster);
				index++;
			}
		}

		doc.appendChild(meta);

		metadata = doc.toString();

		metadata = DiagramExportation.format(metadata);
		metadata = DiagramExportation.createXmlHeader(metadata);
	}

	/**
	 * RPC to save metadata of the concept map.
	 */
	public void saveRpc() {	
		// Initialize the service proxy.
		if (exportSvc == null) {
			exportSvc = GWT.create(ExportService.class);
		}

		// Set up the callback object.
		AsyncCallback<ExportService> callback = new AsyncCallback<ExportService>() {
			public void onFailure(Throwable caught) {
				Window.alert("Saving Metadata failed. Caused by: "
						+ caught.getMessage());
			}

			public void onSuccess(ExportService result) {
				String saved = Languages.getMsgs().get("mapSaved");
				System.out.println(saved);
				//Window.alert(saved);
			}
		};
		User.setAllData();
		String dir = User.getOrganisation();
		String fileName = User.getFileName();
		createMetadata();
		exportSvc.saveFile(metadata, dir, fileName, "saveMeta", callback);
	}

	public void load() {
		List<UserClusterInfo> list = UserClusterInfo.getDataProvider().getList();
		for (UserClusterInfo cluster : list) {
			int index = cluster.getId();
			ArrayList<Concept> concepts = cluster.concepts;
			for (Concept concept : concepts) {
				// System.out.println(concept.getElement().getId()+" goes to "+index);
				com.google.gwt.user.client.Element revert = (com.google.gwt.user.client.Element) concept
						.getElement();
				DOM.setStyleAttribute(revert, "border", "6px outset "
						+ Concept.pallete[index]);
//				DOM.setStyleAttribute(revert, "border", "5px solid "
//						+ Concept.pallete[index]);
			}
		}
	}

	/**
	 * Load system and user-defined clusters from a file using RPC.
	 */
	public void loadRPC() {
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
					MainView.attachLabels();
					System.out.println("No metadata received");
				} else {
					MainView.dettachLabels();
					String formated = DiagramImportation.format(result);
					Document doc = XMLParser.parse(formated);
					Element root = doc.getDocumentElement();

					NodeList clusters = root.getElementsByTagName("cluster");
					for (int i = 0; i < clusters.getLength(); i++) {
						Element cluster = (Element) clusters.item(i);
						String col = cluster.getAttribute("id");
						String name = cluster.getAttribute("name");
						int index = Integer.valueOf(cluster
								.getAttribute("index"));
						System.out.println(cluster.getAttribute("index") + " "
								+ col);
						int colour = Integer.valueOf(col);
						NodeList concepts = cluster
								.getElementsByTagName("concept");
						ArrayList<Concept> list = new ArrayList<Concept>(0);
						for (int k = 0; k < concepts.getLength(); k++) {
							Concept concept = new Concept();
							Element tmp = (Element) concepts.item(k);
							concept.setId(Integer.valueOf(tmp
									.getAttribute("id")));
							concept.setContent(tmp.getAttribute("name"));
							list.add(concept);
						}
						UserClustering.get().addLoadedCluster(colour, index,
								name, list); // Add cluster to datalist
						for (int j = 0; j < concepts.getLength(); j++) {
							Element concept = (Element) concepts.item(j);
							String id = concept.getAttribute("id");
							Widget revert = MainView.diagramController.getWidgetById(id);
									//.getMyDiagramModel().getFunctionById(id); // .getWidgetById(id);
							System.out.println("Painting" + id + " colour "
									+ colour);
//							DOM.setStyleAttribute(revert.getElement(),
//									"border", "5px solid "
//											+ Concept.pallete[colour]);
							DOM.setStyleAttribute(revert.getElement(),
									"border", "6px outset "
											+ Concept.pallete[colour]);
						}
					}
					metadata = formated;
					loadSysClusters();
					SystemClustering.get().clustersFromMeta();
					MainView.attachLabels();
				}
			}
		};

		importSvc.importMetadata(FileImportation.getFileName(),
				User.getOrganisation(), callback);
	}

	public void loadSysClusters() {
		Document doc = XMLParser.parse(metadata);
		Element root = doc.getDocumentElement();
		NodeList numClusters = root.getElementsByTagName("sys_clusters");
		Element tmp = (Element) numClusters.item(0);
		sysCluster = Integer.valueOf(tmp.getAttribute("number"));
		System.out.println(sysCluster + " stored system clusters");
	}

	public int getSysCluster() { return sysCluster; }
	public void setSysCluster(int sysCluster) { this.sysCluster = sysCluster; }
	
	/**
	 * Remove widget according to its id, from a user defined cluster that belongs
	 * @param widget
	 */
	public void removeFromCluster(int widgetId){
		int wId = widgetId; //Integer.valueOf(widget.getElement().getId());
		List<UserClusterInfo> clusters = UserClustering.getListDataProvider()
				.getList();
		
		for (UserClusterInfo cluster : clusters) {
			ArrayList<Concept> list = cluster.concepts;
			for(int i=0; i<list.size(); i++) {
				Concept concept = list.get(i);
				if(concept.getId() == wId){
					list.remove(i);
				}
			}
		}
	}

}
