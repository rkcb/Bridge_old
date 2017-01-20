package com.bridge.ui;

import javax.servlet.annotation.WebServlet;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.bridge.database.SampleStuff;
import com.bridge.view.UserCompetionsView;
import com.bridge.view.ViewManager;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
@Theme("bridge")
public class BridgeUI extends UI {

    public static final DefaultPasswordService pwService = new DefaultPasswordService();
    public static final String unitName = "BridgeResults";
    public static final ShiroLogin user = new ShiroLogin();
    public static final String jndiConnectionName = "java:comp/env/jdbc/DSTest";
    public static final transient Logger log = LoggerFactory
            .getLogger(BridgeUI.class);
    protected static Navigator navigator;
    protected static ViewManager viewManager = null;
    private static String role = "";
    public static final String anon = "anon";
    public static final String basic = "basic";
    public static final String clubadmin = "clubadmin";
    public static final String admin = "admin";

    @WebServlet(value = "/*", asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = BridgeUI.class, widgetset = "com.bridge.ui.widgetset.BridgeWidgetset")
    @Push(PushMode.AUTOMATIC)
    public static class Servlet extends VaadinServlet {
    }

    @Override
    protected void init(VaadinRequest request) {
        // note that some sample data may require sign in
        // as an admin and then out!

        SampleStuff.create();
        SampleStuff.adminlogin();

        // must be first because there are no users yet!

        role = basic;
        if (SecurityUtils.getSubject().hasRole(role)) {
            setViewManagerState(role);
            navigator.navigateTo(UserCompetionsView.name);
            // navigator.navigateTo(TestView.name);
            // navigator.navigateTo(MainView.name);
        } else {
            setViewManagerState(anon);
        }
    }

    /***
     * getCurrentRole returns the role of this user
     */

    public static String getCurrentRole() {
        return viewManager.getRole();
    }

    // /***
    // * addViewChangeListener controls if the user has permission to get to the
    // * view
    // */
    //
    // protected static void addViewChangeListener() {
    // navigator.addViewChangeListener(new ViewChangeListener() {
    //
    // @Override
    // public boolean beforeViewChange(ViewChangeEvent event) {
    // BridgeUI.o("asking view");
    // String name = event.getViewName();
    // if (viewManager.navigationPermitted(name)) {
    // BridgeUI.o("permitted: " + name);
    // return true;
    // } else {
    // BridgeUI.o("View " + name + " NOT present");
    // BridgeUI.o("NOT permitted: " + name);
    // navigator.navigateTo(LoginView.name);
    // navigator.set
    // return false;
    // }
    // }
    //
    // @Override
    // public void afterViewChange(ViewChangeEvent event) {
    // }
    // });
    // }

    /***
     * setViewManagerState creates only views allowed for this role
     */

    public static void setViewManagerState(String role2) {
        navigator = new Navigator(getCurrent(), getCurrent());
        viewManager = new ViewManager(navigator);
        viewManager.addViews(role2); // sets the effective role (e.g. admin can
                                     // choose to be "basicuser")
    }

    static public void o(String s) {
        System.out.println(s);
    }

    static public void p(String s) {
        System.out.print(s);
    }

}