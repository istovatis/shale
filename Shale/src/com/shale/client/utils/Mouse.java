package com.shale.client.utils;

import com.orange.links.client.shapes.Point;
import com.shale.client.conceptmap.MainView;

public class Mouse {

	public Mouse() {
		mapWidth = MainView.diagramController.getCanvasWidth();
		mapHeight = MainView.diagramController.getCanvasHeight();
	}

	private int mapWidth;
	private int mapHeight;
	private int top;
	private int left;

	// perasma twn sintetagmenwn tou pontikiou
	// kai diorthwsi gia na mi ksefeugei to menu apo to plaisio tou diagram
	public Point getMouse(Point mouse) {
		int left = mouse.getLeft();
		int top = mouse.getTop();

		if (left > mapWidth - 192) {
			left = left - 192;
			mouse.setLeft(left);
		}
		if (top > mapHeight - 200) {
			top = top - 110;
			mouse.setTop(top);
		}
		if (top < 0 || left < 0) {
			top = mapHeight / 2;
			left = mapWidth / 2;
			mouse.setTop(top);
			mouse.setLeft(left);
		}
		return mouse;
	}

	public int correctLeft() {
		left = getLeft();

		if (left > mapWidth - 192)
			return left - 197;
		else
			return left;
	}

	public int correctTop() {
		top = getTop();

		if (top > mapHeight - 200)
			return top - 115;
		else
			return top;
	}

	public int correctLeft(int left) {
		if (left > mapWidth - 192)
			return left - 197;
		else
			return left;
	}

	public int correctTop(int top) {
		if (top > mapHeight - 200)
			return top - 115;
		else
			return top;
	}

	public int getLeft() {
		return MainView.diagramController.getMousePoint().getLeft();
	}

	public int getTop() {
		return MainView.diagramController.getMousePoint().getTop();
	}

}
