package com.bridge.newcalendar;

import com.bridge.calendar.CompetionDialog;
import com.bridge.calendar.WhiteCalendar;
import com.bridge.database.BridgeEvent;
import com.bridge.database.Tournament;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.ui.Button;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class OldCompetionDialog2 extends CompetionDialog {

    protected EntityItem<BridgeEvent> bridgeEventItem;
    protected EntityItem<Tournament> tournamentItem;
    protected TournamentManager2 manager;
    protected Window window;
    protected WhiteCalendar calendar;

    public OldCompetionDialog2(TournamentManager2 manager, Window window) {
        this.manager = manager;
        this.window = window;
        calendar = manager.getCalendar();
        addButtonsAndListeners();
    }

    protected void addButtonsAndListeners() {
        Button delete = new Button("Delete", listener -> deleteClicked());
        cancel.addClickListener(event -> cancelClicked());
        ok.addClickListener(event -> okClicked());
        buttons.addComponents(cancel, delete, ok);
        addComponent(buttons);
    }

    /***
     * editItems enables editing an existing tournament
     */

    public void editItems(EntityItem<BridgeEvent> eventItem,
            EntityItem<Tournament> tournamentItem) {
        eventEditor.setItemSource(eventItem);
        tourEditor.setItemSource(tournamentItem);
        bridgeEventItem = eventItem;
        this.tournamentItem = tournamentItem;
    }

    @Override
    protected void cancelClicked() {
        bridgeEventItem.discard();
        tournamentItem.discard();
        finishEditing();
    }

    protected void deleteClicked() {
        manager.removeTournament(tournamentItem.getItemId());
        calendar.markAsDirty();
        finishEditing();
    }

    @Override
    protected void okClicked() {
        if (tourEditor.areFieldsValid()
                && eventEditor.areTournamentFieldsValid()) {

            if (eventEditor.isOwnerModified()) {
                Object eid = bridgeEventItem.getItemId();
                Object tid = tournamentItem.getItemId();
                manager.changeEventOwner(tid, eid, eventEditor.getOwnerId());
            }
            boolean valid = true;
            tourEditor.commit();

            try {
                eventEditor.commitTournamentEvent();
            } catch (CommitException e) {
                valid = false;
            }

            if (valid) {
                finishEditing();
                calendar.markAsDirty();
            }

        } else {
            Notification n = new Notification("Check the required fields",
                    Type.ERROR_MESSAGE);
            n.show(UI.getCurrent().getPage());
        }
    }

    /***
     * finishEditing closes the dialog and updates the calendar
     */

    protected void finishEditing() {
        window.close();
    }

}
