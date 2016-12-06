package com.bridge.ui.markdown;

import com.vaadin.data.validator.EmailValidator;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;


@SuppressWarnings("serial")
public class EmailLinkWindow extends Window {

	private TextField linkText = new TextField("Link Text");
	private TextField link = new TextField("Email");
	private Button done = new Button("Done");
	
	public EmailLinkWindow(final TextArea area) {
		
		setModal(true);
		link.addValidator(new EmailValidator("Email address is invalid"));
		link.setRequired(true);
		link.setImmediate(true);
		
		done.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if (!link.getValue().isEmpty()) {
					String lt = linkText.getValue();
					if (lt.trim().isEmpty())
						lt = link.getValue().trim();
					Markdown.insert(area, Markdown.buildLink(lt, "mailto:"+link.getValue()));
				}
				close();
			}
		});
		
		FormLayout l = new FormLayout(link, linkText, done);
		l.setMargin(true);
		l.setSpacing(true);
		
		setCaption("Email Link Details");
		setContent(l);
		setWidth("300px");
	}

}
