package com.bridge.newcalendar;

import java.util.Date;

import com.bridge.calendar.BridgeEventProvider;
import com.bridge.database.BridgeEvent;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.components.calendar.handler.BasicEventMoveHandler;
import com.vaadin.ui.components.calendar.handler.BasicEventResizeHandler;

@SuppressWarnings("serial")
public class EventCalendar extends Calendar {

    // only calendar events -- no tournament events (BridgeEvent -> isTournament
    // == false)
    protected static BridgeEventProvider eventProvider;

    public EventCalendar() {

        if (eventProvider == null) {
            eventProvider = new BridgeEventProvider();
            eventProvider.filterIsTournament(false);
        }
        setEventProvider(eventProvider);
        setMonthView();
        setFirstVisibleDayOfWeek(1);
        setTimeFormat(TimeFormat.Format24H);
        setStyleName("whiteCalendar");
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
     * filterEventOwner preserves only those events which belong to the club and
     * removes possible existing club filter
     */

    public void filterEventOwnerId(Object clubId) {
        // eventProvider.filterClubId(clubId); // clubId null value removes
        // filter
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

    /***
     * refresh refreshes the events's container
     */
    public void refresh() {
        eventProvider.refresh();
    }

}
