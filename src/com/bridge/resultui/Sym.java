package com.bridge.resultui;

import org.jsoup.nodes.Document;

@SuppressWarnings("serial")
public class Sym extends HLabel {

    public static String[] s = { "&clubs;", "&diams;", "&hearts;", "&spades;" };

    public Sym(int i) {
        super(s[i]);
        setCaption(null);

    }

    public static String getS(int i) {
        Document d = new Document("");
        String color = i == 1 || i == 2 ? "red" : "black";
        d.appendElement("span").attr("style", "color:" + color).append(s[i]);
        return d.html();
    }

}
