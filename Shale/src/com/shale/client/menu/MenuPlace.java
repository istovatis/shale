package com.shale.client.menu;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class MenuPlace extends Place {
	private String menuToken;
	private static String[] tokens;

	public MenuPlace() {}

	public MenuPlace(String token) {
		this.menuToken = token;
	}

	public MenuPlace(String[] tok) {
		tokens = tok;
	}

	public static String[] getMenuTokens() { return tokens; }

	public String getMenuToken() { return menuToken; }

	public static class Tokenizer implements PlaceTokenizer<MenuPlace> {

		public MenuPlace getPlace(String token) {
			return new MenuPlace(token);
		}

		public String getToken(MenuPlace place) {
			return place.getMenuToken();
		}
	}
}
