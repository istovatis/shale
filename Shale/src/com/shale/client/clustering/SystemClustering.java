package com.shale.client.clustering;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import com.google.gwt.cell.client.AbstractEditableCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.RowStyles;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.shale.client.conceptmap.MainView;
import com.shale.client.element.Concept;
import com.shale.client.importer.FileImportation;
import com.shale.client.importer.ImportService;
import com.shale.client.importer.ImportServiceAsync;
import com.shale.client.savedmap.Metadata;
import com.shale.client.user.User;
import com.shale.client.utils.MyDiagramController;
import com.shale.client.utils.MyDiagramModel;

/**
 * This class communicates with the server in order to partition diagram into
 * clusters. To communicate with the server it uses RPC. The clustered 
 * 
 * It performs also concept clustering based on Girvan-Newman method. 
 * It can also colour concepts based on the clustering results. 
 * Clustering can also be performed in the metadata retrieved from file parsing.
 * 
 * 
 * @author istovatis
 * 
 */
public class SystemClustering {
	int metaClusters; // clusters from meta file
	private ImportServiceAsync importSvc = GWT.create(ImportService.class);
	private boolean restart;
	private boolean increase;
	private int prevGraphs; // number of previous clusters
	private int currentGraphs; // number of current clusters;
	private final int numClusters = Concept.pallete.length;
	private List<AbstractEditableCell<?, ?>> editableCells;
	private ArrayList<ArrayList<Integer>> graphs;
	// the full result from the server - added by van
	private ArrayList<ArrayList<Integer>> fullResult; 
	// the color of a cluster that will be reused at
	// some point so needs to be remembered - added by van
	private int colourRemoved; 
	private ArrayList<ArrayList<Integer>> colouredGraphs;
	// The background color for each cluster (1 to 1 correspondence with
	// colourdedGraphs - added by van
	private ArrayList<Integer> graphsColour;
	// Pair of concepts that are connected. 
	private static ArrayList<Integer[]> connectedConcepts;
	
	ArrayList<ArrayList<Integer>> cpReturnGraphs;
	MyDiagramModel model;

	/**
	 * Information about a cluster.
	 */
	public static class SysClusterInfo implements Comparable<SysClusterInfo> {
		@Override
		public int compareTo(SysClusterInfo arg0) {
			return 0;
		}
	}

	/**
	 * The singleton instance of the class.
	 */
	private static SystemClustering instance;

	public static SystemClustering get() {
		if (instance == null) {
			instance = new SystemClustering();
		}
		return instance;
	}

	public SystemClustering() {
		metaClusters = 1;
		currentGraphs = 1;
		MainView.setGraphCounter(1);
	}

	public int getCurrentGraphs() { return currentGraphs; }
	
	public static ArrayList<Integer[]> getConnectedConcepts() { return connectedConcepts; }

	public static void addConnectedConcepts(Integer[] pair) {
		SystemClustering.connectedConcepts.add(pair);
	}

	public void setCurrentGraphs(int currentGraphs) {
		this.currentGraphs = currentGraphs;
	}

	public int getPrevGraphs() { return prevGraphs; }
	public void setPrevGraphs(int prevClusters) { this.prevGraphs = prevClusters; }

	public ArrayList<ArrayList<Integer>> getGraphs() { return graphs; }

	public boolean isIncrease() { return increase; }
	public void setIncrease(boolean increase) { this.increase = increase; }

	public static HashSet<String> widgets;

	/**
	 * Send the map name details to server. The server opens the map, creates
	 * the graph and creates clusters depending on the the value of
	 * getGraphCounter. Then receive an arraylist of arraylists. Every nested
	 * arraylist holds the vertices of the specific graph. Group all vertices of
	 * the same graph and then set styleName in order to create a group of the
	 * same colour. 
	 * 
	 * Receiving also the clusters, System Clustering calls computeModularityIndex() method
	 * that computes the modularity index of the whole graph. 
	 * 
	 * @param user
	 * @param title
	 * @param org
	 */
	public void getClustersRPC() {

		// Initialize the service proxy.
		if (importSvc == null) {
			importSvc = GWT.create(ImportService.class);
		}

		// Set up the callback object.
		AsyncCallback<ArrayList<ArrayList<Integer>>> callback = new AsyncCallback<ArrayList<ArrayList<Integer>>>() {
			public void onFailure(Throwable caught) {
				Window.alert("Load Concept Map failed. Caused by: "
						+ caught.getMessage());
			}

			public void onSuccess(ArrayList<ArrayList<Integer>> result) {
				Concept.aloneConcepts = 0;
				fullResult = new ArrayList<ArrayList<Integer>>(result);

				int graph = MainView.getGraphCounter();
				if (isIncrease() && ++graph <= Concept.getNumConcepts())
					MainView.setGraphCounter(graph);
				else if (graph > 1) {
					MainView.setGraphCounter(--graph);
				}
				if (result.size() > 0) {
					cpReturnGraphs = new ArrayList<ArrayList<Integer>>(result);

					BetweenClusters modIndex = new BetweenClusters(result);
					modIndex.setLabel("system");
					modIndex.setConnectedConcepts(connectedConcepts);
					if (result.size() > 1) {

						int connClusterConcepts = 0;
						modIndex.clusterAllConnections();
						int nClusters = modIndex.graphSize();
						// clusteredConnected = new int[nClusters];
						for (Integer cluster : modIndex.getZeroClusters()) {
							Concept.aloneConcepts++;
							result.remove(cluster);
							cpReturnGraphs.remove(cluster);
						}

						setCurrentGraphs(result.size());
						// give user the oportunity to name the clusters
						nameClusters();
						System.out.println("-----" + result.size() + " -----");

						// Modularity index computation
						modIndex.computeModularityIndex();
					}
					// In case of one graph return, set all return elements to
					// #CCFF99
					// (it is the default concept colour) background colour.
					if (result.size() == 1) {
						ArrayList<Integer> newList = new ArrayList<Integer>(
								result.get(0));
						graphs.add(newList);
						graphsColour.clear(); // added by van
						graphsColour.add(0); 
						colouredGraphs.clear(); 
						colouredGraphs.add(result.get(0));

						for (Integer widget : result.get(0)) {
							DOM.setStyleAttribute(MainView.diagramController
									.getWidgetById(widget + "").getElement(),
									"backgroundColor", "#CCFF99");
						}
						setCurrentGraphs(1);
						// give user the oportunity to name the clusters
						nameClusters();
						modIndex.setQLabelText(0);
					}
					// If you return more than one graphs, check the graph
					// status
					// with the previous one. If you find a graph that existed
					// in
					// the previous status, remove it from the results
					// arraylist.
					else {
						for (int j = 0; j < graphs.size(); j++) 
							for (int i = 0; i < result.size(); i++)
								if (result.get(i).equals(graphs.get(j))) {
									for (Integer in : result.get(i))
										System.out.print("," + in);
									System.out.println();

									result.remove(i);
									--i;
								}

						// Find the graph with the most elements. This is the
						// new graph. Change the background colour of its
						// elements.
						int max = 0;
						int minGraph = 0;
						int resultSize = result.size();
						for (int i = 0; i < resultSize; i++) 
							if (result.get(i).size() > max) {
								max = result.get(i).size();
								minGraph = i;
							}

						System.out.println("Thelw na vapsw " + resultSize);
						if (resultSize > 0) {
							int colour = 0;
							if (isIncrease()) {
								colour = (MainView.getGraphCounter() % numClusters) - 1;

								System.out.println("Colouring " + colour
										+ " numClusters=" + numClusters
										+ " getGraphCounter="
										+ MainView.getGraphCounter()); 
								updateGraphsAndColors_increase(result, minGraph, colour); 

							} else {
								if (MainView.getGraphCounter() > 1)
									// code by Van to modify previous line
									colour = (MainView.getGraphCounter() % numClusters) - 1;
								else
									colour = (MainView.getGraphCounter() % numClusters) - 1;
								System.out.println("-Colouring " + colour);

							}

							if (!isIncrease()) { // code by van starts here
								updateGraphsAndColors_decrease();
								colour = colourRemoved % numClusters;
							}

							showGraphsAndColors();
							// showGraphsAndResult(result);

							// code by van ends here
							for (Integer widget : result.get(minGraph)) {
								DOM.setStyleAttribute(
										MainView.diagramController
												.getWidgetById(widget + "")
												.getElement(),
										"backgroundColor", Concept.pallete[colour]);
							}
						}

					}

					// set the previous graphs status and number
					graphs.clear();
					setPrevGraphs(cpReturnGraphs.size());
					graphs = new ArrayList<ArrayList<Integer>>(cpReturnGraphs);
				}

				System.out.println(MyDiagramController.addedConcepts
						+ " Added Concepts. " + Concept.aloneConcepts
						+ " with no conns");
				if (MainView.getGraphCounter() >= (MyDiagramController.addedConcepts - Concept.aloneConcepts))
					MainView.help.setEnabled(false);
				else
					MainView.help.setEnabled(true);
			}
		};
		System.out.println("RPC client: Thelw " + MainView.getGraphCounter()
				+ " clusters");

		importSvc.getVertices(FileImportation.getFileName(),
				User.getOrganisation(), MainView.getGraphCounter(),
				MainView.getGraphCounter(), isIncrease(), callback);
	}
	
	/**
	 * Ask for specific number of clusters.  
	 * @param numOfClusters
	 */
	public void getClustersRPC(int numOfClusters) {
		// Initialize the service proxy.
		if (importSvc == null) {
			importSvc = GWT.create(ImportService.class);
		}

		// Set up the callback object.
		AsyncCallback<ArrayList<ArrayList<Integer>>> callback = new AsyncCallback<ArrayList<ArrayList<Integer>>>() {
			public void onFailure(Throwable caught) {
				Window.alert("Load Concept Map failed. Caused by: "
						+ caught.getMessage());
			}
			public void onSuccess(ArrayList<ArrayList<Integer>> result) {
				System.out.println(result.size()+" clusters returned as a result from metadata");
				for(int i=0; i<result.size(); i++){
					ArrayList<Integer> cluster = result.get(i);
					for(int j=0; j<cluster.size(); j++){
						Integer widget = cluster.get(j);
						DOM.setStyleAttribute(MainView.diagramController
								.getWidgetById(widget + "").getElement(),
								"backgroundColor", Concept.pallete[i]);
					}
				}
				// set the previous graphs status and number
				graphs.clear();
				setCurrentGraphs(result.size());
				setPrevGraphs(0);
				nameClusters();
				setPrevGraphs(result.size());
				graphs = new ArrayList<ArrayList<Integer>>(result);
			}
		};
		importSvc.getVertices(FileImportation.getFileName(),
				User.getOrganisation(), numOfClusters-1, numOfClusters-1,
				true, callback);
	}

	/**
	 * Check if server side has restarted the clustering procedure. If so, set
	 * every concept to initial state, clear the hashset with widgets and reset
	 * the counter
	 */
	public void checkRestart() {
		// Initialize the service proxy.
		if (importSvc == null) {
			importSvc = GWT.create(ImportService.class);
		}

		// Set up the callback object.
		AsyncCallback<Boolean> callback = new AsyncCallback<Boolean>() {
			public void onFailure(Throwable caught) {
				Window.alert("Load Concept Map failed. Caused by: "
						+ caught.getMessage());
			}

			@Override
			public void onSuccess(Boolean result) {
				setRestart(result);
				if (result) {
					// get all concepts kept in hashset and set them as default
					for (String concept : widgets) {
						MainView.diagramController.getWidgetById(concept)
								.setStyleName("concept");
					}
					widgets.clear();
					MainView.setGraphCounter(0);
				}
			}
		};

		importSvc.getRestart(callback);
	}

	public boolean isRestart() { return restart; }
	public void setRestart(boolean restart) { this.restart = restart; }

	public void initCellTable() {
		int count = MainView.cellTable.getColumnCount();
		for (int i = 0; i < count; i++)
			MainView.cellTable.removeColumn(0);

		EditTextCell columnCell = new EditTextCell();

		Column<Object, String> nameColumn = new Column<Object, String>(columnCell) {
			@Override
			public String getValue(Object object) {
				return "Set Name";
			}
		};
		MainView.cellTable.addColumn(nameColumn, "Clusters");
	}

	/**
	 * Create editTextCells for the cellTable depending on the returned system
	 * clusters
	 */
	public void nameClusters() {
		int prevRows = getPrevGraphs();
		int moreGraphs = getCurrentGraphs() - prevRows;
		int lessGraphs = prevRows - getCurrentGraphs();
		EditTextCell[] nameCell = null;
		if (moreGraphs > 0) {
			//System.out.println("++++++++"+moreGraphs+"  "+getCurrentGraphs()+" current");
			MainView.cellTable.setRowCount(getCurrentGraphs(), true);
			nameCell = new EditTextCell[moreGraphs];

			for (int i = 0; i < moreGraphs; i++) {
				nameCell[i] = new EditTextCell();
				editableCells.add(nameCell[i]);
			}
		} else if (lessGraphs > 0) {
			MainView.cellTable.setRowCount(prevRows - lessGraphs, true);
			nameCell = new EditTextCell[lessGraphs];
			//System.out.println("---------"+lessGraphs+" "+getCurrentGraphs()+" current");
			for (int i = 0; i < lessGraphs; i++) {
				if (editableCells.size() > 0) {
					nameCell[i] = new EditTextCell();
					editableCells.remove(editableCells.size() - 1);
					prevRows--;
				}
			}
		}

		MainView.cellTable.setRowStyles(new RowStyles<Object>() {
			@Override
			public String getStyleNames(Object p, int rowIndex) {
				return "graph" + (rowIndex % numClusters);
			}
		});

		MainView.cellTable.setRowData(0, editableCells);
	}

	public void parseConnectedConcepts() {
		MainView.dettachLabels();
		model = MainView.diagramController.getMyDiagramModel();
		model.setConnectedConcepts();
		MainView.attachCreator();
	}

	// methods developed by van
	public void showGraphsAndResult(ArrayList<ArrayList<Integer>> result) {
		System.out.println("\n\n");
		for (int i = 0; i < graphs.size(); i++) {
			ArrayList<Integer> g = graphs.get(i);
			System.out.println("---Previous Cluster: " + i
					+ " is the following: " + g.toString() + " coloured with:"
					+ getGraphColor(g) + " " + getNodeColor(g.get(0)));

		}
		System.out.println("\n\n");
		for (int i = 0; i < result.size(); i++) {
			ArrayList<Integer> g = result.get(i);
			System.out.println("--- Current New Cluster: " + i
					+ " is the following: " + g.toString() + "colourd with: "
					+ getGraphColor(g) + " " + getNodeColor(g.get(0)));
		}

	}

	public void showGraphsAndColors() {
		System.out.println("---SHOWING GRAPHS & COLORS---");
		if (colouredGraphs.size() > graphsColour.size()) {
			System.out.println("colouredGraphs size: " + colouredGraphs.size()
					+ " " + colouredGraphs.toString());
			System.out.println("graphscolour size: " + graphsColour.size()
					+ " " + graphsColour.toString());

			for (int i = 0; i < graphsColour.size(); i++)
				System.out.println("graph:" + colouredGraphs.get(i)
						+ " color: " + graphsColour.get(i)
						+ "(from getGraphColor "
						+ getGraphColor(colouredGraphs.get(i)) + ")");
		}

		else if (colouredGraphs.size() <= graphsColour.size()) {
			System.out.println("colouredGraphs size: " + colouredGraphs.size()
					+ " " + colouredGraphs.toString());
			System.out.println("graphscolour size: " + graphsColour.size()
					+ " " + graphsColour.toString());

			for (int i = 0; i < colouredGraphs.size(); i++)
				System.out.println("graph:" + colouredGraphs.get(i)
						+ " color: " + graphsColour.get(i)
						+ "(from getGraphColor "
						+ getGraphColor(colouredGraphs.get(i)) + ")");
		}
		System.out.println("---END OF SHOWING GRAPHS & COLORS---");

	}

	public int getGraphColor(ArrayList<Integer> g) {

		for (int j = 0; j < colouredGraphs.size(); j++) {
			if (colouredGraphs.get(j).equals(g))
				return graphsColour.get(j);
		}
		return -1;
	}

	public int getNodeColor(Integer n) {
		// showGraphsAndColors();
		// System.out.println ("...searching of the color of node " + n);
		for (int j = 0; j < colouredGraphs.size(); j++) {
			ArrayList<Integer> g = colouredGraphs.get(j);
			for (int i = 0; i < g.size(); i++) {
				if (g.get(i).equals(n)) {
					// System.out.println ("color found in graph"+g.toString());
					return graphsColour.get(j);
				}
			}
		}
		return -1;
	}

	public ArrayList<Integer> findColouredGraphHavingNode(Integer n) {
		for (int j = 0; j < colouredGraphs.size(); j++) {
			ArrayList<Integer> g = colouredGraphs.get(j);
			for (int i = 0; i < g.size(); i++) {
				if (g.get(i).equals(n)) {
					return colouredGraphs.get(j);
				}
			}
		}
		return null;
	}

	public boolean removeColouredGraphHavingNode(Integer n) {
		for (int j = 0; j < colouredGraphs.size(); j++) {
			ArrayList<Integer> g = colouredGraphs.get(j);
			for (int i = 0; i < g.size(); i++) {
				if (g.get(i).equals(n)) {
					colouredGraphs.remove(g);
					colourRemoved = graphsColour.get(j);
					graphsColour.remove(j);
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Step 1. the clusters of the graphs NOT in fullResult must be removed from
	 * colouredGraphs Step 2. The cluster of the fullResult NOT in graphs must
	 * be added to colouredGraphs
	 * 
	 */
	public void updateGraphsAndColors_decrease() {
		// Implementation of Step 1.
		int minsize = 100;
		colourRemoved = 200;
		for (int i = 0; i < graphs.size(); i++) {
			ArrayList<Integer> g = graphs.get(i);
			if (!fullResult.contains(g)) {
				int index = colouredGraphs.indexOf(g);
				colouredGraphs.remove(g);
				if (index >= 0) {
					if (colourRemoved > graphsColour.get(index)) {
						colourRemoved = graphsColour.get(index);
					}
					graphsColour.remove(index);
					minsize = g.size();
				} else if (index < 0)
					System.out.println("THIS SHOULD NO HAPPEN: Cannot remove colour");
			}
		}

		// Implementation of Step 2.
		ArrayList<ArrayList<Integer>> mergedGraphs = new ArrayList<ArrayList<Integer>>(
				fullResult);
		for (int i = 0; i < mergedGraphs.size(); i++) {
			ArrayList<Integer> g = mergedGraphs.get(i);
			if (!graphs.contains(g)) {
				colouredGraphs.add(g);
				graphsColour.add(colourRemoved);

			}
		}

	}

	public void updateGraphsAndColors_increase(
			ArrayList<ArrayList<Integer>> result, int minGraph, int colour) {
		if (result.size() > 1) {
			if (result.get(0).equals(result.get(minGraph))) {
				removeColouredGraphHavingNode(result.get(minGraph).get(0));
				colouredGraphs.add(result.get(0));
				colouredGraphs.add(result.get(1));
				graphsColour.add(colour);
				graphsColour.add(colourRemoved);
			} else if (result.get(1).equals(result.get(minGraph))) {
				removeColouredGraphHavingNode(result.get(minGraph).get(0));
				colouredGraphs.add(result.get(1));
				colouredGraphs.add(result.get(0));
				graphsColour.add(colour);
				graphsColour.add(colourRemoved);
			} else {
				graphsColour.add(12); // These cases should never
				graphsColour.add(13); // happen. Added just in case...
			}
		}
	}

	/**
	 * Applying Girvan Newman algorithm and returning as many clusters as
	 * defined in metadata file.
	 */
	public void clustersFromMeta() {
		metaClusters = Metadata.get().getSysCluster();

		setIncrease(true);
		System.out.println(metaClusters + " @Clustering");
		if (metaClusters > 1) {
			System.out.println(FileImportation.getFileName()
					+ User.getOrganisation() + User.getTitle());
			getClustersRPC(metaClusters);
			MainView.decrease.setEnabled(true);
			MainView.cellTable.setVisible(true);
			MainView.closeClustersIcon.setVisible(true);
			MainView.setGraphCounter(metaClusters);
		}
		else
			System.out.println(metaClusters+" returned from metadata @clustersFromMeta. That's problem");
	}

	public void clearAll() {
		// editableCells.clear();
		// graphs.clear();
		// fullResult.clear();
		// colouredGraphs.clear();
		// graphsColour.clear();
		// connectedConcepts.clear();
		// cpReturnGraphs.clear();
		prevGraphs = 0;
		editableCells = new ArrayList<AbstractEditableCell<?, ?>>();
		graphs = new ArrayList<ArrayList<Integer>>(0);
		// the full result from the server - added by van
		fullResult = new ArrayList<ArrayList<Integer>>(0); 
		// the clusters of the concept map - added by van
		colouredGraphs = new ArrayList<ArrayList<Integer>>(0); 
		// The background color for each cluster (1 to 1 correspondence with
		// colourdedGraphs - added by van
		graphsColour = new ArrayList<Integer>(0);
		connectedConcepts = new ArrayList<Integer[]>(0);
	}
}