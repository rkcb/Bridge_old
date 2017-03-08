package com.bridge.resultui;

import java.util.Arrays;
import java.util.HashSet;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;
import com.vaadin.ui.Table;

import scala.bridge.TableFactory;

@SuppressWarnings("serial")
public class ComparisonTable extends Table {

    private IndexedContainer container = new IndexedContainer();
    private TableFactory factory;
    private HashSet<String> numbers = new HashSet<>();
    private HashSet<String> html = new HashSet<>();

    /***
     * lead card and contract are represented in html
     */

    private void addHtmlContainerProperties() {
        html = new HashSet<>();
        html.add("Lead");
        html.add("Contract");
    }

    /***
     * addColumnTypes
     */
    protected void addColumnTypes() {
        String[] numberColumns = { "Board", "IMP_NS", "IMP_EW", "MP_NS",
                "MP_EW", "Percentage_NS", "Percentage_EW", "MP_South",
                "Percentage_South", "Result" };
        numbers = new HashSet<>(Arrays.asList(numberColumns));
        addHtmlContainerProperties();
    }

    public ComparisonTable(TableFactory factory) {
        super();
        addStyleName("pbntable");
        this.factory = factory;
        addColumnTypes();
        addContainerProperties();
        setContainerDataSource(container);
        setSelectable(true);

        // translate header
        for (Object id : factory.comparisonHeader()) {
            setColumnHeader(id, factory.translateFi("comparison", (String) id));
        }
    }

    private void addContainerProperties() {
        String[] header = factory.comparisonHeader();
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

    public void setData(String board, String playerId) {

        container.removeAllItems();

        String[] header = factory.comparisonHeader();
        String[][] scores = factory.comparisonData(board);
        setPageLength(scores.length);
        boolean selected = false;

        for (int i = 0; i < scores.length; i++) {
            Object itemId = container.addItemAt(i);
            Item item = container.getItem(itemId);
            for (int j = 0; j < header.length; j++) {
                @SuppressWarnings("unchecked")
                Property<Object> p = item.getItemProperty(header[j]);
                String s = scores[i][j];
                if (!selected) {
                    selected = select(header[j], s, playerId, itemId);
                }

                if (numbers.contains(header[j])) {
                    float fl = 0;
                    try {
                        fl = Float.parseFloat(s);
                        p.setValue(fl);
                    } catch (NumberFormatException e) {
                    }

                } else if (html.contains(header[j])) {
                    p.setValue(new SuitString(s));
                } else {
                    p.setValue(s);
                }
            }
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
