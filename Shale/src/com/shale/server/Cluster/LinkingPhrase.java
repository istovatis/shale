package com.shale.server.Cluster;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class LinkingPhrase {

	String id;
	String label;
	List fromConcepts;
	List toConcepts;

	public LinkingPhrase(String num, String lbl) {
		this.id = num;
		this.label = lbl;
		this.fromConcepts = new ArrayList();
		this.toConcepts = new ArrayList();

	}

	public String toString() {

		String s = "LinkingPhrase details ";
		return s + "id:" + id + "," + "label: " + label;

	}

	public void showConcepts() {
		Iterator fromIter = fromConcepts.iterator();
		Iterator toIter = toConcepts.iterator();

		System.out.println("From Concepts: \n");
		while (fromIter.hasNext()) {
			Concept concept = (Concept) fromIter.next();
			System.out.println(concept.label);
		}

		System.out.println("To Concepts : \n");
		while (toIter.hasNext()) {
			Concept concept = (Concept) toIter.next();
			System.out.println(concept.label);
		}

		System.out.println("\n----------------------------");

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

}