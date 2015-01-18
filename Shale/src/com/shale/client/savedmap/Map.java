package com.shale.client.savedmap;

/**
 * A map contains an author and a description
 * @author Istovatis -- istovatis@gmail.com --
 *
 */
public class Map {
	
	private String description;
	private static String author;
	
	public void setDescription(String desc) { description = desc; }	
	public String getDescription() { return description; }
	
	public static void setAuthor(String auth) { author = auth; }
	public static String getAuthor() { return author; }
	
}
