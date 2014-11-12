package com.shale.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.ArrayList;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.shale.client.importer.ImportService;
import com.shale.server.Cluster.Cluster;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;

public class ImportServiceImpl extends RemoteServiceServlet implements
		ImportService {

	private boolean restart = false;

	/**
	 * (non-Javadoc)
	 * 
	 * @see com.shale.client.importer.ImportService#importDiagram(java.lang.String[],
	 *      boolean)
	 * 
	 *      Imports the diagram. Check the name of the file. If the imported
	 *      file is made by admin, then construct the fileName. Or else set the
	 *      complete file path.
	 */
	public String importDiagram(String user, String title, String org) {
		String fileName = null;
		fileName = user + "-" + title;
		// Locate the organisation dir
		String dir = org;
		String occasion = "diagram";
		String fileXml = getFileText(dir, fileName, occasion);
		return fileXml;
	}

	/**
	 * New adaptation of importing a diagram, only by giving the filename and
	 * the directory
	 */
	public String importDiagram(String fileName, String dir) {
		String occasion = "diagram";
		System.out.println(fileName + " " + dir);
		String fileXml = getFileText(dir, fileName, occasion);
		return fileXml;
	}

	@Override
	public String importMetadata(String fileName, String org) {
		String occasion = "metadata";
		fileName = "meta-" + fileName;
		String dir = org + File.separator + occasion;
		String fileXml = getFileText(dir, fileName, occasion);
		return fileXml;
	}

	/**
	 * Find and return all concept maps saved by user and admin. The directory
	 * of the file depends on the organisation.
	 */
	/*
	 * public String[] fileFinder(String[] tokens) { // Gets the directory from
	 * the organisation String url = getServletContext().getRealPath(tokens[1]);
	 * Files file = new Files(tokens[0], url); String directory =
	 * file.getDirectory(); File dir; dir = new File(directory); final String
	 * user = tokens[0] + "-"; return dir.list(new FilenameFilter() { public
	 * boolean accept(File dir, String filename) { return
	 * (filename.startsWith(user) || filename .startsWith("admin")); } }); }
	 */

	public String[] fileFinder(String username, String organisation) {
		// Gets the directory from the organisation
		String url = getServletContext().getRealPath(organisation);
		Files file = new Files(username, url);
		String directory = file.getDirectory();
		File dir;
		dir = new File(directory);
		final String user = username + "-";
		return dir.list(new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				return (filename.startsWith(user));
			}
		});
	}

	/**
	 * Retrieve text from fileName. If the fileName is "description.cxl" modify
	 * the fileName
	 * 
	 * @throws UnsupportedEncodingException
	 * 
	 */
	public String getFileText(String dir, String fileName, String occasion) {
		try {
			String path = getServletContext().getRealPath("dict")+File.separator;
			WordNet word = new WordNet(path);
			word.testDictionary(path);
			//word.getSynonyms("sales");
			//System.out.println("-----------");
			//word.getHypernyms("sales");
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		String slash = File.separator;
		fileName = fileName + ".cxl";
		String parsedText = null;
		String url = null;
		BufferedReader inputStream = null;
		// Files file = new Files(fileName, dir);
		// fileName = getServletContext().getRealPath(file.getFullPath());
		if (occasion.equals("descName")) {
			url = getServletContext().getRealPath(dir + slash + "descriptions");
		} else
			url = getServletContext().getRealPath(dir);
		url = url + slash;
		File file = new File(url + fileName);
		try {
			try {
				inputStream = new BufferedReader(new InputStreamReader(
						new FileInputStream(file), "UTF8"));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				System.out.println(e.getLocalizedMessage() + e.getStackTrace());
			}
			String text;
			try {
				while ((text = inputStream.readLine()) != null) {
					if (parsedText == null) {
						parsedText = text;
					} else
						parsedText = parsedText + text;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			System.out.println(e.getLocalizedMessage() + e.getStackTrace());
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println(e.getLocalizedMessage()
							+ e.getStackTrace());
				}
			}
		}
		return parsedText;
	}

	/**
	 * This method communicates with client.
	 */
	public ArrayList<ArrayList<Integer>> getVertices(String fileName,
			String dir, int clusters, int currentGraphs, boolean increase) {
		String occasion = "diagram";
		String fileXml = getFileText(dir, fileName, occasion);

		Cluster cluster = new Cluster();
		cluster.createClusters(fileXml);
		cluster.setFileXML(fileXml);
		cluster.manageGraph(clusters, currentGraphs, increase);
		System.out.println(cluster.getSubGraphsNum() + " clusters from server");
		// System.out.println("Clusters"+cluster.getClusters().size());

		return cluster.getClusters();
	}

	public boolean getRestart() {
		return restart;
	}

	public void setRestart(boolean restart) {
		this.restart = restart;
	}
	
}
