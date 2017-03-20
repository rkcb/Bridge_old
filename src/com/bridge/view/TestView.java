package com.bridge.view;

import com.bridge.resultui.ScoreTable2;
import com.bridge.resultui.TotalScoreTable2;
import com.bridge.ui.BridgeUI;
import com.bridge.ui.EVerticalLayout;
import com.bridge.ui.MainMenu;
import com.pbn.pbnjson.JsonEvents;
import com.pbn.tools.Tools;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

@SuppressWarnings("serial")
public class TestView extends EVerticalLayout implements View {
    public static final String name = "/test";
    private MainMenu mainMenu;
    private TotalScoreTable2 tst;
    private ScoreTable2 st;
    // private TableGrid tst = new TableGrid()

    public TestView(MainMenu mainMenu) {
        // JsonEvents events = new JsonEvents(null);
        this.mainMenu = mainMenu;
        setSizeUndefined();
        JsonEvents jevents = new JsonEvents(Tools.rawEvents("sm1"));
        if (jevents.get(0).getScoreTable().numberColumns() != null) {
            BridgeUI.o("no number columns");
        } else {
            BridgeUI.o("Number columns NOT empty");
        }
        st = new ScoreTable2(jevents);
        st.score("23");
    }

    @Override
    public void enter(ViewChangeEvent event) {
        addComponents(mainMenu, st);
    }

}
