package com.bridge.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.bridge.ui.BridgeUI;
import com.bridge.ui.MainMenu;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;

/***
 * ViewManager checks that each view is only shown if user is authorized to use
 * it. ViewManager is in exactly one role: anon, basic, clubadmin or admin. It
 * will show only views belonging to this role.
 *
 * A new view must be added to ViewManager
 */

public class ViewManager {

    protected final String anon = "anon";
    protected final String basic = "basic";
    protected final String clubadmin = "clubadmin";
    protected final String admin = "admin";
    protected String role = anon;
    protected Navigator navigator;
    protected final String[] r = new String[] { anon, basic, clubadmin, admin };
    @SuppressWarnings("unchecked")
    protected final ArrayList<String> roles = new ArrayList<>(
            Arrays.asList(r));
    protected List<String> permittedViews = new ArrayList<>();

    public ViewManager(Navigator n) {
        navigator = n;
    }

    public void removeViews() {
        for (String name : permittedViews) {
            navigator.removeView(name);
        }
        permittedViews.clear();
    }

    public List<String> permittedSet() {
        return permittedViews;
    }

    /***
     * getRole returns the role of current user Note: user may have several
     * roles
     */

    public String getRole() {
        return role;
    }

    protected void addView(String viewName, View view) {
        navigator.addView(viewName, view);
        permittedViews.add(viewName);
    }

    /***
     * addView adds views to the navigator AND to the permitted view name set.
     * To add a view add it here to proper place and update MainManu accordingly
     * NOTE: "higher" role can see everything that "lower" role sees
     */

    public void addViews(String role) {
        this.role = role;
        removeViews();
        BridgeUI.o("addviews with role: " + role);
        int index = roles.indexOf(role);

        MainMenu mainMenu = new MainMenu(navigator, role);

        LoginView loginView = new LoginView(mainMenu);
        navigator.setErrorView(loginView);

        // >>>>>>>>>>>>>> TEMP only >>>>>>>>>>>>>>>
        addView(TestView.name, new TestView(mainMenu));

        // these views are shown to all; the view nature depends on the role
        addView(MainView.name, new MainView(mainMenu, navigator));
        addView(LoginView.name, new LoginView(mainMenu));
        addView(SignedOutView.name, new SignedOutView());
        addView(UserEventsView.name, new UserEventsView(mainMenu));
        addView(UserCompetionsView.name, new UserCompetionsView(mainMenu));
        addView(UserRegistrationView.name, new UserRegistrationView(mainMenu));
        addView(MPRegistryView.name, new MPRegistryView(mainMenu));
        addView(ResultsView.name, new ResultsView(mainMenu));
        addView(UserNewsView.name, new UserNewsView(mainMenu));

        if (roles.indexOf(basic) <= index) { // these are shown to all "basic"
                                             // users
            addView(PersonalDataView.name, new PersonalDataView(mainMenu));
            addView(PasswordView.name, new PasswordView(mainMenu));
            addView(UserClubMembersView.name,
                    new UserClubMembersView(mainMenu));
        }

        if (roles.indexOf(clubadmin) <= index) { // these are shown to all
                                                 // "clubadmin"s
            addView(ClubCompetionsView.name, new ClubCompetionsView(mainMenu));
            addView(ClubEventsView.name, new ClubEventsView(mainMenu));
            addView(ClubInternalsView.name, new ClubInternalsView(mainMenu));
            addView(UploadView.name, new UploadView(mainMenu));
            addView(ApplicationView.name, new ApplicationView(mainMenu));
            addView(MPRegistryEditView.name, new MPRegistryEditView(mainMenu));
        }

        if (roles.indexOf(admin) <= index) { // these are shown to all "admin"s
            addView(ClubsManagementView.name,
                    new ClubsManagementView(mainMenu));
            addView(AdminPlayerDataEditor.name,
                    new AdminPlayerDataEditor(mainMenu));
            addView(ApplicationView.name, new ApplicationView(mainMenu));
        }

    }

}
