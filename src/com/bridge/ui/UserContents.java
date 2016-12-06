package com.bridge.ui;

import com.bridge.database.Player;
import com.bridge.database.UILanguage;
import com.bridge.database.User;
import com.bridge.ui.BasicUserContents.ROLE;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.ui.HorizontalLayout;

/*** 
 * this class represents the data publicly available to all player
 * as allowed by the owning user
 * */

@SuppressWarnings("serial")
public class UserContents extends HorizontalLayout {
	
	public UserContents() {
	}

	public UserContents(EntityItem<User> userItem,
			EntityItem<Player> playerItem, JPAContainer<UILanguage> languages,
			ROLE role) {
		
		if (userItem != null) {
			//bindFieldsToForms();
			ROLE role2 =  ROLE.SYSTEMADMIN;
			PersonalInformationForm form = 
					new PersonalInformationForm(userItem, role2);
			ContactInformationForm form2 = new  ContactInformationForm(userItem, ROLE.OWNERUSER);
			PlayerInformationForm form3 = new PlayerInformationForm(playerItem, role2);
			super.addComponents(form, form2, form3);
		}
	}
}
	
	
	
	
		
		
		

	

