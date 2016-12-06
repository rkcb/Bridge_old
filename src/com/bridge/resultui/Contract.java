package com.bridge.resultui;

@SuppressWarnings("serial")
public class Contract extends HLabel {

    public Contract(String s) {
        super(null);
        setStyleName("DealContract");
        if (s.matches("[cC]")) {
            s.replaceFirst("[cC]", Suit.getS(0));
        } else if (s.matches("[dD]")) {
            s.replaceFirst("[dD]", Suit.getS(1));
        } else if (s.matches("[hH]")) {
            s.replaceFirst("[hH]", Suit.getS(2));
        } else if (s.matches("[sSD]")) {
            s.replaceFirst("[sS]", Suit.getS(3));
        } else if (s.matches("")) {
        }

    }

}
