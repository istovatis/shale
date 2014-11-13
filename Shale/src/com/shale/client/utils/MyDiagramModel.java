package com.shale.client.utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.user.client.ui.Widget;
import com.orange.links.client.connection.Connection;
import com.orange.links.client.connection.StraightConnection;
import com.orange.links.client.save.DecorationModel;
import com.orange.links.client.save.FunctionModel;
import com.orange.links.client.utils.WidgetUtils;
import com.orange.links.client.save.IsDiagramSerializable;
import com.shale.client.clustering.SystemClustering;
import com.shale.client.clustering.UserClustering;
import com.shale.client.conceptmap.MainView;
import com.shale.client.element.LinkingPhraseModel;
import com.shale.client.element.MapElement;
import com.shale.client.importer.DiagramImportation;

/**
 * This class follows the singleton pattern, as only one instance of
 * MyDiagramModel is neccesary
 * 
 * @author Asterios Bakavos
 * 
 */
public class MyDiagramModel implements IsSerializable {

	// Diagram Properties
	private int width;
	private int height;
	private boolean hasGrid;

	// Functions
	public static Set<FunctionModel> functionRepresentationSet;
	private Map<Widget, String> functionWidgetMap;

	// Links
	private Set<LinkingPhraseModel> linkRepresentationSet;
	
	public MyDiagramModel() {
		functionRepresentationSet = new HashSet<FunctionModel>();
		linkRepresentationSet = new HashSet<LinkingPhraseModel>();
		functionWidgetMap = new HashMap<Widget, String>();
		width = 2200;
		height = 1600;
	}

	/**
	 * The singleton instance of the class.
	 */
	private static MyDiagramModel instance;

	public static MyDiagramModel get() {
		if (instance == null) {
			instance = new MyDiagramModel();
		}
		return instance;
	}

	public void setDiagramProperties(int width, int height, boolean hasGrid) {
		this.width = width;
		this.height = height;
		this.hasGrid = hasGrid;
	}

	public void addFunction(MapElement functionWidget) {

		FunctionModel function = new FunctionModel();

		String id = String.valueOf(functionWidget.getId());
		function.id = id; // .getElement().getId();//++id + "";
		function.top = WidgetUtils.getTop(functionWidget);
		function.left = WidgetUtils.getLeft(functionWidget);

		try {
			function.content = ((IsDiagramSerializable) functionWidget)
					.getContentRepresentation();
			function.identifier = ((IsDiagramSerializable) functionWidget)
					.getType();
		} catch (ClassCastException e) {
			throw new IllegalArgumentException(
					"Widgets must implement the interface Savable to be saved");
		}
		functionRepresentationSet.add(function);
		functionWidgetMap.put(functionWidget, function.id);
	}

	public Widget getFunctionById(String id) {
		for (Widget w : functionWidgetMap.keySet()) {
			if (functionWidgetMap.get(w).equals(id))
				return w;
		}
		return null;
	}
	
	public int getNumberOfFunctions(){
		return (functionRepresentationSet.size()-linkRepresentationSet.size());
	}

	public int getNumberOfStartingLinks(Widget widget) {
		String id = functionWidgetMap.get(widget);
		int numberOfStartingLinks = 0;
		for (LinkingPhraseModel link : linkRepresentationSet) {
			if (link.startId.equals(id))
				numberOfStartingLinks++;
		}
		return numberOfStartingLinks;
	}

	public void removeConceptWithConnections(Widget widget) {
		String id = functionWidgetMap.get(widget);
		if (getNumberOfIncomingLinks(widget) == 0
				&& getNumberOfStartingLinks(widget) == 0) {
			MainView.diagramController.deleteWidget(widget);
		}
		for (LinkingPhraseModel link : linkRepresentationSet) {
			if (link.startId.equals(id) || link.endId.equals(id)) {
				MainView.diagramController.deleteWidget(widget);

			}
		}
	}

	public int getNumberOfIncomingLinks(Widget widget) {
		String id = functionWidgetMap.get(widget);
		int numberOfIncomingLinks = 0;
		for (LinkingPhraseModel link : linkRepresentationSet) {
			if (link.endId.equals(id))
				numberOfIncomingLinks++;
		}
		return numberOfIncomingLinks;
	}

	public void addFunction(FunctionModel functionRepresentation) {
		functionRepresentationSet.add(functionRepresentation);
	}

	public void addLink(Widget startWidget, Widget endWidget,
			int[][] pointList, Connection c) {
		LinkingPhraseModel link = new LinkingPhraseModel();
		int id = ++DiagramImportation.maxId;
		link.setId(String.valueOf(id)); // ++id + "";
		// System.out.println(functionWidgetMap.get(startWidget)+"--------");
		int intLeft = (c.getDecoration().getWidget().getAbsoluteLeft()) - 147;
		int intTop = (c.getDecoration().getWidget().getAbsoluteTop()) - 11;
		link.setLeft(String.valueOf(intLeft));
		link.setTop(String.valueOf(intTop));

		link.pointList = pointList;
		link.startId = functionWidgetMap.get(startWidget);
		link.endId = functionWidgetMap.get(endWidget);
		if (c.getDecoration() != null) {
			Widget w = c.getDecoration().getWidget();
			DecorationModel decoration = new DecorationModel();
			try {
				decoration.content = ((IsDiagramSerializable) w)
						.getContentRepresentation();
				decoration.identifier = ((IsDiagramSerializable) w).getType();
			} catch (ClassCastException e) {
				throw new IllegalArgumentException(
						"Decoration must implement the interface Savable");
			}
			link.decoration = decoration;
		}
		if (c instanceof StraightConnection) {
			link.type = "straight";
		} else {
			link.type = "straightarrow";
		}
		linkRepresentationSet.add(link);
	}

	public void addLink(LinkingPhraseModel linkRepresentation) {
		linkRepresentationSet.add(linkRepresentation);
	}

	public int getWidth() {	return width; }
	public void setWidth(int width) { this.width = width; }
	
	public int getHeight() { return height; }
	public void setHeight(int height) { this.height = height; }

	public boolean isHasGrid() {
		return hasGrid;
	}

	public void setHasGrid(boolean hasGrid) {
		this.hasGrid = hasGrid;
	}

	public Set<FunctionModel> getFunctionRepresentationSet() {
		return functionRepresentationSet;
	}

	public Set<LinkingPhraseModel> getLinkRepresentationSet() {
		return linkRepresentationSet;
	}
	
	/**
	 * Save the pairs of concepts that are connected in the graph.
	 */
	public void setConnectedConcepts() {
		for (LinkingPhraseModel w : linkRepresentationSet) {
			Integer[] pair = new Integer[2];
			pair[0] = new Integer(w.startId);
			pair[1] = new Integer(w.endId);

			SystemClustering.addConnectedConcepts(pair);
			UserClustering.addConnectedConcepts(pair);
		}
	}
	
	public void clear(){
		functionRepresentationSet = new HashSet<FunctionModel>();
		linkRepresentationSet = new HashSet<LinkingPhraseModel>();
		functionWidgetMap = new HashMap<Widget, String>();
	}
}
