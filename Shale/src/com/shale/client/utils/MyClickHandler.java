package com.shale.client.utils;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.shale.client.element.MapElement;

/**
 * A Click Handler implementation 
 * @author Istovatis -- istovatis@gmail.com --
 *
 */
public class MyClickHandler implements ClickHandler {

	private int position;
	public int getPosition() {return position; }
	
	public MyClickHandler(){}
	
	public MyClickHandler(int position) {
		this.position = position;
	}

	@Override
	public void onClick(ClickEvent event) {
		MapElement element = new MapElement();
		int pos = getPosition();

		element.checkElement(event, pos);
	}
}
