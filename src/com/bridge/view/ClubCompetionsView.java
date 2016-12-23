package com.bridge.view;

import java.util.Date;

import com.bridge.calendar.WhiteCalendar;
import com.bridge.database.BridgeEvent;
import com.bridge.database.Tournament;
import com.bridge.newcalendar.NewCompetionDialog2;
import com.bridge.newcalendar.OldCompetionDialog2;
import com.bridge.newcalendar.TournamentManager2;
import com.bridge.ui.EVerticalLayout;
import com.bridge.ui.MainMenu;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.EventClick;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.EventClickHandler;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.RangeSelectHandler;

@SuppressWarnings("serial")
public class ClubCompetionsView extends EVerticalLayout implements View {

    public static final String name = "/admin/competions";

    protected WhiteCalendar calendar;
    protected MainMenu mainMenu;
    protected OldCompetionDialog2 editOld;
    protected NewCompetionDialog2 editNew;
    protected TournamentManager2 manager;
    protected Window window;

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

    /***
     * addCalendarEventHandlers set handlers for user clicks in the calendar
     */
    private void addCalendarEventHandlers() {
        calendar.setHandler((EventClickHandler) event -> {
            window = new Window();
            editOld = new OldCompetionDialog2(manager, window);
            editOldCompetion(event);
        });

        calendar.setHandler((RangeSelectHandler) event -> {
            window = new Window("Create Tournament");
            editNew = new NewCompetionDialog2(manager, window);
            editNewCompetion(event.getStart(), event.getEnd());
        });
    }

    public ClubCompetionsView(MainMenu mainMenu) {
        this.mainMenu = mainMenu;
        calendar = WhiteCalendar.getTournamentCalendar();
        manager = new TournamentManager2(calendar);
        addCalendarEventHandlers();
    }

    @Override
    public void enter(ViewChangeEvent event) {
        addComponents(mainMenu, calendar.getCompositeCalendar());
        calendar.refreshSelectedClub();
    }

}
