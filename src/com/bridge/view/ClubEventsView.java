package com.bridge.view;

import com.bridge.calendar.BridgeEventEditor;
import com.bridge.calendar.WhiteCalendar;
import com.bridge.database.BridgeEvent;
import com.bridge.ui.BridgeUI;
import com.bridge.ui.EHorizontalLayout;
import com.bridge.ui.EVerticalLayout;
import com.bridge.ui.MainMenu;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.EventClick;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.EventClickHandler;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.RangeSelectEvent;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.RangeSelectHandler;

@SuppressWarnings("serial")
public class ClubEventsView extends EVerticalLayout implements View {

    public static final String name = "/admin/events";

    protected MainMenu mainMenu;
    // private WhiteClubEventCalendar calendar;
    protected WhiteCalendar calendar;
    protected Window window;
    protected BridgeEventEditor editor;
    // protected EventDialog eventDialog;
    protected Button cancel;
    protected Button remove;
    protected Button ok;
    protected Object eventId;
    protected BridgeEvent event;

    /***
     * addNewEventButtonsAndActions creates dialog buttons and their actions for
     * a new event
     */

    protected void addNewEventButtonsAndActions() {
        cancel = new Button("Cancel", listener -> {
            window.close();
        });

        ok = new Button("Ok", listener -> {
            BridgeEvent e = editor.getValue();
            if (e != null) {
                e.setOwner(BridgeUI.user.getCurrentClub());
                calendar.addEvent(e);
                window.close();
            } else {
                Notification.show("Check required fields", Type.ERROR_MESSAGE);
            }

        });
    }

    /***
     * addOldEventButtonsAndActions creates dialog buttons and their actions for
     * a old event
     */

    protected void addOldEventButtonsAndActions() {

        cancel = new Button("Cancel", listener -> {
            editor.cancel();
            window.close();
        });
        remove = new Button("Remove", listener -> {
            calendar.removeEvent(eventId);
            window.close();
        });

        ok = new Button("Ok", listener -> {
            if (editor.areEventFieldsValid()) {
                try {
                    editor.commitCalendarEvent();
                    super.markAsDirty();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                calendar.markAsDirty();
                window.close();
            } else {
                Notification.show("Check required fields", Type.ERROR_MESSAGE);
            }
        });
    }

    /***
     * openEditor creates a dialog for the clicked event
     */

    protected void openEditor(EventClick event) {
        window = new Window("Edit Event Details");
        addOldEventButtonsAndActions();

        boolean isTournament = false;
        editor = new BridgeEventEditor(isTournament);
        EHorizontalLayout hl = new EHorizontalLayout(cancel, remove, ok);
        hl.setMargin(false);
        EVerticalLayout vl = new EVerticalLayout(editor, hl);
        vl.setComponentAlignment(hl, Alignment.MIDDLE_RIGHT);
        window.setContent(vl);
        this.event = (BridgeEvent) event.getCalendarEvent();
        eventId = this.event.getId();
        if (eventId != null) {
            editor.setItemSource(calendar.item(eventId));
        }

        getUI().addWindow(window);
        window.center();
        window.setModal(true);
    }

    /***
     * openEditor creates a dialog for defining a new event
     */

    protected void openEditor(RangeSelectEvent event) {
        window = new Window("Edit Event Details");
        addNewEventButtonsAndActions();
        editor = new BridgeEventEditor(false);
        editor.initialize(new BridgeEvent(event.getStart(), event.getEnd()));

        EHorizontalLayout hl = new EHorizontalLayout(cancel, ok);
        hl.setMargin(false);
        EVerticalLayout vl = new EVerticalLayout(editor, hl);
        vl.setComponentAlignment(hl, Alignment.MIDDLE_RIGHT);
        window.setContent(vl);
        getUI().addWindow(window);
        window.center();
        window.setModal(true);
    }

    public ClubEventsView(MainMenu mainMenu) {
        this.mainMenu = mainMenu;
        calendar = WhiteCalendar.getEventCalendar();
        calendar.setHandler((EventClickHandler) event -> openEditor(event));
        calendar.setHandler((RangeSelectHandler) event -> {
            eventId = null;
            openEditor(event);
        });
    }

    @Override
    public void enter(ViewChangeEvent event) {
        addComponents(mainMenu, calendar.getCompositeCalendar());
        calendar.refreshSelectedClub();
        // TODO: event filter must be handled
        // if admin view filters H6 and user view other
    }

}
