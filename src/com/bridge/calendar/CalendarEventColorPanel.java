package com.bridge.calendar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

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
    private List<String> colors;
    private final String circle = "&#9679; ";
    private final String descriptionColor = "black";

    public CalendarEventColorPanel() {
    }

    public CalendarEventColorPanel(String... descriptions) {
        Objects.requireNonNull(descriptions);
        layout = new EVerticalLayout();
        colors = new ArrayList<>();
        Collections.addAll(colors, "#EE399F", "#2d9f19", "#004200", "#ce3812",
                "#2d55cd");

        int descSize = Integer.min(descriptions.length, colors.size());
        for (int i = 0; i < descSize; i++) {
            layout.addComponent(getLabel(i, descriptions[i]));
        }

        setContent(layout);
    }

    private Label getLabel(int colorIndex, String description) {
        Label label = new Label();
        label.setValue(getHtml(colorIndex, description));
        label.setContentMode(ContentMode.HTML);
        label.addStyleName("calendarColorLabel");
        return label;
    }

    private String getHtml(int colorIndex, String description) {
        Document d = Jsoup.parse("");
        Element e = d.select("body").first();
        Element span1 = e.appendElement("span")
                .attr("style", "color:" + colors.get(colorIndex)).html(circle);

        span1.appendElement("span").attr("style", "color:" + descriptionColor)
                .text(description);
        return d.html();
    }

}
