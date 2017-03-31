package com.bridge.resultui;

import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;

import com.bridge.database.PbnFile;
import com.bridge.ui.ETable;
import com.pbn.pbnjson.JsonEvents;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;

@SuppressWarnings("serial")
public class TotalScoreTable2 extends ETable {
    private IndexedContainer container = new IndexedContainer();
    // private TableFactory factory = null;
    private HashSet<String> numbers;
    private JsonEvents jevents;

    private void initialize() {
        if (jevents.totalScoreTableExists()) {
            numbers = jevents.totalScoreTable().getNumberColumns();
            addContainerProperties();
            loadResults();
            setContainerDataSource(container);
            addStyleName("pbntable");
            setSelectable(true);
        }
    }

    public TotalScoreTable2(PbnFile file) {
        if (file != null && file.getJson() != null) {
            jevents = new JsonEvents(file.getJson());
            initialize();
        }
    }

    public TotalScoreTable2(JsonEvents jevents) {
        this.jevents = jevents;
        if (jevents != null && jevents.totalScoreTableExists()) {
            initialize();
        }
    }

    private void addContainerProperties() {
        List<Object> header = jevents.totalScoreTable().dataHeader();
        for (Object element : header) {
            if (numbers.contains(element)) {
                container.addContainerProperty(element, Double.class, null);
            } else {
                container.addContainerProperty(element, String.class, null);
            }
        }
    }

    private void loadResults() {
        List<List<Object>> dataRows = jevents.totalScoreTable().dataRows();
        List<Object> header = jevents.totalScoreTable().dataHeader();
        int i = 0;
        for (List<Object> row : dataRows) {
            Object itemId = container.addItemAt(i);
            Item item = container.getItem(itemId);
            ListIterator<Object> datai = row.listIterator();
            for (Object column : header) {
                Object data = datai.next();
                @SuppressWarnings("unchecked")
                Property<Object> p = item.getItemProperty(column);
                p.setValue(data);
            }
            i++;
        }
    }
}
