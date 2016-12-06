package com.bridge.newcalendar;

import com.bridge.ui.EVerticalLayout;
import com.bridge.ui.MainMenu;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

@SuppressWarnings("serial")
public class NewClubCompetionsView extends EVerticalLayout implements View {

    public static final String name = "/new/admin/competions";

    private WhiteClubCompetionCalendar calendar;
    private MainMenu mainMenu;

    public NewClubCompetionsView(MainMenu mainMenu) {
        this.mainMenu = mainMenu;
        calendar = new WhiteClubCompetionCalendar();
        calendar.addClubSelector();
    }

    @Override
    public void enter(ViewChangeEvent event) {
        addComponents(mainMenu, calendar);
    }

}
