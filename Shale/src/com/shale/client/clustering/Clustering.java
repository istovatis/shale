package com.shale.client.clustering;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;

/**
 * A definition of a group of clusters which combination is a concept map.s 
 * A cluster is regarded as a group of concepts that form a core issue of a given map.
 * @author Istovatis -- istovatis@gmail.com --
 *
 */
public abstract class Clustering {
	// The provider that holds the list of clusters
	protected static ListDataProvider<ClusterInfo> dataProvider = new ListDataProvider<ClusterInfo>();
	protected static List<ClusterInfo> clusterList;
	protected HasData<ClusterInfo> display;
	// Check if the diagram is changed, so cmap must be saved before clustering
	public static boolean isChanged;

	public static class ClusterInfo {
		protected String name;
		protected int id;

		public int getId() { return id; }
		public void setId(int id) { this.id = id; }

		public String getName() { return this.name; }
		public void setName(String name) { this.name = name; }
	}

	/**
	 * Add a display to the database. The current range of interest of the
	 * display will be populated with data.
	 *
	 * @param display a {@Link HasData}.
	 */
	public void addDataDisplay() {
		Set<HasData<ClusterInfo>> displays = dataProvider.getDataDisplays();
		if (displays.size() == 0)
			dataProvider.addDataDisplay(display);
	}

	public void setDataDisplay(HasData<ClusterInfo> display) {
		this.display = display;
	}

	public static ListDataProvider<ClusterInfo> getDataProvider() {
		return dataProvider;
	}

	public void findSimilarity(ArrayList<ArrayList<Integer>> graph1,
		ArrayList<ArrayList<Integer>> graph2) {
	}
}
