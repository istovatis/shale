package com.shale.client.Uima;

import java.util.ArrayList;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.http.client.Response;

/**
 * Committing a POST Rest call by providing a word and the number of neighbor words to search for
 * and getting the respective answer from the Uima Service
 * @author Istovatis -- istovatis@gmail.com --
 *
 */
public class NeighborWordsCollector {
	private String word;	// Word to provide so as to find neighbor words
	private int numWords;	// number of similar words to find
	private final static String serviceLocation = "http://83.212.121.195:8080/StructedInfoService-0.0.1-SNAPSHOT/resources/wordTie/topk/"; 
	
	public String getWord() { return word; }
	public void setWord(String word) { this.word = word; }
	
	public int getNumWords() { return numWords; }
	public void setNumWords(int numWords) { this.numWords = numWords; }
	
	public NeighborWordsCollector() {
		numWords = 4;
	}
	
	public NeighborWordsCollector(String word) {
		numWords = 4;
		this.word = word;
	}
	
	public ArrayList<String> findNeighors() {
		askForNeighbors();
		return new ArrayList<String>();
	}
	
	public void askForNeighbors() {
		askServer(request(), "", handleResponse);
	}
	
	private String request() {
		return serviceLocation + word + "/" + numWords;
	}
	

	public void askServer(String apiUrl, String requestData,
			RequestCallback callBack) {
		RequestBuilder builder = new RequestBuilder(RequestBuilder.POST, URL.encode(apiUrl));
		try {
			builder.setHeader("Content-Type", "application/json");
			builder.setHeader("Access-Control-Allow-Origin", "*");
			builder.setHeader("Access-Control-Allow-Methods",
		            "POST, GET, OPTIONS, DELETE");
			builder.setHeader("Access-Control-Max-Age", "3600");
			builder.setHeader("Access-Control-Allow-Headers", "x-requested-with");
			builder.sendRequest(null, callBack);
			
			
			
		} catch (RequestException e) { 
			System.out.println("Couldn't connect to server. Reason: " + e.getMessage());
		}
	}
	
	private RequestCallback handleResponse = new RequestCallback() {
		public void onError(Request request, Throwable e) {
			System.out.println(e.getMessage());
		}

		public void onResponseReceived(Request request, Response response) {
			String respText;
			if (200 == response.getStatusCode()) {
				respText = response.getText();
			} else {
				respText = "Received HTTP status code other than 200: "
						+ response.getStatusText();
			}
			System.out.println(respText + response.getText());
		}
	};
	
//	protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//	    response.addHeader("Access-Control-Allow-Origin", "*");
//	    response.addHeader("Access-Control-Allow-Methods", "POST, GET");
//	}
	
}
