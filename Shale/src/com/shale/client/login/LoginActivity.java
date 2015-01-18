package com.shale.client.login;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.shale.client.app.ClientFactory;

/**
 * Login Activity. Its main purpose is the redirection to the assigned place.
 * @author Istovatis -- istovatis@gmail.com --
 *
 */
public class LoginActivity extends AbstractActivity{
	
	ClientFactory clientFactory;
	LoginView loginView;
	
	public LoginActivity(ClientFactory cf){
		clientFactory=cf;
	}
	
	public void start(AcceptsOneWidget panel, EventBus eventBus){
		loginView = clientFactory.getLoginView(); 
		panel.setWidget(loginView.asWidget());
		loginView.setPresenter(this);
	}
	
	public void goTo(Place place) {
		try {
			clientFactory.getPlaceController().goTo(place);
		} catch (Exception e) {
			Window.alert("A factory Problem Occured:"+e.getMessage());
		}
	}
}
