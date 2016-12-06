package com.bridge.resultui;

import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class DealNumber extends VerticalLayout {

    public DealNumber(String n) {
        Label nr = new Label(n);
        nr.setStyleName(ValoTheme.LABEL_H1);
        nr.addStyleName("dealNumber");
        setWidth("140px");
        // nr.setStyleName("dealNumber");
        setSizeFull();
        nr.setValue(n);
        addComponent(nr);
    }
}
