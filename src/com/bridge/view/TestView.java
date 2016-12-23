package com.bridge.view;

import com.bridge.ui.EVerticalLayout;
import com.bridge.ui.MainMenu;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

@SuppressWarnings("serial")
public class TestView extends EVerticalLayout implements View {
    public static final String name = "/test";
    private MainMenu mainMenu;

    public TestView(MainMenu mainMenu) {
        this.mainMenu = mainMenu;
        setSizeUndefined();
    }

    @Override
    public void enter(ViewChangeEvent event) {
        addComponents(mainMenu);
    }

}
