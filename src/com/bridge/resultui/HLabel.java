package com.bridge.resultui;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;

@SuppressWarnings("serial")
public class HLabel extends Label {

	public HLabel(String html) {
		super(html);
		setCaption(null);
		setContentMode(ContentMode.HTML);
	}
}
