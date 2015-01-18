package com.shale.server;

import rita.RiWordNet;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.shale.client.semantics.WordNetService;
import com.shale.shared.FieldVerifier;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class WordNetServiceImpl extends RemoteServiceServlet implements
		WordNetService {

	public String greetServer(String concepts, String rootw, String radius)
			throws IllegalArgumentException {
		RiWordNet wordnet = new RiWordNet(null);

		String[] mapc = concepts.split("\\n");
		float distance1s = 0;
		float distance2s = 0;
		float distance3s = 0;
		String[] parentss = null;
		float target = 0;
		float totald = 0;
		String allpos = "n";
		String poss1 = null;
		String poss2 = null;
		String poss3 = null;
		String allresults = "";
		for (int i = 0; i < mapc.length; i++)
			for (int j = i + 1; j < mapc.length; j++) {

				poss1 = wordnet.getBestPos(mapc[i]);
				poss2 = wordnet.getBestPos(mapc[j]);
				poss3 = wordnet.getBestPos(rootw);
				parentss = wordnet.getCommonParents(mapc[i], mapc[j], poss3);

			}
		
		for (int i = 0; i < mapc.length; i++)
			for (int j = i + 1; j < mapc.length; j++) {

				if (parentss != null || parentss.length != 0) {
					for (int k = 0; k < parentss.length; k++) {
						distance1s = wordnet.getDistance(mapc[i], parentss[k],
								poss1);
						distance2s = wordnet.getDistance(mapc[j], parentss[k],
								poss2);
						distance3s = wordnet.getDistance(parentss[k], rootw,
								poss3);
						target = 2 * distance3s / (2 * distance3s + distance1s + distance2s);
						if (target < Float.parseFloat(radius)) {

							allresults += "The link between these concepts is included in the map: ";
							allresults += "<b>" + mapc[i] + "</b>" + " and <b>"
									+ mapc[j] + " </b> and the metric id is: "
									+ "<b>" + target + "</b>. <br />";
							allresults += " The common parent(s) is(are): "
									+ parentss[k] + " <br />";

						}
						totald += target;
					}
				}

				else 
					allresults = "No common parents identified betweeen "
							+ mapc[i] + " and " + mapc[j] + "<br />";
			}

		allresults += "Root is <b> " + rootw
				+ " .</b><br />The total d is : <b>" + totald + ".</b>";

		return allresults;
	}

	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html
	 *            the html string to escape
	 * @return the escaped string
	 */
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}

	/**
	 * If the input is not valid, Throwing an IllegalArgumentException back to
	 * the client when input text does not pass validation
	 */
	public void verifyInput(String input) {
		if (!FieldVerifier.isValidName(input)) {
			throw new IllegalArgumentException(
					"Name must be at least 4 characters long");
		}

		String serverInfo = getServletContext().getServerInfo();
		String userAgent = getThreadLocalRequest().getHeader("User-Agent");

		// Escape data from the client to avoid cross-site script
		// vulnerabilities.
		input = escapeHtml(input);
		userAgent = escapeHtml(userAgent);
	}
}
