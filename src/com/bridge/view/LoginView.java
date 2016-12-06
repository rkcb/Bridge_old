package com.bridge.view;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import com.bridge.database.C;
import com.bridge.database.ShiroRole;
import com.bridge.ui.BridgeUI;
import com.bridge.ui.EFormLayout;
import com.bridge.ui.EVerticalLayout;
import com.bridge.ui.MainMenu;
import com.vaadin.data.Container.Filter;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
public class LoginView extends EVerticalLayout implements View {

    public static final String name = "/login";

    private MainMenu mainMenu;
    // private Navigator navigator = UI.getCurrent().getNavigator();
    // private Label loginLabel = new Label();
    private String caption = "<p style=\"color:red\">Error in signing in - please try again</p>";
    private String caption2 = "<p style=\"color:red\">Error: you don't have this role</p>";
    private Label loginError = new Label(caption, ContentMode.HTML);
    private Label roleError = new Label(caption2, ContentMode.HTML);
    private TextField username = new TextField("Username");
    private PasswordField password = new PasswordField("Password");
    private Button signin = new Button("Sign in");
    private Button newUser = new Button("New User? Create Account");
    private C<ShiroRole> shiroRoles = new C<>(ShiroRole.class);
    private ComboBox roles = new ComboBox("Roles", shiroRoles.c());
    private EFormLayout form = new EFormLayout(loginError, roleError, username,
            password, roles, signin, newUser);

    private Panel panel = new Panel(form);

    public LoginView(final MainMenu menu) {
        super();
        roles.setNullSelectionAllowed(false);
        setupRoles();

        loginError.setValue(caption);
        roleError.setValue(caption2);

        mainMenu = menu;
        addComponents(mainMenu, panel);
        panel.setWidth("650px");
        loginError.setVisible(false);
        roleError.setVisible(false);
        addSignIn();
        addNewUser();

        signin.setClickShortcut(KeyCode.ENTER, null);
    }

    private void setupRoles() {
        roles.setItemCaptionMode(ItemCaptionMode.ITEM);
        if (roles.size() > 0) {
            Filter f = shiroRoles.filterEq("name", "basic");
            ShiroRole r = shiroRoles.at(0);
            shiroRoles.removeFilter(f);
            roles.setValue(r.getId());
            roles.setNullSelectionAllowed(false);
        }
    }

    private void addSignIn() {
        signin.addClickListener(listener -> {
            Subject s = SecurityUtils.getSubject();
            UsernamePasswordToken token = new UsernamePasswordToken(
                    username.getValue(), password.getValue());

            try {
                s.login(token);

                loginError.setVisible(false);
                roleError.setVisible(false);

                if (s.isAuthenticated() && hasSelectedRole()) {

                    String role = shiroRoles.get(roles.getValue()).getName();
                    BridgeUI.o("selected role: " + role);
                    BridgeUI.role = role;
                    BridgeUI.setViewManagerState(role);
                    UI.getCurrent().getNavigator().navigateTo(MainView.name);
                } else if (s.isAuthenticated()) {
                    roleError.setVisible(true);
                }
            } catch (AuthenticationException e) {
                e.printStackTrace();
                loginError.setVisible(true);
                password.setValue("");
            }

        });
    }

    private void addNewUser() {
        newUser.addClickListener(listener -> {
            UI.getCurrent().getNavigator()
                    .navigateTo(UserRegistrationView.name);
        });

    }

    private boolean hasSelectedRole() {
        Object sel = roles.getValue();
        return SecurityUtils.getSubject()
                .hasRole(shiroRoles.get(sel).getName());
    }

    protected String getRoleName() {
        Object id = roles.getValue();
        return roles.getValue() == null ? "anon" : shiroRoles.get(id).getName();
    }

    @Override
    public void enter(ViewChangeEvent event) {
        addComponents(mainMenu, panel);
        username.setValue("admin");
        password.setValue("xxx");
    }

}
