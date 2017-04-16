package com.bridge.resultui;

import org.jsoup.nodes.Document;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;

@SuppressWarnings("serial")
public class Suit extends Label {

    static public final String[] s = { "&spades;", "&hearts;", "&diams;",
            "&clubs;" };

    public Suit(int sym, String suit) {
        // setStyleName(ValoTheme.PANEL_BORDERLESS);
        setContentMode(ContentMode.HTML);
        setStyleName("suit");
        setWidth("150px");
        Document d = new Document("");
        d.appendElement("p").append(getS(sym)).append(" " + suit);
        setValue(d.html());
    }

    /***
     * getS returns a string in HTML which is either black or red suit symbol
     */

    static public String getS(int i) {
        Document d = new Document("");
        String color = i == 1 || i == 2 ? "red" : "black";
        d.appendElement("span").attr("style", "color:" + color).append(s[i]);
        return d.html();
    }
}
