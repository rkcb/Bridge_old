package com.bridge.resultui;

import java.util.Arrays;
import java.util.HashSet;

import com.bridge.ui.ETable;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;

import scala.bridge.TableFactory;

@SuppressWarnings("serial")
public class TotalScoreTable extends ETable {
    // table column types
    // private static HashSet<String> integers;

    private IndexedContainer container = new IndexedContainer();
    private TableFactory factory = null;
    private HashSet<String> numbers;

    public TotalScoreTable(TableFactory factory) {
        super();
        addColumnTypes();
        addStyleName("pbntable");
        setSelectable(true);
        this.factory = factory;
        addContainerProperties();
        loadResults();
        setContainerDataSource(container);
        for (Object id : factory.totalScoreHeader()) {
            setColumnHeader(id, factory.translateFi("total", (String) id));
        }
    }

    /***
     * addColumnTypes adds column types to enable proper column ordering
     */
    protected void addColumnTypes() {
        String[] numberColumns = { "Rank", "PlayerId", "PairId", "TeamId",
                "TotalScoreMP", "TotalScoreVP", "TotalScoreIMP",
                "TotalScoreBAM", "TotalPercentage", "ScorePenalty", "MP" };
        numbers = new HashSet<>(Arrays.asList(numberColumns));

    }

    private void addContainerProperties() {
        String[] header = factory.totalScoreHeader();
        for (String element : header) {
            if (numbers.contains(element)) {
                container.addContainerProperty(element, Float.class, null);
            } else {
                container.addContainerProperty(element, String.class, null);
            }
        }
    }

    private void loadResults() {
        String[][] data = factory.totalScoreData();
        String[] header = factory.totalScoreHeader();
        for (int i = 0; i < data.length; i++) {
            Object itemId = container.addItemAt(i);
            Item item = container.getItem(itemId);
            for (int j = 0; j < header.length; j++) {
                @SuppressWarnings("unchecked")
                Property<Object> p = item.getItemProperty(header[j]);
                if (numbers.contains(header[j])) {
                    float fl = 0;
                    try {
                        fl = Float.parseFloat(data[i][j]);
                    } catch (NumberFormatException e) {
                        // non number found, for example TotalScoreVP
                        // may contain '-' if the team did not earn MPs
                    }
                    p.setValue(fl);

                } else {
                    String s = data[i][j] == null ? "" : data[i][j];
                    p.setValue(s);
                }
            }
        }
    }

}
