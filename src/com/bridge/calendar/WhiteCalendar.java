package com.bridge.calendar;

import java.util.Date;

import com.bridge.database.BridgeEvent;
import com.bridge.database.C;
import com.bridge.database.Club;
import com.bridge.ui.EHorizontalLayout;
import com.bridge.ui.EVerticalLayout;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.calendar.handler.BasicEventMoveHandler;
import com.vaadin.ui.components.calendar.handler.BasicEventResizeHandler;

/***
 * WhiteCalendar is used for tournament and ordinary calendar events.
 * BridgeEventProvider is meant to be filtered such that it is only for
 * tournament events or calendar events; actions for calendar event clicks are
 * handled in the classes that derive from this class
 */
@SuppressWarnings("serial")
public class WhiteCalendar extends Calendar {

    private BridgeEventProvider eventProvider;
    private MenuBar calendarMenu;
    private CalendarEventColorPanel colorPanel;
    private Object clubId;
    private Label clubName;

    /***
     * setClubName sets the club name using club id
     */
    private void setClubName(Object clubId) {
        if (clubId != null) {
            C<Club> cs = new C<>(Club.class);
            clubName.setValue(cs.get(clubId).getName());
        } else {
            clubName.setValue("");
        }
    }

    /***
     * filterEventOwner preserves only those events which belong to the club and
     * removes possible existing club filter
     */

    private void filterEventOwnerId(Object clubId) {
        eventProvider.filterClubId(clubId); // clubId null value removes filter
    }

    private void createMenuBar() {
        calendarMenu = new MenuBar();

        calendarMenu.addItem("Previous Month", command -> rollMonth(-1));
        calendarMenu.addItem("Month View", command -> setMonthView());
        calendarMenu.addItem("Next Month", command -> rollMonth(1));
        calendarMenu.addItem("Set Club", command -> {
            // add club filter command
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
                filterEventOwnerId(id);
                setClubName(id);
                markAsDirty();
            });

            EVerticalLayout l = new EVerticalLayout(clubs, done);

            window.setContent(l);
        });
    }

    /***
     * addEvent adds an event to calendar and then marks the calendar dirty
     */

    protected void addEvent(BridgeEvent event) {
        eventProvider.addEvent(event);
        markAsDirty();
    }

    /***
     * removeEvent removes the event
     */

    protected void removeEvent(Object eventId) {
        eventProvider.removeItem(eventId);
        markAsDirty();
    }

    private void doUISettings() {
        setMonthView();
        addStyleName("whiteCalendar");
        setTimeFormat(TimeFormat.Format24H);
    }

    /***
     * refreshSelectedClub refreshes the calendar event to reflect clubId value
     */
    public void refreshSelectedClub() {
        filterEventOwnerId(clubId);
        setClubName(clubId);
    }

    /***
     * @param provider
     *            BridgeEvent provider
     * @param eventDescriptions
     *            calendar event descriptions, max five
     */
    public WhiteCalendar(BridgeEventProvider provider,
            String... eventDescriptions) {
        eventProvider = provider;
        setEventProvider(provider);
        clubId = null;
        clubName = new Label();
        createMenuBar();
        colorPanel = new CalendarEventColorPanel(eventDescriptions);
        doUISettings();
    }

    public MenuBar getCalendarMenu() {
        return calendarMenu;
    }

    /***
     * getCompositeCalendar builds a calendar which contains the calendar menu,
     * calendar and calendar event color meanings
     */
    public EHorizontalLayout getCompositeCalendar() {
        VerticalLayout vLayout = new VerticalLayout(calendarMenu, clubName,
                this);
        vLayout.setComponentAlignment(clubName, Alignment.MIDDLE_CENTER);
        vLayout.setSpacing(true);
        clubName.addStyleName("calendarClubName");
        clubName.setValue("Selected Club");
        vLayout.setComponentAlignment(calendarMenu, Alignment.MIDDLE_CENTER);
        EHorizontalLayout hLayout = new EHorizontalLayout(vLayout, colorPanel);
        hLayout.setComponentAlignment(colorPanel, Alignment.MIDDLE_CENTER);
        return hLayout;
    }

    /***
     * Does not work in Vaadin 7.4.8 due a bug
     */
    public void disableMoveAndResize() {
        BasicEventMoveHandler mov = null;
        setHandler(mov);
        BasicEventResizeHandler res = null;
        setHandler(res);
    }

    public EntityItem<BridgeEvent> item(Object eventId) {
        return eventProvider.item(eventId);
    }

    /***
     * rollMonth rolls a specified amount of months forward or backward offset:
     * negative for the history and positive for the future
     */

    public void rollMonth(int offset) {
        java.util.Calendar c = getInternalCalendar();
        c.setTime(getStartDate());
        c.roll(java.util.Calendar.MONTH, offset);

        java.util.Calendar cal = getInternalCalendar();
        cal.setTime(c.getTime());
        cal.set(java.util.Calendar.DATE, 1);
        Date start = cal.getTime();
        cal.set(java.util.Calendar.DATE,
                cal.getMaximum(java.util.Calendar.DATE));
        Date end = cal.getTime();

        setStartDate(start);
        setEndDate(end);
    }

    /***
     * setMonthView shows days of the month
     */

    public void setMonthView() {
        java.util.Calendar cal = getInternalCalendar();
        cal.setTime(getStartDate());
        cal.set(java.util.Calendar.DATE, 1);
        Date start = cal.getTime();
        cal.set(java.util.Calendar.DATE,
                cal.getMaximum(java.util.Calendar.DATE));
        Date end = cal.getTime();

        setStartDate(start);
        setEndDate(end);
    }

    /***
     * refresh refreshes the events's container
     */
    public void refresh() {
        eventProvider.refresh();
    }
}
