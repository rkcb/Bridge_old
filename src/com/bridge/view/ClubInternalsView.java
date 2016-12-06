package com.bridge.view;

import com.bridge.ui.BridgeUI;
import com.bridge.ui.EVerticalLayout;
import com.bridge.ui.MainMenu;
import com.bridge.ui.club.ClubInternalsAdminMenu;
import com.bridge.ui.club.NewsList;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;

@SuppressWarnings("serial")
public class ClubInternalsView extends EVerticalLayout implements View {

    public static final String name = "/admin/clubsinternals";

    private MainMenu mainmenu;
    private ClubInternalsAdminMenu clubAdminMenu;
    // private HtmlEditor editor;
    private NewsList news = new NewsList(false);

    public ClubInternalsView(MainMenu menu) {
        mainmenu = menu;
        clubAdminMenu = new ClubInternalsAdminMenu(this);
    }

    /***
     * createNewsItem creates a news item to news list and also creates all DB
     * structures
     */

    public void createNews() {
        Object clubId = BridgeUI.user.getCurrentClubId();
        if (clubId != null) {
            news.create(clubId);
        }
    }

    @Override
    public void enter(ViewChangeEvent event) {
        addComponents(mainmenu);
        news.removeAllComponents();

        if (BridgeUI.user.hasRole("clubadmin")) {
            addComponent(clubAdminMenu);
        }

        Object cid = BridgeUI.user.getCurrentClubId();
        if (cid != null) {
            news.filterAndAddNews(cid);
            addComponent(news);
        }

    }

}
