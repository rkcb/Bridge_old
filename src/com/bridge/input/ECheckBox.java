package com.bridge.input;

import com.vaadin.ui.CheckBox;

@SuppressWarnings("serial")
public class ECheckBox extends CheckBox {

	public ECheckBox(String caption, boolean required) {
		setCaption(caption);
		setRequired(required);
		setImmediate(true);
		setBuffered(false);
	}
}
