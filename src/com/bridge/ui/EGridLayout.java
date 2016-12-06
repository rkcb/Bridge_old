package com.bridge.ui;

import com.vaadin.ui.Component;
import com.vaadin.ui.GridLayout;

@SuppressWarnings("serial")
public class EGridLayout extends GridLayout {

	protected void setDefault() {
		setSpacing(true);
		setMargin(true);
		
	}
	
	public EGridLayout() { setDefault(); }
	
	public EGridLayout(Component... cs) {
		setDefault();
	}
}
