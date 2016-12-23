package com.bridge.newcalendar;

import java.util.Date;

import com.bridge.calendar.CompetionDialog;
import com.bridge.calendar.WhiteCalendar;
import com.bridge.database.BridgeEvent;
import com.bridge.database.Tournament;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class NewCompetionDialog2 extends CompetionDialog {

    protected TournamentManager2 manager;
    protected Window window;
    protected WhiteCalendar calendar;

    public NewCompetionDialog2(TournamentManager2 manager, Window window) {
        this.manager = manager;
        this.window = window;
        calendar = manager.getCalendar();
        addButtons();
    }

    public void createTournament(Date start, Date end) {
        eventEditor.initialize(new BridgeEvent(start, end));
        tourEditor.initialize(new Tournament());
    }

    /***
     * setWindow used for the tournament details dialog
     */

    public void setWindow(Window w) {
        window = w;
    }

    /***
     * okClicked called after the ok button is clicked
     */

    @Override
    protected void okClicked() {
        if (tourEditor.areFieldsValid()
                && eventEditor.areTournamentFieldsValid()) {
            editingNewDone(eventEditor.getValue(), tourEditor.getValue());
            window.close();
            calendar.markAsDirty();
        } else {
            Notification n = new Notification("Check the required fields",
                    Type.ERROR_MESSAGE);
            n.show(UI.getCurrent().getPage());
        }
    }

    /***
     * editingNewDone adds the tournament to the database
     */

    protected void editingNewDone(BridgeEvent e, Tournament t) {
        // Object tid =
        manager.createTournament(e, t);
        // C<Tournament> ts = new C<>(Tournament.class);
        // C<BridgeEvent> es = new C<>(BridgeEvent.class);
        // manager.addCalendarEvent(ts.get(tid).getCalendarEvent());
        // ts.set(tid, "owner", BridgeUI.user.getCurrentClub());
        // Object eid = ts.get(tid).getCalendarEvent().getId();
        // es.set(eid, "owner", BridgeUI.user.getCurrentClub());
    }

    @Override
    protected void cancelClicked() {
        window.close();
    }

    protected void addButtons() {
        buttons.addComponents(cancel, ok);
        addComponent(buttons);
    }
}
