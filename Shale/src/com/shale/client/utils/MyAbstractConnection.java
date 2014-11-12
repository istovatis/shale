package com.shale.client.utils;

import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.MenuItem;
import com.google.gwt.user.client.ui.Widget;
import com.orange.links.client.DiagramController;
import com.orange.links.client.connection.AbstractConnection;
import com.orange.links.client.event.UntieLinkEvent;
import com.orange.links.client.exception.DiagramViewNotDisplayedException;
import com.orange.links.client.menu.ContextMenu;
import com.orange.links.client.shapes.FunctionShape;
import com.orange.links.client.shapes.Point;
import com.orange.links.client.shapes.Shape;


/**
 * Currently unused. Created so as to include "change direction" option at every linking phrase
 */
public class MyAbstractConnection extends AbstractConnection{
	
	public MyAbstractConnection(DiagramController controller, Shape startShape, Shape endShape) throws DiagramViewNotDisplayedException {
		super(controller, startShape, endShape);
	}
	
	@Override
	protected void draw(Point p1, Point p2, boolean lastPoint) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void draw(List<Point> pointList) {
		
	}
	
	@Override
	protected void initMenu() {
		super.initMenu();
		
		menu = new ContextMenu();
		menu.addItem(new MenuItem(deleteMenuText, true, new Command() {
			public void execute() {
				// fireEvent
				FunctionShape startShape = (FunctionShape) getStartShape();
				FunctionShape endShape = (FunctionShape) getEndShape();

				Widget startWidget = startShape.asWidget();
				Widget endWidget = endShape.asWidget();
				controller.fireEvent(new UntieLinkEvent(startWidget, endWidget, MyAbstractConnection.this));
				controller.deleteConnection(MyAbstractConnection.this);
				startShape.removeConnection(MyAbstractConnection.this);
				endShape.removeConnection(MyAbstractConnection.this);
				System.out.println("Yooooo!!!");
				menu.hide();
			}
		}));

		menu.addItem(new MenuItem(straightenMenuText, true, new Command() {
			public void execute() {
				setStraight();
				menu.hide();
			}
		}));
	}
	
	
	
}
