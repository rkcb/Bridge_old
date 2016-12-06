package com.bridge.view;

import com.bridge.calendar.BridgeEventProvider;
import com.bridge.calendar.CalendarEventColorPanel;
import com.bridge.calendar.WhiteCalendar;
import com.bridge.ui.EVerticalLayout;
import com.bridge.ui.MainMenu;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

@SuppressWarnings("serial")
public class TestView extends EVerticalLayout implements View {
    public static final String name = "/test";
    private MainMenu mainMenu;
    private BridgeEventProvider provider;
    private WhiteCalendar calendar;
    private CalendarEventColorPanel colorPanel;

    public TestView(MainMenu mainMenu) {
        this.mainMenu = mainMenu;
        provider = BridgeEventProvider.getCalendarEventProvider();
        calendar = new WhiteCalendar(provider, "Event 1", "Event 2", "Event 3",
                "Event 4", "Event 5");
        setSizeUndefined();
    }

    @Override
    public void enter(ViewChangeEvent event) {
        addComponents(mainMenu, calendar.getCompositeCalendar());
    }

}
