package com.shale.client.app;


import com.orange.links.client.DiagramController;
import com.shale.client.login.LoginPlace;
import com.google.gwt.activity.shared.ActivityManager;
import com.google.gwt.activity.shared.ActivityMapper;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceController;
import com.google.gwt.place.shared.PlaceHistoryHandler;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Shale implements EntryPoint {

	DiagramController currentController = new DiagramController(400, 400);

	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {
		SimplePanel display = new SimplePanel();
		

		ClientFactory clientFactory = GWT.create(ClientFactory.class);

		EventBus eventBus = clientFactory.getEventBus();
		PlaceController placeController = clientFactory.getPlaceController();

		ActivityMapper activityMapper = new AppActivityMapper(clientFactory);
		ActivityManager activityManager = new ActivityManager(activityMapper,
				eventBus);
		activityManager.setDisplay(display);

		AppPlaceHistoryMapper historyMapper = GWT
				.create(AppPlaceHistoryMapper.class);
		PlaceHistoryHandler historyHandler = new PlaceHistoryHandler(
				(PlaceHistoryMapper) historyMapper);
		Place defaultPlace = new LoginPlace();
		historyHandler.register(placeController, eventBus, defaultPlace);

		RootPanel.get("UniqueID").add(display);
		

		historyHandler.handleCurrentHistory();

	}
}
