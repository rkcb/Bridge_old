package com.bridge.view;

import com.bridge.ui.EVerticalLayout;
import com.bridge.ui.MainMenu;
import com.pbn.pbnjson.JsonEvents;
import com.pbn.pbnjson.JsonTotalScoreTable;
import com.pbn.tools.Tools;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

@SuppressWarnings("serial")
public class TestView extends EVerticalLayout implements View {
    public static final String name = "/test";
    private MainMenu mainMenu;
    // private TableGrid tst = new TableGrid()

    public TestView(MainMenu mainMenu) {
        // JsonEvents events = new JsonEvents(null);
        this.mainMenu = mainMenu;
        setSizeUndefined();
        JsonEvents events = new JsonEvents(Tools.rawEvents("sm1"));

        JsonTotalScoreTable tst = events.totalScoreTable();

    }

    @Override
    public void enter(ViewChangeEvent event) {
        addComponents(mainMenu);
    }

}
