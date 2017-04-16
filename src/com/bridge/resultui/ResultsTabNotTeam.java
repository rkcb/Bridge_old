package com.bridge.resultui;

import com.bridge.ui.EHorizontalLayout;
import com.bridge.ui.EVerticalLayout;
import com.pbn.pbnjson.JsonEvents;
import com.vaadin.data.Item;
import com.vaadin.ui.Alignment;

/***
 * ResultsTabNotTeam creates a TabSheet tab contents for a one pbn file;
 */

@SuppressWarnings("serial")
public class ResultsTabNotTeam extends EVerticalLayout {

	private JsonEvents jevents;
	private TotalScoreTable2 totalScoreTable = null;
	private ScoreTable scoreTable = null;
	private BoardDiagram diagram = null;
	private ComparisonTable comparisonTable = null;
	private EHorizontalLayout hLayout = new EHorizontalLayout();
	private EVerticalLayout vLayout = new EVerticalLayout();
	private EVerticalLayout vLayout2 = new EVerticalLayout();
	private String playerId; // player or pair id selected in totalscoretable

	public ResultsTabNotTeam(JsonEvents jevents) {
		this.jevents = jevents;
		buildTables();
	}

	protected void buildTables() {

		if (jevents.totalScoreTableExists()) {
			totalScoreTable = new TotalScoreTable2(jevents);
			totalScoreTable.setPageLength(0);
			vLayout.addComponent(totalScoreTable);

			if (jevents.scoreTableExists()) {
				scoreTable = new ScoreTable(jevents);
				hLayout.addComponent(scoreTable);
				vLayout.addComponent(hLayout);
			}

			if (jevents.dealsExists()) {
				diagram = new BoardDiagram(jevents);
				vLayout2.addComponent(diagram);
			}

			if (jevents.comparisonTableExists()) {
				// comparisonTable = new ComparisonTable(factory);
				comparisonTable = new ComparisonTable(jevents);
				vLayout2.addComponent(comparisonTable);
				hLayout.addComponent(vLayout2);
				if (jevents.dealsExists()) {
					vLayout2.setComponentAlignment(diagram,
							Alignment.MIDDLE_LEFT);
				}
			}

			buildClickConnections();
			addComponent(vLayout);
		}
	}

	private void buildClickConnections() {

		// if totalScoreTable is clickable then show scores
		// for the clicked for the line

		// case for pairs and individuals only
		if (jevents.totalScoreTableExists() && jevents.scoreTableExists()
				&& jevents.competion().matches("Individuals|Pairs")) {
			// enable that clicking a line shows the results for this player or
			// pair
			// Important: currently this feature is not supported for team games
			totalScoreTable.addItemClickListener(event -> {
				Integer id = (Integer) event.getItemId();
				Item item = totalScoreTable.getItem(id);
				// Float f = (Float) item.getItemProperty(factory.idField())
				// .getValue();
				// playerId = String.valueOf(f.intValue());
				playerId = (String) item.getItemProperty(jevents.idField())
						.getValue();
				scoreTable.removeAllItems();
				scoreTable.score(playerId);
			});

			// enable that clicking a line of scoreTable shows the comparison
			// results for this deal
			scoreTable.addItemClickListener(event -> {
				Object id = event.getItemId();
				Item item = scoreTable.getItem(id);

				Integer in = (Integer) item.getItemProperty("Board").getValue();
				String dealId = Integer.toString(in.intValue());

				if (jevents.dealsExists()) {
					diagram.loadDiagram(dealId);
				}

				comparisonTable.setData(dealId, playerId);
			});
		}
	}

}
