package com.bridge.newcalendar;

import com.bridge.calendar.BridgeEventEditor;
import com.bridge.database.BridgeEvent;
import com.bridge.database.C;
import com.bridge.database.Club;
import com.bridge.ui.BridgeUI;
import com.bridge.ui.EHorizontalLayout;
import com.bridge.ui.EVerticalLayout;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.EventClick;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.EventClickHandler;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.RangeSelectEvent;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.RangeSelectHandler;
import com.vaadin.ui.components.calendar.handler.BasicEventMoveHandler;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class WhiteClubEventCalendar extends EVerticalLayout {
    protected EventCalendar calendar;
    protected MenuBar calendarMenu = new MenuBar();
    protected MenuItem clubSelectorTour;
    protected Label clubName = new Label();
    protected EVerticalLayout nameLayout = new EVerticalLayout(clubName);
    protected Panel clubNamePanel = new Panel(nameLayout);
    protected Object clubId = null; // usually current user club
                                    // but admin can change this
    // from ClubEventCalendar2
    protected Window window;
    protected BridgeEventEditor editor;
    // protected EventDialog eventDialog;
    protected Button cancel;
    protected Button remove;
    protected Button ok;
    protected Object eventId;
    protected BridgeEvent event;

    private void addCalendarMenuItems() {
        calendarMenu.addItem("Previous Month",
                command -> calendar.rollMonth(-1));
        calendarMenu.addItem("Month View", command -> calendar.setMonthView());
        calendarMenu.addItem("Next Month", command -> calendar.rollMonth(1));
    }

    private void addEventHandlers() {
        calendar.setHandler((RangeSelectHandler) event -> {
            eventId = null;
            openEditor(event);
        });

        // handles clicks of existing events
        calendar.setHandler((EventClickHandler) event -> openEditor(event));

        BasicEventMoveHandler bmh = null;
        calendar.setHandler(bmh);
    }

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
     * addCalendarWidgets adds all extra widgets around the calendar
     */
    protected void addCalendarWidgets() {
        addCalendarMenuItems();

        addComponents(calendarMenu, clubNamePanel, calendar);
        clubNamePanel.setSizeUndefined();

        clubName.setStyleName(ValoTheme.LABEL_BOLD);
        setComponentAlignment(clubNamePanel, Alignment.MIDDLE_CENTER);
        setComponentAlignment(calendarMenu, Alignment.MIDDLE_CENTER);
    }

    /***
     * openEditor creates a dialog for a new event
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

    public WhiteClubEventCalendar() {
        calendar = new EventCalendar(); // new ClubEventCalendar2();
        calendar.setMonthView();
        calendar.setFirstVisibleHourOfDay(9);
        calendar.setLastVisibleHourOfDay(22);
        setImmediate(true);
        setSizeUndefined();
        addCalendarWidgets();

        addEventHandlers();
        addClubSelector();
    }

    /***
     * setClubName sets the club name using club id
     */
    public void setClubName(Object clubId) {
        if (clubId != null) {
            C<Club> cs = new C<>(Club.class);
            clubName.setValue(cs.get(clubId).getName());
        } else {
            clubName.setValue("");
        }
    }

    /***
     * refreshSelectedClub refreshes the calendar event to reflect clubId value
     */
    public void refreshSelectedClub() {
        calendar.filterEventOwnerId(clubId);
        setClubName(clubId);
    }

    /***
     * addClubSelector adds a menu item to show a club selector for the
     * tournaments to show i.e. admin (and only) user can choose the club of
     * intrest
     */

    public void addClubSelector() {

        if (clubSelectorTour == null) {
            clubSelectorTour = calendarMenu.addItem("Set Club", command -> {

                Window window = new Window("Select Club for Calendar Events");
                window.setWidth("400px");
                window.setHeight("200px");
                getUI().addWindow(window);
                window.center();
                window.setModal(true);
                C<Club> cs = new C<>(Club.class);
                ComboBox clubs = new ComboBox("Club", cs.c());
                clubs.setItemCaptionMode(ItemCaptionMode.ITEM);

                clubs.setFilteringMode(FilteringMode.CONTAINS);
                Button done = new Button("Done");
                done.addClickListener(listener -> {
                    window.close();
                    Object id = clubs.getValue();
                    if (id != null) {
                        calendar.filterEventOwnerId(id);
                        setClubName(id);
                        calendar.markAsDirty();
                    }
                });

                EVerticalLayout l = new EVerticalLayout(clubs, done);

                window.setContent(l);
            });
        }
    }

}
