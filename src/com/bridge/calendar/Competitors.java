package com.bridge.calendar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.shiro.SecurityUtils;
import org.vaadin.maddon.layouts.MGridLayout;

import com.bridge.database.BridgeEvent;
import com.bridge.database.C;
import com.bridge.database.Indy;
import com.bridge.database.Pair;
import com.bridge.database.Player;
import com.bridge.database.Team;
import com.bridge.database.Tournament;
import com.bridge.database.Tournament.Type;
import com.bridge.database.User;
import com.bridge.ui.BridgeUI;
import com.bridge.ui.ETable;
import com.bridge.ui.EVerticalLayout;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

/***
 * Competitors is used to collect playing units (individual, pairs, teams) to
 * the tournament and represent the GUI. It is shown once a user clicks a
 * tournament as a signed in user
 */

@SuppressWarnings("serial")
public class Competitors extends EVerticalLayout {

    protected C<Indy> inds;
    protected C<Pair> pairs;
    protected C<Team> teams;
    protected C<Player> pls = new C<>(Player.class);
    protected Tournament tour;
    protected TextField teamName;
    protected User user;
    protected BridgeEvent event;
    protected ETable table;
    protected Window window = new Window();
    protected TextField pw = new TextField("Password");
    protected TextField pw2 = new TextField("Password"); // used only in team
                                                         // window
    protected Label unitCount = new Label(); // number of competing players,
                                             // pairs or teams
    private Button add = new Button("Add");
    private Button remove = new Button("Remove");
    protected HorizontalLayout widgets = new HorizontalLayout();
    private Object clickedItemId = null;
    private FormLayout pwFl = new FormLayout();
    private ComboBox[] id;
    private HorizontalLayout idsLayout;
    private int unitSize;
    private MGridLayout idLayout;
    private EVerticalLayout editorLayout;
    private Window teamWindow = null;
    private Button teamButton;

    private Set<Long> playingIds = new HashSet<>();

    public Competitors() {
        super();
        window.setResizeLazy(false);
        window.setResponsive(true);
        pw2.setDescription("Asked at team registration removal");
    }

    /***
     * createIdFields creates fields to collect player entity ids of indis,
     * pairs and teams
     */

    protected void createIdFields() {
        id = new ComboBox[unitSize];
        idsLayout = new HorizontalLayout();
        for (int i = 0; i < unitSize; i++) {
            id[i] = new ComboBox();
            id[i].setImmediate(true);
            id[i].setFilteringMode(FilteringMode.CONTAINS);
            id[i].setContainerDataSource(pls.c());
            id[i].setItemCaptionMode(ItemCaptionMode.ITEM);
            final ComboBox cb = id[i];
            add.setEnabled(true);
            idsLayout.setSpacing(true);
            idsLayout.addComponent(cb);
        }
    }

    /***
     * createTable creates a table which contains the playing units:
     * individuals, pairs or teams
     */

    protected <T> void createTable(C<T> c, Class<?> entityClass,
            boolean readOnly) {
        table = new ETable(null, c.c());

        table.addStyleName(ValoTheme.TABLE_COMPACT);
        table.setPageLength(0);
        c.filterEq("tournament.id", tour.getId());
        table.addItemClickListener(event -> clickedItemId = event.getItemId());

        table.setImmediate(true);
        table.with(buildProps()).headers(buildHeaders());
        table.setSelectable(!readOnly);
        table.setReadOnly(readOnly);
    }

    private void updateUnitCount() {
        if (event.getType() == Type.Individual) {
            unitCount.setCaption("Registered players: " + inds.size());
        } else if (event.getType() == Type.Pair) {
            unitCount.setCaption("Registered pairs: " + pairs.size());
        } else if (event.getType() == Type.Team) {
            unitCount.setCaption("Registered teams: " + teams.size());
        }
    }

    /***
     * buildProps adds the container properties which are shown in the table
     */

    protected Object[] buildProps() {
        int extra = event.getType() == Type.Team ? 1 : 0;
        String[] ar = new String[unitSize * 2 + extra];

        if (extra == 1) {
            ar[0] = "name"; // add team name property
        }

        for (int i = extra; i < unitSize + extra; i++) {
            String p = "p" + (i - extra);
            ar[i * 2 - extra] = new String(p);
            ar[i * 2 + 1 - extra] = new String(p + ".club.name");
        }

        return ar;
    }

    protected String[] buildHeaders() {

        int extra = event.getType() == Type.Team ? 1 : 0;
        String[] ar = new String[unitSize * 2 + extra];
        if (extra == 1) {
            ar[0] = "Team Name";
        }

        for (int i = extra; i < unitSize + extra; i++) {
            ar[i * 2 - extra] = new String("Name");
            ar[i * 2 + 1 - extra] = new String("Club");
        }

        return ar;
    }

    /***
     * initialize loads the widgets and the playing units to a table -- if user
     * has not signed in then only participants are shown and all buttons and
     * fields are disabled
     */

    public void initialize(BridgeEvent e, User user, boolean readOnly) {

        tour = e.getTournament();
        event = e;
        this.user = user;

        if (e.getType() == Type.Individual) {
            unitSize = 1;
            setupIndy(readOnly);
            updateUnitCount();
        } else if (e.getType() == Type.Pair) {
            unitSize = 2;
            setupPair(readOnly);
            updateUnitCount();
        } else if (e.getType() == Type.Team) {
            unitSize = 6;
            setupTeam(readOnly);
            updateUnitCount();
        }

        playingIds.addAll(getPlayerIdsInContainer());
    }

    private void createActions() {

        /***
         * add new item if a player is not already in the table
         */
        add.addClickListener(listener -> {

            updateUnitCount();
            boolean idsOk = idsValid();
            boolean auth = isAddingAuthorized();

            if (idsOk && auth) {
                createEntityFromIds();
                updateUnitCount();
                resetFields();
                if (teamWindow != null) {
                    teamWindow.close();
                }
            } else if (!auth) {
                Notification.show("You are not authorized to add players",
                        Notification.Type.ERROR_MESSAGE);
            } else if (!idsOk) {
                Notification.show("Choose unique and enough many players",
                        Notification.Type.ERROR_MESSAGE);
            }

        });

        /***
         * remove the pair if the user is authorized
         */
        remove.addClickListener(listener -> {
            BridgeUI.o("remove before action");
            if (clickedItemId != null) {
                if (authorized(pw.getValue(), getEntityPassword())) {
                    removeSelectedEntity();
                } else {
                    Notification.show(
                            "You are not authorized to remove the player",
                            Notification.Type.ERROR_MESSAGE);
                }
                resetFields();
                clickedItemId = null;
                updateUnitCount();
            } else {
                Notification.show("Click to select a row to remove",
                        Notification.Type.ERROR_MESSAGE);
            }
        });
    }

    private void createEntityFromIds() {
        if (idsValid()) {
            Player[] ps = buildPlayersArray();
            if (event.getType() == Type.Individual) {
                inds.add(new Indy(pw.getValue(), tour, ps));
            } else if (event.getType() == Type.Pair) {
                pairs.add(new Pair(pw.getValue(), tour, ps));
            } else if (event.getType() == Type.Team) {
                // only in team creation pw2 is used in the window
                teams.add(new Team(teamName.getValue(), pw2.getValue(), tour,
                        ps));
            }
        }
    }

    private void removeSelectedEntity() {
        Set<Long> s = new HashSet<>();
        if (event.getType() == Type.Individual) {
            s = inds.get(clickedItemId).getPlayingIds();
            inds.rem(clickedItemId);
        } else if (event.getType() == Type.Pair) {
            s = pairs.get(clickedItemId).getPlayingIds();
            pairs.rem(clickedItemId);
        } else if (event.getType() == Type.Team) {
            s = teams.get(clickedItemId).getPlayingIds();
            teams.rem(clickedItemId);
        }
        playingIds.removeAll(s);
    }

    private Player[] buildPlayersArray() {

        Player[] ps = new Player[unitSize];
        for (int i = 0; i < unitSize; i++) {
            Object pid = id[i].getValue();
            ps[i] = pid == null ? null : pls.get(pid);
        }
        return ps;
    }

    /***
     * getSelectedPlayerIds gives the nonnull ids of the players in the
     * container
     */

    private Set<Long> getPlayerIdsInContainer() {
        Set<Long> s = new HashSet<>();
        if (event.getType() == Type.Individual) {
            for (int i = 0; i < inds.size(); i++) {
                s.addAll(inds.at(i).getPlayingIds());
            }
        } else if (event.getType() == Type.Pair) {
            for (int i = 0; i < pairs.size(); i++) {
                s.addAll(pairs.at(i).getPlayingIds());
            }
        } else if (event.getType() == Type.Team) {
            for (int i = 0; i < teams.size(); i++) {
                s.addAll(teams.at(i).getPlayingIds());
            }
        }
        return s;
    }

    private String getEntityPassword() {
        if (event.getType() == Type.Individual) {
            return inds.get(clickedItemId).getPassword();
        } else if (event.getType() == Type.Pair) {
            return pairs.get(clickedItemId).getPassword();
        } else if (event.getType() == Type.Team) {
            return teams.get(clickedItemId).getPassword();
        } else {
            return null;
        }
    }

    private void resetFields() {
        for (int i = 0; i < unitSize; i++) {
            id[i].setValue(null);
        }
        pw.setValue("");
        pw2.setValue("");
        if (teamName != null) {
            teamName.setValue("");
        }
    }

    private void setupIndy(boolean readOnly) {
        inds = new C<>(Indy.class);
        setupProperties(inds);
        if (readOnly) {
            createTable(inds, Indy.class, readOnly);
            addComponents(table, unitCount);
        } else {
            createWidgets(inds, Indy.class);
            createActions();
        }
    }

    private void setupPair(boolean readOnly) {
        pairs = new C<>(Pair.class);
        setupProperties(pairs);
        if (readOnly) {
            createTable(pairs, Pair.class, readOnly);
            addComponents(table, unitCount);
        } else {
            createWidgets(pairs, Pair.class);
            createActions();
        }
    }

    private void setupTeam(boolean readOnly) {
        teams = new C<>(Team.class);
        setupProperties(teams);
        if (readOnly) {
            createTable(teams, Team.class, readOnly);
            addComponents(table, unitCount);
        } else {
            createWidgets(teams, Team.class);
            createActions();
        }
    }

    private <T> void createWidgets(C<T> c, Class<?> T) {
        updateUnitCount();
        createCommonWidgets();
        // player selector
        createIdFields();
        createTable(c, T, false);
        // participant list

        // add components

        if (event.getType() != Type.Team) {
            pwFl.addComponent(pw);
            pwFl.setMargin(false);

            pw.setCaption("Password");
            widgets.addComponents(add, remove, idsLayout, pwFl);

            widgets.setSpacing(true);
            addComponents(table, unitCount, widgets);
        } else if (event.getType() == Type.Team) {
            createTeamEditor();
            teamButton = new Button("Add Team");
            teamButton.addClickListener(listener -> {
                getUI().addWindow(teamWindow);
                // teamWindow.setModal(true);
            });

            HorizontalLayout buttonsAndPw = new HorizontalLayout();
            pwFl.addComponent(pw);
            pwFl.setMargin(false);

            buttonsAndPw.addComponents(teamButton, remove, pwFl);
            buttonsAndPw.setSpacing(true);
            addComponents(table, unitCount, buttonsAndPw);
        }
    }

    private void createTeamEditor() {
        teamWindow = new Window("Add Team");
        teamWindow.center(); // note http://dev.vaadin.com/ticket/8971
        teamWindow.setResponsive(true);
        teamWindow.setResizeLazy(false);
        teamName = new TextField("Team Name");
        FormLayout teamNameLayout = new FormLayout(teamName);
        teamNameLayout.setSizeUndefined();

        editorLayout = new EVerticalLayout();

        // add id fields
        idLayout = new MGridLayout();
        idLayout.setColumns(2);
        for (int i = 0; i < unitSize; i++) {
            idLayout.addComponent(id[i]);
        }
        FormLayout pw2Fl = new FormLayout();
        pw2 = new TextField("Password");
        pw2.setDescription("Asked at registration removal");
        // add add, remove and pw
        widgets = new HorizontalLayout();
        widgets.setSpacing(true);
        pw2Fl = new FormLayout();
        pw2Fl.setMargin(false);
        pw2Fl.addComponent(pw2);
        widgets.addComponents(add, pw2Fl);

        editorLayout.addComponents(teamNameLayout, idLayout, widgets);
        editorLayout.setComponentAlignment(teamNameLayout,
                Alignment.MIDDLE_CENTER);

        teamWindow.setContent(editorLayout);
        teamWindow.setModal(true);
    }

    private void createCommonWidgets() {
        // editor widgets
        add = new Button("Add");
        remove = new Button("Remove");
        pwFl = new FormLayout();
        pw = new TextField("Password");
    }

    /***
     * idsValid checks that ids are unique and enough many
     */

    private boolean idsValid() {
        List<Long> l = new ArrayList<>();
        // verify that id is not already in use
        boolean error1 = false;
        for (int i = 0; i < unitSize; i++) {
            if (id[i].getValue() != null) {
                l.add((Long) id[i].getValue());
            }
        }
        Iterator<Long> i = l.iterator();
        while (!error1 && i.hasNext()) {
            error1 = playingIds.contains(i.next());
        }

        Set<Long> s = new HashSet<>(l);
        // verify distinct nonnull ids
        boolean error2 = s.size() != l.size();

        // verify sufficient size
        if (event.getType() == Type.Individual) {
            return !error1 && !error2 && s.size() == 1;
        } else if (event.getType() == Type.Pair) {
            return !error1 && !error2 && s.size() == 2;
        } else if (event.getType() == Type.Team) {
            return !error1 && !error2 && s.size() >= 4;
        } else {
            return false;
        }

    }

    private <T> void setupProperties(C<T> c) {

        for (int i = 0; i < unitSize; i++) {
            String p = "p" + i;
            c.nest(p + ".user.firstName", p + ".user.lastName",
                    p + ".federationCode", p + ".club.name");
        }
    }

    /***
     * authorized checks that the given password is correct or that the user is
     * othetwise authorized to edit the participation entry
     */

    private boolean authorized(String pword, String encryptedPw) {

        // Prerequisite: user must be signed in
        // case 1: if the event is masterpoint tour then the user must be
        // fed member or WBF member and give the correct password or be an admin
        // case 2: if the event is not an MP tour then user must
        // submit the correct password or be an admin

        Player p = user != null ? user.getPlayer() : null;
        boolean pwOk = BridgeUI.pwService.passwordsMatch(pword, encryptedPw);
        boolean admin = isAdmin() || isTourClubAdmin();
        boolean member = fedMemberOrEquivalent(p);
        boolean fedReq = event.getMasterPoint();

        BridgeUI.o("pwOk: " + pwOk);

        if (fedReq) {
            return member && (admin || pwOk);
        } else {
            return admin || pwOk;
        }
    }

    private boolean isAddingAuthorized() {
        Player p = user != null ? user.getPlayer() : null;
        boolean member = fedMemberOrEquivalent(p);
        boolean fedReq = event.getMasterPoint();
        boolean signedIn = SecurityUtils.getSubject().isAuthenticated();

        BridgeUI.o(">>>>> member: " + member);
        BridgeUI.o("fedReq: " + fedReq);

        if (fedReq) {
            return member && signedIn;
        } else {
            return signedIn;
        }
    }

    protected boolean passwordMatch(String pw, String encryptedPw) {
        return BridgeUI.pwService.passwordsMatch(pw, encryptedPw);
    }

    protected boolean fedMemberOrEquivalent(Player p) {
        if (p == null) {
            return false;
        } else {
            return p.getFederationMember() || p.getForeignWBFClubMember();
        }
    }

    /***
     * isAdmin checks whether the signed in user has the role clubadmin and
     * belongs to the club which owns the tour
     */

    protected boolean isAdmin() {
        return BridgeUI.user.hasRole("admin");
        // if (user != null && tour.getOwner() != null){
        // boolean adminInTourClub = BridgeUI.user.getPlayerClubName().equals(
        // tour.getOwner().getName());
        // return (BridgeUI.user.hasRole("clubadmin") && adminInTourClub) ||
        // BridgeUI.user.hasRole("admin");
        // } else return false;
    }

    protected boolean isTourClubAdmin() {
        boolean adminInTourClub = BridgeUI.user.getPlayerClubName()
                .equals(tour.getOwner().getName());
        return BridgeUI.user.hasRole("clubadmin") && adminInTourClub;
    }

    public void setReadOnlySet(boolean state) {

    }
}
