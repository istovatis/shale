package com.shale.server.Cluster;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The concept representation in the server
 * @author Istovatis -- istovatis@gmail.com --
 *
 */
class Concept {

	String id;
	String label;
	int x;
	int y;
	List inPhrases;
	List outPhrases;

	public Concept(String num, String lbl) {
		this.id = num;
		this.label = lbl;
		this.inPhrases = new ArrayList();
		this.outPhrases = new ArrayList();

	}

	public String toString() {

		String s = "Concept details ";
		return s + "id:" + id + "," + "label: " + label;

	}

	public void showPhrases() {
		Iterator inIter = inPhrases.iterator();
		Iterator outIter = outPhrases.iterator();

		System.out.println("Input Linking Phrases: \n");
		while (inIter.hasNext()) {
			LinkingPhrase phrase = (LinkingPhrase) inIter.next();
			System.out.println(phrase.label);
		}

		System.out.println("Output Linking Phrases: \n");
		while (outIter.hasNext()) {
			LinkingPhrase phrase = (LinkingPhrase) outIter.next();
			System.out.println(phrase.label);
		}

		System.out.println("\n----------------------------");

	}

	public void showLocation() {

		System.out.println("at (" + x + ", " + y + ")");
	}

	public String getLabel() {
		return label;
	}

	public String getId() {
		return id;
	}

	public void addInPhrases(Concept c) {
		inPhrases.add(c);
	}

	public void addOutPhrases(Concept c) {
		outPhrases.add(c);
	}

	public void setX(int xpos) {
		x = xpos;
	}

	public void setY(int ypos) {
		y = ypos;
	}

}