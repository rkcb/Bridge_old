package com.bridge.resultui;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;

@SuppressWarnings("serial")

/***
 * SuitString is used for lead and contract strings to show them in html
 */
public class SuitString extends Label {

    public SuitString(String s) {
        setContentMode(ContentMode.HTML);
        s = s.trim();

        if (s.matches(".*(n|N)$")) {
            s = s.replaceFirst("(n|N)$", "NT");
        } else if (s.matches(".*(c|C).*")) {
            s = s.replaceFirst("c|C", Suit.getS(3));
        } else if (s.matches(".*(d|D).*")) {
            s = s.replaceFirst("d|D", Suit.getS(2));
        } else if (s.matches(".*(h|H).*")) {
            s = s.replaceFirst("h|H", Suit.getS(1));
        } else if (s.matches(".*(s|S).*")) {
            s = s.replaceFirst("s|S", Suit.getS(0));
        }

        setValue(s);
    }

}
