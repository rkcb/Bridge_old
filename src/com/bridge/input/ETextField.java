package com.bridge.input;

import com.vaadin.ui.TextField;

@SuppressWarnings("serial")
public class ETextField extends TextField {

    // private static String text = "\\w[\\w\\.,-?!\"\\d;:\\s()]*";

    public ETextField(String caption, String regExp, boolean required) {
        setCaption(caption);
        // addValidator(new RegexpValidator(regExp, "Not regular text") );
        setImmediate(true);
        setRequired(required);
        setBuffered(false);
    }

    public ETextField(String caption, boolean required, boolean regularText) {
        // addValidator(new RegexpValidator(text, "Not regular text"));
        setCaption(caption);
        setImmediate(true);
        setRequired(required);
    }

}
