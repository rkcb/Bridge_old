package com.bridge.resultui;

import java.util.Arrays;
import java.util.HashSet;

import com.bridge.ui.ETable;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;

import scala.bridge.TableFactory;

@SuppressWarnings("serial")
public class ScoreTable extends ETable {

    private IndexedContainer container = new IndexedContainer();
    private TableFactory factory;

    private HashSet<String> numbers;
    private HashSet<String> html;

    public ScoreTable(TableFactory factory) {
        super();
        boolean teamMatch = factory.competitionType().matches("team");
        addHtmlContainerProperties();
        this.factory = factory;
        setStyleName("pbntable"); // styles table rows
        if (!teamMatch) {
            setSelectable(true);
        }
        addColumnTypes();
        addContainerProperties();
        setContainerDataSource(container);
        for (Object id : factory.scoreTableHeader()) {
            setColumnHeader(id, factory.translateFi("score", (String) id));
        }

        setPageLength(0);

        if (!teamMatch) {
            if (factory.MPScoring()) {
                addResultColoringMP(); // colored properties are disjoint
            }
            if (factory.IMPScorning()) {
                addResultColoringAnyIMP(); // so there is no collision
            }
        } else if (teamMatch) {
            if (factory.VPScoring()) {
                addResultColoringVP();
            }
        }
    }

    /***
     * lead card and contract are represented in html
     */

    private void addHtmlContainerProperties() {
        html = new HashSet<>();
        html.add("Lead");
        html.add("Contract");
    }

    /***
     * addResultColoring adds a matchpoint scoring coloring
     */

    private void addResultColoringMP() {
        setCellStyleGenerator((source, itemId, propertyId) -> {
            final String pew = "Percentage_EW";
            final String pns = "Percentage_NS";
            final String indi = "Percentage_South";

            final String pid = (String) propertyId;

            if (pid instanceof String && (pid.matches(pew) || pid.matches(pns)
                    || pid.matches(indi))) {
                Float f = (Float) container.getItem(itemId)
                        .getItemProperty(propertyId).getValue();
                if (f == null) {
                    return "white";
                }

                if (f < 25) {
                    return "cellRed";
                } else if (f < 50) {
                    return "cellYellow";
                } else if (f < 75) {
                    return "cellLightgreen";
                } else {
                    return "cellGreen";
                }
            } else {
                return null;
            }
        });
    }

    private void addResultColoringVP() {
        setCellStyleGenerator((source, itemId, propertyId) -> {
            final String vp = "VP_Home";
            final Float topHalf = (float) 15;
            final Float middle = (float) 10;
            final Float bottomHalf = (float) 5;

            final String pid = (String) propertyId;
            if (pid != null && pid.matches(vp)) {
                Float f = (Float) container.getItem(itemId)
                        .getItemProperty(propertyId).getValue();
                if (f == null) {
                    return "white";
                }

                if (f < bottomHalf) {
                    return "cellRed";
                } else if (bottomHalf < f && f < middle) {
                    return "cellYellow";
                } else if (middle < f && f < topHalf) {
                    return "cellLightgreen";
                } else {
                    return "cellGreen";
                }
            } else {
                return null;
            }
        });
    }

    /***
     * addResultColoring adds a coloring for any kind of IMP scoring
     */

    private void addResultColoringAnyIMP() {

        final Float avMax = factory.averageMaxImp();

        setCellStyleGenerator((source, itemId, propertyId) -> {
            final String pew = "IMP_EW";
            final String pns = "IMP_NS";
            final String indi = "IMP_South";
            final Float topHalf = (float) (avMax * 0.4); // 0.4 is guess work
            final Float bottomHalf = (float) (-avMax * 0.4);

            final String pid = (String) propertyId;
            if (pid != null && (pid.matches(pew) || pid.matches(pns)
                    || pid.matches(indi))) {
                Float f = (Float) container.getItem(itemId)
                        .getItemProperty(propertyId).getValue();
                if (f == null) {
                    return "white";
                }

                if (f < bottomHalf) {
                    return "cellRed";
                } else if (bottomHalf < f && f < 0) {
                    return "cellYellow";
                } else if (f >= 0 && f < topHalf) {
                    return "cellLightgreen";
                } else {
                    return "cellGreen";
                }
            } else {
                return null;
            }
        });
    }

    /***
     * addColumnTypes defines which columns are numbers and which are strings
     * these properties enable a proper column ordering
     */

    protected void addColumnTypes() {
        String[] numberColumns = { "Board", "IMP_NS", "IMP_EW", "MP_NS",
                "MP_EW", "Percentage_NS", "Percentage_EW", "MP_South",
                "Percentage_South", "Result", "VP_Home", "VP_Away", "Round" };
        numbers = new HashSet<>(Arrays.asList(numberColumns));

    }

    /***
     * set column types i.e. container properties
     */

    void addContainerProperties() {
        String[] header = factory.scoreTableHeader();

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

    /***
     * score loads data for the playing unit (= indi, pair, team) with id
     */

    public void score(String id) {
        String[] header = factory.scoreTableHeader();
        Object[][] scores = factory.scoreData(id);
        for (int i = 0; i < scores.length; i++) {
            Object itemId = container.addItemAt(i);
            Item item = getItem(itemId);
            for (int j = 0; j < header.length; j++) {
                @SuppressWarnings("unchecked")
                Property<Object> p = item.getItemProperty(header[j]);
                String s = (String) scores[i][j];
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
}
