package com.bridge.view;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.vaadin.dialogs.ConfirmDialog;

import com.bridge.database.C;
import com.bridge.database.Club;
import com.bridge.database.MembershipApplication;
import com.bridge.database.Player;
import com.bridge.database.ShiroRole;
import com.bridge.database.User;
import com.bridge.ui.BridgeUI;
import com.bridge.ui.EHorizontalLayout;
import com.bridge.ui.MainMenu;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.filter.SimpleStringFilter;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class ApplicationView extends VerticalLayout implements View {

    public static final String name = "/admin/applications";
    protected C<MembershipApplication> as = new C<>(
            MembershipApplication.class);
    protected Table table = new Table("");
    protected MainMenu menu;
    protected Button acceptSelected = new Button("Accept selected");

    protected Button removeSelected = new Button("Remove selected");
    protected Button selectAll = new Button("Select All");
    protected Button deselectAll = new Button("Unselect All");
    protected CheckBox foreigners;

    protected EHorizontalLayout buttons = new EHorizontalLayout(selectAll,
            deselectAll, removeSelected, acceptSelected);
    protected Object selectedId = null;
    protected Object selectedClubId = null;
    protected Filter clubFilter = null;
    protected ComboBox clubSelector = null;
    protected C<Club> cs = new C<>(Club.class);
    protected EHorizontalLayout selectors;

    public ApplicationView(MainMenu menu) {

        this.menu = menu;

        table.setPageLength(0);
        setSpacing(true);
        setMargin(true);

        as.nest("user.firstName");
        as.nest("user.lastName");
        as.nest("club.name");
        as.nest("player.federationCode");
        as.nest("player.foreignWBFClubMember");
        as.nest("user.email");

        table.setContainerDataSource(as.c());
        table.setColumnHeader("submitted", "Submitted");
        table.setColumnHeader("user.firstName", "First name");
        table.setColumnHeader("user.lastName", "Last name");
        table.setColumnHeader("club.name", "Club");
        table.setColumnHeader("player.federationCode", "Federation code");
        table.setColumnHeader("user.email", "Email");
        table.setColumnHeader("player.foreignWBFClubMember", "Foreigner");
        table.setPageLength(0);

        table.setVisibleColumns(new Object[] { "submitted", "user.firstName",
                "user.lastName", "club.name", "player.federationCode",
                "user.email", "player.foreignWBFClubMember" });
        table.setMultiSelect(true);
        table.addItemClickListener(event -> selectedId = event.getItemId());

        table.setSelectable(true);
        table.setMultiSelect(true);

        addButtonClicks();

    }

    /***
     * acceptClubSwitches accepts selected membership applications for club
     * switchers
     */

    protected void acceptClubSwitches(List<InternetAddress> emails) {
        Stream<Object> s = as.c().getItemIds().stream()
                .filter(id -> table.isSelected(id));
        List<Object> ids = s.filter(id -> as.get(id).getOldClubId() != null)
                .collect(Collectors.toList());

        for (Object id : ids) {
            MembershipApplication a = as.get(id);
            C<Player> ps = new C<>(Player.class);
            Long pid = a.getPlayerId();
            ps.set(pid, "club", null);
            Club c1 = cs.get(a.getOldClubId());
            Club c2 = cs.get(a.getNewClubId());
            cs.set(c1.getId(), "members", c1.removePlayer(pid));
            ps.set(pid, "club", c2);
            cs.set(c2.getId(), "members", c2.addPlayer(pid));
            try {
                emails.add(new InternetAddress(a.getUser().getEmail()));
            } catch (AddressException e) {
                e.printStackTrace();
            }
            as.rem(a.getId());
        }

    }

    /***
     * getSelected returns the selected items ids in the table
     */

    protected Stream<Object> getSelected() {
        return as.c().getItemIds().stream().filter(id -> table.isSelected(id));
    }

    /***
     * addSelected creates an account for this application and removes the
     * selection from the applications
     */

    protected void addSelected() {

        List<InternetAddress> emails = new ArrayList<>();

        // perform club switches
        acceptClubSwitches(emails);

        C<Player> ps = new C<>(Player.class);
        // C<Club> cs = new C<Club>(Club.class);
        C<User> us = new C<>(User.class);
        Stream<Object> selected = getSelected();

        // add the remaing new account applications

        for (Object id : selected.toArray()) {
            MembershipApplication a = as.get(id);
            try {
                emails.add(new InternetAddress(a.getUser().getEmail()));
            } catch (AddressException e) {
                e.printStackTrace();
            }
            String code = Player.getNewFedCode();
            a.getPlayer().setFederationCode(code);

            Object pid = ps.add(a.getPlayer());
            Object uid = us.add(a.getUser());
            ShiroRole.addRoles(uid, ShiroRole.r[1]);
            us.set(uid, "player", ps.get(pid));
            ps.set(pid, "user", us.get(uid));
            ps.set(pid, "club", a.getPlayer().getClub());

            as.rem(id);

        }
        // send applications information about
        BridgeUI.o(
                "Email notifications disabled in addSelected in ApplicationView");
        // InternetAddress[] emailsAr = new InternetAddress[emails.size()];
        // EmailNotification.send(emails.toArray(emailsAr), "Bridgetili luotu -
        // Your account is ready", "Message");
    }

    protected void addButtonClicks() {
        acceptSelected.addClickListener(listener -> {
            Stream<Object> selected = getSelected();
            if (selected.count() > 0) {
                ConfirmDialog.show(getUI(), "Please Confirm:",
                        "Are you really sure?", "I am", "No",
                        (org.vaadin.dialogs.ConfirmDialog.Listener) dialog -> {
                            if (dialog.isConfirmed()) {
                                addSelected();
                            }
                        });
            } else {
                Notification.show("No Selections", Type.ERROR_MESSAGE);
            }
        });

        removeSelected.addClickListener(listener -> {
            Stream<Object> selected = getSelected();
            if (selected.count() > 0) {
                ConfirmDialog.show(getUI(), "Please Confirm:",
                        "Are you really sure?", "I am", "No",
                        (org.vaadin.dialogs.ConfirmDialog.Listener) dialog -> {
                            if (dialog.isConfirmed()) {

                            } else {
                            }
                        });
            } else {
                Notification.show("No Selections", Type.ERROR_MESSAGE);
            }
        });

        selectAll.addClickListener(listener -> {
            for (Object id : as.c().getItemIds()) {
                table.select(id);
            }
        });

        deselectAll.addClickListener(listener -> {
            for (Object id : as.c().getItemIds()) {
                table.unselect(id);
            }
        });
    }

    /***
     * showOnlyClubAdminClubApplications filters all applicatitions but this
     * user's club application out
     */

    @SuppressWarnings("unused")
    private void addClubFilter() {
        as.removeFilters();
        String name = BridgeUI.user.getPlayerClubName();
        as.nest("player.club.name");
        SimpleStringFilter f = new SimpleStringFilter("player.club.name", name,
                false, false);
        as.filter(f);
    }

    /***
     * addClubSelector adds a widget to filter all but one club applications out
     */

    private void createSelectors() {

        clubSelector = new ComboBox("Club Selector", cs.c());
        clubSelector.setImmediate(true);
        clubSelector.setItemCaptionMode(ItemCaptionMode.ITEM);

        clubSelector.addValueChangeListener(listener -> {
            as.removeFilters();
            if (clubSelector.getValue() != null) {
                clubFilter = as.filterEq("club.id", clubSelector.getValue());
            }

        });

        foreigners = new CheckBox("Show only non-Finnish club applications");
        foreigners.addValueChangeListener(listener -> {
            if (foreigners.getValue()) {
                as.filterEq("player.foreignWBFClubMember", true);
            } else {
                as.removeFilters();
            }
        });

    }

    @Override
    public void enter(ViewChangeEvent event) {
        as.refresh();
        String role = BridgeUI.role;

        if (role.matches("admin") || role.matches("clubadmin")) {
            if (role.matches("admin")) {
                createSelectors();
                addComponents(menu, clubSelector, table, foreigners, buttons);
            } else {
                addComponents(menu, table, buttons);
            }

        } else {
        }
    }

}
