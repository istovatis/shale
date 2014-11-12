package com.shale.client.importer;

import com.google.gwt.event.shared.SimpleEventBus;
import com.google.gwt.place.shared.PlaceController;
import com.google.web.bindery.event.shared.EventBus;

import com.shale.client.app.ClientFactory;
import com.shale.client.conceptmap.MainView;
import com.shale.client.login.LoginView;
import com.shale.client.menu.MenuView;
import com.shale.client.user.insert.InsertView;

public class ClientFactoryImpl implements ClientFactory{
	
	private static EventBus eventBus;
	private static PlaceController placeController;
	
	protected MainView mainView;
	protected LoginView loginView;
	protected MenuView menuView;
	protected InsertView insertView;

	@Override
	public EventBus getEventBus() {
		if (eventBus == null) {
			eventBus = new SimpleEventBus();
		}
		return eventBus;
	}

	@Override
	public PlaceController getPlaceController() {
		if (placeController == null)
			placeController = new PlaceController(getEventBus());
		return placeController;
	}

	public MainView getMainView() {
		mainView = new MainView("start");
		return mainView;
	}
	
	public LoginView getLoginView() {
		loginView = new LoginView();
		return loginView;
	}
	
	public MenuView getMenuView() {
		menuView = new MenuView();
		return menuView;
	}
	
	public InsertView getInsertView() {
		insertView = new InsertView();
		return insertView;
	}


}
