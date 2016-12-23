package com.bridge.calendar;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.bridge.data.EventColorEnum;
import com.bridge.ui.EVerticalLayout;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;

/***
 * CalendarEventColorPanel describes the calendar event color meanings; see
 * bridge.scss for "color1", "color2", ... definitions
 */
@SuppressWarnings("serial")
public class CalendarEventColorPanel extends Panel {

    private EVerticalLayout layout;
    private HashSet<EventColorEnum> colors;
    private final String circleCode = "&#9679; ";
    private final String descriptionColor = "black";

    /***
     * @param events
     *            length is at most five
     */
    public CalendarEventColorPanel(EventColorEnum... events) {
        Objects.requireNonNull(events);
        layout = new EVerticalLayout();
        colors = new HashSet<>();
        Collections.addAll(colors, events);

        for (EventColorEnum color : events) {
            layout.addComponent(getLabel(color));
        }
        setContent(layout);
    }

    private Label getLabel(EventColorEnum color) {
        Label label = new Label();
        label.setValue(getHtml(color));
        label.setContentMode(ContentMode.HTML);
        label.addStyleName("calendarColorLabel");
        return label;
    }

    private String getHtml(EventColorEnum color) {
        Document d = Jsoup.parse("");
        Element e = d.select("body").first();
        Element span1 = e.appendElement("span")
                .attr("style", "color:" + color.code()).html(circleCode);

        span1.appendElement("span").attr("style", "color:" + descriptionColor)
                .text(color.description());
        return d.html();
    }

}
