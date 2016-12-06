package com.bridge.view;

import com.bridge.ui.EVerticalLayout;
import com.bridge.ui.MainMenu;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;

/***
 * MainView is the default view a user sees when starting this application
 */

@SuppressWarnings("serial")
public class MainView extends EVerticalLayout implements View {

    public static final String name = ""; // default name

    private MainMenu mainMenu;
    private MenuBar submenu;
    private Navigator navigator;

    // views which are directly reachable from main menu

    public MainView(MainMenu mainMenu, Navigator navigator) {
        this.mainMenu = mainMenu;
        this.navigator = navigator;
        submenu = new MenuBar();
        submenu.addItem("Master Points", navigateTo(MPRegistryView.name));
        submenu.addItem("News", navigateTo(UserNewsView.name));
        submenu.addItem("Members", navigateTo(UserClubMembersView.name));
    }

    /***
     * navigateTo builds a command which navigates to the view
     */

    private Command navigateTo(final String viewName) {
        return selectedItem -> navigator.navigateTo(viewName);
    }

    @Override
    public void enter(ViewChangeEvent event) {
        addComponents(mainMenu, submenu);
    }

}
