package com.shale.client.menu;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.shale.client.app.ClientFactory;

public class MenuActivity extends AbstractActivity {
	
	ClientFactory clientFactory;
	MenuView menuView;
	
	public  MenuActivity() {}
	
	public MenuActivity(ClientFactory cf){
		clientFactory=cf;
	}
	
	public void start(AcceptsOneWidget panel, EventBus eventBus){
		menuView = clientFactory.getMenuView(); 
		panel.setWidget(menuView.asWidget());
		menuView.setPresenter(this);
	}
	
	public void goTo(Place place) {
		try {
			clientFactory.getPlaceController().goTo(place);
		} catch (Exception e) {
			Window.alert("A factory Problem Occured:"+e.getMessage());
		}
	}
	
}
