package com.bridge.ui.markdown;

import java.util.HashSet;

import com.bridge.ui.BridgeUI;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

@SuppressWarnings("serial")
public class DocumentLinkDetailsWindow extends Window {
	
	private String resourcePath;
	private HashSet<String> fileNames;
	private TextField linkText = new TextField("Link Text");
	private TextField link = new TextField("Link");
	private MyFileUpload upload; 
	private TextArea area;
	private Button done = new Button("Done");
	
	
	public DocumentLinkDetailsWindow(String path, HashSet<String> fileNames, TextArea area) {
		
		resourcePath = path;
		this.fileNames = fileNames;
		this.area = area;
		
		addWidgets();
		setModal(true);
		setWidth("300px");
	}

	private void addWidgets() {
		upload = new MyFileUpload(resourcePath);
		upload.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				String name = "";
				if (upload.getValue() != null)
					name = (upload.getLastFileName());
				link.setValue(name);
			}
		});
		
		done.addClickListener(new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				
				if (upload.getLastFileName() != null ) {
					String description = linkText.isEmpty() ? 
							upload.getLastFileName() : linkText.getValue().trim();
					Markdown.insert(area, "["+description.trim()+"]"+
							"("+"http://"+upload.getLastFileName()+")");
					BridgeUI.o("upload lastFileName: "+upload.getLastFileName());
					fileNames.add(upload.getLastFileName());
				}
				
				close();
			}
		});
		
		FormLayout fl = new FormLayout(linkText, link, upload, done);
		fl.setMargin(true);
		fl.setSpacing(true);
		setContent(fl);
		center();
		setCaption("Link details");
		
	}
}
