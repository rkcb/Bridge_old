package com.bridge.newcalendar;

import com.bridge.database.C;
import com.bridge.database.Club;
import com.bridge.ui.BridgeUI;
import com.bridge.ui.EVerticalLayout;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.EventClickHandler;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.RangeSelectHandler;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class UploadCalendar extends EVerticalLayout {

    protected TournamentCalendar calendar;
    protected MenuBar calendarMenu;
    protected MenuItem clubSelectorTour;
    protected Label clubName;
    protected EVerticalLayout nameLayout;
    protected Panel clubNamePanel;
    protected Object clubId;

    private void addCalendarMenuItems() {
        calendarMenu.addItem("Previous Month",
                command -> calendar.rollMonth(-1));
        calendarMenu.addItem("Month View", command -> calendar.setMonthView());
        calendarMenu.addItem("Next Month", command -> calendar.rollMonth(1));
    }

    /***
     * addCalendarWidgets adds all extra widgets around the calendar
     */
    protected void addCalendarWidgets() {

        calendarMenu = new MenuBar();
        clubName = new Label();
        nameLayout = new EVerticalLayout(clubName);
        clubNamePanel = new Panel(nameLayout);

        addCalendarMenuItems();

        addComponents(calendarMenu, clubNamePanel, calendar);
        clubNamePanel.setSizeUndefined();

        clubName.setStyleName(ValoTheme.LABEL_BOLD);
        setComponentAlignment(clubNamePanel, Alignment.MIDDLE_CENTER);
        setComponentAlignment(calendarMenu, Alignment.MIDDLE_CENTER);

        addComponents(calendarMenu, clubNamePanel, calendar);
        // nameLayout.setComponentAlignment(clubName, Alignment.MIDDLE_CENTER);
        clubNamePanel.setSizeUndefined();

        clubName.setStyleName(ValoTheme.LABEL_BOLD);
        setComponentAlignment(clubNamePanel, Alignment.MIDDLE_CENTER);
        setComponentAlignment(calendarMenu, Alignment.MIDDLE_CENTER);
    }

    public UploadCalendar() {
        calendar = new TournamentCalendar();
        setImmediate(true);
        setSizeUndefined();
        calendar.setFirstVisibleHourOfDay(9);
        calendar.setLastVisibleHourOfDay(22);

        addCalendarWidgets();
        calendar.setMonthView();

    }

    /***
     * setCurrentClub preserves only events or tournaments which belong to the
     * current user; if user is not sign in then
     *
     */

    public void setCurrentClub() {
        Object id = BridgeUI.user.getCurrentClubId();
        if (id != null) {
            calendar.filterEventOwnerId(id);
            setClubName(id);
        }
    }

    public void setEventHandler(EventClickHandler h) {
        calendar.setHandler(h);
    }

    public void setRangeHandler(RangeSelectHandler h) {
        calendar.setHandler(h);
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
                clubs.setNullSelectionAllowed(false);

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

    public void filterEventOwnerId(Object clubId) {
        calendar.filterEventOwnerId(clubId);
    }

    public void setClubName(Object clubId) {
        if (clubId != null) {
            C<Club> cs = new C<>(Club.class);
            clubName.setValue(cs.get(clubId).getName());
        }
    }

}