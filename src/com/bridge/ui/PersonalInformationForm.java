package com.bridge.ui;


import com.bridge.database.User;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.bridge.ui.BasicUserContents.ROLE;

/***
 * PersonalInformationForm: the class is a editor for personal data; 
 * the editing possibilities are limited by the access rights:
 * */

@SuppressWarnings("serial")
public class PersonalInformationForm extends InformationForm {

	private Label header = new Label("Personal information");
	
	private TextField firstName = new TextField("First name");
	private TextField secondName = new TextField("Second name");
	private TextField lastName = new TextField("Last name");
	private TextField yearOfBirth = new TextField("Year of birth");
	private TextField nationality = new TextField("Nationality");
	private ComboBox language = new ComboBox("UI language"); // for user interface
	private CheckBox alive = new CheckBox("Alive");
	
//	private User targetUser;
	private EntityItem<User> userItem;
	
	public PersonalInformationForm(EntityItem<User> userItem, ROLE role) {
		

		super.addComponent(header);
		
		if (userItem != null) {
			super.addComponent(header);
			super.setMargin(true);
			super.setSpacing(true);
			
			this.userItem = userItem;
			this.role = role;
			
//			form = new FormLayout();
			
			// bind fields to the database
			FieldGroup fg = new FieldGroup(userItem);
			fg.buildAndBindMemberFields(this);
			
			
			
			// these fields are always visible
//			form.addComponents(firstName,secondName, lastName);
		 
//			addComponent(form);
//			addButtons();
//			
//			
//			if (role == ROLE.SYSTEMADMIN) {
//				form.addComponents(yearOfBirth, alive, nationality, language);
//				setReadOnlyState(true, true);
//			} else if (role == ROLE.OWNERUSER) {
//				form.addComponents(yearOfBirth, alive, nationality, language);
//				setReadOnlyState(true, true);
//			}
//			else { // CLUBADMIN and USER
//				form.addComponents(alive);
//				setReadOnlyState(true, false);
//			}
			
			alive.setStyleName("informationFormCheckBox");
		}
	}
	
	@Override
	protected void setAllFieldsReadOnly(boolean readOnly) {
		if (role == ROLE.SYSTEMADMIN) {
			firstName.setReadOnly(readOnly);
			secondName.setReadOnly(readOnly);
			lastName.setReadOnly(readOnly);
			yearOfBirth.setReadOnly(readOnly);
			nationality.setReadOnly(readOnly);
			language.setReadOnly(readOnly);
			alive.setReadOnly(readOnly);
		} else if (role == ROLE.OWNERUSER) {
			firstName.setReadOnly(true);
			secondName.setReadOnly(true);
			lastName.setReadOnly(true);
			yearOfBirth.setReadOnly(true);
			nationality.setReadOnly(true);
			language.setReadOnly(readOnly);
			alive.setReadOnly(true);
			
		} else {
			firstName.setReadOnly(true);
			secondName.setReadOnly(true);
			lastName.setReadOnly(true);
			yearOfBirth.setReadOnly(true);
			nationality.setReadOnly(true);
			language.setReadOnly(true);
			alive.setReadOnly(true);
		}
	}
	

	@Override
	protected void okClicked() {
		if (role == ROLE.SYSTEMADMIN) {
			firstName.commit();
			secondName.commit();
			lastName.commit();
			nationality.commit();
			yearOfBirth.commit();
			language.commit();
			alive.commit();
		} else if (role == ROLE.OWNERUSER) {
			language.commit();
		}
		
		setReadOnlyState(true, true);
	}

	@Override
	protected void cancelClicked() {
		this.userItem.refresh();
		
		User u = userItem.getEntity();
		firstName.setValue(u.getFirstName());
		secondName.setValue(u.getSecondName());
		lastName.setValue(u.getLastName());
		nationality.setValue(u.getNationality());
		yearOfBirth.setValue(u.getYearOfBirth());
		language.setValue(u.getLanguage());
		alive.setValue(u.getAlive());
		setReadOnlyState(true, true);
	}
	
	
}
