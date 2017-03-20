package com.bridge.resultui;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;

@SuppressWarnings("serial")
public class HtmlLabel extends Label {

    public HtmlLabel(Object html) {
        setContentMode(ContentMode.HTML);
        setValue((String) html);
    }
}
