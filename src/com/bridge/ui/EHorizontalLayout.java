package com.bridge.ui;

import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;

@SuppressWarnings("serial")
public class EHorizontalLayout extends HorizontalLayout {

	public EHorizontalLayout() {
		setMargin(true);
		setSpacing(true);
		setResponsive(true);
		setSizeUndefined();
	}

	public EHorizontalLayout(Component... children) {
		super(children);
		setMargin(true);
		setSpacing(true);
		setResponsive(true);
		setSizeUndefined();
	}

}
