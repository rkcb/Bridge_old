package com.bridge.ui;


import com.bridge.database.Player;
import com.bridge.database.UILanguage;
import com.bridge.database.User;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

/***
 * BasicUserContents is used to gather the information of the player
 * and show it in read only or editable depending on the permissions.
 * The class can also hide all contact information for privacy if
 * the user is not authorized to see the information
 * */

@SuppressWarnings("serial")
public class BasicUserContents extends VerticalLayout {
	
	// FEDERATION means that only the system admintrators and the user see 
	// the contact information
	// CLUB is the same as above but also the club admintrators of player's club
	// can read the contact information
	// PLAYER means that exactly all registered users of the system can see 
	// the data
	public enum ROLE { USER, OWNERUSER, CLUBADMIN, SYSTEMADMIN }
	
	protected HorizontalLayout dataLayout = new HorizontalLayout();
	
	

	ROLE role = ROLE.USER; // authorization role of the user of this class

	
	public  BasicUserContents() {}
	
	/***
	 * the constructor takes the containers and the id of entity of the interest
	 * and the role of the user of this class
	 * */
	
	public BasicUserContents(EntityItem<User> userItem, EntityItem<Player> playerItem,
			JPAContainer<UILanguage> languages, ROLE role) {
		

	}

	


	public BasicUserContents(Component... children) {
		super(children);
	}
	
//	private void bindFields(Long userId, JPAContainer<User> users, 
//			JPAContainer<Player> players, JPAContainer<UILanguage> languages) {
//		
//		if (users == null) throw new NullPointerException();
//		else {
//			
//			
//			EntityItem<User> userItem = users.getItem(userId);
//			if (userItem == null) throw new NullPointerException();
//			else {
////				FieldGroup fg = new FieldGroup(userItem);
//
////				fg.bind(firstName, "firstName");
////				fg.bind(secondName, "secondName");
////				fg.bind(lastName, "lastName");
////				fg.bind(nationality, "nationality");
////				fg.bind(language, "language");
////				fg.bind(address, "address");
////				fg.bind(town, "town");
////				fg.bind(postalCode, "postalCode");
////				fg.bind(country, "country");
////				fg.bind(emailAddress, "emailAddress");
////				fg.bind(emailAddressPrivacy, "emailAddressPrivate");
////				fg.bind(telephone, "telephone");
////				fg.bind(telephonePrivate, "telephonePrivate");
////				fg.bind(alive, "alive");
////				
////				if (players != null && players.getItem(userId) != null) {
////					EntityItem<Player> playerItem = players.getItem(userId);
////					FieldGroup fg2 = new FieldGroup(playerItem);
////					fg2.bind(federationCode, "federationCode");
////					fg2.bind(masterPoints, "masterPoints");
////					fg2.bind(directorQualifications, "directorQualifications");
////					
////				}  
//			}
//			
////		}
//	} // bindFields()
//	
	
	
	
	/* **
	 * setOnlyRead : sets all editable fields to the "read only" state  
	 * */
//	protected void setOnlyRead(boolean readOnly) {
//		firstName.setReadOnly(readOnly);
//		secondName.setReadOnly(readOnly);
//		lastName.setReadOnly(readOnly);
//		yearOfBirth.setReadOnly(readOnly);
//		nationality.setReadOnly(readOnly);
//		language.setReadOnly(readOnly);
//		alive.setReadOnly(readOnly);
//		
//		address.setReadOnly(readOnly);
//		postalCode.setReadOnly(readOnly);
//		town.setReadOnly(readOnly);
//		telephone.setReadOnly(readOnly);
//		emailAddress.setReadOnly(readOnly);
//		country.setReadOnly(readOnly);
//
//		federationCode.setReadOnly(readOnly);
//		clubSelector.setReadOnly(readOnly);
//		federationMember.setReadOnly(readOnly);
//		masterPoints.setReadOnly(readOnly);
//		directorQualifications.setReadOnly(readOnly);
//	}
//	
	
}
	
