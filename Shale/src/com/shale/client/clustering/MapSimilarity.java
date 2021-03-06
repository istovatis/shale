package com.shale.client.clustering;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Calculate the similarity between pairs of clusterings. Implementation of the
 * publication 'A Similarity Measure for Clustering and Its Applications' by
 * Guadalupe J. Torres et al. Similarity is expressed as a number between 0 and
 * 1. 0 defines a total absence of similarity of the two clusters whereas 1
 * declares that the two clusters are unique.
 *
 */
public class MapSimilarity {
	
	private int m; // The number of clusters of graph1
	private int n; // The number of clusters of graph2
	private double sum;	// The sum of jaccard similarities
	
	public int max(int m, int n){
		return  (m>n) ?  m :  n;
	}
	/**
	 * The Jaccard  Similarity of cluster1 and cluster2
	 * @param cluster1
	 * @param cluster2
	 * @return
	 */
	public double jaccardSimilarity(HashSet<Integer> cluster1, HashSet<Integer> cluster2){
		 HashSet<Integer> intersection = new HashSet<Integer>();
	     HashSet<Integer> union = new HashSet<Integer>();
		
	     // If one of the two users does not have any objects of this DataType
		 if (cluster1 == null || cluster2 == null)
			 return 0.0;

		intersection.addAll(cluster1);
		intersection.retainAll(cluster2);
		union.addAll(cluster1);
		union.addAll(cluster2);
		
		return union.size() == 0 ? 0.0 : intersection.size() / (double) union.size();
	}
	
	/**
	 * Transform arrayLists into Sets and call jaccardSimilarity. Return the double value of similarity.
	 * @param graph1
	 * @param graph2
	 * @return
	 */
	public double findSimilarity(ArrayList<ArrayList<Integer>> graph1, ArrayList<ArrayList<Integer>> graph2){
		
		graph1 = removeEmptyClusters(graph1);
		graph2 = removeEmptyClusters(graph2);
		
		m = graph1.size();
		n = graph2.size();
		
		int max = max(m, n);
		System.out.println("Max of " + m + " and " + n + " = " + max);
		sum = 0;
		HashSet<Integer> cluster1 = new HashSet<Integer>();
		HashSet<Integer> cluster2 = new HashSet<Integer>();

		for(int i=0; i<m; i++)
			for(int j=0; j<n; j++){
				cluster1.addAll(graph1.get(i));
				cluster2.addAll(graph2.get(j));
				double sim = jaccardSimilarity(cluster1, cluster2);
				sum+= sim;
				System.out.println("Sum " + sum + " Sim = "+ sim);
				cluster1.clear();
				cluster2.clear();
			}
		
		return sum / max;
	}
	
	public void testCode(){
		ArrayList<ArrayList<Integer>> graph1  = new ArrayList<ArrayList<Integer>>();
		ArrayList<ArrayList<Integer>> graph2 = new ArrayList<ArrayList<Integer>>();
		
		ArrayList<Integer> C1 = new ArrayList<Integer>();
		for(int i=1; i<7; i++)
			C1.add(i);
		
		ArrayList<Integer> C2 = new ArrayList<Integer>(); //(ArrayList<Integer>) Arrays.asList(5, 6, 7, 8);
		for(int i=7; i<9; i++)
			C2.add(i);
	
		ArrayList<Integer> D1 = new ArrayList<Integer>(); //Arrays.asList(1,2);
		for(int i=1; i<5; i++)
			D1.add(i);
		
		ArrayList<Integer> D2 = new ArrayList<Integer>(); //Arrays.asList(3, 4);
		for(int i=5; i<9; i++)
			D2.add(i);
		
		graph1.add(C1);
		graph1.add(C2);
		
		graph2.add(D1);
		graph2.add(D2);
		 
		System.out.println("*** Similarity man "+ findSimilarity(graph1, graph2)+" ***");
	}
	
	/**
	 * Remove all empty clusters from the graph
	 * @param graph
	 */
	public ArrayList<ArrayList<Integer>> removeEmptyClusters(ArrayList<ArrayList<Integer>> graph){
		for(int i = 0; i < graph.size(); i++)
			if(graph.get(i).size()==0)
				graph.remove(i);

		return graph;
	}
}
