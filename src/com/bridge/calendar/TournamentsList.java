package com.bridge.calendar;

import java.text.SimpleDateFormat;

import com.bridge.data.FilterCheckBox;
import com.bridge.data.FilterComboBox;
import com.bridge.data.FilterDateField;
import com.bridge.data.FilterTextField;
import com.bridge.database.C;
import com.bridge.database.Club;
import com.bridge.database.Tournament;
import com.bridge.ui.EVerticalLayout;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.HeaderRow;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.DateRenderer;

/***
 * TournamentsList is a list which shows tournaments in a grid in which many
 * properties can be filtered
 */
@SuppressWarnings("serial")
public class TournamentsList extends EVerticalLayout {

    private C<Tournament> tournaments;
    private GeneratedPropertyContainer gContainer;
    private C<Club> clubs;
    private Grid grid;

    public TournamentsList() {
        tournaments = new C<>(Tournament.class);
        clubs = new C<>(Club.class);
        String s = "calendarEvent.";
        String mp = s + "masterPoint";
        String start = s + "start";
        String caption = s + "caption";
        String type = s + "type";
        String ownerName = s + "owner.name";
        String town = s + "town";
        tournaments.nest(mp, start, caption, type, ownerName, town);

        gContainer = new GeneratedPropertyContainer(tournaments.c());
        grid = new Grid(gContainer);
        grid.setColumns(mp, start, caption, type, ownerName, town);
        grid.setSizeFull();
        grid.setReadOnly(true);
        SimpleDateFormat df = new SimpleDateFormat("EEE, d. MMM yyyy, HH:mm");
        grid.getColumn(start).setRenderer(new DateRenderer(df));

        HeaderRow filterRow = grid.appendHeaderRow();
        FilterCheckBox.addFilterCheckBox(gContainer, mp, filterRow);
        FilterTextField.addFilterField(gContainer, caption, filterRow);
        FilterTextField.addFilterField(gContainer, town, filterRow);
        FilterComboBox.addFilterIdComboBox(gContainer, ownerName, filterRow,
                clubs.c());
        FilterDateField.addFilterDateField(gContainer, start, filterRow);
        grid.setReadOnly(true);
        grid.setSelectionMode(SelectionMode.NONE);

        addComponent(grid);
    }

    /***
     * refesh refreshes the calendar event container from the db
     */
    public void refresh() {
        tournaments.refresh();
    }

}
