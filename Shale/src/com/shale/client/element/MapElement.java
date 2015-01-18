package com.shale.client.element;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.event.dom.client.HasAllTouchHandlers;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.Widget;
import com.orange.links.client.connection.Connection;
import com.orange.links.client.menu.ContextMenu;
import com.orange.links.client.save.*;
import com.orange.links.client.shapes.Point;
import com.shale.client.Uima.NeighborMenu;
import com.shale.client.Uima.NeighborWordsCollector;
import com.shale.client.Uima.UimaResponse;
import com.shale.client.clustering.UserClustering;
import com.shale.client.conceptmap.MainView;
import com.shale.client.utils.Languages;
import com.shale.client.utils.Mouse;
import com.shale.client.utils.MyClickHandler;
import com.shale.client.utils.MyDiagramModel;

/**
 * Basic Element of the Concept Map. Linking phrases and concepts extend
 * @author Istovatis -- istovatis@gmail.com --
 *
 */
public class MapElement extends Label implements HasAllTouchHandlers,
		IsDiagramSerializable, ContextMenuHandler {
	Dictionary dict;
	protected PopupPanel contextMenu;
	protected MenuItem neighborMenuItem;
	protected Widget widget;
	
	private NeighborWordsCollector wordsCollector;

	protected String content;
	protected String identifier;
	protected int id;

	//save the index of the cluster the MapElement belongs.
	protected int clusterIndex =-1;

	protected static ArrayList<MapElement> widgetList = new ArrayList<MapElement>(0);

	protected Label startLabel;
	protected Label endLabel;
	protected Label linkingPhrase;

	protected int currentLabelPos;
	protected static int selectedPosition;
	
	protected static int linkingPhrasePos;
	protected static int startLabelPos;
	protected static int endLabelPos;
	protected static int left;
	protected static int top;
	protected static boolean isStartSet = false;
	protected static boolean isEndSet = false;
	protected static boolean isLinkingPhraseSet = false;
	protected static boolean isThisLinkingPhrase = false;

	public MapElement(String content) {
		super(content);
		this.content = content;

		initWidget(content);

		// of course it would be better if base would implement
		// HasContextMenuHandlers, but the effect is the same
		addDomHandler(this, ContextMenuEvent.getType());
	}

	public MapElement() {}

	public String getContent() { return content; }

	public void setContent(String content) { this.content = content; }
	
	public int getId() { return id; }
	public void setId(int id) { this.id = id; }

	/*
	 * Arxikopoiei enan clickhandler, thetontas parallila tin trexousa thesi tou
	 * stoixeiou
	 */
	public void chooseIt() {
		setCurrentLabelPos(widgetList.indexOf(this));
		this.addClickHandler(new MyClickHandler(getCurrentLabelPos()) {});
	}

	public void setIdentifier(String identifier) { this.identifier = identifier; }
	
	public void setCurrentLabelPos(int currentLabelPos) { this.currentLabelPos = currentLabelPos; }
	public int getCurrentLabelPos() { return currentLabelPos; }
	
	public static void setSelectedPosition(int position){ selectedPosition = position; }
	public static int getSelectedPosition(){ return selectedPosition; }

	public void setStartLabel(Label startLabel) { this.startLabel = startLabel; }

	public int getStartLabelPos() { return startLabelPos; }
	public void setStartLabelPos(int startLabel) { startLabelPos = startLabel; }

	public void setEndLabelPos(int endLabel) { endLabelPos = endLabel; }

	public int getEndLabelPos() { return endLabelPos; }
	public void setEndLabel(Label endLabel) { this.endLabel = endLabel; }

	public int listSize() {
		return widgetList.size();
	}

	public boolean isSetStartWidget() { return isStartSet; }

	public void setStartWidget() { isStartSet = true; }
	public void unsetStartWidget() { isStartSet = false; }

	public boolean isSetEndWidget() { return isEndSet; }

	public void setEndWidget() { isEndSet = true; }
	public void unsetEndWidget() { isEndSet = false; }
	
	public static void setLeft(int lefty) {	left = lefty; }
	public static int getLeft(){ return left; }
	
	public static void setTop(int topy){ top = topy; }
	public static int getTop(){ return top; }
	
	public int getClusterIndex() {
		return clusterIndex;
	}

	public void setClusterIndex(int clusterIndex) {
		this.clusterIndex = clusterIndex;
	}

	public void CheckIfThisLinkingPhrase() {
		if (this instanceof LinkingPhrase) {
			isThisLinkingPhrase = true;
		} else if (this instanceof Concept) {
			isThisLinkingPhrase = false;
		}
	}

	public Boolean returnEndWidgetLinkingPhrase() {
		Widget widget = endLabel;
		if (widget instanceof LinkingPhrase) {
			return true;
		} else
			return false;
	}

	public void setLinkingPhrasePos(int position) { linkingPhrasePos = position; }
	public int getLinkingPhrasePos() { return linkingPhrasePos; }

	public boolean IsSetLinkingPhrase() { return isLinkingPhraseSet; }
	public void setLinkingPhrase() { isLinkingPhraseSet = true; }
	public void unsetLinkingPhrase() { isLinkingPhraseSet = false; }

	public Widget getListItem(int pos) {
		return widgetList.get(pos);
	}

	@Override
	public String getType() {
		return this.identifier;
	}

	@Override
	public String getContentRepresentation() {
		return content;
	}

	@Override
	public void setContentRepresentation(String contentRepresentation) {
		this.content = contentRepresentation;

	}

	public void drawConnection() {
		MapElement mapElement = new MapElement();
		int startWidgetPos = mapElement.getStartLabelPos();
		Widget startWidget = mapElement.getListItem(startWidgetPos);

		int endWidgetPos = mapElement.getEndLabelPos();
		Widget endWidget = mapElement.getListItem(endWidgetPos);

		Connection c1 = MainView.diagramController.drawStraightArrowConnection(
				startWidget, endWidget);
	}

	public int widgetsLeftPosition(Widget start, Widget end) {
		int left;
		int startLeft = start.getAbsoluteLeft() - 147;
		int endLeft = end.getAbsoluteLeft() - 147;
		int difference = startLeft - endLeft;
		System.out.println("LEFT difference:" + difference + " start:"
				+ startLeft + " end" + endLeft);
		if (difference > 0) {
			left = startLeft - (difference / 2);
		} else
			left = endLeft - (Math.abs(difference) / 2);
		System.out.println("estimated x: " + (left));
		return left;
	}

	public int widgetsTopPosition(Widget start, Widget end) {
		int top;
		int startTop = start.getAbsoluteTop() - 11;
		int endTop = end.getAbsoluteTop() - 11;

		int difference = startTop - endTop;
		System.out.println("TOP difference:" + difference + " start:"
				+ startTop + " end" + endTop);
		if (difference > 0) {
			top = startTop - difference / 2;
		} else
			top = endTop - Math.abs(difference) / 2;
		System.out.println("estimated y:" + (top + 4));
		return top;
	}
	
	/**
	 * Set start and end map element. Thease elements will be used to draw a linking phrase.
	 * If nothing selected: Set start widget and save its position
	 * If start widget selected: Set end widget, save its position and then unset start widget (its position is already set). 
	 * 
	 * @param event
	 * @param currentLabel The selected MapElement position
	 */
	public void checkElement(ClickEvent event, int currentLabel) {
		Concept concept = new Concept();
		// find if checked element is concept or linking phrase
		String type = widgetList.get(currentLabel).getStylePrimaryName();

		// elegxos an to epilegmeno stoixeio einai linking phrase
		CheckIfThisLinkingPhrase();

		if (!isSetStartWidget() && type.equals("concept")) {
			setStartLabelPos(currentLabel);
			// na allaksei to css tou epilegmenou concept
			concept.cssSelected(currentLabel);
			setStartWidget();
			unsetEndWidget();
		} else if (isSetStartWidget()) {
			if (currentLabel != getStartLabelPos() && type.equals("concept")) {
				setEndLabelPos(currentLabel);
				setEndWidget();
				concept.cssSelected(currentLabel);
				unsetStartWidget();
				if (!MainView.insertItem.isVisible()) {
					Mouse mouse = new Mouse();
					int left = mouse.correctLeft(event.getClientX());
					int top = mouse.correctTop(event.getClientY());
					MainView.addInsertItemPanel(left-115, top);
					MainView.typeLabel.setText(Languages.getMsgs().get("windowNewLinking"));
					MainView.typeLabel.setTitle("linkingPhrase");
				}
				if (IsSetLinkingPhrase()) {
					MapElement mapElement = new LinkingPhrase();
					int linkingPos = mapElement.getLinkingPhrasePos();
					Window.alert("Linking Pos:" + linkingPos);
					// mapElement.drawLinkingPhrase();
					unsetLinkingPhrase();
				}
			} else if (type.equals("linkingPhrase"))
				Window.alert("Please select a concept, not a linking phrase");

			else{
				String msg = Languages.getMsgs().get("sameStartEnd");
				Window.alert(msg);
				unSelectEverything();
			}
		}
	}

	public void unSelectEverything() {
		if (isStartSet) {
			unsetStartWidget();
			int position = getStartLabelPos();
			widgetList.get(position).setStyleName("concept");
		}
		if (isEndSet) {
			unsetEndWidget();
			int position = getEndLabelPos();
			widgetList.get(position).setStyleName("concept");

			/*
			 * in case of previously unseted startLabel, because of endLabel
			 * creation, just change the css of startLabel
			 */
			int pos = getStartLabelPos();
			widgetList.get(pos).setStyleName("concept");
		}
	}

	public Point getElementCoordinates(String id) {
		Widget w = MyDiagramModel.get().getFunctionById(id);
		int x = w.getElement().getAbsoluteLeft();
		int y = w.getElement().getAbsoluteTop();
		Point point = new Point(x, y);
		return point;
	}

	public void rename(int position, String name) {
		MapElement widget = widgetList.get(position);
		((IsDiagramSerializable) widget).setContentRepresentation(name);
		widget.setText(name);
		setProperWidth(name);
	}

	public void delete(int position) {
		Window.alert("Remove connection on the block");
	}
	
	public String setProperWidth(String text){
		return String.valueOf(20);
	}

	@Override
	public void onContextMenu(ContextMenuEvent event) {
		// stop the browser from opening the context menu
		event.preventDefault();
		event.stopPropagation();
		setLeft(event.getNativeEvent().getClientX());
		setTop(event.getNativeEvent().getClientY());
		this.contextMenu.setPopupPosition(event.getNativeEvent().getClientX(),
				event.getNativeEvent().getClientY());
		this.contextMenu.show();
	}

	protected void initWidget(String text) {
		this.contextMenu = new ContextMenu();
		String renameTxt = Languages.getDictionary().get("rename");
		String deleteTxt = Languages.getDictionary().get("del");
		String addToClusterTxt = Languages.getDictionary().get("addToCluster");
		String findHelpfulWordsTxt = Languages.getDictionary().get("findNeighbors");
		
		addRenameOption(renameTxt);
		addDeleteOption(deleteTxt);
		addToClusterOption(addToClusterTxt);
		findHelpfulWords(findHelpfulWordsTxt);
		
		wordsCollector = new NeighborWordsCollector(text);
		wordsCollector.askForNeighbors();
	}
	
	/**
	 * Add rename option to every concept
	 * @param rename
	 */
	protected void addRenameOption(String rename) {
		((ContextMenu) this.contextMenu).addItem(new MenuItem(rename, true,
				new Command() {
					public void execute() {
						// fireEvent
						contextMenu.hide();
						setSelectedPosition(getCurrentLabelPos());
						MainView.addInsertItemPanel(getLeft() - 192, getTop());
						String rename = Languages.getMsgs().get("rename");
						MainView.typeLabel.setText(rename);
						// keep the previous text name
						MainView.itemName.setText(widgetList.get(getCurrentLabelPos()).getContent());
						MainView.itemName.setFocus(true);
						MainView.typeLabel.setTitle("rename");
					}
				}));
	}
	
	/**
	 * Provide a delete element option to map element
	 * @param deleteTxt
	 */
	protected void addDeleteOption(String deleteTxt) {
		((ContextMenu) this.contextMenu).addItem(new MenuItem(deleteTxt, true,
				new Command() {
					public void execute() {
						// fireEvent
						delete(getCurrentLabelPos());
						contextMenu.hide();
					}
				}));
	}
	
	/**
	 * Add to cluster option for concepts
	 * @param addToClusterTxt
	 */
	protected void addToClusterOption(String addToClusterTxt) {
		((ContextMenu) this.contextMenu).addItem(new MenuItem(addToClusterTxt,
				true, new Command() {
					public void execute() {
						// fireEvent
						Concept widget = (Concept) widgetList.get(getCurrentLabelPos());
						widgetList.get(getCurrentLabelPos()).setClusterIndex(
								UserClustering.getChosen());
						// widget.setClusterIndex(UserClustering.getChosen());
						UserClustering.borderIt(widget);
					}
				}));
	}
	
	protected void findHelpfulWords(String findHelpfulWordsTxt) {
		
		neighborMenuItem = new MenuItem(findHelpfulWordsTxt,
				true, new Command() {
					public void execute() {
						Concept widget = (Concept) widgetList.get(getCurrentLabelPos());
						UimaResponse neighborWords = wordsCollector.getNeighors();
						contextMenu.getWidget().getOffsetHeight();
						NeighborMenu list = new NeighborMenu(neighborWords.getWords(), widget.getAbsoluteTop(), widget.getAbsoluteTop());		
						neighborMenuItem.setSubMenu(NeighborMenu.getMenuBar());
					}
				});
		((ContextMenu) this.contextMenu).addItem(neighborMenuItem);
	}
	
	public void setLanguage(){
		// Get current language and set all the UI widgets
		dict = Languages.getDictionary();
	}

}
