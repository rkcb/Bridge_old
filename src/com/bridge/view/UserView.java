package com.bridge.view;


import java.util.HashSet;

import scala.actors.threadpool.Arrays;

import com.bridge.database.Player;
import com.bridge.database.UILanguage;
import com.bridge.database.User;
import com.bridge.ui.BasicUserContents.ROLE;
import com.bridge.ui.ContactInformationForm;
import com.bridge.ui.PersonalInformationForm;
import com.bridge.ui.PlayerInformationForm;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

/***
 * this class is typically visible to the user and the
 * administrators only -- it used to edit user related data
 * */

@SuppressWarnings("serial")
public class UserView extends VerticalLayout implements View {

	public static final String name = "/user";
	public final static String[] rls = new String[] {"anon"};
	@SuppressWarnings("unchecked")
	public final static HashSet<String> roles = new HashSet<String>(Arrays.asList(rls));
	
	
	private JPAContainer<User> us = JPAContainerFactory.make(User.class, "BridgeResults");
	private JPAContainer<Player> ps = JPAContainerFactory.make(Player.class, "BridgeResults");
	@SuppressWarnings("unused")
	private JPAContainer<UILanguage> langs = JPAContainerFactory.make(UILanguage.class, "BridgeResults");
	
	private HorizontalLayout informationForms = new HorizontalLayout();
	private PersonalInformationForm personalForm;
	private ContactInformationForm contactForm;
	private PlayerInformationForm playerForm;
	
	private EntityItem<User> user;
	private EntityItem<Player> player;
	
	public UserView(String federationCode, ROLE role) {
//		addUsersAndPlayers();
		role = ROLE.SYSTEMADMIN;
		user = findUser("123");
		player = findPlayer(user);
		
		personalForm = new PersonalInformationForm(user, role);
		contactForm = new ContactInformationForm(user, role);
		playerForm = new PlayerInformationForm(player, role);
		
		informationForms.addComponents(personalForm, contactForm, playerForm);
		super.addComponent(informationForms);
		
	}

	@Override
	public void enter(ViewChangeEvent event) {
	}
	
	private EntityItem<User> findUser(String federationCode) {
		if (federationCode != null) {
			Filter filter = new Compare.Equal("federationCode", federationCode);
			ps.addContainerFilter(filter);
			System.out.println("size == "+ps.size());
			if (ps.size() == 1) {
				Object itemId = ps.getIdByIndex(getComponentCount());
				return us.getItem(itemId);
			} else return null;
		} else return null;
	}
	
	private EntityItem<Player> findPlayer(EntityItem<User> userItem) {
		if (userItem != null) {
			return ps.getItem(userItem.getItemId());
		} else return null;
	}

	public String getName() { return name; }
	
}
