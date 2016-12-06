package com.bridge.newcalendar;

import java.util.Date;

import com.bridge.database.BridgeEvent;
import com.bridge.database.C;
import com.bridge.database.Club;
import com.bridge.database.Tournament;
import com.bridge.ui.EVerticalLayout;
import com.vaadin.addon.jpacontainer.EntityItem;
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
import com.vaadin.ui.components.calendar.CalendarComponentEvents.EventClick;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.EventClickHandler;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.RangeSelectHandler;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class WhiteClubCompetionCalendar extends EVerticalLayout {

    protected TournamentCalendar calendar;
    protected MenuBar calendarMenu = new MenuBar();
    protected MenuItem clubSelectorTour;
    protected Label clubName = new Label();
    protected EVerticalLayout nameLayout = new EVerticalLayout(clubName);
    protected Panel clubNamePanel = new Panel(nameLayout);
    protected Object clubId = null;

    protected TournamentManager2 manager;
    protected Window window;
    protected OldCompetionDialog2 editOld;
    protected NewCompetionDialog2 editNew;

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
        addCalendarMenuItems();

        addComponents(calendarMenu, clubNamePanel, calendar);
        clubNamePanel.setSizeUndefined();

        clubName.setStyleName(ValoTheme.LABEL_BOLD);
        setComponentAlignment(clubNamePanel, Alignment.MIDDLE_CENTER);
        setComponentAlignment(calendarMenu, Alignment.MIDDLE_CENTER);
    }

    protected void editOldCompetion(EventClick event) {
        BridgeEvent e = (BridgeEvent) event.getCalendarEvent();

        EntityItem<BridgeEvent> ei = manager.getEventItem(e.getId());
        EntityItem<Tournament> ti = manager
                .getTournamentItem(e.getTournament().getId());
        editOld.editItems(ei, ti);

        window.setContent(editOld);
        window.setModal(true);
        getUI().addWindow(window);
    }

    /***
     * editNewCompetion adds a dialog to edit a new tournament
     */

    protected void editNewCompetion(Date start, Date end) {
        editNew.createTournament(start, end);
        window.setContent(editNew);
        window.setModal(true);
        getUI().addWindow(window);
        editNew.setWindow(window);
    }

    private void addCalendarEventHandlers() {
        calendar.setHandler((EventClickHandler) event -> {
            window = new Window();
            editOld = new OldCompetionDialog2(manager, window);
            editOldCompetion(event);
        });

        calendar.setHandler((RangeSelectHandler) event -> {
            window = new Window();
            editNew = new NewCompetionDialog2(manager, window);
            editNewCompetion(event.getStart(), event.getEnd());
        });

    }

    // =====================================================================
    public WhiteClubCompetionCalendar() {
        calendar = new TournamentCalendar();
        calendar.setMonthView();
        calendar.setFirstVisibleHourOfDay(9);
        calendar.setLastVisibleHourOfDay(22);
        setImmediate(true);
        setSizeUndefined();

        manager = new TournamentManager2(calendar);

        addCalendarWidgets();
        addCalendarEventHandlers();

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
