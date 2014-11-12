package com.shale.client.conceptmap;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.user.client.Window;
import com.shale.client.app.ClientFactory;

public class MainPlace extends Place {
	private static String mainName;
	private static String[] tokens;

	ClientFactory clientFactory;

	public MainPlace() {
	}

	public MainPlace(String token) {
		this.mainName = token;
	}
	
	public MainPlace(String [] tok){
		tokens = tok;
	}
	
	public static String [] getMainTokens(){
		return tokens;
	}
	
	public static String getMain() {
		return mainName;
	}

	public static class Tokenizer implements PlaceTokenizer<MainPlace> {

		private static String token;

		public MainPlace getPlace(String token) {
			return new MainPlace(token);
		}

		public String getToken(MainPlace place) {
			return place.getMain();
		}

		public String getToken() {
			return this.getToken();
		}

		public void showToken() {
			Window.alert("token: " + this.getToken());
		}
	}
}
