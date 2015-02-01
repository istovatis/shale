package com.shale.client.utils;

import com.google.gwt.i18n.client.Dictionary;
import com.shale.client.conceptmap.MainView;
import com.shale.client.login.LoginView;
import com.shale.client.menu.MenuView;
import com.shale.client.user.User;
import com.shale.client.user.insert.InsertView;

/**
 * Setting the dictionary comprising English and Greek language. 
 * The definition and functionality of Dictionary are provided by i18n library.
 * @author Istovatis -- istovatis@gmail.com --
 *
 */
public class Languages {

	private static Dictionary dictionary;
	private static Dictionary msgs;
	private static boolean isLoaded;

	public static void setDictionary(Dictionary dict) { dictionary = dict; }
	public static Dictionary getDictionary() { return dictionary; }

	public static void setMsgs(Dictionary err) { msgs = err; }
	public static Dictionary getMsgs() { return msgs; }
	
	public static boolean isLoaded() { return isLoaded; }
	public static void setLoaded(boolean isLoaded) { Languages.isLoaded = isLoaded; }

	/**
	 * Sets the organisation name
	 * 
	 * @param dict: The current dictionary
	 *            
	 */
	public static void setOrgLabels(Dictionary dict) {
		String tei = dict.get("tei");
		String anatolia = dict.get("anatolia");
		nameOrgLabels(tei, anatolia);
	}

	public static void setGroupLabels(Dictionary dict) {
		String student = dict.get("student");
		String teacher = dict.get("teacher");
		nameGroupLabels(student, teacher);
	}
	
	public static void nameGroupLabels(String student, String teacher) { }
	
	public static void nameMenuViewButtons(String open, String student, String newMap, String logout) { }
	
	/**
	 * Setting buttons linked with Main View.
	 * @param dict
	 */
	public static void setMainviewButtons(Dictionary dict) {
		String newCon = dict.get("newCon");
		String save = dict.get("save");
		String open = dict.get("open");
		String ok = dict.get("submit");
		String cancel = dict.get("cancel");
		nameMainviewButtons(newCon, save, open, ok, cancel);
	}
	
	public static void nameMainviewButtons(String newCon, String save,
			String open, String ok, String cancel) {
		//MainView.newItemIcon.setText(newCon);
		MainView.importBtn.setText(open);
		//MainView.exportIcon.setText(save);
		MainView.ok.setText(ok);
		MainView.cancel.setText(cancel);
		MainView.okNewMap.setText(ok);
		MainView.cancelNewMap.setText(cancel);
	}
	
	public static void setMainViewLabels(Dictionary dict){
		String title = dict.get("title");
		String desc = dict.get("desc");
		String org = null;
		if(User.getOrganisation().equals("tei"))
			org = dict.get("tei");
		else if(User.getOrganisation().equals("anatolia"))
			org = dict.get("anatolia");
		nameMainViewLabels(title, desc, org);
	}
	
	public static void nameMainViewLabels(String title,String desc, String org){
		MainView.titleLabel.setText(title);
		MainView.descLabel.setText(desc);
		MainView.orgLabel.setText(org);
		String username = User.getUsername();
		String rank = User.getGroup();
		String upper =rank.toUpperCase();
		rank = rank.replace(rank.charAt(0), upper.charAt(0));
		MainView.usernameLabel.setText(username+" ("+rank+")");
	}
	
	//MenuView
	public static void setMenuViewButtons(Dictionary dict) {
		String open = dict.get("open");
		String student = dict.get("student");
		String newMap = dict.get("newMap");
		String logout = dict.get("logout");
		nameMenuViewButtons(open, student, newMap, logout);
	}
		
	public static void setMenuViewLabels(Dictionary dict) {
		String students = dict.get("user");
		String map = dict.get("map");
		String org = null;
		if(User.getOrganisation().equals("tei"))
			org = dict.get("tei");
		else if(User.getOrganisation().equals("anatolia"))
			org = dict.get("anatolia");
		String name = dict.get("username");
		nameMenuViewLabels(students, map, name, org);
	}
	
	public static void nameMenuViewLabels(String students, String maps, String name, String org) {
		//MenuView.usernameLabel.setText(name+"  ");
		MenuView.orgLabel.setText(org);
		MenuView.studentLabel.setText(students);
		MenuView.mapLabel.setText(maps);
	}
	
	public static void setMenuViewUis(Dictionary dict){
		setMenuViewLabels(dict);
		setMenuViewButtons(dict);
	}
	
	//LoginView
	
	/**
	 * Sets all the login form fields. Call nameLoginLabels to save the text of
	 * the widget.
	 * 
	 * @param dict
	 *            :The current dictionary
	 */
	public static void setLoginLabels(Dictionary dict) {
		String username = dict.get("username");
		String pass = dict.get("password");
		String show = dict.get("showPass");
		nameLoginLabels(username, pass, show);
	}
	
	public static void setAllUIs(Dictionary dict) {
		setLoginLabels(dict);
		setOrgLabels(dict);
		setOkCancel(dict);
		setGroupLabels(dict);
	}

	public static void setOkCancel(Dictionary dict) {
		String ok = dict.get("submit");
		String cancel = dict.get("cancel");
		nameOkCancel(ok, cancel);
	}
	
	public static void nameOkCancel(String ok, String canc) {
		if(ok.isEmpty() || canc.isEmpty()){
			LoginView.done.setText("Submit");
			LoginView.cancel.setText("Cancel");
		}
		else{
			LoginView.done.setText(ok);
			LoginView.cancel.setText(canc);
		}
	}

	public static void nameLoginLabels(String username, String pass,
			String show) {
		LoginView.usernameLabel.setText(username);
		LoginView.passwordLabel.setText(pass);
		LoginView.showPassLabel.setText(show);
		// LoginView.orgLabel.setText(org);
		/*LoginView.groupLabel.setText(you);
		LoginView.authorLabel.setText(teacher);
		LoginView.newDescLabel.setText(newDesc);
		LoginView.descriptionLabel.setText(desc);*/
	}

	public static void nameOrgLabels(String tei, String anatolia) {
		/*
		 * LoginView.checkTei.setText(tei);
		 * LoginView.checkAnatolia.setText(anatolia);
		 */
	}
	
	//InsertView
	public static void setInsertViewUis(Dictionary dict){
		setInsertViewLabels(dict);
		setInsertViewButtons(dict);
	}
	
	public static void setInsertViewLabels(Dictionary dict){
		String username = dict.get("username");
		String pass = dict.get("password");
		String show = dict.get("showPass");
		nameInsertViewLabels(username, pass, show);
	}
	
	public static void nameInsertViewLabels(String username, String password, String show){
		InsertView.usernameLabel.setText(username);
		InsertView.passwordLabel.setText(password);
		InsertView.showPassLabel.setText(show);
	}
	
	public static void setInsertViewButtons(Dictionary dict){
		String ok = dict.get("submit");
		String cancel = dict.get("cancel");
		nameInsertViewButtons(ok, cancel);
	}
	
	public static void nameInsertViewButtons(String ok, String cancel){
		InsertView.done.setText(ok);
		InsertView.cancel.setText(cancel);
	}

	public static void setEnglish() {
		Dictionary dict = Dictionary.getDictionary("userInfos_en");
		Dictionary msg = Dictionary.getDictionary("msgs_en");
		setAllUIs(dict);
		setDictionary(dict);
		setMsgs(msg);
	}

	public static void setGreek() {
		Dictionary dict = Dictionary.getDictionary("userInfos");
		Dictionary msg = Dictionary.getDictionary("msgs");
		Languages.setAllUIs(dict);
		Languages.setDictionary(dict);
		Languages.setMsgs(msg);
	}
	
	public void setLanguage(){
		// Get current language and set all the UI widgets
		Dictionary dict = Languages.getDictionary();
		//winDict = Languages.getMsgs();
		Languages.setMenuViewUis(dict);	
	}
}
