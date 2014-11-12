package com.shale.client.conceptmap;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.allen_sauer.gwt.dnd.client.drop.SimpleDropController;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.cellview.client.CellTable;

import java.util.ArrayList;
import java.util.HashMap;

import com.shale.client.clustering.BetweenClusters;
import com.shale.client.clustering.Clustering;
import com.shale.client.clustering.MapSimilarity;
import com.shale.client.clustering.SystemClustering;
import com.shale.client.clustering.UserClustering;
import com.shale.client.clustering.UserClustering.UserClusterInfo;
import com.shale.client.element.Concept;
import com.shale.client.element.LinkingPhrase;
import com.shale.client.element.MapElement;
import com.shale.client.exporter.DiagramExportation;
import com.shale.client.importer.DiagramImportation;
import com.shale.client.importer.FileImportation;
import com.shale.client.savedmap.Map;
import com.shale.client.savedmap.Metadata;
import com.shale.client.user.Student;
import com.shale.client.user.Teacher;
import com.shale.client.user.User;
import com.shale.client.utils.Languages;
import com.shale.client.utils.Mouse;
import com.shale.client.utils.MyDiagramController;
import com.shale.client.utils.MyDiagramModel;

/**
 * The main view
 * 
 * @author istovatis
 * 
 */
public class MainView extends Composite {

	MainActivity mainActivity;
	User user;
	DiagramExportation exportDiagram;
	String[] tokens = MainPlace.getMainTokens();
	private static MainViewUiBinder uiBinder = GWT.create(MainViewUiBinder.class);
	@UiField public HTMLPanel mainPanel;
	@UiField public VerticalPanel verticalPanel;
	@UiField public FocusPanel focusPanel;
	@UiField public HorizontalPanel horizontalPanel;
	@UiField public static VerticalPanel insertItem;
	@UiField public static Label typeLabel;
	@UiField public static TextArea itemName;
	@UiField public static Button cancel;
	@UiField public static Button ok;
	@UiField public HorizontalPanel cancelOk;
	@UiField public MenuBar menuBar;
	@UiField public MenuItem newConcept;
	@UiField public static Image exportIcon;
	@UiField public static Button importBtn;
	@UiField public static Image newItemIcon;
	@UiField public static Tree fileTree;
	@UiField public static Tree descriptionTree;
	@UiField public VerticalPanel insertMap;
	@UiField public static Label titleLabel;
	@UiField public TextArea titleText;
	@UiField public static Label descLabel;
	@UiField public TextArea descText;
	@UiField public static Button cancelNewMap;
	@UiField public static Button okNewMap;
	@UiField public static Label creator;
	@UiField public static Button help;
	@UiField public static Button decrease;
	@UiField Button increaseUsrClustBtn;
	// set celltables for user and system clustering naming
	@UiField(provided = true)
	public static CellTable<Object> cellTable = new CellTable<Object>();
	@UiField(provided = true)
	public static CellTable<UserClusterInfo> userCellTable = new CellTable<UserClusterInfo>();


	static @UiField
	Label SimilarityLabel;		//clusterSimilarity label
	@UiField
	HorizontalPanel clusterPanel;
	@UiField Image similarityIcon;
	@UiField Button userModularityBtn;
	@UiField public static Label UserQLabel;
	@UiField public static Label QLabel;
	@UiField public static VerticalPanel nameClustersPanel;
	@UiField public static Image closeClustersIcon;
	@UiField public static VerticalPanel userInfoPanel;
	@UiField public static Label orgLabel;
	@UiField public static Label FQLabel;
	@UiField public static Label usernameLabel;

	public static MyDiagramController diagramController;
	public static PickupDragController dragController;
	private DiagramImportation diagram;
	DropController dropController;

	private final int mapWidth = 2200;
	private final int mapHeight = 1600;
	private static int left;
	private static int top;
	private Student student;

	Dictionary dict;
	Dictionary winDict;

	// the number of clusters
	private static int graphCounter = 1;

	SystemClustering cluster =  SystemClustering.get();
	UserClustering userCluster = UserClustering.get();

	interface MainViewUiBinder extends UiBinder<Widget, MainView> {
	}

	public MainView() {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public MainView(String content) {

		initWidget(uiBinder.createAndBindUi(this));
		diagramController = new MyDiagramController(mapWidth, mapHeight);
		diagramController.showGrid(true);
		focusPanel.add(diagramController.getView());

		// Create a DragController for each logical area where a set of
		// draggable
		// widgets and drop targets will be allowed to interact with one
		// another.
		dragController = new PickupDragController(diagramController.getView(),
				true);

		// Positioner is always constrained to the boundary panel
		// Use 'true' to also constrain the draggable or drag proxy to the
		// boundary panel
		dragController.setBehaviorConstrainedToBoundaryPanel(true);

		// Epitrepei tin epilogi pollaplwn widgets me xrisi CTRL-click
		dragController.setBehaviorMultipleSelection(false);
		diagramController.registerDragController(dragController);
		dropController = new SimpleDropController(focusPanel);
		// Register to DropController me ton DragController
		dragController.registerDropController(dropController);
		dragController.setBehaviorDragStartSensitivity(1);

		diagramController.addWidget(insertItem, 100, 100);
		addNameClusterPanel();
		addUserInfoPanel();
		
		// Get current language and set all the UI widgets
		if (Languages.isLoaded()) {
			winDict = Languages.getMsgs();
			Languages.setMainviewButtons(Languages.getDictionary());
			Languages.setMainViewLabels(Languages.getDictionary());
			// Languages.setEnglish();
		} else {
			System.out.println("Go Login!");
			// mainActivity.goTo(new LoginPlace());
			// Languages.setEnglish();
		}
		// Store the file name of the concept map
		FileImportation.updateFileName();
		resetGraphCounter();
		MyDiagramController.idToWidgetMap = new HashMap<String, Widget>();
		MyDiagramController.addedConcepts = 0;
		cluster.clearAll();
		cluster.initCellTable();
		userCluster.clearAll();
		userCluster.initCellTable();
		loadDiagram();
		// refresh celltables whenever a new map is asked
		cellTable.setRowCount(0, true);
		userCellTable.setRowCount(0, true);
		// String title = tokens[1];
		// if (!title.equals("Untitled")) {
		// creator = new Label(User.getTitle() + " Created by " +
		// Map.getAuthor());
		// // The line above commented to make program leighter
		// //diagram.diagramRPC(tokens[0], tokens[1], tokens[2]);
		// }
		// else{
		// creator = new Label("New Map Created by " + Map.getAuthor());
		// }
		DiagramImportation.maxId = 0;
		MainView.attachCreator();
	}

	/**
	 * Clears the diagram and imports a new one with value:name.title.cxl For
	 * students, if map is loaded for the first time, import the description
	 * from teacher's file. If this is a new map, do not import anything.
	 * 
	 */
	public void loadDiagram() {
		// add insertItem and clusters panel
		diagramController.addWidget(insertItem, 100, 100);
		addNameClusterPanel();
		addUserInfoPanel();
		// clear diagramController
		diagramController.clearDiagram();
		// Load Diagram that was selected @MenuView
		diagram = new DiagramImportation();

		if (!User.getTitle().equals("Untitled")) {
			creator = new Label(User.getTitle() + " Created by "
					+ Map.getAuthor());
			diagram.diagramRPC();
		} else if (User.getTitle().equals("Untitled")) {
			creator = new Label("New Map Created by " + Map.getAuthor());
		}
		// init description -only for students-
		if (User.getGroup().equals("student")) {
			student = new Student();
			student.initMyDescription();
		}
	}

	/**
	 * Show or hide the cluster icon
	 * 
	 * @param e
	 */
	@UiHandler("userClusterIcon")
	void onUserClusterIconClick(ClickEvent e) {
		if (!increaseUsrClustBtn.isVisible()) {
			increaseUsrClustBtn.setVisible(true);
			userModularityBtn.setVisible(true);
		}
		if (!userCellTable.isVisible()) {
			closeClustersIcon.setVisible(true);
			userCellTable.setVisible(true);
		}
	}

	@UiHandler("increaseUsrClustBtn")
	void onIncreaseUsrClustBtnClick(ClickEvent e) {
		userCluster.nameClusters();
		showUserCluster();
	}
	
	@UiHandler("userModularityBtn")
	void onUserModularityBtnClick(ClickEvent e) {
		userCluster.ComputeModularity(userCluster.getAllConceptIds());
	}

	/**
	 * Show or hide the cluster icon
	 * 
	 * @param e
	 */
	@UiHandler("clusterIcon")
	void onClusterIconClick(ClickEvent e) {
		if (!cellTable.isVisible()) {
			cellTable.setVisible(true);
			closeClustersIcon.setVisible(true);
		}
		if (help.isVisible()) {
			// help.setVisible(false);
			decrease.setEnabled(false);
		} else {
			help.setVisible(true);
			decrease.setVisible(true);
		}

	}
	
	/**
	 * Close name clusters window
	 * @param e
	 */
	@UiHandler("closeClustersIcon")
	public void onCloseClustersIconClick(ClickEvent e){
		cellTable.setVisible(false);
		userCellTable.setVisible(false);
		closeClustersIcon.setVisible(false);
	}

	/**
	 * Increase the number of clusters (substructures) of the map
	 * 
	 * @param e
	 */
	@UiHandler("help")
	public void onHelpclick(ClickEvent e) {
		cluster.setIncrease(true);
		getClusterGraph();	
		////cluster.resetBetweenClusters();
		decrease.setEnabled(true);
		showCluster();
	}

	/**
	 * Decrease the number of clusters (substructures) of the map
	 * 
	 * @param e
	 */
	@UiHandler("decrease")
	public void onDecreaseclick(ClickEvent e) {
		cluster.setIncrease(false);
		getClusterGraph();
		
		// set the between clusters edge counter to 0
		////cluster.resetBetweenClusters();
		if (getGraphCounter() == 1) {
			decrease.setEnabled(false);
			cellTable.setRowCount(1, true);
		}
		showCluster();
	}

	/**
	 * Get graph clustering using girvan-newton algorithm. If increase is true,
	 * then ask for increased number of clusters. When false ask for fewer
	 * clusters.
	 * 
	 * @param e
	 */
	public void getClusterGraph() {
		// you must save the file first.
		// saveFile();
		// In case of a new map, save it first and then cluster.
		if (User.getGroup().equals("teacher")
				&& User.getTitle().equals("Untitled")) {
			if (!insertMap.isVisible()) {
				insertMap.setVisible(true);
				diagramController.addWidget(insertMap, 300, 200);
			}
		} else {
			// if map has changed (user added or deleted concept or linking phrase),
			// save first and then cluster.
			if (Clustering.isChanged) {
				saveFile();
			}else{
				cluster.getClustersRPC();
			}
		}
	}

	@UiHandler("newItemIcon")
	public void onNewItemIconClick(ClickEvent e) {
		insertItem.setVisible(true);
		diagramController.addWidget(insertItem, 100, 100);
		typeLabel.setText(winDict.get("windowNewCon"));
		typeLabel.setTitle("concept");
		itemName.setFocus(true);
	}

	// elegxos tou typeLabel kai eisagwgi eite linking phrase eite concept
	@UiHandler("ok")
	public void onOkClick(ClickEvent e) {
		String name = itemName.getText();
		// eisagwgi concept
		if (typeLabel.getTitle().equals("concept") && !name.isEmpty()) {
			Concept concept = new Concept(name);
			diagramController.addWidget(concept, left, top);
			dragController.makeDraggable(concept);
			MyDiagramModel.get().addFunction(concept);
			itemName.setText("");
			removeInsertItemPanel();
			Clustering.isChanged = true;
			Widget w = concept.asWidget();
			String strId = String.valueOf(concept.getId());
			MyDiagramController.idToWidgetMap.put(strId, w);
			MyDiagramController.addedConcepts++;
		} else if (name.isEmpty()) {
			Window.alert(winDict.get("noName"));
			removeInsertItemPanel();
		}

		// Linking Phrase insertion
		// If end widget is not linking phrase, then draw a connection else show
		// a warning message
		if (typeLabel.getTitle().equals("linkingPhrase") && !name.isEmpty()) {
			try {
				LinkingPhrase linkingPhrase = new LinkingPhrase(name);
				linkingPhrase.drawLinkingPhrase(name);
				dragController.makeDraggable(linkingPhrase);
				itemName.setText("");
				removeInsertItemPanel();
				Clustering.isChanged = true;
			} catch (NullPointerException ex) {
				Window.alert("Linking Phrases are not selectable at the moment.");
			}
		}
		// Rename Map Element
		if (typeLabel.getTitle().equals("rename") && !name.isEmpty()) {
			itemName.setText("");
			MapElement renamed = new MapElement();
			renamed.setContent(name);
			renamed.rename(MapElement.getSelectedPosition(), name);
			removeInsertItemPanel();
		}
		// Save File
		if (typeLabel.getTitle().equals("save") && !name.isEmpty()) {
			User.setTitle(name);
			saveFile();
			Metadata.get().saveRpc();
		}
		// Set Description (For admins only) -Not used in this version!-
		if (typeLabel.getTitle().equals("description") && !name.isEmpty()) {
			// set description
			User.setDescription(name);
			diagramController.addWidget(insertItem, 300, 200);
			typeLabel.setText(winDict.get("windowSave"));
			typeLabel.setTitle("save");
		}
		// Set Description (For admins only) //Depricated
		/*
		 * if (typeLabel.getTitle().equals("description") && !name.isEmpty()) {
		 * Description description = new Description();
		 * description.addDescription(name); itemName.setText("");
		 * diagramController.addWidget(insertItem, 300, 200);
		 * typeLabel.setText(winDict.get("windowSave"));
		 * typeLabel.setTitle("save"); }
		 */
	}

	@UiHandler("cancel")
	public void onCancelClick(ClickEvent e) {
		itemName.setText("");
		removeInsertItemPanel();
		MapElement element = new MapElement();
		element.unSelectEverything();
	}

	@UiHandler("okNewMap")
	public void onOkNewMapClick(ClickEvent e) {
		String title = titleText.getText();
		String desc = descText.getText();
		if (!(title.isEmpty()) && !(desc.isEmpty())) {
			User.setTitle(title);
			User.setDescription(desc);
			FileImportation.fileName4NewMap();
			// delete insertMap Panel from diagram
			diagramController.deleteWidget(insertMap);
			// Save map
			saveFile();
			Metadata.get().saveRpc();
		}
	}

	@UiHandler("cancelNewMap")
	public void onCancelNewMapClick(ClickEvent e) {
		insertMap.setVisible(false);
		// remove from diagram
		removeInsertItemPanel();
	}

	// When Double click on diagram, insert a concept
	@UiHandler("focusPanel")
	public void onDoubleClick(DoubleClickEvent e) {
		// apenergopoiisi tou doubleclick ean einai to panel insertItem anoixto
		if ((!insertItem.isVisible()) && (!insertMap.isVisible())) {
			Mouse mousePoint = new Mouse();
			left = mousePoint.correctLeft();
			top = mousePoint.correctTop();
			diagramController.addWidget(insertItem, left, top);
			insertItem.setVisible(true);
			itemName.setFocus(true);
			typeLabel.setText(winDict.get("windowNewCon"));
			typeLabel.setTitle("concept");
			dragController.makeDraggable(insertItem);
		}
	}

	// Save Map
	/**
	 * If teacher chose to add a new map, then the title is "Untitled", so show
	 * the dialog box to add a new name to the map.
	 * 
	 * @param e
	 */
	@UiHandler("exportIcon")
	public void onExportIconClick(ClickEvent e) {
		try {
			if (!insertMap.isVisible()) {
				insertMap.setVisible(true);
				// if teacher pressed new map then show save window
				if (User.getGroup().equals("teacher")
						&& User.getTitle().equals("Untitled")) {
					diagramController.addWidget(insertMap, 300, 200);
				} else {
					saveFile();
					Metadata.get().saveRpc();
				}
			}
		} catch (IllegalArgumentException exception) {
			Window.alert(exception.getMessage());
		}
	}

	// Import Diagram
	@UiHandler("importBtn")
	public void onImportBtnClick(ClickEvent e) {
		if (!fileTree.isVisible()) {
			fileTree.clear();
			String group = User.getGroup();
			User.setAllData();
			if (group.equals("teacher")) {
				Teacher teacher = new Teacher();
				teacher.showMyMaps();
			} else if (group.equals("student")) {
				Student student = new Student();
				student.showMyMaps();
			}
		}
	}

	public void setPresenter(MainActivity pr) {
		mainActivity = pr;
	}

	public static void addInsertItemPanel(int left, int top) {
		diagramController.addWidget(insertItem, left, top);
		String submit = Languages.getDictionary().get("submit");
		String can = Languages.getDictionary().get("cancel");
		ok.setText(submit);
		cancel.setText(can);
		insertItem.setVisible(true);
	}

	public void saveFile() {
		// remove the label showing the author and the imported map.
		dettachLabels();
		// make insertMap invisible
		insertMap.setVisible(false);
		// Set title as tokens[3]
		String export = diagramController.exportDiagram();
		itemName.setText("");
		exportDiagram = new DiagramExportation();
		exportDiagram.diagramRPC(export);
		// add creator label to Map diagram
		attachCreator();
		addNameClusterPanel();
		addUserInfoPanel();
	}

	public static int getGraphCounter() {
		return graphCounter;
	}

	public static void setGraphCounter(int graphCounter) {
		MainView.graphCounter = graphCounter;
	}

	public void resetGraphCounter() {
		graphCounter = 1;
	}

	/**
	 * Attach insertItem and creator labels if they are not attached
	 */
	public static void attachLabels() {
		if (!insertItem.isAttached()) {
			diagramController.addWidget(insertItem, 100, 100);
			dragController.makeDraggable(insertItem);
		}
		if (!creator.isAttached()) {
			// add a label showing the map creator and author.
			diagramController.addWidget(creator, 600, 5);
		}
		if(!nameClustersPanel.isAttached()){
			addNameClusterPanel();
		}
		if(!userInfoPanel.isAttached()){
			addUserInfoPanel();
		}
	}

	public static void attachCreator() {
		if (!creator.isAttached()) {
			// add a label showing the map creator and author.
			diagramController.addWidget(creator, 600, 5);
		}
	}

	/**
	 * Dettach insertItem and creator labels if they are attached
	 */
	public static void dettachLabels() {
		removeInsertItemPanel();
		removeClustersPanel();
		removeUserInfoPanel();
		if (creator.isAttached()) {
			diagramController.deleteWidget(creator);
		}
	}

	public static void removeInsertItemPanel() {
		if (insertItem.isAttached()) {
			diagramController.deleteWidget(insertItem);
			insertItem.setVisible(false);
		}
	}
	
	public static void removeClustersPanel() {
		if (nameClustersPanel.isAttached()) {
			diagramController.deleteWidget(nameClustersPanel);
		}
	}
	
	public static void addNameClusterPanel(){
		diagramController.addWidget(nameClustersPanel, 10, 300);
		dragController.makeDraggable(nameClustersPanel);
	}
	
	public static void addUserInfoPanel(){
		diagramController.addWidget(userInfoPanel, 10, 10);
		dragController.makeDraggable(userInfoPanel);
	}
	
	public static void removeUserInfoPanel() {
		if (userInfoPanel.isAttached()) {
			diagramController.deleteWidget(userInfoPanel);
		}
	}
	
	public void showUserCluster(){
		userCellTable.setVisible(true);
		closeClustersIcon.setVisible(true);
	}
	
	public void showCluster(){
		cellTable.setVisible(true);
		closeClustersIcon.setVisible(true);
	}
	
	@UiHandler("similarityIcon")
	public void onSimilarityIconClick(ClickEvent e) {
		ArrayList<ArrayList<Integer>> userClusters = userCluster.getAllConceptIds();
		ArrayList<ArrayList<Integer>> systemClusters  = cluster.getGraphs();
		//calculate similarity with user clustering
		MapSimilarity sim = new MapSimilarity();
		double similar = sim.findSimilarity(systemClusters, userClusters);
		System.out.println("Similarity: "+BetweenClusters.formatValue(similar));
		SimilarityLabel.setText(" Sim= "+BetweenClusters.formatValue(similar)+"");
		SimilarityLabel.setStyleName("showInfos");
	}

}