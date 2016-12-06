package com.bridge.calendar;

import com.bridge.database.BridgeEvent;
import com.bridge.database.Tournament;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class TabContents extends VerticalLayout {

	private EntityItem<BridgeEvent> eventItem;
	private EntityItem<Tournament> tourItem;
	private BridgeEvent bridgeEvent;
	private Tournament tournament;
	
	public TabContents() {
		eventItem = null;
		tourItem = null;
		bridgeEvent = new BridgeEvent();
		tournament = new Tournament();
	}

	public TabContents(EntityItem<BridgeEvent> bi, EntityItem<Tournament> ti) {
		eventItem = bi;
		tourItem = ti;
		bridgeEvent = null;
		tournament = null;
	}
	
	public TabContents(BridgeEvent event, Tournament tour) {
		this.bridgeEvent = event;
		this.tournament = tour;
	}

	public EntityItem<BridgeEvent> getBridgeEventItem() {
		return eventItem;
	}
	
	public EntityItem<Tournament> getTournamentItem() {
		return tourItem;
	}

	public BridgeEvent getBridgeEvent() {
		return bridgeEvent;
	}

	public void setBridgeEvent(BridgeEvent bridgeEvent) {
		this.bridgeEvent = bridgeEvent;
	}

	public Tournament getTournament() {
		return tournament;
	}

	public void setTournament(Tournament tournament) {
		this.tournament = tournament;
	}

	public boolean editItems() {
		return eventItem != null && tourItem != null;
	}
}
