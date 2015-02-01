package com.shale.client.element;

import com.google.gwt.core.client.GWT;
import com.orange.links.client.menu.ContextMenu;
import com.orange.links.client.utils.LinksClientBundle;
import com.shale.client.clustering.Clustering;
import com.shale.client.conceptmap.MainView;
import com.shale.client.importer.DiagramImportation;
import com.shale.client.importer.ImportService;
import com.shale.client.importer.ImportServiceAsync;
import com.shale.client.savedmap.Metadata;
import com.shale.client.utils.MyDiagramController;
import com.shale.client.utils.MyDiagramModel;
import com.shale.client.utils.Savable;

/**
 * Concept is the main abstraction or generalization that is provided by user to
 * clarify his knowledge to the given topic. Concepts have a text which is
 * actually set by user. In concept map concepts have a certain position in the
 * map.
 * 
 * @author Istovatis -- istovatis@gmail.com --
 *
 */
public class Concept extends MapElement implements Savable {

	protected ContextMenu menu;
	//the total number of concepts on diagram
	private static int numConcepts;
	public static int aloneConcepts = 0;	//concepts with no conns
	private ImportServiceAsync importSvc = GWT.create(ImportService.class);
	protected static String deleteMenuText = "Delete";
	public static String pallete[] = {"#CCFF99", "#55dfba", "#df55cd", "#99aa88", "#eeaa11", "#996633", "#ccccee", "#ff3366", "#ffff66", "#0099ff", "#99ddaa", "#cc4455", "#99ccee", "#99ffff", "#999999", "#faaa99", "#993366", "#6677bb", "#995544", "#cc4499", "#cc5511", "#9977aa"};
	public Concept() {}
	
	public Concept(String content) {
		super(content);
		this.setStyleName("concept");
		this.setIdentifier("concept");
		this.setContentRepresentation(content);
		this.setText(content);
		this.unSelectEverything();
		int prevId = DiagramImportation.getMaxId();
		this.id = prevId+1;
		this.setId(this.id);
		DiagramImportation.setMaxId(this.id);
		widgetList.add(this);
		this.chooseIt();
	}
	
	public Concept(String content, String id) {
		super(content);
		this.setStyleName("concept");
		this.setIdentifier("concept");
		this.setContentRepresentation(content);
		this.setText(content);
		this.unSelectEverything();
		int prevId = DiagramImportation.getMaxId();
		this.id = Integer.valueOf(id);
		this.setId(this.id);
		if(prevId<this.id){
			DiagramImportation.setMaxId(this.id);	
		}
		widgetList.add(this);
		this.chooseIt();
	}

	@Override
	public String getIdentifier() {
		return identifier;
	}

	public void cssSelected(int position) {
		widgetList.get(position).addStyleName(
				LinksClientBundle.INSTANCE.css().translucide());
	}

	/**
	 * Remove targetPanel and insertItem to avoid ClassCastException (widget
	 * must implement Savable...)
	 * 
	 * 
	 * @see com.istovatis.examples.experimentals.client.MapElement#delete(int)
	 */
	public void delete(int position) {
		MainView.dettachLabels();
		MapElement widget = widgetList.get(position);
		int id  = widget.id;
		MainView.diagramController.deleteWidget(widget);
		MyDiagramModel.get().removeConceptWithConnections(widget);
		Clustering.isChanged = true;
		Metadata.get().removeFromCluster(id);
		MyDiagramController.addedConcepts--;
		
		//add creator label to Map diagram 
		MainView.attachCreator();
	}
	
	public static int getNumConcepts() { return numConcepts; }
	public static void setNumConcepts(int numConcept) { numConcepts = numConcept; }

}
