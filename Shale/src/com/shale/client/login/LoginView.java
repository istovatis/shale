package com.shale.client.login;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasText;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.SimpleCheckBox;
import com.shale.client.conceptmap.MainPlace;

import com.shale.client.menu.MenuActivity;
import com.shale.client.menu.MenuPlace;
import com.shale.client.savedmap.Description;
import com.shale.client.user.Student;
import com.shale.client.user.Teacher;
import com.shale.client.user.User;
import com.shale.client.utils.Languages;


/**
 * The first view presented to user when entering the map. User inserts
 * username and password. Credentials are checked at the server and authorized
 * user is allowed to proceed to Menu View.
 * 
 * @author Istovatis -- istovatis@gmail.com --
 *
 */
public class LoginView extends Composite implements HasText {
	
	@UiField public static Button cancel;
	@UiField public static Button done;
	@UiField public static TextBox username;
	@UiField public PasswordTextBox password;
	@UiField public VerticalPanel organisation;
	@UiField public Image flag_gr;
	@UiField public Image flag_en;
	@UiField public static Label usernameLabel;
	@UiField public static Label passwordLabel;
	@UiField public SimpleCheckBox showPassword;
	@UiField public static Label showPassLabel;
	/*
	 * @UiField static CheckBox checkStudent;
	 * 
	 * @UiField static CheckBox checkTeacher;
	 */

	private LoginActivity loginActivity;
	private MenuActivity menuActivity;
	private String[] tokens = new String[7];
	private Description file;
	private Teacher teacher;
	private Student student;

	private static LoginViewUiBinder uiBinder = GWT
			.create(LoginViewUiBinder.class);

	interface LoginViewUiBinder extends UiBinder<Widget, LoginView> {
	}

	public LoginView() {

		initWidget(uiBinder.createAndBindUi(this));
		Languages.setLoaded(true);
		// Set english as default language and set UIs text
		Languages.setEnglish();
		// Import Descriptions
		// file = new Description();
		student = new Student();
		teacher = new Teacher();
		student.loadUsersFile();
		teacher.loadUsersFile();

		// file.loadDescriptionsFile();
		listenToEnter();
		// login.loadUsersFile();
	}

	public LoginView(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));
	}

	public void setText(String text) {
		cancel.setText(text);
	}

	public String getText() {
		return cancel.getText();
	}

	public String getUsername() {
		return username.getText();
	}

	/*
	 * username-author:tokens[0], password:tokens[1], mail:tokens[2], title:
	 * tokens[3], description:tokens[4], organisation:tokens[5], group:tokens[6]
	 */
	public void setTokens() {
		tokens[0] = username.getText();
		tokens[1] = password.getText();
		//tokens[2] = mail.getText();
		// set tokens[5] as organisation. Organisation initialized in
		// ValidateUser method of class User.
		tokens[5] = User.getOrganisation();
		// set 3 values to user
		User.setUsername(tokens[0]);
		User.setPassword(tokens[1]);
		User.setMail(tokens[2]);
	}

	public void setPresenter(LoginActivity pr) {
		loginActivity = pr;
	}

	@UiHandler("username")
	void onUsernameKeyUp(KeyUpEvent e) {
		checkDoneState();
	}

	@UiHandler("password")
	void onPasswordKeyUp(KeyUpEvent e) {
		checkDoneState();
	}

	/**
	 * Check if username and password is not empty. Then provide two operations:
	 * 1st:If asddStudent=true add a student. 2nd: If you don't want to add a
	 * student, set username, password and mail and go to MenuView.
	 * 
	 * 
	 * @param e
	 */
	@UiHandler("done")
	void onDoneClick(ClickEvent e) {
		checkAndProcceed();
	}

	@UiHandler("flag_en")
	void onFlag_enClick(ClickEvent e) {
		Languages.setEnglish();
	}

	@UiHandler("flag_gr")
	void onFlag_grClick(ClickEvent e) {
		Languages.setGreek();
	}

	// Show or hide password characters
	@UiHandler("showPassword")
	public void onShowPasswordCheck(ClickEvent e) {
		if (showPassword.getValue() == true)
			password.getElement().setAttribute("type", "text");
		else if (showPassword.getValue() == false)
			password.getElement().setAttribute("type", "password");
	}

	public void goToMainView() {
		loginActivity.goTo(new MainPlace(tokens));
	}

	public void goToMenuView() {
		loginActivity.goTo(new MenuPlace("null"));
	}

	/**
	 * Check if user pressed password
	 * 
	 * @return true if password pressed or else false
	 */
	public boolean isPasswordPressed() {
		boolean checked = true;
		if (password.getText().isEmpty()) {
			String err = Languages.getMsgs().get("noPass");
			Window.alert(err);
			checked = false;
		}
		return checked;
	}

	/**
	 * Check if user pressed username
	 * 
	 * @return true if password pressed and username doesn't contain the
	 *         character "-", or else false
	 */
	public boolean isUsernamePressed() {
		boolean checked = true;
		if (username.getText().isEmpty() || username.getText().equals("-")) {
			username.setText("");
			String err = Languages.getMsgs().get("wrongUsername");
			Window.alert(err);
			checked = false;
		}
		return checked;
	}

	/**
	 * Check if user chose description from the list
	 * 
	 * @return true if user chose description from the list or else false
	 */
	public boolean isDescriptionChosen() {
		boolean checked = false;
		if (!User.getDescription().equals("")) {
			checked = true;
		} else {
			String err = Languages.getMsgs().get("noDesc");
			Window.alert(err);
		}
		return checked;
	}

	/**
	 * Enable done button if username and password is not null, or else disable
	 * it.
	 * 
	 */
	public void checkDoneState() {
		if (username.getText().isEmpty() || password.getText().isEmpty()) {
			done.setEnabled(false);
		} else if (!(username.getText().isEmpty())
				&& !(password.getText().isEmpty())) {
			done.setEnabled(true);
		}
	}

	/**
	 * When user press enter, go to next page
	 */
	public void listenToEnter() {
		password.addKeyDownHandler(new KeyDownHandler() {

			@Override
			public void onKeyDown(KeyDownEvent event) {
				if (event.getNativeKeyCode() == KeyCodes.KEY_ENTER) {
					checkAndProcceed();
				}
			}
		});
	}
	
	/**
	 * Check firstly if username and password are set. Then validate user.
	 */
	public void checkAndProcceed() {
		try {
			if (isUsernamePressed()) {
				if (isPasswordPressed()) {
					String user = username.getText();
					String pass = password.getText();
					// Check if user is teacher or student and validate
					// user.
					boolean validate = false;
					validate = teacher.validateUser(user, pass);
					if (!validate) {
						validate = student.validateUser(user, pass);
						if (!validate)
							Window.alert(Languages.getMsgs().get(
									"wrongUserPass"));
					}
					if (validate) {
						setTokens();
						goToMenuView();
					}
				}
			}
		} catch (NullPointerException e1) {
			Window.alert("A Pointer Exception Problem Has Occured: "
					+ e1.getCause());
		}
	}
}
