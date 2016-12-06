package com.bridge.calendar;


import java.util.Date;
import java.util.GregorianCalendar;

import com.bridge.database.BridgeEvent;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Calendar;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.components.calendar.event.CalendarEvent;
import com.vaadin.shared.ui.datefield.Resolution;

@SuppressWarnings("serial")
public class CalendarForm extends FormLayout {

	TextField caption; 
	TextArea description; 
	DateField startDate; 
	DateField endDate; 
	CheckBox isAllDay;
	
	BeanItem<BridgeEvent> currentEvent; // the event being edited
	Calendar calendar;
	Boolean editMode; 
	
	JPAContainer<BridgeEvent> eventContainer;

	public CalendarForm(final BeanItem<BridgeEvent> item, 
			Calendar cal, JPAContainer<BridgeEvent> jpac, Boolean editMode) {
		
		// bind editors to respective data sources
		caption = new TextField("Otsikko", item.getItemProperty("caption"));
		description = new TextArea("Kuvaus", item.getItemProperty("description"));
		startDate = new DateField("Alkaa", item.getItemProperty("start"));
		endDate = new DateField("Paattyy", item.getItemProperty("end"));

		// initializations
		currentEvent = item;
		calendar = cal;
		this.editMode = editMode;
		eventContainer = jpac;
	
		BridgeEvent event = (BridgeEvent) item.getBean();
		isAllDay = new CheckBox("Koko paiva", event.isAllDay());
		
		startDate.setResolution (Resolution.MINUTE);
		endDate.setResolution (Resolution.MINUTE);

		caption.setImmediate(true);
		description.setImmediate(true);
		startDate.setImmediate(true);
		endDate.setImmediate(true);

		isAllDay.addValueChangeListener(new Property.ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				((BridgeEvent) item.getBean()).setAllDay(isAllDay.getValue());
				if (isAllDay.getValue()) {
					GregorianCalendar cal = new GregorianCalendar();
					int hour = java.util.Calendar.HOUR_OF_DAY;
					cal.setTime(startDate.getValue());
					cal.set(hour, cal.getActualMinimum(hour));
					startDate.setValue(cal.getTime());
				
					cal.setTime(endDate.getValue());
					cal.set(hour, cal.getActualMaximum(hour));
					cal.set(java.util.Calendar.MINUTE, 
							cal.getActualMaximum(java.util.Calendar.MINUTE));
					endDate.setValue(cal.getTime());
				}
			}
		});
		
		addComponents(caption, description, startDate, endDate, isAllDay);
		setSpacing(true);
		setMargin(true);
		// TESTING !!
		
	}
	
	Boolean addEvent() {
		
		Date end = ((BridgeEvent) currentEvent.getBean()).getEnd();
		Date start = ((BridgeEvent) currentEvent.getBean()).getStart();
		Boolean correctDate = new Boolean(end.after(start));
		
		if (correctDate) {
			
			
			if (!editMode) {
				eventContainer.addEntity(currentEvent.getBean());
				calendar.addEvent(currentEvent.getBean());	
			} else {
				eventContainer.removeItem(currentEvent.getBean().getId());
				eventContainer.addEntity(currentEvent.getBean());
			}
			eventContainer.sort(new String[]{"start"}, new boolean[]{true});		
			
			return true;
		} else
			return false;
		
	}
		
	void removeEvent() {
		eventContainer.removeItem(currentEvent.getBean().getId());
		calendar.removeEvent((CalendarEvent) currentEvent.getBean());
		
		if (eventContainer.removeItem(currentEvent.getBean().getId()))
			 Notification.show(
		                "Remove from container succeeded",
		                Notification.Type.HUMANIZED_MESSAGE);
		
//		eventContainer.remo
	}
		
        
	

	
}
