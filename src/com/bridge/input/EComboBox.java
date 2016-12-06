package com.bridge.input;

import com.vaadin.ui.ComboBox;

@SuppressWarnings("serial")
public class EComboBox extends ComboBox {

	public EComboBox(String caption, boolean required) {
		setCaption(caption);
		setImmediate(true);
		setRequired(required);
		setBuffered(false);
		setNullSelectionAllowed(false);
	}
}
