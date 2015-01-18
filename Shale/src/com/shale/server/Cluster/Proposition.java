package com.shale.server.Cluster;

/**
 * The proposition representation in server side
 * @author Istovatis -- istovatis@gmail.com --
 *
 */
class Proposition {

	String id;
	Concept fromConcept;
	Concept toConcept;
	LinkingPhrase linkingPhrase;

	public LinkingPhrase getLinkingPhrase() {
		return linkingPhrase;
	}

	public void setLinkingPhrase(LinkingPhrase linkingPhrase) {
		this.linkingPhrase = linkingPhrase;
	}

	Proposition(Concept from, Concept to, LinkingPhrase phrase) {
		this.fromConcept = from;
		this.toConcept = to;
		this.linkingPhrase = phrase;
	}

	public String getId() { return id; }
	public void setId(String id) { this.id = id; }
	
	public Concept getToConcept() { return toConcept; }
	public void setToConcept(Concept toConcept) { this.toConcept = toConcept; }
	
	public Concept getFromConcept() { return fromConcept; }
	public void setFromConcept(Concept fromConcept) { this.fromConcept = fromConcept; }

	public String toString() {

		String s = "Proposition: ";
		s = s + "id:" + id;

		s = s + "[" + fromConcept.label + "]" + " --" + linkingPhrase.label
				+ "--" + "[" + toConcept.label + "]";
		return s;

	}

}