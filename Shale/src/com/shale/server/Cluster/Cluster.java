package com.shale.server.Cluster;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

import edu.uci.ics.jung.algorithms.cluster.EdgeBetweennessClusterer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class Cluster extends RemoteServiceServlet {

	private static ArrayList<Integer> links = new ArrayList<Integer>(0);
	private static ArrayList<Integer> vertices = new ArrayList<Integer>(0);
	private ArrayList<ArrayList<Integer>> graphs = new ArrayList<ArrayList<Integer>>(
			0);
	private Graph<Integer, Integer> graph = new SparseMultigraph<Integer, Integer>();
	private String fileXML;
	// Set graph as Directed of Undirected
	private final static EdgeType graphType = EdgeType.UNDIRECTED;

	public static EdgeType getGraphtype() {
		return graphType;
	}

	public String getFileXML() {
		return fileXML;
	}

	public void setFileXML(String fileXML) {
		this.fileXML = fileXML;
	}

	private int numGraphs;

	public int getNumGraphs() {
		return numGraphs;
	}

	public void setNumGraphs(int numGraphs) {
		this.numGraphs = numGraphs;
	}

	public void createClusters(String fileXml) {
		final DomParserCM2 dpCM = new DomParserCM2();
		dpCM.buildCM(fileXml);
		graph = dpCM.buildCM4Jung();
	}

	/**
	 * Cluster the graph. The number of clusters is defined as a parameter to
	 * the method. It uses EdgeBetweennessClusterer algorithm. Every concept of
	 * the new cluster is saved in an arraylist.
	 */
	public void manageGraph(int numGraphs, int currentGraphs, boolean increase) {
		// EdgeBetweennessClusterer: Compute edge betweenness for all edges in
		// current graph
		// Remove edge with highest betweenness
		if (numGraphs <= graph.getEdgeCount() && numGraphs >= 0) {
			EdgeBetweennessClusterer<Integer, Integer> clusterer = new EdgeBetweennessClusterer<Integer, Integer>(
					numGraphs);
			Set<Set<Integer>> clusterSet = clusterer.transform(graph);

			List<Integer> edges = clusterer.getEdgesRemoved();
			for (Integer e : edges) {
				for (Integer ver : graph.getIncidentVertices(e)) {
					vertices.add(ver);
				}
				links.add(e);
			}
			System.out.println(clusterSet.size()+" sizy");
			
			for (Iterator<Set<Integer>> cIt = clusterSet.iterator(); cIt
					.hasNext();) {
				Set<Integer> vertice = cIt.next();
				if(vertice.size()<=1){
					cIt.remove();
				}
				else{
					Graph<Integer, Integer> sub = groupCluster(graph, vertice);
					ArrayList<Integer> arrayList = new ArrayList<Integer>(
							sub.getVertices());
					graphs.add(arrayList);	
				}
					//				for (Integer v : vertices) {
//					System.out.println("Remove vertex" + v);
//				}
			}
			setNumGraphs(graphs.size());
			System.out.println("@Server "+graphs.size()+". Wanted: "+currentGraphs+" or "+numGraphs);
			if (increase) {
				if (getNumGraphs() <= currentGraphs) {
					System.out.println("Icrease the same");
					clearAll();
					manageGraph(numGraphs + 1, currentGraphs, increase);
				}
			} else {
				if (getNumGraphs() >= currentGraphs) {
					System.out.println("Decrease the same");
					clearAll();
					manageGraph(numGraphs - 1, currentGraphs, increase);
				}
			}
		}
	}

	/**
	 * Get all subgraphs.
	 */
	public ArrayList<ArrayList<Integer>> getClusters() {
		return graphs;
	}

	/**
	 * Find cluster vertices and add them to the subgraph.
	 */
	private Graph<Integer, Integer> groupCluster(Graph graph,
			Set<Integer> vertices) {
		Graph<Integer, Integer> subGraph = null;
		if (vertices.size() <= graph.getVertexCount()) {
			subGraph = SparseMultigraph.<Integer, Integer> getFactory()
					.create();
			for (Integer v : vertices) {
				subGraph.addVertex(v);
				;
			}
		}
		return subGraph;
	}

	public int getSubGraphsNum() {
		return graphs.size();
	}

	/**
	 * Clear all data structures and regenerate the initial graph
	 */
	public void clearAll() {
		links = new ArrayList<Integer>(0);
		vertices = new ArrayList<Integer>(0);
		graphs = new ArrayList<ArrayList<Integer>>(0);

		final DomParserCM2 dpCM = new DomParserCM2();
		dpCM.buildCM(fileXML);
		graph = dpCM.buildCM4Jung();
	}

}
