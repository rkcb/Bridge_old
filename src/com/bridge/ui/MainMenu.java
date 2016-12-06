package com.bridge.ui;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.bridge.database.User;
import com.bridge.view.AdminPlayerDataEditor;
import com.bridge.view.ApplicationView;
import com.bridge.view.ClubCompetionsView;
import com.bridge.view.ClubEventsView;
import com.bridge.view.ClubInternalsView;
import com.bridge.view.ClubsManagementView;
import com.bridge.view.LoginView;
import com.bridge.view.MPRegistryEditView;
import com.bridge.view.MainView;
import com.bridge.view.PasswordView;
import com.bridge.view.PersonalDataView;
import com.bridge.view.SignedOutView;
import com.bridge.view.TestView;
import com.bridge.view.UploadView;
import com.bridge.view.UserCompetionsView;
import com.bridge.view.UserEventsView;
import com.vaadin.navigator.Navigator;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.VerticalLayout;

/***
 * MainMenu is visible in every view reachable from it. MainMenu is constructed
 * to show exactly the items that are permitted to the current user role
 */

@SuppressWarnings("serial")
public class MainMenu extends VerticalLayout {

    protected Label loginStatus = new Label();
    protected MenuItem main; // all userRoles
    protected MenuItem test;
    protected MenuItem events; // almost as with other userRoles but read only
    protected MenuItem competions; // almost as with other userRoles but read
                                   // only
    protected MenuItem clubAdmin; // to club admins or system admins
    protected MenuItem admin; // for system admins only
    protected MenuItem allAdmin;
    protected MenuItem applications; // for system admins only
    protected MenuItem login; // always visible
    protected MenuItem logout; // visible only to users signed in
    protected MenuItem mpRegistry;

    protected MenuItem myAccount; // visible to all but anonymous users
    protected MenuItem news;
    protected MenuItem registration; // visible to all
    protected MenuItem personalData; // visible to all signed in users

    protected MenuItem adminUploadResults; // visible to all clubadmins and
                                           // admins
    protected MenuItem adminEditMPs;
    protected MenuItem adminCompetions; // visible to all clubadmins and admins
    protected MenuItem adminEvents; // visible to all clubadmins and admins
    protected MenuItem adminUserDataEditor; // visible to all clubadmins and
                                            // admins
    protected MenuItem adminClubInternals; // visible to all clubadmins and
                                           // admins
    protected MenuItem adminMpRegistry; // visible to all clubadmins and admins
    protected MenuItem adminFederation; // visible to admins only
    protected MenuItem adminMembershipApplications; // visible to club admins
                                                    // and system admins

    private Navigator navigator;
    private MenuBar menubar = new MenuBar();
    public final String role;

    public MainMenu(Navigator navigator, String role) {
        this.role = role;
        this.navigator = navigator;
        addComponents(menubar);
        menubar.setAutoOpen(true);
        updateLoginStatus();
        setSpacing(false);

        menubar.addItem("Test", navigateTo(TestView.name));

        switch (role) {
        case "anon":
            addAnonMenuItems();
            break;
        case "basic":
            addBasicMenuItems();
            break;
        case "clubadmin":
            addClubAdminMenuItems();
            break;
        case "admin":
            addAdminMenuItems();
            break;
        default:
            break;
        }
    }

    public void updateLoginStatus() {
        Subject s = SecurityUtils.getSubject();
        BridgeUI.o("updateLoginStatus principal: " + s.getPrincipal());
        if (s.isAuthenticated()) {
            loginStatus.setValue("Hi " + s.getPrincipal());
        } else {
            loginStatus.setValue("");
            loginStatus.setVisible(false);
        }
    }

    /***
     * addAnonMenuItems adds the menu items visible to anonymous users i.e.
     * users who are signed in
     */

    protected void addAnonMenuItems() {
        main = menubar.addItem("Home", navigateTo(MainView.name));
        competions = menubar.addItem("Competions",
                navigateTo(UserCompetionsView.name));
        events = menubar.addItem("Events", navigateTo(UserEventsView.name));
        clubAdmin = menubar.addItem("Management", navigateTo(LoginView.name));
        login = menubar.addItem("Sign In", navigateTo(LoginView.name));
    }

    /***
     * addBasicMenuItems adds the menu items visible to the users who have
     * signed in the role basic
     */

    protected void addBasicMenuItems() {
        main = menubar.addItem("Home", navigateTo(MainView.name));
        personalData = menubar.addItem("Account", null);
        personalData.addItem("Personal Data",
                navigateTo(PersonalDataView.name));
        personalData.addItem("Change Password", navigateTo(PasswordView.name));
        competions = menubar.addItem("Competions",
                navigateTo(UserCompetionsView.name));
        events = menubar.addItem("Events", navigateTo(UserEventsView.name));
        clubAdmin = menubar.addItem("Management", navigateTo(LoginView.name));
        logout = menubar.addItem("Sign out", signOut());
    }

    /***
     * addClubAdminMenuItems adds the menu items visible to the users who have
     * signed in the role clubadmin
     */

    protected void addClubAdminMenuItems() {
        main = menubar.addItem("Home", navigateTo(MainView.name));
        personalData = menubar.addItem("Account", null);
        personalData.addItem("Personal Data",
                navigateTo(PersonalDataView.name));
        personalData.addItem("Change Password", navigateTo(PasswordView.name));

        competions = menubar.addItem("Competions",
                navigateTo(UserCompetionsView.name));
        events = menubar.addItem("Events", navigateTo(UserEventsView.name));
        allAdmin = menubar.addItem("Management", null);
        allAdmin.addItem("Result Upload", navigateTo(UploadView.name));
        allAdmin.addItem("Edit Masterpoints",
                navigateTo(MPRegistryEditView.name));
        allAdmin.addItem("Club Internals", navigateTo(ClubInternalsView.name));
        allAdmin.addItem("Membership Applications",
                navigateTo(ApplicationView.name));
        allAdmin.addItem("Competions", navigateTo(ClubCompetionsView.name));
        allAdmin.addItem("Events", navigateTo(ClubEventsView.name));
        logout = menubar.addItem("Sign out", signOut());
    }

    /***
     * addAdminMenuItems adds the menu items visible to the users who have
     * signed in the role admin
     */

    protected void addAdminMenuItems() {
        main = menubar.addItem("Home", navigateTo(MainView.name));
        personalData = menubar.addItem("Account", null);
        personalData.addItem("Personal Data",
                navigateTo(PersonalDataView.name));
        personalData.addItem("Change Password", navigateTo(PasswordView.name));
        competions = menubar.addItem("Competions",
                navigateTo(UserCompetionsView.name));
        events = menubar.addItem("Events", navigateTo(UserEventsView.name));
        allAdmin = menubar.addItem("Management", null);
        allAdmin.addItem("Result Upload", navigateTo(UploadView.name));
        allAdmin.addItem("Edit Masterpoints",
                navigateTo(MPRegistryEditView.name));
        allAdmin.addItem("Club Internals", navigateTo(ClubInternalsView.name));
        allAdmin.addItem("Membership Applications",
                navigateTo(ApplicationView.name));
        allAdmin.addItem("Competions", navigateTo(ClubCompetionsView.name));
        allAdmin.addItem("Events", navigateTo(ClubEventsView.name));

        allAdmin.addItem("User Data Editor",
                navigateTo(AdminPlayerDataEditor.name));
        allAdmin.addItem("Membership Applications",
                navigateTo(ApplicationView.name));
        allAdmin.addItem("Create a New Club",
                navigateTo(ClubsManagementView.name));
        logout = menubar.addItem("Sign out", signOut());
    }

    protected Command signOut() {
        return selectedItem -> close();
    }

    protected void close() {
        navigator.navigateTo(SignedOutView.name);
    }

    public void setLoggedInUser() {
        User u = BridgeUI.user.getCurrentUser();
        if (u != null) {
            loginStatus.setValue("Welcome, " + u.getUsername());
            logout.setVisible(false);
        }
    }

    /***
     * navigateTo builds a command which navigates to the view
     */

    private Command navigateTo(final String viewName) {
        return selectedItem -> navigator.navigateTo(viewName);
    }

}
