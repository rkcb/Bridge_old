package com.bridge.view;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class SignedOutView extends VerticalLayout implements View {
    public static final String name = "/user/signedout";
    private Label l = new Label("You have signed out");

    public SignedOutView() {
    }

    @Override
    public void enter(ViewChangeEvent event) {
        addComponent(l);
        setMargin(true);
        addComponents(l);
        Subject s = SecurityUtils.getSubject();
        s.logout();

        // getUI();
        // getUI().getSession().close();
        // VaadinService.getCurrentRequest().getWrappedSession().invalidate();
        // UI ui = UI.getCurrent();
        // BridgeUI bui = (BridgeUI) ui;
        // getUI().getPage().setLocation("http://bridge.fi");
        // BridgeUI.setViewManagerState("anon");
    }

}
