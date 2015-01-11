package com.shale.client.Uima;

import java.util.ArrayList;

import com.google.gwt.event.dom.client.ContextMenuEvent;
import com.google.gwt.event.dom.client.ContextMenuHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.MenuItem;
import com.shale.client.conceptmap.MainView;
import com.shale.client.utils.Languages;

public class NeighborMenu implements ContextMenuHandler{

	public static MenuBar menuBar;

	private static ArrayList<String> words = new ArrayList<String>();
	private int left;
	private int top;
		
	public static MenuBar getMenuBar() {
		return menuBar;
	}
	
	public static ArrayList<String> getWords() {
		return words;
	}
	
	public NeighborMenu(ArrayList<String> neighborWords, int left, int top) {
		words = neighborWords;
		this.left = left;
		this.top = top;
		init();
	}
	
	public void init() {
		menuBar = new MenuBar(true);
		menuBar.setAutoOpen(true);
		menuBar.setFocusOnHoverEnabled(true);
		menuBar.setVisible(true);
		addNeighborhoodList();
	}
	
	@Override
	public void onContextMenu(ContextMenuEvent event) {
		// stop the browser from opening the context menu
		event.preventDefault();
		event.stopPropagation();
	}
	
	protected void addNeighborhoodList() {
		for (final String word : words) {
			menuBar.addItem(new MenuItem(word, true,
					new Command() {
						public void execute() {

							MainView.addInsertItemPanel(left, top);
							String rename = Languages.getMsgs().get("rename");
							MainView.typeLabel.setText(rename);
						
							MainView.itemName.setFocus(true);
							MainView.typeLabel.setTitle(word);
						}
					}));	
		}
		

	}

}
