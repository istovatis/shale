package com.shale.client.clustering;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.cell.client.FieldUpdater;
import com.shale.client.conceptmap.MainView;
import com.shale.client.element.Concept;
import com.shale.client.utils.MyDiagramController;
import com.shale.client.utils.MyDiagramModel;

/**
 * This class enables user to set his own clusters of the concept map. The total
 * number of user defined clusters must not be greater than the total number of
 * concepts. Every cluster has an id, a name, an index, which is the position at
 * the userCell table, and a list of concepts. This class makes use of the
 * singleton pattern, and only one instance of the UserClustering is alive.
 * 
 * This class is part of the "Guide and Reflection" Module.
 * 
 * @author Asterios Bakavos
 * 
 */
public class UserClustering {

	// The provider that holds the list of clusters
	private static ListDataProvider<UserClusterInfo> dataProvider = new ListDataProvider<UserClusterInfo>();
	private List<AbstractEditableCell<?, ?>> editableCells = new ArrayList<AbstractEditableCell<?, ?>>();
	private static final SingleSelectionModel<UserClusterInfo> selectionModel = new SingleSelectionModel<UserClusterInfo>(
			UserClustering.UserClusterInfo.KEY_PROVIDER);
	private Column<UserClusterInfo, Boolean> checkColumn;
	private Column<UserClusterInfo, Boolean> deleteColumn;
	// Pair of concepts that are connected.
	private static ArrayList<Integer[]> connectedConcepts;
	// The provider that holds the list of clusters
	// private static ListDataProvider<UserClusterInfo> dataProvider = new
	// ListDataProvider<UserClusterInfo>();

	private static List<UserClusterInfo> clusterList;

	public static int getChosen() {
		int id = -1;
		try {
			id = selectionModel.getSelectedObject().getId();
		} catch (NullPointerException e) {
			Window.alert("Please select a cluster first");
		}
		return id;
	}

	public UserClustering() {
		clusterList = dataProvider.getList();
		clusterList.add(createClusterInfo());
		connectedConcepts = new ArrayList<Integer[]>();
	}

	public static void addConnectedConcepts(Integer[] pair) {
		UserClustering.connectedConcepts.add(pair);
	}

	/**
	 * The singleton instance of the class.
	 */
	private static UserClustering instance;

	public static UserClustering get() {
		if (instance == null) {
			instance = new UserClustering();
		}
		return instance;
	}

	/**
	 * Information about a cluster.
	 */
	public static class UserClusterInfo implements Comparable<UserClusterInfo> {
		protected String name;
		protected int id;
		private static int nextId = 0;
		public ArrayList<Concept> concepts;
		private int index;

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getName() {
			return this.name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public UserClusterInfo() {
			this.name = "Set name";
			this.concepts = new ArrayList<Concept>(0);
			this.index = MainView.userCellTable.getRowCount();
			this.id = nextId;
			nextId++;
		}

		public UserClusterInfo(int id, int index, String name,
				ArrayList<Concept> concepts) {
			this.concepts = concepts;
			this.index = index;
			this.id = id;
			this.name = name;
			nextId++;
		}

		public static ListDataProvider<UserClusterInfo> getDataProvider() {
			return dataProvider;
		}

		/**
		 * The key provider that provides the unique ID of a contact.
		 */
		public static final ProvidesKey<UserClusterInfo> KEY_PROVIDER = new ProvidesKey<UserClusterInfo>() {
			@Override
			public Object getKey(UserClusterInfo item) {
				return item == null ? null : item.getId();
			}
		};

		@Override
		public int compareTo(UserClusterInfo o) {
			// TODO Auto-generated method stub
			return 0;
		}
	}

	private final class ColumnExtension extends Column {
		private ColumnExtension(Cell cell) {
			super(cell);
		}

		@Override
		public Object getValue(Object object) {
			return null;
		}
	}

	/**
	 * Add a display to the database. The current range of interest of the
	 * display will be populated with data.
	 * 
	 * @param display
	 *            a {@Link HasData}.
	 */
	public void addDataDisplay(HasData<UserClusterInfo> display) {
		Set<HasData<UserClusterInfo>> displays = dataProvider.getDataDisplays();
		if (displays.size() == 0) {
			dataProvider.addDataDisplay(display);
		}
	}

	/**
	 * Create editTextCells for the cellTable depending on the returned system
	 * clusters
	 */
	public void nameClusters() {
		increaseRows();
		EditTextCell nameCell = new EditTextCell();
		editableCells.add(nameCell);

		MainView.userCellTable.setRowStyles(new RowStyles<UserClusterInfo>() {
			@Override
			public String getStyleNames(UserClusterInfo p, int rowIndex) {
				return "graph" + p.getId();
			}
		});
	}

	/**
	 * Increase the number of User Clusters, only when the total concepts on the
	 * map are greater than the current clusters.
	 */
	public void increaseRows() {
		MainView.dettachLabels();
		int modelLinks = MyDiagramModel.get().getNumberOfFunctions();
		// MyDiagramModel model =
		// MainView.diagramController.getMyDiagramModel();
		// int links = model.getLinkRepresentationSet().size();
		// int all = model.getFunctionRepresentationSet().size();
		int concepts = MyDiagramController.addedConcepts;
		int rows = MainView.userCellTable.getRowCount();
		if (modelLinks == (concepts)) {
			// System.out.println("ola komple");
		} else
			System.out.println(modelLinks + "!=" + (concepts));
		if ((concepts) != MainView.diagramController.getMapSize()) {
			System.out.println(MainView.diagramController.getMapSize());
		}
		if ((concepts) > rows) {
			MainView.userCellTable.setRowCount(rows++, true);
			clusterList = dataProvider.getList();
			clusterList.add(createClusterInfo());
			System.out.println("User cluster added");
		} else {
			System.out.println("(" + rows
					+ ") Clusters can not be more than concepts (" + (concepts)
					+ ")");
		}
		// add creator label to Map diagram
		MainView.attachLabels();
		MainView.attachCreator();
	}

	private UserClusterInfo createClusterInfo() {
		UserClusterInfo contact = new UserClusterInfo();
		return contact;
	}

	private UserClusterInfo createClusterInfo(int colour, int index,
			String name, ArrayList<Concept> list) {
		UserClusterInfo contact = new UserClusterInfo(colour, index, name, list);
		return contact;
	}

	/**
	 * Initialise cell table with two columns. The first is for the naming and
	 * the second to detect the selected row.
	 */
	public void initCellTable() {
		int count = MainView.userCellTable.getColumnCount();
		for (int i = 0; i < count; i++) {
			MainView.userCellTable.removeColumn(0);
		}
		List<UserClusterInfo> addedClusters = dataProvider.getList();
		for (UserClusterInfo cluster : addedClusters) {
			clusterList.remove(cluster);
		}
		EditTextCell columnCell = new EditTextCell();

		UserClusterInfo.nextId = 0;
		MainView.userCellTable.setRowCount(0);
		clusterList.clear();
		Column<UserClusterInfo, String> nameColumn = new Column<UserClusterInfo, String>(
				columnCell) {
			@Override
			public String getValue(UserClusterInfo object) {
				String ret = null;

				try {
					ret = object.name;
					if (ret.equals(null)) {
						return "Set Name";
					}
				} catch (NullPointerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return "Set Name";
				}
				return ret;
			}
		};

		MainView.userCellTable.addColumn(nameColumn, "My Clusters");
		nameColumn.setFieldUpdater(new FieldUpdater<UserClusterInfo, String>() {
			@Override
			public void update(int index, UserClusterInfo object, String value) {
				object.name = value;
			}
		});

		// set the selection model for the cell table
		MainView.userCellTable.setSelectionModel(selectionModel,
				DefaultSelectionEventManager
						.<UserClusterInfo> createCheckboxManager());

		// Checkbox columnn
		checkColumn = new Column<UserClusterInfo, Boolean>(new CheckboxCell(
				true, false)) {
			@Override
			public Boolean getValue(UserClusterInfo object) {
				// Get the value from the selection model
				return selectionModel.isSelected(object);
			}

		};

		deleteColumn = new ColumnExtension(new ButtonCell());
		deleteColumn.setCellStyleNames("sendButton");
		deleteColumn
				.setFieldUpdater(new FieldUpdater<UserClusterInfo, Boolean>() {
					@Override
					public void update(int index, UserClusterInfo object,
							Boolean value) {
						MainView.dettachLabels();
						ArrayList<Concept> list = object.concepts;
						for (Concept concept : list) {
							System.out.println("Remove " + concept.getContent());
							// Element revert = concept.getElement();
							// DOM.setStyleAttribute(revert, "border",
							// "1px outset "
							// + "#CCC");

							String strId = String.valueOf(concept.getId());
							Widget revert2 = MainView.diagramController
									.getMyDiagramModel().getFunctionById(strId);

							DOM.setStyleAttribute(revert2.getElement(),
									"border", "1px outset " + "#CCC");
						}

						// The user clicked on the button for the passed
						// auction.
						dataProvider.getList().remove(index);
						List<UserClusterInfo> tmp = dataProvider.getList();
						for (UserClusterInfo tmpCluster : tmp) {
							int prev = tmpCluster.index;
							if (index < prev) {
								tmpCluster.index = --prev;
							}
						}
						MainView.attachCreator();
						MainView.attachLabels();
						MainView.userCellTable.redraw();

						/**
						 * Alternative way of changing border.
						 * MainView.diagramController
						 * .deleteWidget(MainView.creator);
						 * ArrayList<MapElement> list = MapElement.widgetList;
						 * for (MapElement element : list) { if
						 * (element.getClusterIndex() == object.getId()) {
						 * Concept widget = (Concept) element; Element a =
						 * widget.getElement(); DOM.setStyleAttribute(a,
						 * "border", "1px outset " + "#CCC"); } }
						 */
					}
				});

		MainView.userCellTable.addColumn(checkColumn, "Choose");
		MainView.userCellTable.addColumn(deleteColumn, "Delete");

		UserClustering.get().addDataDisplay(MainView.userCellTable);
	}

	/**
	 * Add a border to the concept. Also add the concept to the cluster
	 * 
	 * @param element
	 */
	public static void borderIt(Concept concept) {
		Element element = concept.getElement();
		int index = getChosen();
		if (index >= 0) {
			element.setTabIndex(index);
			// first remove the concept from the cluster that belongs. Then add
			// it to the selected cluster
			removeFromCluster(concept);
			addToCluster(concept);
			DOM.setStyleAttribute(element, "border", "6px outset "
					+ Concept.pallete[index]);
		}
	}

	/**
	 * Add a concept to the selected cluster
	 * 
	 * @param concept
	 */

	public static void addToCluster(Concept concept) {
		clusterList = dataProvider.getList();
		int index = 0;
		try {
			index = selectionModel.getSelectedObject().index;
		} catch (NullPointerException e) {
			System.out.println("Please choose a cluster first");
		}
		ArrayList<Concept> clusterConcepts = clusterList.get(index).concepts;
		if (!contains(concept, index)) {
			clusterConcepts.add(concept);
			System.out.println("added "+clusterConcepts.size()+" "+contains(concept, index));
		}
	}

	public static void removeFromCluster(Concept concept) {
		for (int i = 0; i < clusterList.size(); i++) {
			ArrayList<Concept> clusterConcepts = clusterList.get(i).concepts;
			if (clusterConcepts.contains(concept)) {
				clusterConcepts.remove(concept);
				System.out.println("Already existed!");
			}
		}
	}

	/**
	 * Return the concepts of the index list
	 * 
	 * @param index
	 * @return
	 */
	public ArrayList<Concept> getClusterConcepts(int index) {
		return clusterList.get(index).concepts;
	}

	/**
	 * Get all concept ids.
	 * 
	 * @return
	 */
	public ArrayList<ArrayList<Integer>> getAllConceptIds() {
		ArrayList<ArrayList<Integer>> clusters = new ArrayList<ArrayList<Integer>>();
		for (int i = 0; i < getNumClusters(); i++) {
			ArrayList<Integer> cluster = new ArrayList<Integer>();
			ArrayList<Concept> concepts = clusterList.get(i).concepts;
			for (int j = 0; j < concepts.size(); j++) {
				cluster.add(concepts.get(j).getId());
			}
			clusters.add(cluster);
		}
		System.out.println(clusters.size() + " size!");
		return clusters;
	}

	public static int getNumClusters() {
		return MainView.userCellTable.getRowCount();
	}

	public static SingleSelectionModel<UserClusterInfo> getSelectionModel() {
		return selectionModel;
	}

	public static ListDataProvider<UserClusterInfo> getListDataProvider() {
		return dataProvider;
	}

	/**
	 * Add a cluster loaded from a metadata file. Method parameters are the
	 * metadata of the loaded cluster.
	 * 
	 * @param colour
	 * @param index
	 * @param name
	 * @param list
	 */
	public void addLoadedCluster(int colour, int index, String name,
			ArrayList<Concept> list) {
		int rows = MainView.userCellTable.getRowCount();
		MainView.userCellTable.setRowCount(rows++, true);
		// MainView.userCellTable.getRowElement(rows-1).setTitle(name);
		clusterList = dataProvider.getList();
		clusterList.add(createClusterInfo(colour, index, name, list));

		MainView.userCellTable.setRowStyles(new RowStyles<UserClusterInfo>() {
			@Override
			public String getStyleNames(UserClusterInfo p, int rowIndex) {
				return "graph" + p.getId();
			}
		});
		List<UserClusterInfo> values = dataProvider.getList();
		// MainView.userCellTable.setRowData(values);
	}

	public void ComputeModularity(ArrayList<ArrayList<Integer>> graph) {
		BetweenClusters modIndex = new BetweenClusters(graph);
		modIndex.setConnectedConcepts(connectedConcepts);
		modIndex.clusterAllConnections();
		modIndex.setLabel("user");
		// Modularity index computation
		modIndex.computeModularityIndex();
	}

	public void clearAll() {
		connectedConcepts.clear();
	}
	
	public static boolean contains(Concept concept, int index){
		int id = concept.getId();
		UserClusterInfo cluster = clusterList.get(index);
		//for(UserClusterInfo cluster : clusterList){
			for(Concept cpt : cluster.concepts){
				if(cpt.getId() == id){
					return true;
				}
			}
		//}
			return false;
	}
}