package com.bridge.ui;

import java.io.File;

import com.vaadin.server.FileResource;
import com.vaadin.server.VaadinService;

public class Icons {

    private static final String basepath = VaadinService.getCurrent()
            .getBaseDirectory().getAbsolutePath();
    private static final String vaadin_icons = "/WEB-INF/icons/v-png/";

    public static FileResource getVaadin(String caption, String name) {
        FileResource resource = new FileResource(
                new File(basepath + vaadin_icons + name));

        return resource;// new Image(caption, resource);
    }

}
