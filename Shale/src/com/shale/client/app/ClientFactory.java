package com.shale.client.app;

import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;
import com.shale.client.conceptmap.MainView;
import com.shale.client.login.LoginView;
import com.shale.client.menu.MenuView;
import com.shale.client.user.insert.InsertView;

public interface ClientFactory {
	public EventBus getEventBus();
	public PlaceController getPlaceController();
	
	public MainView getMainView();
	public LoginView getLoginView();
	public MenuView getMenuView();
	public InsertView getInsertView();
}
