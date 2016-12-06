package com.bridge.ui;

import com.bridge.database.User;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;


@SuppressWarnings("serial")
public class ContactInformation extends InformationForm {

	
	protected Label group = new Label();

	protected TextField username = new TextField("Username");
	protected TextField address = new TextField("Address");
	protected TextField town = new TextField("Town");
	protected TextField postalCode = new TextField("Postal code");
	protected TextField country = new TextField("Country");
	protected TextField email = new TextField("Email");
	protected TextField telephone = new TextField("Telephone");
	protected CheckBox emailPrivate = new CheckBox("Email hidden");  
	protected CheckBox telephonePrivate = new CheckBox("Telephone nr. hidden");
	protected EntityItem<User> userItem;
	protected FieldGroup fg;
	
	public ContactInformation(boolean isAnonymous){
		initialize();
		setReadOnlyState(true, false);
		setAllFieldsReadOnly(true);
		setState(isAnonymous);
	}
	
	public ContactInformation(EntityItem<User> user, boolean isAnonymous){
		addComponents(group, username, address, town, postalCode, country, email, telephone,
				emailPrivate, telephonePrivate, buttons);	
		
		initialize();
		setReadOnlyState(true, true);
		setUser(user);
		setState(isAnonymous);
		setAllFieldsReadOnly(true);
		setMargin(true);
		setSpacing(true);
	}
	
	protected void initialize(){
		addComponents(group, username, address, town, postalCode, country, email, telephone,
				emailPrivate, telephonePrivate, buttons);	
		
		setComponentAlignment(group, Alignment.MIDDLE_CENTER);
		group.setContentMode(ContentMode.HTML);
		group.setValue("<b>Contact information</b>");
		
		setMargin(true);
		setSpacing(true);
		
		addButtons();
	}
	
	public void setState(boolean isAnonymous){
		if (isAnonymous)
			setFieldSAnonym();
		
		setAllFieldsReadOnly(true);
	}
	
	public void setUser(EntityItem<User> ui){
		this.userItem = ui;	
		fg = new FieldGroup(ui);
		fg.bindMemberFields(this);
		setReadOnlyState(true, true);
	}
	
	protected void o(String s) {
		System.out.println(s);
	}

	/***
	 * setFieldSAnonym sets the fields suitable for anonymous users
	 * */
	
	protected void setFieldSAnonym(){
		username.setVisible(false);
		address.setVisible(false);
		town.setVisible(false);
		postalCode.setVisible(false);
		country.setVisible(false);
		email.setVisible(!emailPrivate.getValue());
		telephone.setVisible(!telephonePrivate.getValue());
		buttons.setVisible(false);
	}

	/***
	 * setAllFieldsReadOnly sets the read only state of  
	 * the fields editable by current user
	 * */
	
	@Override
	protected void setAllFieldsReadOnly(boolean readOnly) {
		username.setReadOnly(true); // username must not be edited
		address.setReadOnly(readOnly);
		town.setReadOnly(readOnly);
		postalCode.setReadOnly(readOnly);
		country.setReadOnly(readOnly);
		email.setReadOnly(readOnly);
		telephone.setReadOnly(readOnly);
		emailPrivate.setReadOnly(readOnly);
		telephonePrivate.setReadOnly(readOnly);
	}

	@Override
	protected void okClicked()  {
		try {
			fg.commit();
			userItem.commit();
		} catch (CommitException e) {
			e.printStackTrace();
		}
		setReadOnlyState(true, true);
	}

	@Override
	void cancelClicked() {
		fg.discard();
	}
	
	
	

}
