package com.bridge.resultui;

import java.util.List;

import com.pbn.pbnjson.JsonEvent;
import com.pbn.pbnjson.JsonEvents;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Panel;

/*
 * BoardDiagram represents the board as a 3 x 3 grid
 * containing the hands, points, board number, zones
 * etc. depending on the available information
 */

@SuppressWarnings("serial")
public class BoardDiagram extends Panel {

	protected GridLayout grid = new GridLayout(3, 3);
	protected JsonEvents jevents;
	protected Hand[] hands = new Hand[4];
	protected DealNumber dealNumber;
	protected DealPoints points;
	protected VulZones zones;

	public BoardDiagram(JsonEvents jevents) {
		this.jevents = jevents;
	}

	/*
	 * loadDiagram lays out the deal diagram
	 * @param board number
	 */
	public void loadDiagram(String board) {

		grid.removeAllComponents();
		JsonEvent jevent = jevents.event(board);

		addHands(jevent);
		setContent(grid);
		grid.setMargin(true);
		grid.setSizeFull();
		setSizeUndefined();
		addPoints(jevent);
		addZones(jevent);
		addDealNumber(board);
	}

	protected void addHands(JsonEvent jevent) {

		int i = 0;
		for (List<String> suits : jevent.getDeal()) {
			hands[i] = new Hand(suits);
			i++;
		}
		//
		// for (int i = 0; i < 4; i++) {
		// hands[i] = new Hand(e.deal()[i]);
		// }

		grid.setWidth("280px");
		grid.setHeight("280px");
		grid.addComponent(hands[0], 1, 0);
		grid.setComponentAlignment(hands[0], Alignment.MIDDLE_CENTER);
		grid.addComponent(hands[1], 2, 1);
		grid.setComponentAlignment(hands[1], Alignment.MIDDLE_CENTER);
		grid.addComponent(hands[2], 1, 2);
		grid.setComponentAlignment(hands[2], Alignment.MIDDLE_CENTER);
		grid.addComponent(hands[3], 0, 1);
		grid.setComponentAlignment(hands[3], Alignment.MIDDLE_RIGHT);
		// grid.setComponentAlignment(hands[3], Alignment.MIDDLE_CENTER);
	}

	/*
	 * addPoints adds hcp points for each player
	 * @param jevent contains
	 */
	protected void addPoints(JsonEvent jevent) {
		String[] pts = new String[4];
		for (int i = 0; i < 4; i++) {
			pts[i] = jevent.hcp(i);
		}
		points = new DealPoints(pts);
		grid.addComponent(points, 0, 2);
		grid.setComponentAlignment(points, Alignment.MIDDLE_CENTER);
	}

	protected void addZones(JsonEvent jevent) {
		zones = new VulZones(jevent.getVulnerable(), jevent.getDealer());
		grid.addComponent(zones, 0, 0);
		grid.setComponentAlignment(zones, Alignment.MIDDLE_CENTER);
	}

	protected void addDealNumber(String board) {
		dealNumber = new DealNumber(board);
		grid.addComponent(dealNumber, 1, 1);
		grid.setComponentAlignment(dealNumber, Alignment.MIDDLE_CENTER);
	}

	protected void addOptimum() {

	}
}
