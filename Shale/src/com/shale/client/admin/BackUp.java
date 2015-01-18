package com.shale.client.admin;

import java.util.Date;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.shale.client.exporter.ExportService;
import com.shale.client.exporter.ExportServiceAsync;

/**
 * A Back Up tool provided only to Admins. folders and the files containing are
 * backed up. Essential folders such as "tei" and "anatolia" containing all
 * concept maps, so as teachers.cxl and students.cxl containing all users must
 * me backed up. The target location is se to server side @ExportServicempl
 * class as C:\\dastasvn\cmap. The folder name that contains all back up files
 * is the current date.
 * 
 * @author Istovatis -- istovatis@gmail.com --
 *
 */
public class BackUp {
	public String outDir;
	public String name;
	// number of complete back ups
	private int completeBackUps;
	// number of folders to back up
	private int folders;

	public int getFolders() { return folders; }
	public void setFolders(int folders) { this.folders = folders; }

	public int getCompleteBackUps() { return completeBackUps; }
	public void setCompleteBackUps(int completeBackUps) { this.completeBackUps = completeBackUps; }

	/**
	 * Increase the number of complete back ups
	 */
	public void increaseCompleteBackUps() {
		int increase = getCompleteBackUps() + 1;
		setCompleteBackUps(increase);
	}

	private ExportServiceAsync exportSvc = GWT.create(ExportService.class);

	public void backup() {
		setNow();
	}

	/**
	 * Set the current date as "YYYY"-"M"-"d".
	 */
	public void setNow() {
		String day = DateTimeFormat.getFormat("d").format(new Date());
		String month = DateTimeFormat.getFormat("M").format(new Date());
		String year = DateTimeFormat.getFormat("yyyy").format(new Date());
		this.name = year + "-" + month + "-" + day;
	}

	public String getNow() {
		return name;
	}

	/**
	 * Back up the given list of folders. Increase the number of complete back
	 * ups in case of success back up. If the numvber of complete back ups
	 * equals the given list, then all folders were successfully backed up.
	 * 
	 * @param folders
	 *            :The list of all folders that are set to back up
	 */
	public void backUpFolders(String[] folders) {
		setFolders(folders.length);
		setCompleteBackUps(0);
		// Initialize the service proxy.
		if (exportSvc == null) {
			exportSvc = GWT.create(ExportService.class);
		}
		for (String folder : folders) {
			// Set up the callback object.
			AsyncCallback<ExportService> callback = new AsyncCallback<ExportService>() {
				public void onFailure(Throwable caught) {
					Window.alert("Could not back up. Caused by: "
							+ caught.getMessage());
				}

				public void onSuccess(ExportService result) {
					increaseCompleteBackUps();
					if (getCompleteBackUps() == getFolders())
						Window.alert("Back up was successful.");
				}
			};
			String target = getNow();
			exportSvc.copyDirectory(folder, target, callback);
		}

	}
}
