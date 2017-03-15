package com.bridge.view;

import java.util.Arrays;
import java.util.HashSet;

import com.bridge.database.User;
import com.bridge.ui.BridgeUI;
import com.bridge.ui.ContactInformation;
import com.bridge.ui.MainMenu;
import com.bridge.ui.PersonalInformation;
import com.bridge.ui.PlayerInformation;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class UserManagementView extends VerticalLayout implements View {

    static final String name = "/user/management/";
    public final static String[] rls = new String[] { "anon" };
    public final static HashSet<String> roles = new HashSet<>(
            Arrays.asList(rls));
    protected PersonalInformation pei;
    protected PlayerInformation pli;
    protected ContactInformation ci;
    protected VerticalLayout layout;
    protected HorizontalLayout informationLayout = new HorizontalLayout();
    protected JPAContainer<User> us = JPAContainerFactory.make(User.class,
            BridgeUI.unitName);
    protected MainMenu mainMenu;

    public UserManagementView(MainMenu menu) {
        mainMenu = menu;
        setSpacing(true);
        setMargin(true);

        //
        // addSearchUserCaptions();
        // addSearchFilter();
        // searchUser.addValueChangeListener(new ValueChangeListener() {
        // @Override
        // public void valueChange(ValueChangeEvent event) {
        // }
        // });
    }

    protected void loadForms(Object userItemId) {

    }

    // protected void addSearchFilter() {
    // searchUser.setFilteringMode(FilteringMode.CONTAINS);
    // }
    //
    // protected void addSearchUserCaptions() {
    // searchUser.setContainerDataSource(us);
    // for (Object id:searchUser.getItemIds()){
    // User u = us.getItem(id).getEntity();
    // searchUser.setItemCaption(id, u.toString());
    // }
    // }

    protected HorizontalLayout getForms() {
        informationLayout.addComponents(pei, ci, pli);
        return informationLayout;
    }

    @Override
    public void enter(ViewChangeEvent event) {
        addComponent(mainMenu);
        // addComponent(searchUser);
    }

}
