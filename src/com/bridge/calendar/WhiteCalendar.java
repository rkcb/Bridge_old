package com.bridge.calendar;

import java.util.Date;
import java.util.HashSet;

import com.bridge.data.EventColorEnum;
import com.bridge.database.BridgeEvent;
import com.bridge.database.C;
import com.bridge.database.Club;
import com.bridge.ui.BridgeUI;
import com.bridge.ui.EHorizontalLayout;
import com.bridge.ui.EVerticalLayout;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.And;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.data.util.filter.Not;
import com.vaadin.data.util.filter.Or;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
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

    private static BridgeEventProvider tournamentProvider;
    private static BridgeEventProvider eventProvider;

    private boolean isTournamentCalendar;
    private boolean isMasterPoint;
    private Object clubId; // entity id of the club of interest
    private String town;
    private BridgeEventProvider provider;
    private MenuBar calendarMenu;
    private CalendarEventColorPanel colorPanel;
    private Label filterDescription;
    private HashSet<String> townNameList;
    private CheckBox masterPoint;
    private MenuItem searchItem;

    private VerticalLayout vLayout;
    private EHorizontalLayout hLayout;

    private String getClubName(Object clubId) {
        C<Club> cs = new C<>(Club.class);
        return clubId == null ? "" : cs.get(clubId).getName();
    }

    private void createMenuBar() {
        calendarMenu = new MenuBar();

        calendarMenu.addItem("Previous Month", command -> rollMonth(-1));
        calendarMenu.addItem("Month View", command -> setMonthView());
        calendarMenu.addItem("Next Month", command -> rollMonth(1));
        searchItem = calendarMenu.addItem("Search",
                command -> createEventSelector());
    }

    private HashSet<String> getTownNames() {
        C<BridgeEvent> events = new C<>(BridgeEvent.class);
        HashSet<String> towns = new HashSet<>();

        for (int i = 0; i < events.size(); i++) {
            String twn = events.at(i).getTown();
            if (twn != null && !twn.isEmpty()) {
                towns.add(twn);
            }
        }

        if (townNameList == null) {
            townNameList = towns;
        }

        return towns;
    }

    /***
     * createEventSelector
     */
    private void createEventSelector() {

        // create dialog window
        Window window = new Window("Search Preferences");
        // window.setWidth("400px");
        // window.setHeight("200px");
        getUI().addWindow(window);
        window.center();
        window.setModal(true);

        // create club selector
        C<Club> cs = new C<>(Club.class);
        ComboBox clubs = new ComboBox("Club", cs.c());
        clubs.setItemCaption(null, "All Clubs");
        clubs.setItemCaptionMode(ItemCaptionMode.ITEM);
        clubs.setFilteringMode(FilteringMode.CONTAINS);
        clubs.setInputPrompt("Write or Select");

        // create town selector
        ComboBox town = new ComboBox("Town", getTownNames());
        town.setFilteringMode(FilteringMode.CONTAINS);
        town.setInputPrompt("Write or Select");

        // create master point selector
        if (isTournamentCalendar) {
            masterPoint = new CheckBox("Only Master Point Tournaments");
        }

        Button done = new Button("Done");
        done.addClickListener(listener -> {
            if (isTournamentCalendar) {
                isMasterPoint = masterPoint.getValue();
            }
            clubId = clubs.getValue();
            this.town = null;
            if (town.getValue() != null) {
                this.town = (String) town.getValue();
            }
            addSearchFilters();
            setFilterDescription();

            window.close();
            markAsDirty();
        });

        EVerticalLayout l = new EVerticalLayout(town, clubs, done);
        if (isTournamentCalendar) {
            l.addComponentAsFirst(masterPoint);
        }
        window.setContent(l);
    }

    /***
     * addEvent adds an event to calendar and then marks the calendar dirty
     */

    protected void addEvent(BridgeEvent event) {
        eventProvider.addEvent(event);
        markAsDirty();
    }

    /***
     * @param provider
     *            BridgeEvent provider either for calendar event or tournament
     * @param events
     *            calendar event types in use
     */
    private WhiteCalendar(BridgeEventProvider provider, EventColorEnum[] events,
            boolean isTournamentCalendar) {
        this.isTournamentCalendar = isTournamentCalendar;
        this.provider = provider;
        setEventProvider(provider);
        // by default show home club for and nothing otherwise
        clubId = getDefaultCalendarClub();
        filterDescription = new Label();
        setFilterDescription();
        createMenuBar();
        colorPanel = new CalendarEventColorPanel(events);
        doUISettings();
        vLayout = null;
        hLayout = null;
    }

    private void setFilterDescription() {
        if (isTournamentCalendar) {
            if (clubId == null) { // no specific club
                if (town != null && !town.isEmpty()) { // specific town
                    String mp = isMasterPoint ? " MP " : " ";
                    filterDescription
                            .setValue("All" + mp + "Tournaments in " + town);
                } else { // no specific town
                    if (isMasterPoint) { // only master points
                        filterDescription
                                .setValue("All Master Point Tournaments");
                    } else { // mp or non-mp tournaments
                        filterDescription.setValue("All Tournaments");
                    }
                }
            } else { // specific club
                if (isMasterPoint) { // master points
                    filterDescription.setValue(
                            "MP Tournaments of " + getClubName(clubId));
                } else {
                    filterDescription.setValue(
                            "All Tournaments of " + getClubName(clubId));
                }
            }
        } else { // event calendar
            if (clubId == null) { // no specific club
                if (town != null && !town.isEmpty()) { // specific town
                    filterDescription.setValue("Events in " + town);
                } else {
                    filterDescription.setValue("All Events");
                }
            } else { // specific club
                filterDescription.setValue("Events of " + getClubName(clubId));
            }
        }
    }

    /***
     * addSearchFilters checks the search variables (clubId for example) and
     * adds filters accordingly
     */
    public void addSearchFilters() {
        provider.removeSearchFilters();
        // filter club with the entity id
        if (clubId != null) {
            provider.addSearchFilters(new Compare.Equal("owner.id", clubId));
        }

        // filter master point tournaments
        if (isTournamentCalendar && isMasterPoint) {
            provider.addSearchFilters(new Compare.Equal("masterPoint", true));
        }

        if (town != null && !town.isEmpty()) {
            BridgeUI.o("Add town filter: " + town);
            provider.addSearchFilters(
                    new SimpleStringFilter("town", town, true, false));
        }

        Object id = BridgeUI.user.getCurrentClubId();

        // filter events whose
        Filter privateFilter = new Compare.Equal("privateEvent", true);
        Filter sameClubFilter = new Compare.Equal("owner.id", id);
        Filter privateAndSameFilter = new And(privateFilter, sameClubFilter);
        Filter combinedFilter = new Or(new Not(privateFilter),
                privateAndSameFilter);
        provider.addSearchFilters(combinedFilter);
    }

    /***
     * @return a calendar for tournaments; event provider is shared between
     *         these instances; note that event filters must be updated when
     *         moving from one calendar to another
     *
     */
    public static WhiteCalendar getTournamentCalendar() {
        if (tournamentProvider == null) {
            tournamentProvider = BridgeEventProvider.getTournamentProvider();
        }
        return new WhiteCalendar(tournamentProvider,
                EventColorEnum.tournamentEvents(), true);
    }

    /***
     * @return a tournament which hides search menu item if user has not signed
     *         in as "admin"
     */
    public static WhiteCalendar getUploadCalendar() {
        WhiteCalendar calendar = getTournamentCalendar();

        /***
         * note that only admins can upload results for Federation since
         * Federation has no members
         */
        calendar.clubId = BridgeUI.user.getCurrentClubId();
        String role = BridgeUI.getCurrentRole();

        BridgeUI.o("role:" + role);

        boolean hasClubAdmin = BridgeUI.user.hasRole("clubadmin");
        boolean hasAdmin = BridgeUI.user.hasRole("admin");

        if (role.matches("clubadmin|admin") && (hasClubAdmin || hasAdmin)) {
            if (!hasAdmin) {
                calendar.hideSearchMenu();
            }
        } else {
            calendar.hideSearchMenu();
        }
        calendar.addSearchFilters();
        calendar.setFilterDescription();

        return calendar;
    }

    /***
     * @return a calendar for events; event provider is shared between these
     *         instances; note that event filters must be updated when moving
     *         from one calendar to another
     */
    public static WhiteCalendar getEventCalendar() {
        if (eventProvider == null) {
            eventProvider = BridgeEventProvider.getCalendarEventProvider();
        }
        return new WhiteCalendar(eventProvider, EventColorEnum.calendarEvents(),
                false);
    }

    /***
     * removeEvent removes the event
     */

    public void removeEvent(Object eventId) {
        eventProvider.removeItem(eventId);
        markAsDirty();
    }

    private void doUISettings() {
        setMonthView();
        addStyleName("whiteCalendar");
        setTimeFormat(TimeFormat.Format24H);
    }

    /***
     * getCalendarMenu
     *
     * @return MenuBar which contains month navigation and event filtering
     */
    public MenuBar getCalendarMenu() {
        return calendarMenu;
    }

    public BridgeEventProvider getProvider() {
        return provider;
    }

    private Object getDefaultCalendarClub() {
        Object clubId = BridgeUI.user.getCurrentClubId();

        if (clubId == null || !BridgeUI.user.isSignedIn()) {
            C<Club> clubs = new C<>(Club.class);
            clubs.filterEq("federation", true);
            if (clubs.size() == 1) {
                clubId = clubs.at(0).getId();
            }
        }
        return clubId;
    }

    /***
     * hideSearchMenu hides the "Search" menu item
     */
    public void hideSearchMenu() {
        searchItem.setVisible(false);
    }

    /***
     * getCompositeCalendar builds a calendar which contains the calendar menu,
     * calendar and calendar event color meanings
     */
    public EHorizontalLayout getCompositeCalendar() {
        if (vLayout == null && hLayout == null) {
            vLayout = new VerticalLayout(calendarMenu, filterDescription, this);
            vLayout.setComponentAlignment(filterDescription,
                    Alignment.MIDDLE_CENTER);
            vLayout.setSpacing(true);
            filterDescription.addStyleName("calendarClubName");
            vLayout.setComponentAlignment(calendarMenu,
                    Alignment.MIDDLE_CENTER);
            hLayout = new EHorizontalLayout(vLayout, colorPanel);
            hLayout.setComponentAlignment(colorPanel, Alignment.MIDDLE_CENTER);
        }
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

    public Object addEventGetId(BridgeEvent e) {
        return eventProvider.addEvent(e);
    }

}
