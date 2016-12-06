package com.bridge.resultui;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;


@SuppressWarnings("serial")
public class VulZones extends VerticalLayout{

	public VulZones(String z, String dealer) {
		setMargin(true);
		Label l = new Label("Vul: "+z);
		Label l2 = new Label("Dealer: "+dealer);
		addComponents(l,l2);
	}
}
