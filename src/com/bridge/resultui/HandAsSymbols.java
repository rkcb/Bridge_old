package com.bridge.resultui;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class HandAsSymbols extends Panel {

    private VerticalLayout hLayout = new VerticalLayout();
    private Label[] labels = new Label[4];
    private String[] symbols = { "&spades;", "&hearts;", "&diams;", "&clubs;" };

    public HandAsSymbols() {
        super();
        for (int i = 0; i < labels.length; i++) {
            labels[i] = new Label();
            labels[i].setContentMode(ContentMode.HTML);
            hLayout.addComponent(labels[i]);
        }
        setContent(hLayout);

        addStyleName("myway");
    }

    public void setSuits(String[] suits) {
        for (int i = 0; i < suits.length; i++) {
            labels[i].setValue(symbols[i] + suits[i]);
        }
    }
}
