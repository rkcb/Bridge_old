package com.bridge.ui;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.bridge.database.BridgeEvent;
import com.bridge.database.Participant;
import com.bridge.database.Player;
import com.bridge.database.Tournament;
import com.bridge.database.Tournament.Type;
import com.bridge.database.User;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.filter.Compare;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Table;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class ParticipantListCreator extends VerticalLayout {

    protected HorizontalLayout editorLayout = new HorizontalLayout();
    protected final int maxTeamSize = 6;

    protected Type type = null;
    protected JPAContainer<Player> ps = JPAContainerFactory.make(Player.class,
            BridgeUI.unitName);
    protected JPAContainer<Tournament> ts = JPAContainerFactory
            .make(Tournament.class, BridgeUI.unitName);
    protected Button ok = new Button("Ok");
    protected Button remove = new Button("Remove");
    protected Button newEntry = new Button("Add");
    protected Table table = null;
    protected BeanContainer<Long, Participant> pas = new BeanContainer<>(
            Participant.class);
    protected Tournament tour = null;
    protected BridgeEvent event = null;
    protected TextField teamName;
    protected ComboBox[] pls;
    protected ComboBox singlePl;
    protected ComboBox id = new ComboBox();
    protected TextField pw = new TextField("Password");
    protected BeanContainer<Long, IndividualRepresentation> indiContainer = new BeanContainer<>(
            IndividualRepresentation.class);
    protected BeanContainer<Long, PairRepresentation> pairContainer = new BeanContainer<>(
            PairRepresentation.class);
    protected Set<Long> idsInTable = new HashSet<>();
    protected Long clickedItemId;
    protected Object addItemId;

    public ParticipantListCreator() {
        setMargin(true);
        setSpacing(true);
        setSizeUndefined();
        ts.setAutoCommit(true);
        ps.setReadOnly(true);

    }

    private void loadTableData() {

        // if (event.getType() == Type.Individual){
        // indiContainer.removeAllItems();
        // indiContainer.setBeanIdProperty("id");
        // if (indiContainer.size() > 0){
        // for (Participant pt : tour.getRegisteredPlayers()){
        // for (Object pid : pt){
        // Player pl = ps.getItem(pid).getEntity();
        // idsInTable.add(pl.getId());
        // indiContainer.addBean(new IndividualRepresentation(pl,
        // (long) indiContainer.size()));
        // }
        ////
        // }}
        // table.setContainerDataSource(indiContainer);
        // } else if (event.getType() == Type.Pair) {
        // pairContainer.setBeanIdProperty("id");
        //
        // for (Participant pt : tour.getRegisteredPlayers()){
        //// Iterator<Object> i = pt.iterator();
        //// Player p1 = i.next();
        //// Player p2 = i.next();
        //// pairContainer.addBean(new PairRepresentation(p1, p2, pt.getId()));
        // }
        // table.setContainerDataSource(pairContainer);
        // } else if (event.getType() == Type.Team){
        // // create a tree of teamss
        // }
    }

    /***
     * createEditor creates fields to edit an existing entry or create a new one
     */
    protected void createEditor(Type type) {

        editorLayout.removeAllComponents();
        table = new Table();
        setIdList(); // candidate player list
        loadTableData(); // loads registered units

        if (type == Type.Individual) {
            FormLayout fl = new FormLayout();
            fl.addComponent(pw);
            editorLayout.addComponents(id, newEntry, remove, fl);

            table.setPageLength(19);
            table.setWidth("720px");
            table.addStyleName(ValoTheme.TABLE_COMPACT);
            addComponents(table, editorLayout);
            editorLayout.setSpacing(true);

            addButtonActions();
            setTableHeader();

        } else if (type == Type.Pair) {
            pls = new ComboBox[2];
            for (int i = 0; i < maxTeamSize; i++) {
                pls[i] = new ComboBox("Player " + (i + 1));
                editorLayout.addComponent(pls[i]);
            }
        } else if (type == Type.Team) {
            for (int i = 0; i < maxTeamSize; i++) {
                pls[i] = new ComboBox("Player " + (i + 1));
                editorLayout.addComponent(pls[i]);
            }
        }
    }

    private void setTableHeader() {
        table.setColumnHeader("id", "#");
        table.setColumnHeader("name", "Name");
        table.setColumnHeader("club", "Club");
        table.setColumnHeader("code", "Code");
        table.setVisibleColumns("id", "name", "code", "club");
    }

    private void addButtonActions() {
        if (type == Type.Individual) {
            newEntry.addClickListener(new Button.ClickListener() {
                @SuppressWarnings("unchecked")
                @Override
                public void buttonClick(ClickEvent event) {

                    Long pid = (Long) id.getValue();
                    if (pid != null && !idsInTable.contains(pid)) {
                        // add the player to the table and update the related
                        // information
                        Player p = ps.getItem(pid).getEntity();
                        IndividualRepresentation r = new IndividualRepresentation(
                                p, (long) indiContainer.size() + 1);

                        indiContainer.addBean(r);
                        EntityItem<Tournament> ti = ts.getItem(tour.getId());
                        Set<Participant> pts = (Set<Participant>) ti
                                .getItemProperty("registeredPlayers")
                                .getValue();
                        pts.add(new Participant(pid, pw.getValue()));
                        ti.getItemProperty("registeredPlayers").setValue(pts);
                        ti.commit();
                        idsInTable.add(pid);
                        id.setValue(null);
                    } else if (idsInTable.contains(pid)) {
                        Notification.show("The player is already in the list.",
                                Notification.Type.ERROR_MESSAGE);
                    }
                }
            });

            remove.addClickListener(event -> {
                if (clickedItemId != null) {
                    IndividualRepresentation ir = indiContainer
                            .getItem(clickedItemId).getBean();
                    indiContainer.removeItem(clickedItemId);
                    idsInTable.remove(ir.getPid());

                    id.setValue(null);
                    removeParticipant(clickedItemId);
                    clickedItemId = null;

                } else {
                    Notification.show("Click the table row to remove an entry.",
                            Notification.Type.ERROR_MESSAGE);
                }
            });
        }
    }

    private void setIdList() {
        id.setContainerDataSource(ps);
        id.setItemCaptionMode(ItemCaptionMode.ITEM);
    }

    /***
     * removeNonFederationMembers only federation members are allowed to play
     */
    protected void removeNonFederationMembers() {
        ps.removeAllContainerFilters();
        ps.addContainerFilter(new Compare.Equal("federationMember", true));
    }

    /***
     * initializeEditor sets the tournament type, participants and tour bean
     */

    public void initializeEditor(BridgeEvent e) {
        tour = e.getTournament();
        event = e;
        pas.removeAllItems();
        type = e.getType();
        pas.setBeanIdProperty("id");
        idsInTable.clear();
        createEditor(type);
        createTableListener();
    }

    /***
     * createTableListener listens row clicks
     */
    private void createTableListener() {
        table.setSelectable(true);
        table.addItemClickListener(
                event -> clickedItemId = (Long) event.getItemId());
    }

    @SuppressWarnings("unused")
    private void o(String string) {
        System.out.println(string);
    }

    protected String getPlayerName(Long id) {
        return ps.getItem(id).getEntity().toString();
    }

    /***
     * buildTable builds a list of registered units: individuals, pairs or teams
     */
    protected void buildTableForTeam(Set<Participant> s) {

    }

    protected void buildTableForPair(Set<Participant> s) {
    }

    protected void buildTableForIndy(Set<Participant> s) {
    }

    public void initializeForPair() {
    }

    public void removeSingletonParticipant(User user) {
    }

    protected void removeTableItem() {

        if (type == Type.Individual) {

        }
    }

    // /***
    // * addParticipant adds the participant to DB
    // */
    //
    // protected void addParticipant() {
    // EntityItem<Tournament> ti = ts.getItem(tour.getId());
    // }

    /***
     * removeParticipant removes the participant from the DB
     */

    @SuppressWarnings("unchecked")
    protected void removeParticipant(Long partId) {
        EntityItem<Tournament> ti = ts.getItem(tour.getId());

        Set<Participant> s = (Set<Participant>) ti
                .getItemProperty("registeredPlayers").getValue();
        Iterator<Participant> i = s.iterator();
        Participant pt = null;
        boolean found = false;
        if (s.size() > 0) {
            while (!found) {
                pt = i.next();
                found = pt.getId() == partId;
            }
            s.remove(pt);
            ti.getItemProperty("registeredPlayers").setValue(s);
            ti.commit();
        }
    }

}
