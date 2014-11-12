package com.shale.client.user.insert;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

public class InsertPlace extends Place {
	private String insertToken;

	public InsertPlace() {
	}

	public InsertPlace(String token) {
		this.insertToken = token;
	}

	public String getInsertToken() {
		return insertToken;
	}

	public static class Tokenizer implements PlaceTokenizer<InsertPlace> {

		public InsertPlace getPlace(String token) {
			return new InsertPlace(token);
		}

		public String getToken(InsertPlace place) {
			return place.getInsertToken();
		}
	}
}
