package com.bridge.view;

import com.bridge.database.C;
import com.bridge.database.MasterPointMessage;
import com.bridge.database.Player;
import com.bridge.ui.EFormLayout;
import com.bridge.ui.EHorizontalLayout;
import com.bridge.ui.EVerticalLayout;
import com.bridge.ui.MainMenu;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.grid.HeightMode;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Grid;
import com.vaadin.ui.MenuBar;

@SuppressWarnings("serial")
public class MPRegistryView extends EVerticalLayout implements View {

    public static final String name = "/user/mpregistry";

    protected MainMenu mainMenu;
    protected MenuBar submenu;
    protected Grid mpGrid;
    protected EFormLayout mpSearchFl;
    protected EFormLayout messageSearchFl;
    protected ComboBox mpSearch;
    protected ComboBox messageSearch;
    protected static C<Player> players = new C<>(Player.class);
    protected EHorizontalLayout mpHLayout;
    protected EHorizontalLayout messageHLayout;
    protected Button mpClear;
    protected Button messageClear;

    protected Grid messageGrid = null;
    protected C<MasterPointMessage> messages;

    /***
     * refreshPlayer refreshes whole players container
     */
    public static void refreshPlayers() {
        players.refresh();
    }

    private void createSubmenu() {
        submenu = new MenuBar();
        submenu.addItem("Log History", command -> showMPMessages());
        submenu.addItem("MP Registry", command -> {
            removeAllComponents();
            addComponents(mainMenu, submenu, mpHLayout, mpGrid);
            messages = null;
        });
    }

    private void createMpSearch() {
        mpSearch = new ComboBox("Search");
        mpSearch.setWidth("300px");
        mpSearch.setFilteringMode(FilteringMode.CONTAINS);
        mpSearch.setItemCaptionMode(ItemCaptionMode.ITEM);
        mpSearch.setContainerDataSource(players.c());
        mpSearch.setTextInputAllowed(true);
        mpSearch.addValueChangeListener(event -> {
            Object id = mpSearch.getValue();
            if (id != null) {
                players.removeFilters();
                players.filterEq("id", id);
            } else {
                players.removeFilters();
            }
        });

        mpSearch.setInputPrompt("Write a Name");
        mpSearchFl = new EFormLayout(mpSearch);
        mpSearchFl.setMargin(false);

    }

    private void createMpTable() {
        mpGrid = new Grid(players.c());
        mpGrid.setReadOnly(true);
        mpGrid.setColumns("user.firstName", "user.lastName", "club.name",
                "masterPoints");
        mpGrid.getColumn("club.name").setHeaderCaption("Club");
        mpGrid.setImmediate(true);
    }

    public MPRegistryView(MainMenu mainMenu) {
        this.mainMenu = mainMenu;
        addComponent(mainMenu);
        createSubmenu();
        createMpSearch();

        players.nest("user.firstName", "user.lastName", "club.name");

        createMpTable();

        mpClear = new Button("Clear");
        mpClear.addClickListener(event -> mpSearch.setValue(null));

        mpHLayout = new EHorizontalLayout(mpSearchFl, mpClear);
        mpHLayout.setSpacing(true);
        mpHLayout.setMargin(true);
    }

    private void createMessageContainer() {
        messages = new C<>(MasterPointMessage.class);
        messages.nest("receiver.id");
        messages.nest("receiver.user.firstName");
        messages.nest("receiver.user.lastName");
        messages.nest("receiver.federationCode");
        messages.nest("committer.federationCode");
    }

    private void createMessageSearch() {
        messageSearch = new ComboBox("Search", players.c());
        messageSearch.setInputPrompt("Write a Name");
        messageSearch.setItemCaptionMode(ItemCaptionMode.ITEM);
        messageSearch.setTextInputAllowed(true);
        messageSearch.setFilteringMode(FilteringMode.CONTAINS);
        messageSearch.addValueChangeListener(event -> {
            Object id = messageSearch.getValue();
            if (id != null) {
                messages.removeFilters();
                messages.filterEq("receiver.id", id);
            } else {
                messages.removeFilters();
            }
        });
        messageSearch.setWidth("300px");
    }

    private void showMPMessages() {
        removeAllComponents();
        players.removeFilters();

        createMessageContainer();
        createMessageSearch();

        messageClear = new Button("Clear");
        messageClear.addClickListener(listener -> messageSearch.setValue(null));

        messageSearchFl = new EFormLayout(messageSearch);
        messageHLayout = new EHorizontalLayout(messageSearchFl, messageClear);
        messageSearchFl.setMargin(false);

        messageGrid = new Grid("History", messages.c());
        messageGrid.setColumns("date", "receiver.user.firstName",
                "receiver.user.lastName", "receiver.federationCode",
                "masterPoints", "committer.federationCode");
        messageGrid.getColumn("committer.federationCode")
                .setHeaderCaption("Committer's Code");
        messageGrid.setHeightMode(HeightMode.ROW);
        messageGrid.setWidth("90%");
        addComponents(mainMenu, submenu, messageHLayout, messageGrid);
    }

    @Override
    public void enter(ViewChangeEvent event) {
        removeAllComponents();
        addComponents(mainMenu, submenu, mpHLayout, mpGrid);
    }

}
