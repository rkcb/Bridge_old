package com.bridge.view;

import com.bridge.calendar.BridgeEventReader;
import com.bridge.calendar.WhiteCalendar;
import com.bridge.database.BridgeEvent;
import com.bridge.ui.BridgeUI;
import com.bridge.ui.EVerticalLayout;
import com.bridge.ui.MainMenu;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.Table;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.EventClickHandler;

@SuppressWarnings("serial")
public class UserEventsView extends EVerticalLayout implements View {

    public static final String name = "/user/events";

    private Window window = new Window();
    private BridgeEventReader reader = new BridgeEventReader(window);
    private TabSheet contents = new TabSheet();
    private Tab readerDialogTab = null;
    private Tab participantsTab = null;
    // private WhiteUserEventCalendar calendar = new WhiteUserEventCalendar();
    private WhiteCalendar calendar = WhiteCalendar.getEventCalendar();

    private MainMenu mainMenu;

    /***
     * setEventClickHandler adds an event click handler which is called when an
     * existing event is clicked
     */

    private void setEventClickHandler() {
        calendar.setHandler((EventClickHandler) event -> {
            if (event.getCalendarEvent() instanceof BridgeEvent) {
                BridgeEvent e = (BridgeEvent) event.getCalendarEvent();

                // load the clicked event to the dialog
                if (e.isRegistration() && participantsTab == null) {
                    setParticipantsTab(e);
                }

                reader.initialize(e, BridgeUI.user.getCurrentUsername());

                window.setContent(new EVerticalLayout(contents));
                window.setModal(true);
                window.setCaption("Calendar Event");

                getUI().addWindow(window);
            }
        });
    }

    protected void setParticipantsTab(BridgeEvent event) {
        Table table = reader.createParticipantsTable(event.getParticipants());
        EVerticalLayout v = new EVerticalLayout(table);
        participantsTab = contents.addTab(v);
        readerDialogTab.setCaption("Event");
        participantsTab.setCaption("Participants");
    }

    public UserEventsView(MainMenu mainMenu) {
        this.mainMenu = mainMenu;
        contents.setSizeUndefined();
        readerDialogTab = contents.addTab(reader.getDialog());
        setEventClickHandler();
    }

    @Override
    public void enter(ViewChangeEvent event) {
        calendar.refreshSelectedClub();
        addComponents(mainMenu, calendar.getCompositeCalendar());
    }

}
