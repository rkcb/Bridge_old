package com.bridge.calendar;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.bridge.database.Player;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

/***
 * PlayerList is a widget to choose a set of players
 */
@SuppressWarnings("serial")
public class PlayerList extends VerticalLayout {

    // adds the selected player to the list
    private ComboBox add = new ComboBox();

    private JPAContainer<Player> players = JPAContainerFactory
            .make(Player.class, "BridgeResults");
    private Set<Player> selectedPlayers = new HashSet<>();

    public PlayerList() {
        players.setReadOnly(true);
        addSelector();
        add.setImmediate(true);

    }

    public PlayerList(String label) {
        setCaption(label);
        addSelector();
    }

    /***
     * refreshPlayers refreshes the available player list from the DB
     */

    public void refreshPlayers() {
        players.refresh();
    }

    /***
     * clear removes all selected players
     */

    public void clear() {
        removeAllComponents();
        addComponent(add);
        selectedPlayers.clear();
    }

    public void setRequired(boolean status) {
        add.setRequired(true);
    }

    public void setSelectedPlayers(Set<Player> players) {

        clear();
        if (players != null) {
            for (Player p : players) {
                addPlayer(p);
            }
        }
    }

    public void setLabel(String label) {
        add.setCaption(label);
    }

    @SuppressWarnings("unused")
    private void o(String s) {
        System.out.println(s);
    }

    public Set<Player> getSelected() {
        return selectedPlayers;
    }

    public boolean isEmpty() {
        return selectedPlayers.isEmpty();
    }

    private void addSelector() {
        add.setFilteringMode(FilteringMode.CONTAINS);
        add.setContainerDataSource(players);
        add.setImmediate(true);
        add.setNullSelectionAllowed(false);

        for (Object id : players.getItemIds()) {
            Player pl = players.getItem(id).getEntity();
            String caption = pl.getFullName() + ", " + pl.getFederationCode();
            add.setItemCaption(id, caption);
        }

        add.addValueChangeListener(event -> {
            addPlayer();
            add.setValue(null);
        });

        super.addComponent(add);

    }

    private class Selected extends HorizontalLayout {

        private Player player;
        private Button remove;

        public Set<Player> addSelectedPlayer(Set<Player> list) {
            list.add(player);
            return list;
        }

        public Set<Player> removeSelectedPlayer(Set<Player> list) {
            list.remove(player);
            return list;
        }

        @SuppressWarnings("unused")
        public void setButtonVisible(boolean status) {
            remove.setVisible(status);
        }

        public Selected(final Object itemId) {

            setStyleName("selected");

            remove = new Button("x");
            remove.addStyleName(ValoTheme.BUTTON_TINY);
            Label name = new Label();
            name.setStyleName("selectedLabel");

            // player = players.getItem(add.getValue()).getEntity();
            player = players.getItem(itemId).getEntity();
            String code = player.getFederationCode();
            if (code == null) {
                code = "";
            }

            name.setValue(player.getFullName() + ", " + code);
            final Selected row = this;
            remove.addClickListener(event -> removePlayer(row, itemId));
            addComponents(name, remove);
            setComponentAlignment(name, Alignment.MIDDLE_CENTER);
        }
    }

    private boolean isSelectedPlayer(Player player) {

        Iterator<Player> i = selectedPlayers.iterator();
        boolean found = false;
        while (i.hasNext() && !found) {
            Player p = i.next();
            found = player.getId() == p.getId();
        }
        return found;
    }

    /***
     * addPlayer adds the player to this layout and
     */

    public void addPlayer(Player player) {
        if (!isSelectedPlayer(player)) {
            Selected selected = new Selected(player.getId());
            selected.addSelectedPlayer(selectedPlayers);
            addComponent(selected);
        }
    }

    /***
     * addPlayer adds player to the list and from the layout
     */

    private void addPlayer() {
        if (add.getValue() != null) {
            addPlayer(players.getItem(add.getValue()).getEntity());
        }
    }

    /***
     * removePlayer removes the player from the list and from the layout
     */

    private void removePlayer(Selected selected, Object itemId) {
        removeComponent(selected);
        selected.removeSelectedPlayer(selectedPlayers);
    }

    public int size() {
        return selectedPlayers.size();
    }
}
