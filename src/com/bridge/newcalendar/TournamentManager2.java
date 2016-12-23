package com.bridge.newcalendar;

import com.bridge.calendar.BridgeEventProvider;
import com.bridge.calendar.WhiteCalendar;
import com.bridge.database.BridgeEvent;
import com.bridge.database.C;
import com.bridge.database.Club;
import com.bridge.database.Tournament;
import com.bridge.ui.BridgeUI;
import com.vaadin.addon.jpacontainer.EntityItem;

public class TournamentManager2 {

    protected WhiteCalendar calendar;
    protected C<Tournament> tournaments = new C<>(Tournament.class);
    protected C<Club> clubs = new C<>(Club.class);
    protected BridgeEventProvider provider;

    public TournamentManager2(WhiteCalendar calendar) {
        this.calendar = calendar;
        provider = calendar.getProvider();
    }

    public WhiteCalendar getCalendar() {

        return calendar;
    }

    /***
     * createTournament takes the entities and creates a competion from these
     * entities. Note that the information is not yet committed to the database
     *
     * @param e
     *            bridge event entity
     * @param t
     *            tournament entity
     * @return tournament entity item build from the given entities NOTE: the
     *         user must be signed in; this is assumed here and user must have a
     *         nonnull club returns tournament item id
     */
    public Object createTournament(BridgeEvent e, Tournament t) {

        e.setIsTournament(true);
        e.setOwner(BridgeUI.user.getCurrentClub());
        Object eid = provider.addEvent(e);
        Object tid = tournaments.add(t);
        bindEventAndTournament(tid, eid);
        tournaments.item(tid);

        return tid;
    }

    /***
     * createTournament takes the entities and creates a competion from these
     * entities. Note that the information is not yet committed to the database
     *
     * @param e
     *            bridge event entity
     * @param t
     *            tournament entity
     * @param owner
     *            owner of the tournament
     * @return tournament entity item build from the given entities NOTE: the
     *         user must be signed in; this is assumed here and user must have a
     *         nonnull club returns tournament item id
     */
    public Object createTournament(BridgeEvent e, Tournament t, Club owner) {

        e.setIsTournament(true);
        e.setOwner(owner);
        // Object eid = calendar.addEventGetId(e);
        Object eid = provider.addEvent(e);
        Object tid = tournaments.add(t);

        bindEventAndTournament(tid, eid);
        tournaments.item(tid);

        return tid;
    }

    /***
     * bindEventAndTournament creates one to one DB relation between the
     * tournament and the event
     *
     * @returns true if no error was found and false otherwise
     */

    public boolean bindEventAndTournament(Object tourId, Object eventId) {

        if (tourId != null && eventId != null) {
            tournaments.set(tourId, "calendarEvent",
                    provider.getContainer().get(eventId));
            provider.getContainer().set(eventId, "tournament",
                    tournaments.get(tourId));
            return true;
        } else {
            return false;
        }
    }

    /***
     * removeTournament removes the tournament with the given id returns true if
     * the operation succeeded and the tournament existed
     */
    public boolean removeTournament(Object tournamentId) {
        if (tournamentId != null) {
            // DB relation: Event (1) <-> (1) Tour (1)<-(m) Club

            Object tid = tournamentId;
            Object eid = tournaments.get(tid).getCalendarEvent().getId();

            tournaments.set(tid, "owner", null);
            tournaments.set(tid, "calendarEvent", null);
            tournaments.rem(tid);

            provider.getContainer().set(eid, "tournament", null);
            provider.getContainer().set(eid, "owner", null);
            tournaments.rem(tid);
            provider.getContainer().rem(eid);
            return true;
        } else {
            return false;
        }
    }

    public EntityItem<Tournament> getTournamentItem(Object tournamentId) {
        return tournamentId == null ? null : tournaments.item(tournamentId);
    }

    public EntityItem<BridgeEvent> getEventItem(Object eventId) {
        return provider.getContainer().item(eventId);
    }

}
