package com.bridge.resultui;

import com.bridge.ui.EHorizontalLayout;
import com.bridge.ui.EVerticalLayout;
import com.pbn.pbnjson.JsonEvents;
import com.vaadin.data.Item;

import scala.bridge.TableFactory;

@SuppressWarnings("serial")
public class ResultsTabTeam extends EVerticalLayout {
    private TableFactory factory;
    private JsonEvents jevents;
    private TotalScoreTable2 totalScoreTable = null;
    private ScoreTable2 scoreTable = null;
    // private D diagram = null;
    // private ComparisonTable comparisonTable = null;
    private EHorizontalLayout hLayout = new EHorizontalLayout();
    // private String idField;
    private EVerticalLayout vLayout = new EVerticalLayout();
    // private EVerticalLayout vLayout2 = new EVerticalLayout();
    private String playerId; // player or pair id sel

    public ResultsTabTeam(JsonEvents jevents) {
        this.jevents = jevents;
        buildTables();
    }

    protected void buildTables() {
        if (jevents.totalScoreTableExists()) {
            totalScoreTable = new TotalScoreTable2(jevents);
            totalScoreTable.setPageLength(0);
            vLayout.addComponent(totalScoreTable);

            if (factory.scoreTableSupported()) {
                scoreTable = new ScoreTable2(jevents);
                hLayout.addComponent(scoreTable);
                vLayout.addComponent(hLayout);
            }
            buildClickConnections();

            addComponent(vLayout);
        }
    }

    private void buildClickConnections() {

        if (factory.totalScoreSupported() && factory.scoreTableSupported()) {
            totalScoreTable.addItemClickListener(event -> {
                Integer id = (Integer) event.getItemId();
                Item item = totalScoreTable.getItem(id);
                Float f = (Float) item.getItemProperty(factory.idField())
                        .getValue();
                playerId = String.valueOf(f.intValue());
                scoreTable.removeAllItems();
                scoreTable.score(playerId);
            });
        }

    }
}
