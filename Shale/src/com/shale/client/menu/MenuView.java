package com.shale.client.menu;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.VerticalPanel;

import com.shale.client.admin.BackUp;
import com.shale.client.conceptmap.MainPlace;
import com.shale.client.login.LoginPlace;
import com.shale.client.savedmap.Map;
import com.shale.client.user.Student;
import com.shale.client.user.Teacher;
import com.shale.client.user.User;
import com.shale.client.user.insert.InsertPlace;
import com.shale.client.utils.Languages;

public class MenuView extends Composite {

	private MenuActivity menuActivity;
	private static MenuViewUiBinder uiBinder = GWT
			.create(MenuViewUiBinder.class);

	Dictionary dict;
	Dictionary winDict;
	@UiField
	public static Label username;
	@UiField
	public HorizontalPanel namePanel;
	@UiField
	public HorizontalPanel orgPanel;
	@UiField
	public static Label organisation;
	@UiField
	public static ListBox myStudentsList;
	@UiField
	public static Tree myStudentsTree;
	@UiField
	public static ListBox myMapsList;
	@UiField
	public static Label mapLabel;
	@UiField
	public static Label studentLabel;
//	static @UiField
//	Image openBtn;
	@UiField
	public static Label groupLabel;
	@UiField
	public static Label orgLabel;
//	static @UiField
//	Button addStudentBtn;
	@UiField
	public HorizontalPanel teacherPanel;
//	static @UiField
//	Image newMapBtn;
	@UiField
	public static VerticalPanel UserInfosPanel;
	@UiField
	public static Image logout;
	@UiField
	public static Button backUpBtn;
	@UiField 
	public static Image openIcon;
	@UiField
	public Image newMapIcon;
	@UiField
	public static Image addStudentIcon;
	Teacher teacher = new Teacher();
	Student student = new Student();

	interface MenuViewUiBinder extends UiBinder<Widget, MenuView> {
	}

	public MenuView() {
		initWidget(uiBinder.createAndBindUi(this));

		// Get current language and set all the UI widgets
		dict = Languages.getDictionary();
		winDict = Languages.getMsgs();
		if (dict == null) {
			dict = Dictionary.getDictionary("userInfos_en");
			winDict = Dictionary.getDictionary("msgs_en");
		}
		Languages.setMenuViewUis(dict);

		setUserInfos(); // Show username and organisation
		if (User.getGroup().equals("teacher")) {
			teacherPanel.setVisible(true);
			teacher.initUsersListBox();
			teacher.showMyMaps();
			teacher.initMyStudentsForMenu();
			groupLabel.setText(dict.get("teacher"));
			backUp();
		} else if (User.getGroup().equals("student")) {
			student.showMyTeacherMaps();
			student.initUsersListBox();
			groupLabel.setText(dict.get("isStudent"));
		}
	}

	public MenuView(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));

	}

	public void setUserInfos() {
		String username = User.getUsername();
		MenuView.username.setText(username);
	}

	public void setPresenter(MenuActivity pr) {
		menuActivity = pr;
	}

	/**
	 * In case of teacher check the value of myStudentList. In case of student,
	 * show my teacher's maps
	 * 
	 * @param e
	 */
	@UiHandler("openIcon")
	public void onOpenIconClick(ClickEvent e) {
		int index = myStudentsList.getSelectedIndex();
		int mapIndex = myMapsList.getSelectedIndex();
		int countMaps = myMapsList.getItemCount();
		if (countMaps > 0) {
			String title = myMapsList.getItemText(mapIndex);
			String user = null;
			User.setTitle(title);

			user = myStudentsList.getItemText(index);
			if (User.getGroup().equals("teacher")) {
				if (user.equals(User.getUsername())) {
					user = "admin" + "-" + user;
				}
			} else if (User.getGroup().equals("student")) {
				if (user.equals(User.getUsername())) {
					user = User.getUsername();
				} else if (user.equals(Student.getMyTeacher())) {
					String myTeacher = Student.getMyTeacher();
					user = "admin" + "-" + myTeacher;
				}
			}
			Map.setAuthor(myStudentsList.getItemText(index));
			User.setTitle(title);
			String org = User.getOrganisation();
			goToMainView(user, title, org);
		}
	}

	public void goToMainView(String user, String title, String org) {
		String tokens[] = new String[3];
		tokens[0] = user;
		tokens[1] = title;
		tokens[2] = org;
		menuActivity.goTo(new MainPlace(tokens));
	}

	/**
	 * New map. Available only for teachers
	 * 
	 * @param e
	 */
	@UiHandler("newMapIcon")
	public void onNewMapIconClick(ClickEvent e) {
		if (User.getGroup().equals("teacher")) {
			String org = User.getOrganisation();
			// set title as Untitled
			String title = "Untitled";
			User.setTitle(title);
			String user = User.getUsername();
			user = "admin" + "-" + user;
			Map.setAuthor(user);
			goToMainView(user, title, org);
		}
	}

	@UiHandler("addStudentIcon")
	public void onAddStudentIconClick(ClickEvent e) {
		if (User.getGroup().equals("teacher")) {
			menuActivity.goTo(new InsertPlace());
		}
	}

	@UiHandler("logout")
	public void onLogoutClick(ClickEvent e) {
		menuActivity.goTo(new LoginPlace());
	}

	/**
	 * Admins can back up folders with users and concpept maps
	 */
	public void backUp() {
		String[] admins = { "kehris", "Administrator" };
		String loginUser = User.getUsername();
		for (String admin : admins) {
			if (loginUser.equals(admin)) {
				backUpBtn.setVisible(true);
			}
		}

	}

	@UiHandler("backUpBtn")
	public void onbackUpBtnClick(ClickEvent e) {
		if (backUpBtn.isVisible()) {
			BackUp backUp = new BackUp();
			backUp.setNow();
			String[] folders = { "tei", "anatolia", "students.cxl",
					"teachers.cxl" };
			backUp.backUpFolders(folders);
		}
	}
}
