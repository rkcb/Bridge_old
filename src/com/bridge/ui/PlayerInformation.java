package com.bridge.ui;

import com.bridge.database.Player;
import com.bridge.ui.PersonalInformation.STATE;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

@SuppressWarnings("serial")
public class PlayerInformation extends InformationForm {

	protected Label group = new Label("<b>Player information</b>");
	protected CheckBox federationMember = new CheckBox("Federation member");
	protected CheckBox foreignWBFClubMember = new CheckBox("Foreign club member");
	protected TextField federationCode = new TextField("Federation code");
	protected TextArea directorQualifications = new TextArea("Director qualifications");
	protected TextField masterpoints = new TextField("Masterpoints");
	
	protected EntityItem<Player> playerItem;
	protected STATE state; // state defines how the fields behave in different userRoles
	protected FieldGroup fg;
	
	public PlayerInformation(EntityItem<Player> player, STATE state) {
		initialize();
		setPlayer(player);
		this.state = state; 
		setReadOnlyState(true, state != STATE.Anonymous);
	}
	
	public void setPlayer(EntityItem<Player> playerItem){
		this.playerItem = playerItem; 
		fg = new FieldGroup(playerItem);
		fg.bindMemberFields(this);
		setAllFieldsReadOnly();
		setReadOnlyState(true, state != STATE.Anonymous);
	}
	
	public PlayerInformation(STATE state){
		initialize();
		setReadOnlyState(true, false);
		this.state = state;
	}
	
	protected void initialize(){
		
		group.setContentMode(ContentMode.HTML);
		
		addFields();
		setComponentAlignment(group, Alignment.MIDDLE_CENTER);
		addButtons();
		
		setMargin(true);
		setSpacing(true);
		setAllFieldsReadOnly();
	}

	
	/***
	 * setAllFieldsReadOnly sets all fields in this class to the read only 
	 * state
	 * */
	
	protected void setAllFieldsReadOnly(){
		federationCode.setReadOnly(true);
		federationMember.setReadOnly(true);
		foreignWBFClubMember.setReadOnly(true);
		directorQualifications.setReadOnly(true);
		masterpoints.setReadOnly(true);
	}
	

	protected void addFields() {
		addComponents(group, federationCode, federationMember, foreignWBFClubMember, 
				directorQualifications, masterpoints);
	}
	
	/***
	 * setAllFieldsReadOnly enables or disables exactly the permitted
	 * fields -- used by the edit button
	 * */
	
	@Override
	protected void setAllFieldsReadOnly(boolean readOnly) {
		
		if (state == STATE.Admin){
			// fed code will be assigned automatically and remains the same forever
//			federationCode.setReadOnly(readOnly);
			federationMember.setReadOnly(readOnly);
			foreignWBFClubMember.setReadOnly(readOnly);
			directorQualifications.setReadOnly(readOnly);
			
		}
		
		if (state == STATE.Owner)
			directorQualifications.setReadOnly(readOnly);
	}

	@Override
	void okClicked() {
		try {
			fg.commit();
			playerItem.commit();
			setReadOnlyState(true, state != STATE.Anonymous);
		} catch (CommitException e) {
			e.printStackTrace();
		}
	}

	@Override
	void cancelClicked() {
		fg.discard();
	}
	


}
