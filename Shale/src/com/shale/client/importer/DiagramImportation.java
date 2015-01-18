package com.shale.client.importer;

import java.util.HashSet;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;
import com.orange.links.client.save.DecorationModel;
import com.orange.links.client.save.DiagramSerializationService;
import com.orange.links.client.save.FunctionModel;
import com.shale.client.clustering.SystemClustering;
import com.shale.client.conceptmap.MainView;
import com.shale.client.element.LinkingPhraseModel;
import com.shale.client.exporter.DiagramSaveFactoryImpl;
import com.shale.client.savedmap.Map;
import com.shale.client.savedmap.Metadata;
import com.shale.client.user.User;
import com.shale.client.utils.MyDiagramModel;

/**
 * Retrieves an XML representation of the map and builds a diagram
 * representation based on this map.
 * 
 * @author Istovatis -- istovatis@gmail.com --
 *
 */
public class DiagramImportation extends DiagramSerializationService {
	
	public static int maxId = 0;

	private ImportServiceAsync importSvc = GWT.create(ImportService.class);

	/*
	 * Parses xml file and passes a DiagramModel to DiagramController
	 */
	public static MyDiagramModel importDiagrams(String xml) {
		
		//register hashset of concepts added at diagram
		SystemClustering.widgets = new HashSet<String>();
		
		String formated = format(xml);
		Document doc = XMLParser.parse(formated);
		Element root = doc.getDocumentElement();

		// retrive description and set it
		String desc = retrieveDesc(doc);
		User.setDescription(desc);

		// DiagramRepresentation
		MyDiagramModel.get().clear();
		MyDiagramModel diagramRepresentation = MyDiagramModel.get(); //new MyDiagramModel();

		NodeList nodeList = root.getElementsByTagName("concept");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element functionElement = (Element) nodeList.item(i);
			FunctionModel function = new FunctionModel();
			function.id = functionElement.getAttribute("id");
			function.identifier = "concept";
			function.content = functionElement.getAttribute("label");

			NodeList appearanceList = root
					.getElementsByTagName("concept-appearance");
			Element element = (Element) appearanceList.item(i);
			String left = element.getAttribute("x");
			String top = element.getAttribute("y");
			function.left = Integer.parseInt(left);
			function.top = Integer.parseInt(top);
			
			System.out.println("Loading "+function.id+function.content);
			
			diagramRepresentation.addFunction(function);
			Integer id = Integer.valueOf(function.id);
			if(id > maxId){
				maxId = id ;
			}
		}

		nodeList = root.getElementsByTagName("linking-phrase");
		for (int i = 0; i < nodeList.getLength(); i++) {
			Element linkElement = (Element) nodeList.item(i);
			LinkingPhraseModel link = new LinkingPhraseModel();
			link.setId(linkElement.getAttribute("id"));
			String content = linkElement.getAttribute("label");
			// Get decoration if exists

			// Element decoration = (Element)
			// linkElement.getElementsByTagName("decoration").item(0);
			DecorationModel decorationRepresentation = new DecorationModel();
			decorationRepresentation.content = content;
			decorationRepresentation.identifier = "linking phrase";
			link.decoration = decorationRepresentation;

			NodeList connectionNodeList = root
					.getElementsByTagName("connection");
			for (int j = 0; j < connectionNodeList.getLength(); j++) {
				Element element = (Element) connectionNodeList.item(j);
				if (element.getAttribute("from-id").equals(link.getId())) {
					link.endId = element.getAttribute("to-id");
				} else if (element.getAttribute("to-id").equals(link.getId())) {
					link.startId = element.getAttribute("from-id");
				}
				Integer id = Integer.valueOf(link.getId());
				if(id > maxId){
					maxId = id ;
				}
			}

			/*
			 * NodeList appearanceList = root
			 * .getElementsByTagName("linking-phrase-appearance");
			 */
			NodeList pointNodeList = root
					.getElementsByTagName("linking-phrase-appearance");

			Element pointElement = (Element) pointNodeList.item(i);
			int[][] pointList = new int[pointNodeList.getLength()][2];
			for (int j = 0; j < pointNodeList.getLength(); j++) {
				int x = Integer.decode(pointElement.getAttribute("x"));
				int y = Integer.decode(pointElement.getAttribute("y"));
				int[] point = { x, y };
				pointList[j] = point;
			}
			link.pointList = pointList;
			link.type = "straight";

			diagramRepresentation.addLink(link);
		}

		Set<LinkingPhraseModel> linkSet = diagramRepresentation
				.getLinkRepresentationSet();
		/*
		 * for (LinkingPhraseModel el : linkSet) {
		 * System.out.println("********************"); System.out.println(el.id
		 * + el.decoration.content + " start:" + el.startId + el.endId +
		 * "coordinates: " + el.startId + el.endId); }
		 */
		
		diagramRepresentation.setConnectedConcepts();
		
		return diagramRepresentation;
	}

	public static String format(String xml) {
		xml = xml.replace(':', '-');

		return xml;
	}

	/**
	 * RPC call to retrieve file "user-title".cxl from dir org.When retrieve,
	 * call retriveDesc to get description from map.
	 * 
	 * @param user
	 * @param title
	 * @param org
	 */
	public void diagramRPC() {
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
					//add a label showing the map creator and author.
					MainView.creator.setText(Map.getAuthor()+" has not developed this concept map yet");
					MainView.diagramController.addWidget(MainView.creator, 600, 5);
				} else {
					MainView.diagramController.importDiagram(result,
							new DiagramSaveFactoryImpl());
					Metadata.get().loadRPC();
				}
			}
		};
		importSvc.importDiagram(FileImportation.fileName, User.getOrganisation(), callback);
	}

	/**
	 * Read text and retrieve description from dc:description node.
	 */
	public static String retrieveDesc(Document doc) {
		Element root = doc.getDocumentElement();
		NodeList dcDescripion = root.getElementsByTagName("dc-description");
		String description = null;
		for (int i = 0; i < dcDescripion.getLength(); i++) {
			Node desc = (Node) dcDescripion.item(i);
			int len = desc.getFirstChild().getNodeValue().length();
			if (len > 0) {
				description = desc.getFirstChild().getNodeValue();
			}
			MainView.FQLabel.setText(description);
		}
		return description;
	}
	
	public static int getMaxId(){
		return maxId;
	}
	
	public static void setMaxId(int max){
		maxId = max;
	}
	
}
