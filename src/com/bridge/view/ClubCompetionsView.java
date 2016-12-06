package com.bridge.view;

import com.bridge.newcalendar.WhiteClubCompetionCalendar;
import com.bridge.ui.EVerticalLayout;
import com.bridge.ui.MainMenu;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

@SuppressWarnings("serial")
public class ClubCompetionsView extends EVerticalLayout implements View {

    public static final String name = "/admin/competions";

    private WhiteClubCompetionCalendar calendar;
    private MainMenu mainMenu;

    public ClubCompetionsView(MainMenu mainMenu) {
        this.mainMenu = mainMenu;
        calendar = new WhiteClubCompetionCalendar();
        calendar.addClubSelector();
    }

    @Override
    public void enter(ViewChangeEvent event) {
        calendar.refreshSelectedClub();
        addComponents(mainMenu, calendar);
    }

}
