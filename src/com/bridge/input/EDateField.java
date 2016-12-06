package com.bridge.input;


import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.DateField;

@SuppressWarnings("serial")
public class EDateField extends DateField {

	public EDateField(String caption, boolean required) {
		String s = "dd.MM.yyyy, HH:mm";
		setDateFormat(s);
		setImmediate(true);
		setCaption(caption);
		setBuffered(false);
		setRequired(required);
		setResolution(Resolution.MINUTE);
		setShowISOWeekNumbers(true);
		
		
	}
}
