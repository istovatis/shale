package com.shale.client.app;

import com.google.gwt.activity.shared.Activity;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.place.shared.Place;

import com.shale.client.conceptmap.MainActivity;
import com.shale.client.conceptmap.MainPlace;
import com.shale.client.login.LoginActivity;
import com.shale.client.login.LoginPlace;
import com.shale.client.menu.MenuActivity;
import com.shale.client.menu.MenuPlace;
import com.shale.client.user.insert.InsertActivity;
import com.shale.client.user.insert.InsertPlace;

public class AppActivityMapper implements ActivityMapper {
	private ClientFactory clientFactory;

	AppActivityMapper(ClientFactory cf) {
		super();
		clientFactory = cf;
	}

	@Override
	public Activity getActivity(Place place) {
		if (place instanceof MainPlace)
			return new MainActivity(clientFactory);
		else if(place instanceof LoginPlace)
			return new LoginActivity(clientFactory);
		else if(place instanceof MenuPlace)
			return new MenuActivity(clientFactory);
		else if(place instanceof InsertPlace)
			return new InsertActivity(clientFactory);
		else
			return null;
	}
}
