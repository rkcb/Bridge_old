package com.bridge.calendar;

import com.bridge.ui.BridgeUI;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;

public class Fac extends JPAContainerFactory {

    public Fac() {
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static <S> JPAContainer<S> make(Class S) {
        return make(S, BridgeUI.unitName);
    }

}
