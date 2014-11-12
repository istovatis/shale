package com.shale.client.utils;

import java.util.Map;
import java.util.Set;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Position;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;
import com.orange.links.client.DiagramController;
import com.orange.links.client.connection.Connection;
import com.orange.links.client.connection.StraightArrowConnection;
import com.orange.links.client.event.TieLinkEvent;
import com.orange.links.client.menu.ContextMenu;
import com.orange.links.client.save.FunctionModel;
import com.orange.links.client.shapes.DecorationShape;
import com.orange.links.client.shapes.FunctionShape;
import com.shale.client.conceptmap.MainView;
import com.shale.client.element.Concept;
import com.shale.client.element.LinkingPhrase;
import com.shale.client.element.LinkingPhraseModel;
import com.shale.client.element.MapElement;
import com.shale.client.exporter.DiagramExportation;
import com.shale.client.exporter.DiagramSaveFactoryImpl;
import com.shale.client.importer.DiagramImportation;

public class MyDiagramController extends DiagramController {
	
	public static Map<String, Widget> idToWidgetMap;
	protected static int counter=1;
	public static int addedConcepts=0;	//number of added concepts
	
	public MyDiagramController(int canvasWidth, int canvasHeight) {
		super(canvasWidth, canvasHeight);
	}

	protected void addToMenu(MenuItem newContent) {
		canvasMenu = new ContextMenu();
		canvasMenu.addItem(newContent);
	}

	@Override
	protected void initMenu() {

		canvasMenu = new ContextMenu();
		canvasMenu.addItem(new MenuItem("Show Focus Question", new Command() {

			@Override
			public void execute() {
				Window.alert(MainView.FQLabel.getText());
			}
		}));

	}

	public String exportDiagram() {
		return DiagramExportation.exportDiagram(getMyDiagramModel());
	}

	public MyDiagramModel getMyDiagramModel() {
		MyDiagramModel diagramRepresentation = MyDiagramModel.get();
		diagramRepresentation.clear();
		// Removed line after rev 83
		// diagramRepresentation.setDiagramProperties(this.canvasWidth,
		// this.canvasHeight, this.showGrid);

		// Add function
		for (Widget startWidget : functionsMap.keySet()) {
			//System.out.println("From MyDiagramC "+startWidget.getElement().getId());
			diagramRepresentation.addFunction((MapElement) startWidget);
		}
		// Add links
		for (Widget startWidget : functionsMap.keySet()) {
			for (Widget endWidget : functionsMap.get(startWidget).keySet()) {
				Connection c = functionsMap.get(startWidget).get(endWidget);
				int[][] pointList = new int[c.getMovablePoints().size()][2];
				int i = 0;
				for (com.orange.links.client.shapes.Point p : c
						.getMovablePoints()) {
					int[] point = { p.getLeft(), p.getTop() };
					pointList[i] = point;
					i++;
				}
				diagramRepresentation.addLink(startWidget, endWidget,
						pointList, c);
				c.setStraight();
			}
		}
		return diagramRepresentation;
	}
	
	public void importDiagram(String diagramXmlExport, DiagramSaveFactoryImpl saveFactory) {
		
		MyDiagramModel diagramRepresentation = DiagramImportation
				.importDiagrams(diagramXmlExport);
		// Display the converted graphical representation
		clearDiagram();
		// Add Functions
		Set<FunctionModel> functionSet = diagramRepresentation
				.getFunctionRepresentationSet();
		//set the number of concepts
		Concept.setNumConcepts(functionSet.size());
		for (FunctionModel function : functionSet) {

			Widget w = saveFactory.getFunctionByType(function.identifier,
					function.content, function.id);
			addWidget(w, function.left, function.top);
			w.getElement().setId(function.id);
			MainView.dragController.makeDraggable(w);
			idToWidgetMap.put(function.id, w);
			addedConcepts++;
			
		}
		// Add links
		for (LinkingPhraseModel link : diagramRepresentation.getLinkRepresentationSet()) {
			Widget w1 = idToWidgetMap.get(link.startId);
			Widget w2 = idToWidgetMap.get(link.endId);
			StraightArrowConnection c;
			if (link.type != null && link.type.equals("straight")) {
				
				c =  (StraightArrowConnection) MainView.diagramController.drawStraightArrowConnection(w1, w2);
				c.setStraight();
				c.drawHighlight();
			} else {
				c = (StraightArrowConnection) drawStraightArrowConnection(w1, w2);
				c.setStraight();
			}
			if (link.decoration != null) {
				addDecoration(saveFactory.getDecorationByType(
						link.decoration.identifier, link.decoration.content), c);
				Widget w = saveFactory.getDecorationByType("linking phrase", link.getId());
			
				idToWidgetMap.put(link.getId(), w);
			}

			// Fire TieEvent
			handlerManager.fireEvent(new TieLinkEvent(w1, w2, c));
		}
	}
	
	/**
	 * Modified inDragBuildArrow, to provide linking phrase generation
	 * @see com.orange.links.client.DiagramController#onMouseMove(com.google.gwt.event.dom.client.MouseMoveEvent)
	 */
	
	protected void onMouseMove(MouseMoveEvent event) {
		if(!isAllowingUserInteractions()){
			return;
		}
		
		int mouseX = event.getRelativeX(topCanvas.getElement());
		int mouseY = event.getRelativeY(topCanvas.getElement());
		mousePoint.setLeft(mouseX);
		mousePoint.setTop(mouseY);

		int offsetMouseX = event.getClientX();
		int offsetMouseY = event.getClientY();
		mouseOffsetPoint.setLeft(offsetMouseX);
		mouseOffsetPoint.setTop(offsetMouseY);
	}

	protected void onMouseUp(MouseUpEvent event) {
		if(!isAllowingUserInteractions()){
			return;
		}
		
		// Test if Right Click
		if (event.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
			event.stopPropagation();
			event.preventDefault();
			showContextualMenu();
			return;
		}

		if (inDragMovablePoint) {
			movablePoint.setFixed(true);
			topCanvas.setBackground();
			inDragMovablePoint = false;
			highlightConnection.setAllowSynchronized(true);
			return;
		}

		if (inDragBuildArrow) {
			FunctionShape functionUnderMouse = getShapeUnderMouse();
			if (functionUnderMouse != null) {
				Widget widgetSelected = functionUnderMouse.asWidget();
				if(startFunctionWidget != widgetSelected && (!startFunctionWidget.getStyleName().equals("linkingPhrase"))){
					Connection c = drawStraightArrowConnection(startFunctionWidget, widgetSelected);
					MapElement linkingPhrase = new LinkingPhrase("new "+counter);
					MainView.diagramController.addDecoration(linkingPhrase, c);
					MainView.diagramController.addWidgetAtMousePoint(linkingPhrase);
					fireEvent(new TieLinkEvent(startFunctionWidget, widgetSelected, c));
				}
			}
			topCanvas.setBackground();
			deleteConnection(buildConnection);
			inDragBuildArrow = false;
			buildConnection = null;
			if(highlightFunctionShape != null){
				highlightFunctionShape.draw();
				highlightFunctionShape = null;
			}
			clearAnimationsOnCanvas();
		}

		if (inEditionDragMovablePoint) {
			inEditionDragMovablePoint = false;
			clearAnimationsOnCanvas();
		}
	}
	
	/**
	 * Add a widget as a decoration on a connection
	 * 
	 * @param decoration
	 *            widget that will be in the middle of the connection
	 * @param decoratedConnection
	 *            the connection where the decoration will be put
	 */
	public void addDecoration(Widget decoration, Connection decoratedConnection) {
		decoration.getElement().getStyle().setZIndex(10);
		decoration.getElement().getStyle().setPosition(Position.ABSOLUTE);
		widgetPanel.add(decoration);	
		MainView.diagramController.addWidget(decoration, 100, 100);
		decoratedConnection.setDecoration(new DecorationShape(this, decoration));
		decoratedConnection.setSynchronized(false);
	}
	
	public Widget getWidgetById(String id){
		return idToWidgetMap.get(id);
	}
	
	public int getMapSize(){
		return idToWidgetMap.size();
	}
	
	/**
	 * Override this method so as to store all movable points to an arraylist.
	 */
	@Override
	protected void onMouseDown(MouseDownEvent event) {
		if(!isAllowingUserInteractions()){
			return;
		}
		
		// Test if Right Click
		if (event.getNativeButton() == NativeEvent.BUTTON_RIGHT) {
			return;
		}

		if (inEditionSelectableShapeToDrawConnection) {
			inDragBuildArrow = true;
			inEditionSelectableShapeToDrawConnection = false;
			drawBuildArrow(startFunctionWidget, mousePoint);
			return;
		}

		if (inEditionDragMovablePoint) {
			inDragMovablePoint = true;
			inEditionDragMovablePoint = false;
			movablePoint = highlightConnection.addMovablePoint(highlightPoint);
			System.out.println(highlightConnection.getMovablePoints());
//			List<Point> points = highlightConnection.getMovablePoints();
//			for(Point p : points){
//				System.out.println(p);
//			}
//			System.out.println("------------");
			allMovablePoints();
			highlightConnection.setSynchronized(false);
			highlightConnection.setAllowSynchronized(false);
			movablePoint.setTrackPoint(mousePoint);
			// Set canvas foreground to avoid dragging over a widget
			topCanvas.setForeground();
			return;
		}
	}
	
	public void allMovablePoints(){
		for(Connection con : connections){
			System.out.println(con.getMovablePoints());
		}
	}
	
	public int getAddedWidgets(){
		return idToWidgetMap.size();
	}
}