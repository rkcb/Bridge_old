package com.bridge.ui;

import com.vaadin.ui.Component;
import com.vaadin.ui.FormLayout;

@SuppressWarnings("serial")
public class EFormLayout extends FormLayout {

	public EFormLayout() {
		setSpacing(true);
		setMargin(true);
	}

	public EFormLayout(Component... children) {
		
		super(children);
		super.setSpacing(true);
		super.setMargin(true);
	}

}
