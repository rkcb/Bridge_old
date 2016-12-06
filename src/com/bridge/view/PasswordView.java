package com.bridge.view;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.UnavailableSecurityManagerException;
import org.apache.shiro.authc.AuthenticationException;

import com.bridge.database.C;
import com.bridge.database.User;
import com.bridge.ui.BridgeUI;
import com.bridge.ui.EVerticalLayout;
import com.bridge.ui.MainMenu;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.PasswordField;

@SuppressWarnings("serial")
public class PasswordView extends EVerticalLayout implements View {

    public static final String name = "/user/password";

    protected PasswordField pwOld = new PasswordField("Old Password");
    protected PasswordField pwNew1 = new PasswordField("New Password");
    protected PasswordField pwNew2 = new PasswordField("New Password Again");
    protected Button submit = new Button("Submit Change");
    protected Label message = new Label();
    protected MainMenu mainMenu;
    protected EVerticalLayout l = new EVerticalLayout(pwOld, pwNew1, pwNew2,
            message, submit);
    protected Panel panel = new Panel(l);
    protected C<User> us = new C<>(User.class);

    public PasswordView(MainMenu menu) {
        mainMenu = menu;
        message.setValue("Your password has been changed");
        l.setSizeUndefined();
        panel.setWidth("600px");
        l.setComponentAlignment(pwOld, Alignment.MIDDLE_CENTER);
        l.setComponentAlignment(pwNew1, Alignment.MIDDLE_CENTER);
        l.setComponentAlignment(pwNew2, Alignment.MIDDLE_CENTER);
        l.setComponentAlignment(message, Alignment.MIDDLE_CENTER);
        l.setComponentAlignment(submit, Alignment.MIDDLE_RIGHT);

        if (SecurityUtils.getSubject().isAuthenticated()) {
            User u = BridgeUI.user.getCurrentUser();
            pwOld.setRequired(true);
            pwNew1.setRequired(true);
            pwNew2.setRequired(true);
            pwNew2.addValidator(new StringLengthValidator(
                    "Password must be at least 3-40 long", 3, 40, false));
            pwNew2.addValidator(value -> {
                if (!pwNew1.getValue().matches(pwNew2.getValue())) {
                    throw new InvalidValueException(
                            "New passwords do not match");
                }
                if (pwNew2.getValue().length() < 4) {
                    throw new InvalidValueException(
                            "New password must be at least 8 characters long");
                }

            });

            pwOld.addValidator(value -> {
                try {
                } catch (AuthenticationException e1) {
                    throw new InvalidValueException(
                            "Old password is incorrect");
                } catch (UnavailableSecurityManagerException e2) {
                    e2.printStackTrace();
                }
            });

            submit.addClickListener(listener -> {
                try {
                    message.setVisible(false);

                    pwOld.validate();
                    pwNew2.validate();

                    String enc = User.passwordService
                            .encryptPassword(pwNew1.getValue());
                    us.set(u.getId(), "password", enc);
                    if (User.passwordService.passwordsMatch(pwNew1.getValue(),
                            us.get(u.getId()).getPassword())) {
                        BridgeUI.o("New password matches");
                    }

                    pwOld.setValue("");
                    pwNew1.setValue("");
                    pwNew2.setValue("");

                    message.setVisible(true);
                } catch (InvalidValueException e) {
                }

            });
        }
    }

    @Override
    public void enter(ViewChangeEvent event) {
        addComponents(mainMenu, panel);
        l.setComponentAlignment(message, Alignment.MIDDLE_RIGHT);
        message.setVisible(false);
    }

}
