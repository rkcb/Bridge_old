package com.bridge.resultui;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.pbn.pbnjson.JsonEvents;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Table;

@SuppressWarnings("serial")
public class ComparisonTable2 extends Table {

    private IndexedContainer container = new IndexedContainer();
    private JsonEvents jevents;
    private HashSet<String> numbers = new HashSet<>();
    private HashSet<String> html = new HashSet<>();

    public ComparisonTable2(JsonEvents jevents) {
        this.jevents = jevents;
        addStyleName("pbntable");
        addContainerProperties();
        setContainerDataSource(container);
        setSelectable(true);
    }

    private void addContainerProperties() {
        numbers = jevents.get(0).getScoreTable().numberColumns();
        html = jevents.get(0).getScoreTable().htmlColumns();
        List<String> header = jevents.comparisonHeader();
        for (String element : header) {
            if (numbers.contains(element)) { // Double
                container.addContainerProperty(element, Double.class, null);
            } else if (html.contains(element)) { // contract or lead
                container.addContainerProperty(element, HtmlLabel.class, null);
            } else {
                container.addContainerProperty(element, String.class, null);
            }
        }
    }

    public void setData(String board, String playerId) {

        container.removeAllItems();

        List<String> header = jevents.comparisonHeader();
        List<List<Object>> scores = jevents.comparisonData(board);
        setPageLength(scores.size());
        boolean selected = false;

        int i = 0;
        for (List<Object> row : scores) {
            Object itemId = container.addItemAt(i);
            Item item = container.getItem(itemId);
            Iterator<Object> rowit = row.iterator();
            for (String column : header) {
                Object cell = rowit.next();
                if (!selected && cell instanceof String) {
                    selected = select(column, (String) cell, playerId, itemId);
                }
                @SuppressWarnings("unchecked")
                Property<Object> p = item.getItemProperty(column);
                if (html.contains(column)) {
                    p.setValue(new HtmlLabel(cell));
                } else {
                    p.setValue(cell);
                }
            }
            i++;
        }
    }

    private boolean select(String colName, String id, String playerId,
            Object itemId) {
        if (colName.contains("Id_") && playerId.matches(id)) {
            select(itemId);
            return true;
        } else {
            return false;
        }
    }

}
