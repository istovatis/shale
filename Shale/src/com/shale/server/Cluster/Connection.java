package com.shale.server.Cluster;

/**
 * The connection representation in the server
 * @author Istovatis -- istovatis@gmail.com --
 *
 */
class Connection {

	String id;
	Concept concept;
	LinkingPhrase linkingPhrase;
	boolean fromConcept;
	boolean inProposition; // shows if this connection is part of oa proposition

	public Connection(String num, Concept c, LinkingPhrase lp, boolean fromC) {
		this.id = num;
		this.concept = c;
		this.linkingPhrase = lp;
		fromConcept = fromC;
		inProposition = false;
	}

	public String toString() {

		String s = "Connection details ";
		s = s + "id:" + id;

		if (fromConcept == true) {
			s = s + " from-id: [" + concept.id + "] [" + concept.label
					+ "] to-id --" + linkingPhrase.id + " --"
					+ linkingPhrase.label + "--";
			return s;
		}

		else {
			s = s + " from-id: --" + linkingPhrase.id + "-- --"
					+ linkingPhrase.label + "-- to-id  [" + concept.id + "] ["
					+ concept.label + "]";
			return s;

		}

	}

}