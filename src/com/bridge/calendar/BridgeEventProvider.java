package com.bridge.calendar;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import com.bridge.database.BridgeEvent;
import com.bridge.database.C;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.ui.components.calendar.event.CalendarEditableEventProvider;
import com.vaadin.ui.components.calendar.event.CalendarEvent;
import com.vaadin.ui.components.calendar.event.CalendarEvent.EventChangeEvent;
import com.vaadin.ui.components.calendar.event.CalendarEventProvider.EventSetChangeNotifier;

/***
 * BridgeEventProvider provides both calendar events and tournament events for
 * calendars
 */
@SuppressWarnings("serial")
public class BridgeEventProvider implements CalendarEditableEventProvider,
        EventSetChangeNotifier, CalendarEvent.EventChangeListener {

    private Filter startTime;
    private Filter endTime;
    private Filter eventFilter; // filter calendar or tournament events
    private Filter clubFilter; // filter club id

    private List<EventSetChangeListener> listeners = new ArrayList<>();

    protected List<BridgeEvent> eventList = new ArrayList<>();

    private JPAContainer<BridgeEvent> container;
    private C<BridgeEvent> events;

    public BridgeEventProvider() {
        events = new C<>(BridgeEvent.class);
        events.nest("owner.id");
        container = events.c();
        container.sort(new Object[] { "start" }, new boolean[] { true });
    }

    /***
     * getTournamentProvider returns a provider for tournament events
     */
    public static BridgeEventProvider getTournamentProvider() {
        BridgeEventProvider provider = new BridgeEventProvider();
        provider.filterIsTournament(true);
        return provider;
    }

    /***
     * getTournamentProvider returns a provider for ordinary calendar events
     * which do not describe tournaments
     */
    public static BridgeEventProvider getCalendarEventProvider() {
        BridgeEventProvider provider = new BridgeEventProvider();
        provider.filterIsTournament(false);
        return provider;
    }

    /***
     * refresh removes internal calendar events and replaces these with the
     * container events
     */

    public void refresh() {
        listeners.clear();
        eventList.clear();
        addAllContainerEvents();
    }

    private void addAllContainerEvents() {
        for (Object object : container.getItemIds()) {
            Long idd = (Long) object;
            addEvent(container.getItem(idd).getEntity());
        }
    }

    /***
     * item returns the entity item for the id
     */
    public EntityItem<BridgeEvent> item(Object entityId) {
        return events.item(entityId);
    }

    public C<BridgeEvent> getContainer() {
        return events;
    }

    /***
     * filterTournaments preserves all events whose isTournament property equals
     * the argument
     */

    public void filterIsTournament(boolean filterValue) {
        if (eventFilter != null) {
            events.removeFilter(eventFilter);
        }
        eventFilter = events.filterEq("isTournament", filterValue);
    }

    /***
     * filterClub preserves all events whose owner id matches clubId
     */

    public void filterClubId(Object clubId) {
        if (clubFilter != null) {
            events.removeFilter(clubFilter);
        }

        if (clubId != null) {
            clubFilter = events.filterEq("owner.id", clubId);
        }
    }

    public void addEventFilter(Filter f) {
        container.addContainerFilter(f);
    }

    private void updateStartAndEndFilters(Date startDate, Date endDate) {
        container.removeContainerFilter(startTime);
        container.removeContainerFilter(endTime);

        startTime = new Compare.GreaterOrEqual("start", startDate);
        endTime = new Compare.LessOrEqual("end", endDate);

        container.addContainerFilter(startTime);
        container.addContainerFilter(endTime);
    }

    public List<Filter> getEventFilters() {
        return container.getAppliedFilters();
    }

    @Override
    public List<CalendarEvent> getEvents(Date startDate, Date endDate) {

        updateStartAndEndFilters(startDate, endDate);

        List<CalendarEvent> results = new ArrayList<>();

        for (Object object : container.getItemIds()) {
            Long idd = (Long) object;
            BridgeEvent event = container.getItem(idd).getEntity();
            if (event.getIsTournament()) {
                String color = event.getMasterPoint() ? "color2" : "color1";
                event.setStyleName(color);
            }
            results.add(event);
        }

        return results;
    }

    /***
     * removeItem removes this event
     */
    public void removeItem(Object eventId) {
        if (events.has(eventId)) {
            removeEvent(events.get(eventId));
            events.set(eventId, "owner", null);
            events.rem(eventId);
            fireEventSetChange();
        }
    }

    // public List<BridgeEvent> getEventList() {
    // return eventList;
    // }

    /***
     * updateEvent commits changes to the edited event and commits changes to
     * calendar and to DB -- things to remember: - provider's list of events
     * (shown in calendar) - JPA container - DB - BridgeEvent is in 1-1
     * relationship to Tournament in DB
     */

    public boolean updateEvent(BridgeEvent event) {
        if (event != null && container.containsId(event.getId())) {
            ListIterator<BridgeEvent> i = eventList.listIterator();
            boolean found = false;
            BridgeEvent e = null;
            while (i.hasNext() && !found) {
                e = i.next();
                found = e.getId() == event.getId();
            }
            if (found) {
                i.set(event);
            }
            eventList.add(event);

            container.commit();
            fireEventSetChange();
            return found;
        } else {
            return false;
        }
    }

    /***
     * addEvent adds the event to internal list and to the container
     *
     * @return
     */

    @Override
    public void addEvent(CalendarEvent event) {
        if (event != null && event instanceof BridgeEvent) {

            BridgeEvent e = (BridgeEvent) event;
            events.add(e); // persists
            eventList.add(e);
            e.addListener(this);
            fireEventSetChange();
        }
    }

    /***
     * addEvent adds the event to the database and to the list of calendar
     * events
     */
    public Object addEvent(BridgeEvent event) {
        if (event != null && event instanceof BridgeEvent) {

            BridgeEvent e = event;
            Object id = events.add(e); // persists
            eventList.add(e);
            e.addListener(this);
            fireEventSetChange();
            return id;
        } else {
            return null;
        }
    }

    /***
     * removeEvent removes event from internal container only not from the DB
     */
    @Override
    public void removeEvent(CalendarEvent event) {
        if (event != null && event instanceof BridgeEvent) {
            BridgeEvent e = (BridgeEvent) event;
            e = findByItemId(e.getId());

            if (e != null) {
                e.removeListener(this);
                eventList.remove(e);
                fireEventSetChange();
            }
        }
    }

    public boolean containsEvent(BridgeEvent event) {
        return eventList.contains(event);
    }

    @Override
    public void eventChange(EventChangeEvent changeEvent) {
        fireEventSetChange();
    }

    public void addListener(EventSetChangeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(EventSetChangeListener listener) {
        listeners.remove(listener);
    }

    /**
     * Fires a eventsetchange event. The event is fired when either an event is
     * added to or removed from the event provider
     */
    protected void fireEventSetChange() {
        EventSetChangeEvent event = new EventSetChangeEvent(this);

        for (EventSetChangeListener listener : listeners) {
            listener.eventSetChange(event);
        }
    }

    protected BridgeEvent findByItemId(Long id) {
        Iterator<BridgeEvent> i = eventList.iterator();
        boolean found = false;
        BridgeEvent e = null;
        while (i.hasNext() && !found) {
            e = i.next();
            if (e.getId() == id) {
                found = true;
            }
        }
        return e;
    }

    public int listSize() {
        return eventList.size();
    }

    public int ContainerSize() {
        return events.size();
    }

    @Override
    public void addEventSetChangeListener(EventSetChangeListener listener) {
    }

    @Override
    public void removeEventSetChangeListener(EventSetChangeListener listener) {
    }

}
