package com.shale.client.user.insert;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.i18n.client.Dictionary;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.SimpleCheckBox;
import com.shale.client.user.Student;
import com.shale.client.user.Teacher;
import com.shale.client.utils.Languages;

public class InsertView extends Composite  {
	@UiField
	public static Button done;
	@UiField public static TextBox username;
	@UiField public static Label passwordLabel;
	@UiField PasswordTextBox password;
	@UiField public static Button cancel;
	@UiField public static Label usernameLabel;
	@UiField public static ListBox myStudentList;
	@UiField Button addBtn;
	@UiField Button editBtn;
	@UiField Button delBtn;
	@UiField VerticalPanel DataPanel;
	@UiField SimpleCheckBox showPassword;
	@UiField public static Label showPassLabel;
	
	private InsertActivity insertActivity;
	private static InsertVoewUiBinder uiBinder = GWT
			.create(InsertVoewUiBinder.class);

	Dictionary dict;
	Dictionary winDict;
	Teacher teacher;
	Student studentsCXL;
	
	//set edit as true when you want to edit student
	private boolean edit = false;
	interface InsertVoewUiBinder extends UiBinder<Widget, InsertView> {
	}

	public InsertView() {
		initWidget(uiBinder.createAndBindUi(this));
		
		// Get current language and set all the UI widgets
		dict = Languages.getDictionary();
		winDict = Languages.getMsgs();
		Languages.setInsertViewUis(dict);
		
		//show my student list
		teacher = new Teacher();
		teacher.initMyStudentsForInsert();
	} 	

	public InsertView(String firstName) {
		initWidget(uiBinder.createAndBindUi(this));

	}

	@UiHandler("done")
	void onDoneClick(ClickEvent e) {
		String student = username.getText();
		String pass = password.getText();
		if((!student.isEmpty()) && (!pass.isEmpty())){
			teacher = new Teacher();
			if(edit){
				String oldName = getSelectedStudent();
				teacher.editStudent(oldName, student, pass);
			}
			else if(!edit){
				teacher.newStudent(student, pass);
			}	
		}
		//empty username and password text
		username.setText("");
		password.setText("");
		DataPanel.setVisible(false);
		
		//reload students.cxl file!
		studentsCXL = new Student();
		studentsCXL.loadUsersFile();
	}
	
	@UiHandler("username")
	void onUsernameKeyUp(KeyUpEvent e) {
		checkDoneState();
	}

	@UiHandler("password")
	void onPasswordKeyUp(KeyUpEvent e) {
		checkDoneState();
	}
	
	@UiHandler("addBtn")
	public void onAddBtnClick(ClickEvent e){
		DataPanel.setVisible(true);
		edit = false;
	}
	
	@UiHandler("delBtn")
	public void onDelBtnClick(ClickEvent e){
		teacher = new Teacher();
		String student = getSelectedStudent();
		//Teacher must confirm deletion to procceed.
		if(Window.confirm("Deleting "+student+". Are you sure?"))
		teacher.removeStudent(student);
	}
	
	@UiHandler("editBtn")
	public void onEditBtnClick(ClickEvent e){
		DataPanel.setVisible(true);
		edit = true;
		String student = getSelectedStudent();
		username.setText(student);
	}
	
	@UiHandler("cancel")
	public void onCancelClick(ClickEvent e){
		username.setText("");
		DataPanel.setVisible(false);
	}
	
	public void setPresenter(InsertActivity pr) {
		insertActivity = pr;
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
	
	public static String getSelectedStudent(){
		int index = myStudentList.getSelectedIndex();
		String student = myStudentList.getItemText(index);
		
		return student;
	}
	
	//Show or hide password characters
	@UiHandler("showPassword")
	public void onShowPasswordCheck(ClickEvent e){
		if(showPassword.getValue()==true)
			password.getElement().setAttribute("type", "text");
		else if(showPassword.getValue()==false)
			password.getElement().setAttribute("type", "password");
	}
}
