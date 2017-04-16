package com.bridge.resultui;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class VulZones extends VerticalLayout {

	public VulZones(String zones, String dealer) {
		setMargin(true);
		if (zones != null && !zones.isEmpty()) {
			Label l = new Label("Vul: " + zones);
			addComponent(l);
		}
		if (dealer != null && !dealer.isEmpty()) {
			Label l2 = new Label("Dealer: " + dealer);
			addComponent(l2);
		}
	}
}
