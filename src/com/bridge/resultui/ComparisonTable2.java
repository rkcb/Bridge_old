package com.bridge.resultui;

import java.util.HashSet;
import java.util.List;

import com.pbn.pbnjson.JsonEvents;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Table;

public class ComparisonTable2 extends Table {

    private IndexedContainer container = new IndexedContainer();
    private JsonEvents jevents;
    private HashSet<String> numbers = new HashSet<>();
    private HashSet<String> html = new HashSet<>();

    public ComparisonTable2(JsonEvents jevents) {
        addContainerProperties();
        numbers = jevents.get(0).getScoreTable().numberColumns();
    }

    private void addContainerProperties() {
        List<String> header = jevents.comparisonHeader();
        for (String element : header) {
            if (numbers.contains(element)) { // float
                container.addContainerProperty(element, Float.class, null);
            } else if (html.contains(element)) { // contract or lead
                container.addContainerProperty(element, SuitString.class, null);
            } else {
                container.addContainerProperty(element, String.class, null);
            }
        }
    }
}
