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
package com.shale.client.exporter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Interface that is necessary for the RPC mechanism  
 * @author Istovatis -- istovatis@gmail.com --
 *
 */
@RemoteServiceRelativePath("ExportService")
public interface ExportService extends RemoteService {

	public void saveFile(String export,String dir, String fileName, String occasion);
	
	/**
	 * Rename an existing file, from oldUser-title to newUser-title.
	 */
	public void renameFile(String dir, String oldUser, String newUser, String title);
	
	public void copyDirectory(String sourceLocation, String targetLocation);
	
	/**
	 * Utility class for simplifying access to the instance of async service.
	 */
	public static class Util {
		private static ExportServiceAsync instance;

		public static ExportServiceAsync getInstance() {
			if (instance == null) {
				instance = GWT.create(ExportService.class);
			}
			return instance;
		}
	}
}
