package com.bridge.ui;


import com.bridge.database.User;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

@SuppressWarnings("serial")
public class PersonalInformation extends InformationForm {
	// Anonymous is for users without logging  
	// Owner is for the account owner
	// Admin is for the system administrators
	
	public enum STATE { Anonymous, Owner, Admin }; 
	
	protected Label group = new Label("<b>Personal information</b>");
	
	protected TextField firstName = new TextField("First name");
	protected TextField secondName = new TextField("Second name");
	protected TextField lastName = new TextField("Last name");
	protected TextField yearOfBirth = new TextField("Year of birth");
	protected TextField nationality = new TextField("Nationality");
	protected ComboBox language = new ComboBox("UI language"); // for user interface
	protected CheckBox alive = new CheckBox("Alive");
	
	protected EntityItem<User> userItem;
	protected FieldGroup fg;
	protected STATE state;
	
	
	public PersonalInformation(EntityItem<User> userItem, STATE state) {
		this.state = state;
		initialize();
		setReadOnlyState(true, true);
		setUser(userItem);
		setAllFieldsReadOnly();
	}

	public PersonalInformation(STATE state){
		
		this.state = state;
		initialize();
		setReadOnlyState(true, false);
		setAllFieldsReadOnly();
	}
	
	protected void initialize(){
		group.setContentMode(ContentMode.HTML);
		group.setReadOnly(true);
		addComponents(group, firstName, secondName, lastName, 
				yearOfBirth, nationality, language, alive);
		setComponentAlignment(group, Alignment.MIDDLE_CENTER);
		setMargin(true);
		setSpacing(true);
		addButtons();
	}

	public void setUser(EntityItem<User> userItem){
		this.userItem = userItem;
		userItem.setBuffered(true);
		fg = new FieldGroup(userItem);
		fg.bindMemberFields(this);
		setReadOnlyState(true, true);
		
	}
	
	protected void setFieldState(STATE state) {
		switch (state) { 
		case Anonymous: setAnonymousFields(true); 
			break;
		case Owner: setOwnerFields(true);
			break;
		case Admin:;
			default: break;
		}
	}

	/***
	 * setAllFieldsReadOnly sets all existing fields to the read only state 
	 * */
	
	protected void setAllFieldsReadOnly(){
		firstName.setReadOnly(true);
		secondName.setReadOnly(true);
		lastName.setReadOnly(true);
		yearOfBirth.setReadOnly(true);
		nationality.setReadOnly(true);
		language.setReadOnly(true);
		alive.setReadOnly(true);
	}
	
	protected void addFields() {
		addComponents(group, firstName, secondName, lastName, yearOfBirth,
				nationality, language, alive);
	}

	/***
	 * setAllFieldsReadOnly enables or disables exactly the permitted
	 * fields 
	 * */
	
	@Override
	protected void setAllFieldsReadOnly(boolean readOnly) {
		
		if (state == STATE.Anonymous) {} // all fields are always read only
		else if (state == STATE.Owner) {
			language.setReadOnly(readOnly);
		} else if (state == STATE.Admin){
			firstName.setReadOnly(readOnly);
			secondName.setReadOnly(readOnly);
			lastName.setReadOnly(readOnly);
			yearOfBirth.setReadOnly(readOnly);
			nationality.setReadOnly(readOnly);
			language.setReadOnly(readOnly);
			alive.setReadOnly(readOnly);
		} else {}
	}
	

	
	
	@Override
	protected void okClicked() {
		try {
			fg.commit();
			userItem.commit();
		} catch (CommitException e) {
			e.printStackTrace();
		}
		setReadOnlyState(true, true);
	}

	@Override
	protected void cancelClicked() {
		fg.discard();
	}
	
	/***
	 * setAnonymousFields sets fields shown to an anonymous user
	 * */
	protected void setAnonymousFields(boolean state){
		buttons.setVisible(!state);
		secondName.setVisible(!state);
		yearOfBirth.setVisible(!state);
		language.setVisible(!state);
	}
	
	protected void setOwnerFields(boolean state){
		language.setReadOnly(state);
	}
	
	protected void setAdminFields(boolean state){
		
	}
	
	protected void o(String s){ System.out.println(s); }
	

}
