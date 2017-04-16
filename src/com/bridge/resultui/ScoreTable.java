package com.bridge.resultui;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.bridge.ui.ETable;
import com.pbn.pbnjson.JsonEvents;
import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.util.IndexedContainer;

@SuppressWarnings("serial")
public class ScoreTable extends ETable {

    private IndexedContainer container = new IndexedContainer();
    private JsonEvents jevents;

    private HashSet<String> numbers;
    private HashSet<String> htmls;

    boolean indiOrPairs;
    boolean teams;

    public ScoreTable(JsonEvents jevents) {
        this.jevents = jevents;
        addStyleName("pbntable"); // styles table rows
        indiOrPairs = jevents.competion().matches("Individuals|Pairs");
        teams = jevents.competion().matches("Teams");
        if (indiOrPairs) {
            setSelectable(true);
        }
        addContainerProperties();
        setContainerDataSource(container);
        setPageLength(0);

        // cell coloring
        if (indiOrPairs) {
            if (jevents.mpScoring()) {
                System.out.println("mp scoring");
                addResultColoringMP(); // colored properties are disjoint
            }
            if (jevents.impScoring()) {
                addResultColoringAnyIMP(); // so there is no collision
            }
        } else if (teams) {
            if (jevents.vpScoring()) {
                addResultColoringVP();
            }
        } else {
            System.out.println("no color");
        }
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
                Double f = (Double) container.getItem(itemId)
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
                Double f = (Double) container.getItem(itemId)
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

        final Double avMax = jevents.averageMaxIMP();

        setCellStyleGenerator((source, itemId, propertyId) -> {
            final String pew = "IMP_EW";
            final String pns = "IMP_NS";
            final String indi = "IMP_South";
            final Float topHalf = (float) (avMax * 0.4); // 0.4 is guess work
            final Float bottomHalf = (float) (-avMax * 0.4);

            final String pid = (String) propertyId;
            if (pid != null && (pid.matches(pew) || pid.matches(pns)
                    || pid.matches(indi))) {
                Double d = (Double) container.getItem(itemId)
                        .getItemProperty(propertyId).getValue();
                if (d == null) {
                    return "white";
                }

                if (d < bottomHalf) {
                    return "cellRed";
                } else if (bottomHalf < d && d < 0) {
                    return "cellYellow";
                } else if (d >= 0 && d < topHalf) {
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
     * set column types i.e. container properties
     */

    void addContainerProperties() {
        List<String> header = jevents.scoreHeader();
        numbers = jevents.get(0).getScoreTable().numberColumns();
        htmls = jevents.get(0).getScoreTable().htmlColumns();

        // JsonScoreTable header does not contain "Board" and
        // the team results does not contain "Board" column
        if (indiOrPairs) {
            container.addContainerProperty("Board", Integer.class, null);
        }

        for (String element : header) {
            if (numbers.contains(element)) { // Double
                container.addContainerProperty(element, Double.class, null);
            } else if (htmls.contains(element)) { // contract or lead
                container.addContainerProperty(element, HtmlLabel.class, null);
            } else {
                container.addContainerProperty(element, String.class, null);
            }
        }
    }

    /***
     * score loads data for the playing unit (= indi, pair, team) with id
     */

    @SuppressWarnings("unchecked")
    public void score(String id) {
        List<String> header = jevents.scoreHeader();
        List<List<Object>> data = jevents.scoreData(id);

        int i = 0;
        for (List<Object> row : data) {
            Object itemId = container.addItemAt(i);
            Item item = getItem(itemId);
            // only indi or pairs need the board; teams not!
            if (indiOrPairs) {
                item.getItemProperty("Board")
                        .setValue(Integer.parseInt(jevents.get(i).getBoard()));
            }
            Iterator<Object> rowi = row.iterator();
            for (String column : header) {
                Object cell = rowi.next();
                Property<Object> p = item.getItemProperty(column);
                if (htmls.contains(column)) {
                    p.setValue(new HtmlLabel(cell));
                } else {
                    p.setValue(cell);
                }
            }
            i++;
        }
    }
}
