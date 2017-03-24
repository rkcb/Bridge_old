package com.bridge.resultui;

import com.vaadin.ui.VerticalLayout;

public class Hand2 extends VerticalLayout {

    public Hand2(String[] suits) {
        setWidth("100px");
        for (int i = 0; i < suits.length; i++) {
            Suit s = new Suit(i, suits[i]);
            addComponent(s);

        }
    }
}
