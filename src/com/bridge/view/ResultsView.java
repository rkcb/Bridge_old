package com.bridge.view;

import com.bridge.resultui.Results;
import com.bridge.ui.EVerticalLayout;
import com.bridge.ui.MainMenu;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

@SuppressWarnings("serial")
public class ResultsView extends EVerticalLayout implements View {

    public static String name = "/user/results";
    private MainMenu mainMenu;
    protected static Long id = null;
    protected static Results results = null;

    public ResultsView(MainMenu menu) {
        mainMenu = menu;
    }

    public static void loadResults(Long tourId) {
        id = tourId;
        results = new Results(id);
    }

    @Override
    public void enter(ViewChangeEvent event) {
        removeAllComponents();
        addComponent(mainMenu);
        if (results != null) {
            addComponent(results);
        }
    }

}
