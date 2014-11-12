package com.shale.server.Cluster;

/**
 * Parses the diagram saved in .cxl file. Loads concepts, linking phrases,
 * propositions and calclulates maxCentralityConcepts.
 * 
 */

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import edu.uci.ics.jung.algorithms.layout.CircleLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.visualization.BasicVisualizationServer;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import org.apache.commons.collections15.Transformer;

import java.awt.Dimension;
import javax.swing.JFrame;

public class DomParserCM2 {

	// No generics
	List myConcepts;
	List myLinkingPhrases;
	List myConnections;
	List myPropositions;
	Document dom;

	int maxCentrality;
	List maxCentralityConcepts;
	int conceptCount;
	int wordCount;
	double avgWordCount;
	int linkingPhraseCount;
	int propositionCount;
	List rootConcepts;
	int subMapCount; // == root concepts count
	int rootChildCount;
	List orphans;
	int orphansCount;
	double avgLinkPhrasesPerConcept;
	double avgPropositionsPerConcept;
	List branches;

	public DomParserCM2() {
		// create a list to hold the employee objects
		myConcepts = new ArrayList();
		myLinkingPhrases = new ArrayList();
		myConnections = new ArrayList();
		myPropositions = new ArrayList();
		maxCentralityConcepts = new ArrayList();
		rootConcepts = new ArrayList();
		orphans = new ArrayList();
		branches = new ArrayList();
	}

	/**
	 * Parse the xml file and get the dom object in the initial version of
	 * ClusteringDemo, parseXMLFile was called in order to parse file, not the
	 * text of the file.
	 */
	public void buildLists(String filename) {
		parseXmlFileText(filename);

		// get each element and create anobject
		parseDocument4Concepts();
		parseDocument4LinkingPhrases();
		parseDocument4Connections();
		makePropositions();
		parseDocument4ConceptLocations();

	}

	public void buildCM(String filename) {
		buildLists(filename);
		setPhrasesToConcepts();
		setConceptsToPhrases();
	}

	private void parseXmlFile(String filename) {
		// get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {

			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			// parse using builder to get DOM representation of the XML file
			dom = db.parse(filename);

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private void parseXmlFileText(String filename) {
		// get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		try {

			// Using factory get an instance of document builder
			DocumentBuilder db = dbf.newDocumentBuilder();

			// Parse using builder to get DOM representation of the XML file
			// If you want to directly give the content of a file, you have to
			// give it an InputStream,
			// for example a ByteArrayInputStream.
			dom = db.parse(new InputSource(new ByteArrayInputStream(filename
					.getBytes("utf-8"))));

		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (SAXException se) {
			se.printStackTrace();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private void parseDocument4Concepts() {
		// get the root elememt
		Element docEle = dom.getDocumentElement();

		// get a nodelist of <concept> elements
		NodeList nl = docEle.getElementsByTagName("concept");
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {

				// get the concept element
				Element cl = (Element) nl.item(i);

				// get the Concept object
				Concept c = getConcept(cl);

				// add it to list
				myConcepts.add(c);
			}
		}
	}

	private void parseDocument4ConceptLocations() {
		Element docEle = dom.getDocumentElement();

		NodeList nl = docEle.getElementsByTagName("concept-appearance");
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {
				Element cl = (Element) nl.item(i);
				getConceptLocation(cl);
			}
		}
	}

	private void parseDocument4LinkingPhrases() {
		Element docEle = dom.getDocumentElement();

		NodeList nl = docEle.getElementsByTagName("linking-phrase");
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {

				// get the concept element
				Element lp1 = (Element) nl.item(i);

				// get the Concept object
				LinkingPhrase lp = getLinkingPhrase(lp1);

				// add it to list
				myLinkingPhrases.add(lp);
			}
		}
	}

	private void parseDocument4Connections() {
		// get the root elememt
		Element docEle = dom.getDocumentElement();

		// get a nodelist of <concept> elements
		NodeList nl = docEle.getElementsByTagName("connection");
		if (nl != null && nl.getLength() > 0) {
			for (int i = 0; i < nl.getLength(); i++) {

				Element cl = (Element) nl.item(i);

				Connection c = getConnection(cl);

				myConnections.add(c);
			}
		}
	}

	/**
	 * I take a concept element and read the values in, create a Concept object
	 * and return it
	 * 
	 * @param conceptEl
	 * @return
	 */
	private Concept getConcept(Element conceptEl) {

		// for each <employee> element get text or int values of
		// name ,id, age and name

		// for each <concept> element get text value of label

		String id = conceptEl.getAttribute("id");

		String label = conceptEl.getAttribute("label");

		// String label = getTextValue(conceptEl,"label");

		// Create a new Employee with the value read from the xml nodes
		Concept c = new Concept(id, label);

		return c;
	}

	private void getConceptLocation(Element appearEl) {
		String id = appearEl.getAttribute("id");
		String x = appearEl.getAttribute("x");
		String y = appearEl.getAttribute("y");

		Integer intX = new Integer(x);
		Integer intY = new Integer(y);

		int ix = intX.intValue();
		int iy = intY.intValue();

		Concept concept = getConceptById(id);
		concept.setX(ix);
		concept.setY(iy);

	}

	/**
	 * I take a concept element and read the values in, create a Concept object
	 * and return it
	 * 
	 * @param conceptEl
	 * @return
	 */
	private LinkingPhrase getLinkingPhrase(Element linkingPhraseEl) {

		String id = linkingPhraseEl.getAttribute("id");

		String label = linkingPhraseEl.getAttribute("label");

		LinkingPhrase lp = new LinkingPhrase(id, label);

		return lp;
	}

	private Connection getConnection(Element connectionEl) {

		Connection c = null;

		String id = connectionEl.getAttribute("id");

		String labelFrom = connectionEl.getAttribute("from-id");

		String labelTo = connectionEl.getAttribute("to-id");

		if (isConcept(labelFrom)) {
			Concept concept = getConceptById(labelFrom);
			LinkingPhrase linkingPhrase = getLinkingPhraseById(labelTo);
			c = new Connection(id, concept, linkingPhrase, true);
		} else {
			Concept concept = getConceptById(labelTo);
			LinkingPhrase linkingPhrase = getLinkingPhraseById(labelFrom);
			c = new Connection(id, concept, linkingPhrase, false);
		}

		return c;
	}

	public boolean isConcept(String id) {
		Iterator it = myConcepts.iterator();
		while (it.hasNext()) {
			Concept c = (Concept) it.next();
			if (c.id.equals(id))
				return true;
		}
		return false;
	}

	LinkingPhrase getLinkingPhraseById(String id) {
		Iterator it = myLinkingPhrases.iterator();
		while (it.hasNext()) {
			LinkingPhrase lp = (LinkingPhrase) it.next();
			if (lp.id.equals(id))
				return lp;
		}
		return null;
	}

	public void makePropositions() {
		Iterator it = myConnections.iterator();
		Proposition proposition = null;
		for (int j = 0; it.hasNext(); j++) {
			Connection connection1 = (Connection) it.next();
			Concept concept1 = connection1.concept;
			LinkingPhrase lp1 = connection1.linkingPhrase;
			int index = j;
			for (int i = index + 1; i < myConnections.size(); i++) {
				Connection connection2 = (Connection) myConnections.get(i);
				LinkingPhrase lp2 = connection2.linkingPhrase;
				if (lp1.id.equals(lp2.id)) {
					Concept concept2 = connection2.concept;
					if (connection1.fromConcept == true) {
						System.out.println("c1 " + concept1 + " c2 " + concept2
								+ " lp1" + lp1 + "ld id" + lp1.id);
						proposition = new Proposition(concept1, concept2, lp1);
						connection1.inProposition = true;
						connection2.inProposition = true;
						myPropositions.add(proposition);
					}
					if (connection2.fromConcept == true) {
						proposition = new Proposition(concept2, concept1, lp1);
						connection1.inProposition = true;
						connection2.inProposition = true;
						myPropositions.add(proposition);
					}

				}
			}

		}

	}

	Concept getConceptById(String id) {
		Iterator it = myConcepts.iterator();
		while (it.hasNext()) {
			Concept c = (Concept) it.next();
			if (c.id.equals(id))
				return c;
		}
		return null;
	}

	Concept getConceptByLabel(String label) {
		Iterator it = myConcepts.iterator();
		while (it.hasNext()) {
			Concept c = (Concept) it.next();
			if (c.label.equals(label))
				return c;
		}
		return null;
	}

	int getConceptPositionById(String id) {
		Iterator it = myConcepts.iterator();
		int i = 0;
		while (it.hasNext()) {
			Concept c = (Concept) it.next();
			i++;
			if (c.id.equals(id))
				return i;
		}
		return -1;
	}

	String getConceptLabelByPosition(int i) {
		if (i < 0 || i > myConcepts.size()) {
			System.out
					.println("in method getConceptLabelByPosision: index out of bounds: "
							+ i);
			System.exit(1);
		}

		Concept concept = (Concept) myConcepts.get(i - 1);
		return concept.label;
	}

	public int getConceptLocationYByPosition(int i) {
		Concept concept = (Concept) myConcepts.get(i - 1);
		return concept.y;
	}

	public int getConceptLocationXByPosition(int i) {
		Concept concept = (Concept) myConcepts.get(i - 1);
		return concept.x;
	}

	public String getLinkingPhraseByPosition(int i) {
		if (i < 0 || i > myPropositions.size()) {
			System.out
					.println("in method getConceptLabelByPosision: index out of bounds: "
							+ i);
			System.exit(1);
		}

		Proposition proposition = (Proposition) myPropositions.get(i - 1);
		LinkingPhrase linkingPhrase = proposition.linkingPhrase;
		return linkingPhrase.label;
	}

	/**
	 * I take a xml element and the tag name, look for the tag and get the text
	 * content i.e for <employee><name>John</name></employee> xml snippet if the
	 * Element points to employee node and tagName is name I will return John
	 * 
	 * @param ele
	 * @param tagName
	 * @return
	 */
	private String getTextValue(Element ele, String tagName) {
		String textVal = null;
		NodeList nl = ele.getElementsByTagName(tagName);
		if (nl != null && nl.getLength() > 0) {
			Element el = (Element) nl.item(0);
			textVal = el.getFirstChild().getNodeValue();
		}

		return textVal;
	}

	/**
	 * Calls getTextValue and returns a int value
	 * 
	 * @param ele
	 * @param tagName
	 * @return
	 */
	private int getIntValue(Element ele, String tagName) {
		// in production application you would catch the exception
		return Integer.parseInt(getTextValue(ele, tagName));
	}

	/**
	 * Iterate through the list and print the content to console
	 */
	public void printData() {

		System.out.println("No of Concepts '" + myConcepts.size() + "'.");

		Iterator it = myConcepts.iterator();
		int i = 0;
		while (it.hasNext()) {
			i++;
			Concept concept = (Concept) it.next();
			System.out.println(" " + i + " " + concept.toString());
			concept.showPhrases();
			concept.showLocation();

		}

		it = myLinkingPhrases.iterator();
		while (it.hasNext()) {
			LinkingPhrase phrase = (LinkingPhrase) it.next();
			System.out.println(phrase.toString());
			phrase.showConcepts();

		}

		System.out.println("\n\nNo of Linking Phrases '"
				+ myLinkingPhrases.size() + "'.");

		it = myLinkingPhrases.iterator();
		while (it.hasNext()) {
			System.out.println(it.next().toString());
		}

		System.out.println("\n\nNo of Connections '" + myConnections.size()
				+ "'.");

		it = myConnections.iterator();
		while (it.hasNext()) {
			System.out.println(it.next().toString());
		}

		System.out.println("\n\nNo of Propositions '" + myPropositions.size()
				+ "'.");

		it = myPropositions.iterator();
		while (it.hasNext()) {
			System.out.println(it.next().toString());
		}

		System.out.println("*Vertices  " + myConcepts.size());

		Iterator itC = myConcepts.iterator();
		int k = 0;
		while (itC.hasNext()) {
			k++;
			Concept concept = (Concept) itC.next();
			System.out.println(" " + k + "   \"" + concept.label + "\"");

		}

		System.out.println("\n\n*Edges     " + myPropositions.size());

		it = myPropositions.iterator();
		int j = 0;
		while (it.hasNext()) {
			j++;
			Proposition prop = (Proposition) it.next();
			Concept fromC = (Concept) prop.fromConcept;
			Concept toC = (Concept) prop.toConcept;
			System.out.println(" " + j + " " + getConceptPositionById(fromC.id)
					+ " " + getConceptPositionById(toC.id) + " 1.0");

		}

		System.out.println("\n\nNo of Branches'" + branches.size() + "'.");

		it = branches.iterator();
		while (it.hasNext()) {
			System.out.println(it.next().toString());
		}

	}

	/**
	 * For each Concept fills-in the fields inPhrases, outPhrases Logic: Scans
	 * the list of connections. For each connection: locates the concept of the
	 * connection if (fromConcept is true) the connection is added in
	 * concept.outPhrases else connection is added in concept.inPhrases
	 */

	public void setPhrasesToConcepts() {
		Iterator it = myConnections.iterator();
		while (it.hasNext()) {
			Connection connection = (Connection) it.next();
			Concept concept = connection.concept;
			if (connection.fromConcept)
				concept.outPhrases.add(connection.linkingPhrase);
			else
				concept.inPhrases.add(connection.linkingPhrase);
		}
	}

	/**
	 * For each LinkingPhrase fills-in the fields inConcepts, outConcepts Logic:
	 * Scans the list of connections. For each connection: locates the linking
	 * phrase of the connection if (fromConcept is true) the concept is added in
	 * linkingPhrase.fromConcept else concept is added in
	 * linkingPhrase.toConcept
	 */
	public void setConceptsToPhrases() {
		Iterator it = myConnections.iterator();
		while (it.hasNext()) {
			Connection connection = (Connection) it.next();
			LinkingPhrase phrase = connection.linkingPhrase;
			if (connection.fromConcept)
				phrase.fromConcepts.add(connection.concept);
			else
				phrase.toConcepts.add(connection.concept);
		}
	}

	public void setBranches() {
		Iterator it = myConcepts.iterator();
		while (it.hasNext()) {
			Concept concept = (Concept) it.next();
			if (concept.inPhrases.size() >= 1 && concept.outPhrases.size() >= 2)
				branches.add(concept.label);
		}

		it = myLinkingPhrases.iterator();
		while (it.hasNext()) {
			LinkingPhrase phrase = (LinkingPhrase) it.next();
			if (phrase.fromConcepts.size() >= 1
					&& phrase.toConcepts.size() >= 2)
				branches.add(phrase.label);
		}

	}

	public void setRootConcepts() {
		Iterator it = myConcepts.iterator();
		while (it.hasNext()) {
			Concept concept = (Concept) it.next();
			if (concept.inPhrases.size() == 0 && concept.outPhrases.size() > 0)
				rootConcepts.add(concept);
		}
	}

	public int getCentrality(int ci) {
		Concept con = (Concept) myConcepts.get(ci);
		int centrality = 0;

		Iterator it = myPropositions.iterator();
		while (it.hasNext()) {
			Proposition p = (Proposition) it.next();
			Concept fromC = p.fromConcept;
			Concept toC = p.toConcept;
			if (fromC == con || toC == con)
				centrality++;
		}
		return centrality;
	}

	public int getCentrality(Concept con) {
		int centrality = 0;

		Iterator it = myPropositions.iterator();
		while (it.hasNext()) {
			Proposition p = (Proposition) it.next();
			Concept fromC = p.fromConcept;
			Concept toC = p.toConcept;
			if (fromC == con || toC == con)
				centrality++;
		}
		return centrality;
	}

	public int getCentrality(String label) {
		Concept con = (Concept) getConceptByLabel(label);
		int centrality = 0;

		Iterator it = myPropositions.iterator();
		while (it.hasNext()) {
			Proposition p = (Proposition) it.next();
			Concept fromC = p.fromConcept;
			Concept toC = p.toConcept;
			if (fromC == con || toC == con)
				centrality++;
		}
		return centrality;
	}

	public List getMaxCentralityConcepts() {
		int maxCent = 0;
		Concept maxCon = null;
		List maxConList = new ArrayList();

		for (int i = 0; i < myConcepts.size(); i++) {
			Concept con = (Concept) myConcepts.get(i);
			int cent = getCentrality(con);
			if (cent > maxCent) {
				maxCent = cent;
				maxCon = con;
				maxConList.removeAll(maxConList);
				maxConList.add(maxCon);
			}
			if (cent == maxCent) {
				Iterator it = maxConList.iterator();
				while (it.hasNext()) {
					Concept tc = (Concept) it.next();
					if (tc != maxCon)
						maxConList.add(maxCon);
				}
			}
		}
		return maxConList;
	}

	public int getMaxCentrality() {

		Concept con = (Concept) maxCentralityConcepts.get(0);
		return getCentrality(con);

	}

	public void setConceptCount() {
		conceptCount = getConceptCount();
	}

	public int getConceptCount() {
		conceptCount = myConcepts.size();
		return conceptCount;
	}

	public int getConceptWordCount(Concept c) {
		String str[] = c.label.split(" ");
		return str.length;
	}

	public int getConceptsWordCount() {
		int wc = 0;
		Iterator it = myConcepts.iterator();
		while (it.hasNext()) {
			Concept con = (Concept) it.next();
			wc += getConceptWordCount(con);
		}
		return wc;
	}

	public int getLinkingPhraseCount() {
		return myLinkingPhrases.size();
	}

	public void setLinkingPhraseCount() {
		linkingPhraseCount = getLinkingPhraseCount();
	}

	public int getPropositionCount() {
		return myPropositions.size();
	}

	public void setPropositionCount() {
		propositionCount = getPropositionCount();
	}

	public int getRootChildCount(Concept root) {
		int rcc = 0;
		Iterator it = myPropositions.iterator();
		while (it.hasNext()) {
			Proposition proposition = (Proposition) it.next();
			Concept fromConcept = proposition.fromConcept;
			if (fromConcept.equals(root))
				rcc++;
		}
		return rcc;
	}

	public int getRootChildCount() {
		int rcc = 0;
		Iterator it = rootConcepts.iterator();
		while (it.hasNext()) {
			Concept root = (Concept) it.next();
			rcc += getRootChildCount(root);
		}
		return rcc;

	}

	public void setOrphans() {
		Iterator it = myConcepts.iterator();
		while (it.hasNext()) {
			Concept concept = (Concept) it.next();
			if (concept.inPhrases.size() == 0 && concept.outPhrases.size() == 0) {
				orphans.add(concept);
				orphansCount++;
			}
		}

	}

	public int getSubMapCount() {
		return rootConcepts.size();
	}

	public void setAvgLinkPhrasesPerConcept() {
		if (linkingPhraseCount != 0 && conceptCount != 0)
			avgLinkPhrasesPerConcept = 1.0 * linkingPhraseCount / conceptCount;

	}

	public void setAvgPropositionPerConcept() {
		if (propositionCount != 0 && conceptCount != 0)
			avgPropositionsPerConcept = 1.0 * propositionCount / conceptCount;

	}

	// JUNG-related module
	// Modify graph to save concepts' ids.
	// Create a directed or undirected graph 
	public Graph<Integer, Integer> buildCM4Jung() {
		Graph<Integer, Integer> g;
		g = new SparseMultigraph<Integer, Integer>();

		Iterator it = myConcepts.iterator();
		while (it.hasNext()) {
			Concept concept = (Concept) it.next();
			g.addVertex(new Integer(concept.getId()));
		}
		it = myPropositions.iterator();
		int j = 0;
		while (it.hasNext()) {
			j++;
			Proposition prop = (Proposition) it.next();
			Concept fromC = (Concept) prop.fromConcept;
			Concept toC = (Concept) prop.toConcept;
			LinkingPhrase lp = prop.getLinkingPhrase();
			g.addEdge(new Integer(lp.getId()), new Integer(fromC.id),
					new Integer(toC.id), Cluster.getGraphtype());
		}

		it = myConnections.iterator();
		int i = 0;

		return g;
	}

	public void showGraph() {
		Graph<Integer, Integer> g;
		g = buildCM4Jung();

		Layout<Number, Number> layout = new CircleLayout(g);
		layout.setSize(new Dimension(300, 300)); // sets the initial size of the
													// layout space
		// The BasicVisualizationServer<V,E> is parameterized by the vertex and
		// edge types
		BasicVisualizationServer<Number, Number> vv = new BasicVisualizationServer<Number, Number>(
				layout);
		vv.setPreferredSize(new Dimension(350, 350)); // Sets the viewing area
														// size
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller()); // added
																					// by
																					// vangelis

		Transformer<Number, String> vertexLabel = new Transformer<Number, String>() {
			public String transform(Number i) {
				return getConceptLabelByPosition(i.intValue());
			}
		};

		vv.getRenderContext().setVertexLabelTransformer(vertexLabel);

		JFrame frame = new JFrame("Simple Graph View");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().add(vv);
		frame.pack();
		frame.setVisible(true);
	}

	// end of JUNG-related module

	public static void main(String[] args) {
		// create an instance
		DomParserCM2 dpCM = new DomParserCM2();

		// call run example
		// System.out.println (args[0]);
		dpCM.buildCM("CXL_Files\\cricket 2.1.cmap.cxl");
		// dpCM.printData();

		for (int i = 0; i < dpCM.myConcepts.size(); i++) {
			Concept con = (Concept) dpCM.myConcepts.get(i);
			System.out.println("Centrality (" + con.getLabel() + ") = "
					+ dpCM.getCentrality(con));
		}

		dpCM.maxCentralityConcepts = dpCM.getMaxCentralityConcepts();
		dpCM.maxCentrality = dpCM
				.getCentrality((Concept) dpCM.maxCentralityConcepts.get(0));

		System.out.println("\n max Centrality Concepts are: ");

		Iterator it = dpCM.maxCentralityConcepts.iterator();
		while (it.hasNext()) {
			Concept con = (Concept) it.next();
			System.out.println(" " + con.label + " ");
		}

		System.out.println(" with centrality: " + dpCM.maxCentrality);

		dpCM.wordCount = dpCM.getConceptsWordCount();

		System.out.println("\n Total number of words in Concepts: "
				+ dpCM.wordCount);

		dpCM.avgWordCount = 1.0 * dpCM.wordCount / dpCM.myConcepts.size();

		System.out
				.println("\n Average words per concept: " + dpCM.avgWordCount);

		dpCM.linkingPhraseCount = dpCM.getLinkingPhraseCount();
		System.out.println("\n Total number of linking phrases: "
				+ dpCM.linkingPhraseCount);

		dpCM.propositionCount = dpCM.getPropositionCount();
		System.out.println("\n Total number of propositions: "
				+ dpCM.propositionCount);

		dpCM.setRootConcepts();
		System.out.println("\n ROOT CONCEPTS: ");
		it = dpCM.rootConcepts.iterator();
		while (it.hasNext()) {
			Concept con = (Concept) it.next();
			System.out.println(" " + con.label + " ");
		}

		dpCM.rootChildCount = dpCM.getRootChildCount();
		System.out.println("\nRoot child count : " + dpCM.rootChildCount);

		dpCM.setOrphans();
		System.out.println("\nOrphans Count = " + dpCM.orphansCount);

		dpCM.subMapCount = dpCM.getSubMapCount();
		System.out.println("\nSub map count = " + dpCM.subMapCount);

		dpCM.setConceptCount();
		dpCM.setPropositionCount();
		dpCM.setAvgLinkPhrasesPerConcept();
		System.out.println("\nAverage LinkingPhrases per Concept: "
				+ dpCM.avgLinkPhrasesPerConcept);

		dpCM.setAvgPropositionPerConcept();
		System.out.println("\nAverage Proposition per Concept: "
				+ dpCM.avgPropositionsPerConcept);

		dpCM.setBranches();
		System.out.println("\nBranches count = " + dpCM.branches.size());
		dpCM.printData();

		dpCM.showGraph();

	}

}
