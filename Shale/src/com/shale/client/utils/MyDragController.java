package com.shale.client.utils;

import com.allen_sauer.gwt.dnd.client.DragStartEvent;
import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;

public class MyDragController extends PickupDragController {

	public MyDragController(AbsolutePanel boundaryPanel,
			boolean allowDroppingOnBoundaryPanel) {
		super(boundaryPanel, allowDroppingOnBoundaryPanel);
	}

	public void onDragStart(DragStartEvent event) {
		super.dragStart();
		Window.alert("wow");
	}

}
