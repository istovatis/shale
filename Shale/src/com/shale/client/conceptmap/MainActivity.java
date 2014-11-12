package com.shale.client.conceptmap;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import com.shale.client.app.ClientFactory;

public class MainActivity extends AbstractActivity {
	ClientFactory clientFactory;
	MainView mainView;

	public MainActivity(ClientFactory cf) {
		clientFactory = cf;
	}

	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		mainView = clientFactory.getMainView();
		panel.setWidget(mainView.asWidget());
		mainView.setPresenter(this);
	}

	public void goTo(Place place) {
		try {
			clientFactory.getPlaceController().goTo(place);
		} catch (Exception e) {
			Window.alert("A factory Problem Occured:" + e.getMessage());
		}
	}
}
