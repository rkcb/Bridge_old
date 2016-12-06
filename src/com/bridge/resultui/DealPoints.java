package com.bridge.resultui;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;

@SuppressWarnings("serial")
public class DealPoints extends GridLayout {

	private class L extends Label {
		public L(String p) {
			setContentMode(ContentMode.HTML);
			setValue(p);
		}
	}
	
	public DealPoints(String[] points) {
		super(3,3);
		// assume N, E, S, W
		
		addComponent(new L(points[0]), 1, 0);
		addComponent(new L(points[1]), 2, 1);
		addComponent(new L(points[2]), 1, 2);
		addComponent(new L(points[3]), 0, 1);
	}

}
