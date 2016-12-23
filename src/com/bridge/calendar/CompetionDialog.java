package com.bridge.calendar;

import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

/***
 * CompetionDialog allow user to add a new event to an existing competion, a new
 * tournament or remove the
 */

@SuppressWarnings("serial")
public class CompetionDialog extends VerticalLayout {

    protected Button cancel = new Button("Cancel");
    protected Button ok = new Button("Ok");
    protected HorizontalLayout buttons = new HorizontalLayout();

    protected BridgeEventEditor eventEditor = new BridgeEventEditor(true);
    protected TournamentEditor tourEditor = new TournamentEditor();
    protected GridLayout fields = new GridLayout();

    public CompetionDialog() {

        fields.setColumns(2);
        fields.setMargin(true);
        fields.setSpacing(true);
        tourEditor.insertFields(fields);

        // formlayout looks much better -- improve this
        eventEditor.embedOrganizersAndDirectors(tourEditor.getDirectors(),
                tourEditor.getOrganizers());
        addComponents(eventEditor);

        addButtonListeners();

        buttons.setMargin(true);
        buttons.setSpacing(true);
    }

    protected void o(String s) {
        System.out.println(s);
    }

    protected void addButtonListeners() {
        cancel.addClickListener(event -> cancelClicked());

        ok.addClickListener(event -> okClicked());
    }

    protected void cancelClicked() {
    }

    protected void okClicked() {
    }

}
