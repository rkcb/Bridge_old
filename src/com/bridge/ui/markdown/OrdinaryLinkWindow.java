package com.bridge.ui.markdown;

import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@SuppressWarnings("serial")
public class OrdinaryLinkWindow extends Window {
	
	protected TextField link = new TextField("Link");
	protected TextField linkText  = new TextField("Link Text");
	protected Button done = new Button("Done");
	protected TextArea area;
	
	public OrdinaryLinkWindow(final TextArea area) {
		
		link.setRequired(true);
		FormLayout l = new FormLayout(link, linkText, done);
		l.setMargin(true);
		l.setSpacing(true);
		setCaption("Link Details");
		setModal(true);
		setContent(l);
		setSizeUndefined();
		setWidth("300px");
		center();
		
		done.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				String l = buildUrl(link.getValue().trim());
				String lt = linkText.getValue().trim();
				lt = lt.isEmpty() ? l : lt;
				
				if (!l.isEmpty()) {
					Markdown.insert(area, "["+lt+"]"+"("+l+")");
				}
				
				close();
			}
		});
		
		this.area = area;
		
		
	}
	
	protected boolean beginsWithHttp(String url) {
		boolean x = false;
	
		if (url != null) {
			x = url.startsWith("http://");
			x = x || url.startsWith("https://");
			return x;
		} else return false;
	}
	
	/***
	 * buildUrl prepends http:// to the link iff it is missing
	 * */

	protected String buildUrl(String link) {
		StringBuilder b = new StringBuilder("http://");
		
		if (link != null && !link.isEmpty() && !beginsWithHttp(link)) {
			b.append(link);
			return b.toString();
		} else if (link != null && !link.isEmpty())
			return link;
		else return "";
	}
	
	
	
}
