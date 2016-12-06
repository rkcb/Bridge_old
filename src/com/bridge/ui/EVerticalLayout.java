package com.bridge.ui;

import com.vaadin.ui.Component;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class EVerticalLayout extends VerticalLayout {

    public EVerticalLayout() {
        setMargin(true);
        setSpacing(true);
    }

    public EVerticalLayout(Component... children) {

        super(children);
        setMargin(true);
        setSpacing(true);
    }

}
