package com.bridge.resultui;

import java.util.List;

import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class Hand extends VerticalLayout {

    public Hand(List<String> suits) {
        setWidth("100px");
        int i = 0;
        for (String suit : suits) {
            addComponent(new Suit(i++, suit));
        }
    }
}
