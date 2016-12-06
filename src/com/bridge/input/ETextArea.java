package com.bridge.input;

import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.ui.TextArea;

@SuppressWarnings("serial")
public class ETextArea extends TextArea {

	private static String text = "[\\w\\.,-?!\"\\d;:\\s()]*"; 
	
	public ETextArea(String caption, String regExp, boolean required) {
		setImmediate(true);
		setBuffered(false);
		setRequired(required);
		setCaption(caption);
		if (regExp != null)
			addValidator(new RegexpValidator(regExp, "Not valid text"));
	}
	
	public ETextArea(String caption, boolean required, boolean regularText) {
		setImmediate(true);
		setRequired(required);
		setCaption(caption);
		if (regularText)
			addValidator(new RegexpValidator(text, "Not valid text"));
	}
	
}
