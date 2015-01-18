package com.shale.client.Uima;

import java.util.ArrayList;

public class UimaResponse {
	
	private String query;	// Word to provide so as to find neighbor words
	public String getQuery() { return query; }
	public void setQuery(String query) { this.query = query; }

	private ArrayList<String> words;	
	public ArrayList<String> getWords() { return words; }
	public void setWords(ArrayList<String> words) { this.words = words;}
	
	private int k;	// number of similar words to find
	public int getK() { return k; }
	public void setK(int k) { this.k = k; }
	
}
