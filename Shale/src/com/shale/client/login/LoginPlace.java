package com.shale.client.login;

import com.google.gwt.place.shared.Place;
import com.google.gwt.place.shared.PlaceTokenizer;

/**
 * The place that is correlated with the Login View 
 * @author Istovatis -- istovatis@gmail.com --
 *
 */
public class LoginPlace extends Place{
	
	private String loginToken;
	
	public LoginPlace(){}
	
	public LoginPlace(String token){
		this.loginToken=token;
	}
	
	public String getLoginToken()
	{
		return loginToken;
	}
	
	public static class Tokenizer implements PlaceTokenizer<LoginPlace>{
		
		public LoginPlace getPlace(String token){
			return new LoginPlace(token);
		}
		
		public String getToken(LoginPlace place){
			return place.getLoginToken();
		}
	}
}

