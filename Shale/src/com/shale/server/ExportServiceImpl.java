/*
 * Copyright 2011 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.shale.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.channels.FileChannel;

import com.shale.client.exporter.ExportService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class ExportServiceImpl extends RemoteServiceServlet implements
		ExportService {

	/**
	 * Saves export data to fileName.
	 * 
	 */
	public void saveFile(String export, String dir, String fileName,
			String occasion) {
		String slash = File.separator;
		fileName = fileName + ".cxl";
		String url = null;
		if (occasion.equals("saveMeta")) {
			fileName = "meta-"+fileName;
			url = getServletContext().getRealPath(dir + slash + "metadata");
		} else
			url = getServletContext().getRealPath(dir);
		url = url + slash;
		// System.out.println("From:saveFile url:"+url+" filename"+fileName);
		File exportFile = new File(url + fileName);
		try {
			exportFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(exportFile), "UTF-8"));
			out.write(export);
			out.close();

		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void renameFile(String dir, String oldUser, String newUser,
			String title) {
		String slash = File.separator;
		String url = getServletContext().getRealPath(dir) + slash;

		String oldName = oldUser + "-" + title;
		String newName = newUser + "-" + title;
		System.out.println(oldName + "new" + newName);
		// Obtain the reference of the existing file
		File oldFile = new File(url + oldName);
		// Now invoke the renameTo() method on the reference, oldFile in this
		// case
		oldFile.renameTo(new File(url + newName));
	}

	/**
	 * Copy source directory. Get all directory children, that may be files of
	 * folders. In case of folder get the children of this folder. In case of
	 * file, transfer the file data to target file.
	 */
	public void copyDirectory(String source, String target) {
		String slash = File.separator;
		String realSource = getServletContext().getRealPath(source);
		File sourceLocation = new File(realSource);
		File targetLocation = new File("C:" + slash + "CMAP-backup" + slash
				+ target, source);

		if (sourceLocation.isDirectory()) {
			// If targetLocation does not exist, it will be created.
			if (!targetLocation.exists()) {
				if (!new File("C:" + slash + "CMAP-backup" + slash + target)
						.mkdir())
					;
				targetLocation.mkdir();
			}
			String[] children = sourceLocation.list();

			for (int i = 0; i < children.length; i++) {
				copyDirectory(source + slash + children[i], target);
				// System.out.println("source:"+source+"target"+target+" child"+children[i]);
			}
		} else {
			try {
				FileChannel ic = new FileInputStream(sourceLocation).getChannel();
				FileChannel oc = new FileOutputStream(targetLocation)
						.getChannel();
				ic.transferTo(0, ic.size(), oc);
				//System.out.println("!!!"+sourceLocation.getAbsolutePath().toString());
				//System.out.println("@@@"+sourceLocation.getAbsolutePath().toString());
				ic.close();
				oc.close();
			} catch (IOException e) {
				System.out.println("IOException occured: " + e.getMessage().toString());
			}
		}
	}

}
