package com.bridge.resultui;

import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class H extends VerticalLayout {
	
	public H(String[] suits) {
		setWidth("100px");
		for (int i = 0; i<suits.length; i++) {
			Suit s = new Suit(i, suits[i]);
			addComponent(s);
			
		}
	}
}
