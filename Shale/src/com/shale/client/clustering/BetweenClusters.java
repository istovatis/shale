package com.shale.client.clustering;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Formatter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.NumberFormat;
import com.shale.client.conceptmap.MainView;


/**
 * Examine the connections between clusters. Compute the connected concepts
 * between clusters or within the cluster.Find clusters with 0 connected
 * concepts. Modularity index implementation. Measure the clarity of cluster
 * separation, by computing the difference between: (a) the proportion of edges
 * of the map located between concepts of the same idea and (b) the proportion
 * of edges that would have existed between concepts, if the edges were placed
 * randomly.
 * 
 * More detailed theory at the paper "The modularity of learning as a prevalent
 * issue in concept map assessment"
 * 
 * @author istovais -istovatis@gmail.com-
 * 
 */
public class BetweenClusters {
	// Count the connected concepts between cluster i and cluster j
	private int[][] betweenClustersCounter;
	// pairs of connected concepts
	private ArrayList<Integer[]> connectedConcepts;
	// The graph which contains cluster
	private ArrayList<ArrayList<Integer>> graph;
	private float modularity = 0; // The modularity index value
	private int numEdgesBetClusters; // count edges between two clusters
	private int numClusters; // The number of clusters
	private int[] rowSum; // Number of connected edges. ex when
										// rowSum[3]=5,
	// cluster 3 has 5 connected edges
	private ArrayList<Integer> zeroClusters; // clusters with zero connections
	private String label;	// Label of MainView to be valued.

	public BetweenClusters(ArrayList<ArrayList<Integer>> graph) {
		connectedConcepts = new ArrayList<Integer[]>(0);
				zeroClusters = new ArrayList<Integer>(0);
		this.graph = graph;
		numClusters = graph.size();
		rowSum = new int[numClusters];
		betweenClustersCounter = new int[numClusters][numClusters];
	}

	public BetweenClusters() {
		connectedConcepts = new ArrayList<Integer[]>(0);
	}

	public void clusterAllConnections() {
		for (int i = 0; i < graph.size(); i++) {
			clusterConnections(i);
		}
	}

	/**
	 * Take the list of pair connected concepts and detect if they belong to the
	 * cluster. If variable edge=2, then both concepts of the pair belong to the
	 * given cluster. If variable edge=1, then only one concept (start or end)
	 * belongs to the given cluster. In this case, search all other clusters so
	 * as to detect where the other edge belongs.
	 * 
	 * @param cluster
	 * @return
	 */
	public int clusterConnections(int cluster) {
		System.out.println(cluster + "********");
		int contains = 0;
		int edge;
		betweenClustersCounter[cluster][cluster] = 0;
		showClusterConcepts(cluster);
		for (Integer[] concept : connectedConcepts) {
			edge = 0;
			if (graph.get(cluster).contains(concept[0])) {
				// System.out.println("Has start" + concept[0]);
				contains++; // This cluster contains one more edge
				edge++;
			}
			if (graph.get(cluster).contains(concept[1])) {
				// System.out.println("Has end" + concept[1]);
				contains++;
				edge++;
			}
			if (edge == 1) {
				for (int i = 0; i < numClusters; i++) {
					if (i != cluster) {
						if (graph.get(i).contains(concept[0])) {
							betweenClustersCounter[cluster][i]++;
							betweenClustersCounter[i][cluster]++;
						}
					}
				}
				numEdgesBetClusters++;
			}
			if (edge == 2) {
				// System.out.print("before cluster " + cluster + ": "
				// + betweenClustersCounter[cluster][cluster] + "with "
				// + concept[0] + " and " + concept[1]);
				betweenClustersCounter[cluster][cluster] += 2;
			}
		}
		if(contains==0){
			zeroClusters.add(cluster);
		}
		rowSum[cluster] = contains;
		showConncectedPairs();
		return contains;
	}

	public void removeZeroClusters() {
		for (Integer cluster : zeroClusters) {
			graph.remove(cluster);
			numClusters--;
		}
	}

	/**
	 * Show the concepts contained in the specific cluster
	 * 
	 * @param cluster
	 */
	public void showClusterConcepts(int cluster) {
		for (Integer in : graph.get(cluster)) {
			System.out.print(" " + in);
		}
		System.out.println();
	}

	/**
	 * Show connected pairs between all clusters. (including the conncected
	 * pairs inside the cluster)
	 * 
	 * @param nClusters
	 */
	public void showConncectedPairs() {
		for (int i = 0; i < numClusters; i++) {
			for (int j = 0; j < numClusters; j++) {
				System.out.println(i + "with" + j + " has"
						+ betweenClustersCounter[i][j] + " connected");
			}
			System.out.println();
		}
		System.out
				.println("Edge between clusters = " + numEdgesBetClusters / 2);
	}

	/**
	 * Computation of modularity index value. Add the connected edges within the
	 * clusters divided by the number of edges and subtract the power of the
	 * connected edges between two clusters divided by the number of the edges
	 * 
	 * @param numClusters
	 * @param rowSum
	 */
	public void computeModularityIndex() {
		modularity = 0;
		int endings = connectedConcepts.size() * 2;
		System.out.println("Endings: " + endings + " Clusters: " + numClusters);
		for (int i = 0; i < numClusters; i++) {
			modularity += (float) (betweenClustersCounter[i][i]) / endings;
			float sum = rowSum[i];
			float div = (float) sum / endings;
			double power = Math.pow(div, 2.0);
			System.out.println((float) (betweenClustersCounter[i][i]) / endings
					+ " - " + power+" "+rowSum[i]+" "+betweenClustersCounter[i][i]);
			modularity -= (float) power;
		}
		// for (int i = 0; i < numClusters; i++) {
		// for (int j = 0; j < numClusters; j++) {
		// if (i == j) {
		// modularity += (double) (betweenClustersCounter[i][j]) / endings;
		// System.out.println((betweenClustersCounter[i][j]) / endings
		// + ".. i,j=" + betweenClustersCounter[i][j] * 2
		// + " endings= " + endings);
		// } else {
		// double sum = rowSum[i];
		// double div = (double) sum / endings;
		// double power = Math.pow(div, 2.0);
		// modularity -= (double) power;
		// }
		// }
		// }
		setQLabelText();
	}

	/**
	 * Number of connected edges in the given cluster.
	 * 
	 * @param cluster
	 */
	public int getNumConnectedEdges(int cluster) {
		return rowSum[cluster];
	}

	public void setQLabelText() {
		String value = formatValue(modularity);
		if(label.equals("user")){
			MainView.UserQLabel.setText("Q= " + value);
		}
		else if (label.equals("system")){
			MainView.QLabel.setText("Q= " + value);
		}
		System.out.println("Modularity index (Q) =" + value);
	}

	public void setQLabelText(float modularity) {
		String value = formatValue(modularity);
		if(label.equals("user")){
			MainView.UserQLabel.setText("Q= " + value);
		}
		else if (label.equals("system")){
			MainView.QLabel.setText("Q= " + value);
		}
		System.out.println("Modularity index (Q) =" + value);
	}

	public void setConnectedConcepts(ArrayList<Integer[]> connectedConcepts) {
		this.connectedConcepts = connectedConcepts;
	}

	public int graphSize() {
		return graph.size();
	}

	public ArrayList<Integer> getZeroClusters() {
		return zeroClusters;
	}
	
	public void setLabel(String label){
		this.label = label;
	}
	
	/**
	 * Return a  3 digit precision of the given value
	 * @param value
	 * @return
	 */
	public static String formatValue(float value){
		NumberFormat fmt = NumberFormat.getDecimalFormat();
		return fmt.format(value);
	}
	
	/**
	 * Return a  3 digit precision of the given value
	 * @param value
	 * @return
	 */
	public static String formatValue(double value){
		NumberFormat fmt = NumberFormat.getDecimalFormat();
		return fmt.format(value);
	}

}
