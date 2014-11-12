package com.shale.client.user.insert;

import com.google.gwt.activity.shared.AbstractActivity;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.place.shared.Place;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.shale.client.app.ClientFactory;

public class InsertActivity extends AbstractActivity{
	ClientFactory clientFactory;
	InsertView insertView;

	public InsertActivity(ClientFactory cf) {
		clientFactory = cf;
	}

	public void start(AcceptsOneWidget panel, EventBus eventBus) {
		insertView = clientFactory.getInsertView();
		panel.setWidget(insertView.asWidget());
		insertView.setPresenter(this);
	}

	public void goTo(Place place) {
		try {
			clientFactory.getPlaceController().goTo(place);
		} catch (Exception e) {
			Window.alert("A factory Problem Occured:" + e.getMessage());
		}
	}
}
