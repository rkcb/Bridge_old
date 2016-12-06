package com.bridge.calendar;

import java.text.SimpleDateFormat;

import com.bridge.data.FilterComboBox;
import com.bridge.data.FilterDateField;
import com.bridge.data.FilterTextField;
import com.bridge.database.BridgeEvent;
import com.bridge.database.C;
import com.bridge.database.Club;
import com.bridge.ui.EVerticalLayout;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Grid.HeaderRow;
import com.vaadin.ui.Grid.SelectionMode;
import com.vaadin.ui.renderers.DateRenderer;

@SuppressWarnings("serial")
public class CalendarEventsList extends EVerticalLayout {

    private C<BridgeEvent> events;
    private GeneratedPropertyContainer gContainer;
    private C<Club> clubs;
    private Grid grid;

    public CalendarEventsList() {
        events = new C<>(BridgeEvent.class);
        clubs = new C<>(Club.class);
        events.nest("owner.name");
        events.filterEq("isTournament", false);

        gContainer = new GeneratedPropertyContainer(events.c());
        grid = new Grid(gContainer);
        grid.setColumns("start", "caption", "owner.name", "town");
        grid.setSizeFull();
        grid.setReadOnly(true);
        grid.setSelectionMode(SelectionMode.NONE);

        SimpleDateFormat df = new SimpleDateFormat("EEE, d. MMM yyyy, HH:mm");
        grid.getColumn("start").setRenderer(new DateRenderer(df));

        HeaderRow filterRow = grid.appendHeaderRow();
        FilterTextField.addFilterField(gContainer, "caption", filterRow);
        FilterTextField.addFilterField(gContainer, "town", filterRow);
        FilterComboBox.addFilterIdComboBox(gContainer, "owner.name", filterRow,
                clubs.c());
        FilterDateField.addFilterDateField(gContainer, "start", filterRow);
        addComponent(grid);
    }

    /***
     * refesh refreshes the calendar event container from the db
     */
    public void refresh() {
        events.refresh();
    }

}
