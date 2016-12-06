package com.bridge.view;

import com.bridge.newcalendar.WhiteClubEventCalendar;
import com.bridge.ui.EVerticalLayout;
import com.bridge.ui.MainMenu;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

@SuppressWarnings("serial")
public class ClubEventsView extends EVerticalLayout implements View {

    public static final String name = "/admin/events";

    private MainMenu mainMenu;
    private WhiteClubEventCalendar calendar;

    public ClubEventsView(MainMenu mainMenu) {
        this.mainMenu = mainMenu;
        calendar = new WhiteClubEventCalendar();
        calendar.addClubSelector();
    }

    @Override
    public void enter(ViewChangeEvent event) {
        calendar.refreshSelectedClub();
        addComponents(mainMenu, calendar);
        // TODO: event filter must be handled
        // if admin view filters H6 and user view other
    }

}
