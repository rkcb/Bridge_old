package com.bridge.view;

import com.bridge.database.C;
import com.bridge.database.Club;
import com.bridge.ui.BridgeUI;
import com.bridge.ui.EVerticalLayout;
import com.bridge.ui.MainMenu;
import com.bridge.ui.club.NewsList;
import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class UserNewsView extends EVerticalLayout implements View {

    public static final String name = "/user/news";
    public C<Club> cs = new C<>(Club.class);

    private MainMenu mainmenu;
    private MenuBar submenu;
    private NewsList news = new NewsList(true);
    // current user's club id
    private Object clubId = BridgeUI.user.getCurrentClubId();
    // private MenuItem wordSearch;
    // private MenuItem generalSearch;

    public UserNewsView(MainMenu menu) {
        mainmenu = menu;
        addSubmenu();
    }

    protected void addSubmenu() {
        // TODO: fed news button (filter)
        // TODO: own club news button (filter)
        // TODO: news search by club id
        // TODO: search by content
        // TODO: set topic relative to a specific club only
        // TODO: show all news

        submenu = new MenuBar();

        // add submenu items
        addMyClub();
        addFedNews();
        addClubSelector();
    }

    protected void addMyClub() {
        if (clubId != null) {
            submenu.addItem("My Club",
                    selectedItem -> news.filterAndAddNews(clubId));
        }
    }

    /***
     * addFedNews filters fed news -- currently this the signed in club as fed
     * club does not exist!! TODO: fed club
     */
    protected void addFedNews() {
        if (clubId != null) {
            submenu.addItem("Fed News",
                    selectedItem -> news.filterAndAddNews(clubId));
        }
    }

    protected void addClubSelector() {
        submenu.addItem("Select Club",
                selectedItem -> showClubSelectorWindow());
    }

    /***
     * showClubSelectorWindow filters news by the selected club id -- empty
     * value means all clubs are selected
     */
    protected void showClubSelectorWindow() {

        Window window = new Window("Select Club for News");
        window.setWidth("400px");
        window.setHeight("200px");
        getUI().addWindow(window);
        window.center();
        window.setModal(true);
        C<Club> cs = new C<>(Club.class);
        ComboBox clubs = new ComboBox("Club", cs.c());
        clubs.setItemCaptionMode(ItemCaptionMode.ITEM);
        clubs.setNullSelectionAllowed(true);

        clubs.setFilteringMode(FilteringMode.CONTAINS);
        Button done = new Button("Done");
        done.setClickShortcut(KeyCode.ENTER);
        // filter
        done.addClickListener(listener -> {
            window.close();
            Object id = clubs.getValue();
            news.filterAndAddNews(id); // null removes filters
        });

        EVerticalLayout l = new EVerticalLayout(clubs, done);
        clubs.focus();
        window.setContent(l);

    }

    @Override
    public void enter(ViewChangeEvent event) {
        addComponents(mainmenu, submenu, news);
        cs.refresh(); // to keep sync with newly added news
        news.filterAndAddNews(clubId);
    }

}
