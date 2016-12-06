package com.bridge.view;

import com.bridge.database.C;
import com.bridge.database.Club;
import com.bridge.database.Player;
import com.bridge.database.User;
import com.bridge.ui.EGrid;
import com.bridge.ui.EHorizontalLayout;
import com.bridge.ui.EVerticalLayout;
import com.bridge.ui.MainMenu;
import com.bridge.ui.search.SearchComboBox;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Item;
import com.vaadin.data.util.GeneratedPropertyContainer;
import com.vaadin.data.util.PropertyValueGenerator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;

@SuppressWarnings("serial")
public class UserClubMembersView extends EVerticalLayout implements View {

    public static String name = "/user/clubmembers";

    private MainMenu mainMenu;
    private static C<Player> players;
    private Label search;
    private SearchComboBox<Player> searchPlayer;
    private SearchComboBox<Club> searchClub;
    private Button clear;
    private EHorizontalLayout hLayout;
    private EGrid<Player> membersGrid;
    private Filter clubFilter;
    private Filter playerFilter;

    public UserClubMembersView(MainMenu mainMenu) {
        this.mainMenu = mainMenu;
        players = new C<>(Player.class);
        search = new Label("Search");
        searchClub = new SearchComboBox<>(null, Club.class);
        searchClub.setInputPrompt("Write a Club Name");
        searchPlayer = new SearchComboBox<>(null, Player.class);
        searchPlayer.setInputPrompt("Write a Player Name");
        clear = new Button("Clear");
        clear.addClickListener(listener -> {
            searchClub.setValue(null);
            searchPlayer.setValue(null);
        });

        hLayout = new EHorizontalLayout(search, searchClub, searchPlayer,
                clear);
        hLayout.setComponentAlignment(search, Alignment.MIDDLE_CENTER);
        players.nest("user.firstName", "user.lastName", "club.id", "user.email",
                "user.emailPrivate", "user.telephone", "user.telephonePrivate");
        membersGrid = new EGrid<>(Player.class, players);

        membersGrid.setWidth("90%");
        membersGrid.setHeightByRows(20.0);
        addSearches();

        embedContactInformation(membersGrid);

        membersGrid.setColumns("user.firstName", "user.lastName",
                "federationCode", "club", "federationMember", "user.email",
                "user.telephone");

    }

    /***
     * embedContactInformation adds email and telephone columns to grids
     * container
     */
    @SuppressWarnings("rawtypes")
    protected void embedContactInformation(EGrid<Player> grid) {

        // generate email addresses and telephone number
        // as privacy settings allow
        GeneratedPropertyContainer genContainer = new GeneratedPropertyContainer(
                players.c());
        genContainer.addGeneratedProperty("user.email",
                new PropertyValueGenerator() {
                    @Override
                    public Object getValue(Item item, Object itemId,
                            Object propertyId) {
                        User user = players.get(itemId).getUser();
                        if (user.getEmailPrivate()) {
                            return "Hidden";
                        } else {
                            return user.getEmail();
                        }
                    }

                    @Override
                    public Class<String> getType() {
                        return String.class;
                    }
                });

        genContainer.addGeneratedProperty("user.telephone",
                new PropertyValueGenerator() {
                    @Override
                    public Object getValue(Item item, Object itemId,
                            Object propertyId) {
                        User user = players.get(itemId).getUser();
                        if (user.getTelephonePrivate()) {
                            return "Hidden";
                        } else {
                            return user.getTelephone();
                        }
                    }

                    @Override
                    public Class<String> getType() {
                        return String.class;
                    }
                });

        grid.setContainerDataSource(genContainer);
    }

    /***
     * addSearched creates and defines the search comboboxes
     */
    protected void addSearches() {
        searchClub.addValueChangeListener(listener -> {
            if (searchClub.getValue() != null) {
                if (clubFilter != null) {
                    players.removeFilter(clubFilter);
                }
                clubFilter = players.filterEq("club.id",
                        searchClub.getItem().getId());
            } else {
                if (clubFilter != null) {
                    players.removeFilter(clubFilter);
                }
            }
        });
        searchPlayer.addValueChangeListener(listener -> {
            if (searchPlayer.getValue() != null) {
                if (playerFilter != null) {
                    players.removeFilter(playerFilter);
                }
                playerFilter = players.filterEq("id", searchPlayer.getValue());
            } else {
                if (playerFilter != null) {
                    players.removeFilter(playerFilter);
                }
            }
        });

    }

    /***
     * refresh refreshes players container
     */
    public static void refresh() {
        players.refresh();
    }

    @Override
    public void enter(ViewChangeEvent event) {
        addComponents(mainMenu, hLayout, membersGrid);
    }

}
