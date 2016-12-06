package com.bridge.newcalendar;

import com.bridge.database.C;
import com.bridge.database.Club;
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
import com.vaadin.ui.themes.ValoTheme;

/***
 * calendar which is for user calendar events only
 */

@SuppressWarnings("serial")
public class WhiteUserEventCalendar extends EVerticalLayout {

    protected EventCalendar calendar;
    protected MenuBar calendarMenu = new MenuBar();
    protected MenuItem clubSelectorTour;
    protected Label clubName = new Label();
    protected EVerticalLayout nameLayout = new EVerticalLayout(clubName);
    protected Panel clubNamePanel = new Panel(nameLayout);
    protected Object clubId = null; // usually current user club

    private void addCalendarMenuItems() {
        calendarMenu.addItem("Previous Month",
                command -> calendar.rollMonth(-1));
        calendarMenu.addItem("Month View", command -> calendar.setMonthView());
        calendarMenu.addItem("Next Month", command -> calendar.rollMonth(1));
    }

    public WhiteUserEventCalendar() {
        calendar = new EventCalendar();
        calendar.setMonthView();
        calendar.setFirstVisibleHourOfDay(9);
        calendar.setLastVisibleHourOfDay(22);
        setImmediate(true);
        setSizeUndefined();

        addCalendarWidgets();
    }

    protected void addCalendarWidgets() {
        addCalendarMenuItems();

        addComponents(calendarMenu, clubNamePanel, calendar);
        clubNamePanel.setSizeUndefined();

        clubName.setStyleName(ValoTheme.LABEL_BOLD);
        setComponentAlignment(clubNamePanel, Alignment.MIDDLE_CENTER);
        setComponentAlignment(calendarMenu, Alignment.MIDDLE_CENTER);
    }

    public void setEventHandler(EventClickHandler h) {
        calendar.setHandler(h);
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
                        // filterTournament(isTournament);
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
