package com.bridge.database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import com.vaadin.ui.components.calendar.event.CalendarEvent;
import com.vaadin.ui.components.calendar.event.CalendarEvent.EventChangeNotifier;

/***
 * BridgeEvent has two userRoles: it stores information for ordinary calendar
 * event and for tournament. A tournament must have a calendar event. On the
 * other hand BridgeEvent may store only ordinary calendar event without linking
 * to a tournament see the property isTournament
 *
 */

@Entity
public class BridgeEvent
        implements EventChangeNotifier, Serializable, CalendarEvent {

    private static final long serialVersionUID = -5627972711447979677L;
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @OneToOne(mappedBy = "calendarEvent", cascade = CascadeType.ALL)
    @Valid
    private Tournament tournament = null;

    @ManyToOne(cascade = CascadeType.ALL)
    private Club owner;

    private boolean isTournament = false;
    private boolean masterPoint = false;

    // indicates whether this event wants registration for participation
    private boolean registration = false;
    // usual for description text
    // private static final String desc =
    // "(\\p{IsAlphabetic}+\\s*-*[\\.\\,\\;\\?\\!\\(\\)\"]*)+";
    // private static final String place = "[\\p{IsAlphabetic}\\-\\s]*";
    private static final String currency = "(\\d*|\\d+[.,]{0,1}\\d+)\\S{0,1}";

    private String caption = new String("");

    private String description = new String("");

    @OneToMany
    @NotNull
    private Set<User> participants = new HashSet<>();

    private String town = new String("");

    // @NotNull
    private String country = new String("");

    @Temporal(TemporalType.TIMESTAMP)
    private Date start = new Date(0);

    @Temporal(TemporalType.TIMESTAMP)
    private Date end;

    private String styleName;

    @Pattern(regexp = currency, message = "Bad characters: try 1.3 for example")
    // @NotNull
    private String price = new String("");

    @Temporal(TemporalType.DATE)
    private Date signInStart;

    @Temporal(TemporalType.DATE)
    private Date signInEnd;

    private Tournament.Type type;

    private transient List<EventChangeListener> listeners = new ArrayList<>();

    private boolean isAllDay;

    private boolean isPrivate = false;

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public List<EventChangeListener> getListeners() {
        return listeners;
    }

    public void setListeners(List<EventChangeListener> listeners) {
        this.listeners = listeners;
    }

    public void setTournament(Tournament tournament) {
        this.tournament = tournament;
    }

    public Date getNow() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 18);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();

    }

    public BridgeEvent() {
        start = getNow();
        end = getNow();
        tournament = null;
    }

    public BridgeEvent(String caption, String description,
            boolean isTournament) {
        this.caption = caption;
        this.description = description;
        this.isTournament = isTournament;
        start = getNow();
        end = getNow();
    }

    public BridgeEvent(String caption) {
        this.caption = caption;
        start = getNow();
        end = getNow();
        tournament = null;
    }

    public BridgeEvent(Date start, Date end) {
        id = null;
        tournament = null;
        this.start = start;
        this.end = end;
    }

    public BridgeEvent(String caption, String description, Date date) {
        this.caption = caption;
        this.description = description;
        start = date;
        end = date;
        tournament = null;
    }

    public BridgeEvent(String caption, String description, Date startDate,
            Date endDate) {
        this.caption = caption;
        this.description = description;
        start = startDate;
        end = endDate;
        tournament = null;
    }

    public BridgeEvent(String caption, String description, Date startDate,
            Date endDate, Tournament.Type type) {
        this.caption = caption;
        this.description = description;
        start = startDate;
        end = endDate;
        this.type = type;
        tournament = null;
    }

    public BridgeEvent(String caption, String description, Date startDate,
            Date endDate, Tournament tournament2) {
        this.caption = caption;
        this.description = description;
        start = startDate;
        end = endDate;
        tournament = tournament2;
    }

    public Tournament.Type getType() {
        return type;
    }

    public void setType(Tournament.Type type) {
        this.type = type;
    }

    @Override
    public String getCaption() {
        return caption;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public Date getEnd() {
        return end;
    }

    @Override
    public Date getStart() {
        return start;
    }

    @Override
    public String getStyleName() {
        return styleName;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEnd(Date end) {
        this.end = end;
    }

    public void setStart(Date start) {
        this.start = start;
    }

    public void addListener(EventChangeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(EventChangeListener listener) {
        listeners.remove(listener);
    }

    // /**
    // * Fires an event change event to the listeners. Should be triggered when
    // * some property of the event changes.
    // */
    // protected void fireEventChange() {
    // EventChange event = new EventChange(this);
    //
    // for (EventChangeListener listener : listeners) {
    // listener.eventChange(event);
    // }
    // }

    /***
     * getTournament gives the tournament which this event describes
     *
     * @return returns null if this event is not in relation with a tournament
     *         and otherwise returns the tournament
     */
    public Tournament getTournament() {
        return tournament;
    }

    public boolean belongsToTournament(Tournament t) {
        if (tournament != null && t != null && tournament.getId() != null
                && t.getId() != null) {
            return tournament.getId() == t.getId();
        } else {
            return false;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    @Override
    public boolean isAllDay() {
        return false;
    }

    public void setAllDay(boolean isAllDay) {
        this.isAllDay = isAllDay;
    }

    public void setStyleName(String styleName) {
        this.styleName = styleName;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public Date getSignInStart() {
        return signInStart;
    }

    public void setSignInStart(Date signInStart) {
        this.signInStart = signInStart;
    }

    public Date getSignInEnd() {
        return signInEnd;
    }

    public void setSignInEnd(Date signInEnd) {
        this.signInEnd = signInEnd;
    }

    @Override
    public void addEventChangeListener(EventChangeListener listener) {
    }

    @Override
    public void removeEventChangeListener(EventChangeListener listener) {
    }

    public boolean isRegistration() {
        return registration;
    }

    public void setRegistration(boolean registration) {
        this.registration = registration;
    }

    public Set<User> getParticipants() {
        return participants;
    }

    public void setParticipants(Set<User> p) {
        participants = p;
    }

    public boolean getMasterPoint() {
        return masterPoint;
    }

    public void setMasterPoint(boolean masterPoint) {
        this.masterPoint = masterPoint;
    }

    public Boolean getIsTournament() {
        return isTournament;
    }

    public void setIsTournament(Boolean isTournament) {
        this.isTournament = isTournament;
    }

    public Club getOwner() {
        return owner;
    }

    public void setOwner(Club owner) {
        this.owner = owner;
    }

    public void setTournament(boolean isTournament) {
        this.isTournament = isTournament;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

}
