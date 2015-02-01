package com.shale.client.element;

import java.util.Set;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;
import com.orange.links.client.connection.Connection;
import com.orange.links.client.menu.ContextMenu;
import com.shale.client.clustering.Clustering;
import com.shale.client.conceptmap.MainView;
import com.shale.client.utils.Languages;
import com.shale.client.utils.MyDiagramModel;
import com.shale.client.utils.Savable;

/**
 * Linking Phrase is the clue between the concepts. Although it is not taken
 * account when analysing the map, linking phrases help user to expand the whole
 * concept map. Linking phrase has its own text and a certain position in the
 * map.
 * 
 * @author Istovatis -- istovatis@gmail.com --
 *
 */
public class LinkingPhrase extends MapElement implements Savable {

	public LinkingPhrase(String content) {
		super(content);
		this.setStyleName("linkingPhrase");
		this.setIdentifier("linking phrase");
		this.setContentRepresentation(content);
		this.setText(content);
		this.setProperWidth(content);
		widgetList.add(this);
		this.chooseIt();
	}

	public LinkingPhrase() {
		super();
	}

	public Label createLinkingPhrase(String name) {
		// Create linking Phrase
		this.setStyleName("linkingPhrase");
		this.setIdentifier("linking phrase");
		this.setContentRepresentation(name);
		this.setText(name);
		widgetList.add(this);
		this.chooseIt();
		return this;
	}

	@Override public String getIdentifier() { return identifier; }

	public void delete(int position) {
		MapElement widget = widgetList.get(position);
		widgetList.remove(position);
		MainView.diagramController.deleteWidget(widget);
		Clustering.isChanged = true;
	}
	
	/**
	 * Wrap the beginning and the end concept and add a connection between them.
	 * @param name
	 */
	public void drawLinkingPhrase(String name) {
		MapElement mapElement = new MapElement();
		int startWidgetPos = mapElement.getStartLabelPos();
		Widget startWidget = mapElement.getListItem(startWidgetPos);

		int endWidgetPos = mapElement.getEndLabelPos();
		Widget endWidget = mapElement.getListItem(endWidgetPos);
		int left = widgetsLeftPosition(startWidget, endWidget);
		int top = widgetsTopPosition(startWidget, endWidget);

		Connection c1 = MainView.diagramController.drawStraightArrowConnection(
				startWidget, endWidget);

		MapElement linkingPhrase = new LinkingPhrase(name);
		MainView.diagramController.addDecoration(linkingPhrase, c1);
		MainView.diagramController.addWidgetAtMousePoint(linkingPhrase);
		//set id to linking phrase
		MainView.diagramController.getView().getElement().setId(linkingPhrase.getText());
		unSelectEverything();
	}
	
	/**
	 * Having a starting and an end concept, draw a linking phrase that
	 * will link start and end concept. The form of concept-linking_phrase-concept
	 * is called proposition.
	 */
	public void drawLinkingPhrase() {
		MapElement mapElement = new MapElement();
		int startWidgetPos = mapElement.getStartLabelPos();
		Widget startWidget = mapElement.getListItem(startWidgetPos);

		int endWidgetPos = mapElement.getEndLabelPos();
		Widget endWidget = mapElement.getListItem(endWidgetPos);
		int left = widgetsLeftPosition(startWidget, endWidget);
		int top = widgetsTopPosition(startWidget, endWidget);

		Connection c1 = MainView.diagramController.drawStraightArrowConnection(
				startWidget, endWidget);
		int linkingPhrasePos = mapElement.getLinkingPhrasePos();
		Widget linkingPhrase = mapElement.getListItem(linkingPhrasePos);
		MainView.diagramController.addDecoration(linkingPhrase, c1);

		/*
		 * The following command adds the linkingphrase solving the problem of
		 * visible linking phrase content on a window appearence. The delete(int
		 * position) method depends on the this command
		 */
		MainView.diagramController.addWidget(linkingPhrase, left, top);
	}

	protected void initWidget(String text) {
		contextMenu = new ContextMenu();
		contextMenu.setAnimationEnabled(true);
		String ren = Languages.getDictionary().get("rename");
		String reverse = "Reverse Direction";
		((ContextMenu) this.contextMenu).addItem(new MenuItem(ren, true,
				new Command() {
					public void execute() {
						// fireEvent
						contextMenu.hide();
						setSelectedPosition(getCurrentLabelPos());
						MainView.diagramController.addWidget(
								MainView.insertItem, 400, 200);
						MainView.insertItem.setVisible(true);
						String rename = Languages.getMsgs().get("rename");
						MainView.typeLabel.setText(rename);
						MainView.itemName.setFocus(true);
						MainView.typeLabel.setTitle("rename");
					}
				}));
		// Add Reverse Arrow Direction Option
		((ContextMenu) this.contextMenu).addItem(new MenuItem(reverse, true,
				new Command() {
					public void execute() {
						// fireEvent
						Set<LinkingPhraseModel> linkSet = MyDiagramModel.get()
								.getLinkRepresentationSet();
						String thisPosition = String.valueOf(currentLabelPos);
						System.out.println("I chose "+thisPosition);
						for (LinkingPhraseModel link : linkSet) {
							System.out.println("Check "+link.id);
							if(link.id.equals(thisPosition))
								System.out.println(link.decoration+" chosen");
						}
						//MapElement widget = widgetList.get(currentLabelPos);
					}
				}));
	}
}