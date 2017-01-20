package com.bridge.view;

import java.util.Calendar;

import org.vaadin.dialogs.ConfirmDialog;

import com.bridge.database.C;
import com.bridge.database.Club;
import com.bridge.database.MembershipApplication;
import com.bridge.database.Player;
import com.bridge.database.ShiroRole;
import com.bridge.database.User;
import com.bridge.ui.BridgeUI;
import com.bridge.ui.EHorizontalLayout;
import com.bridge.ui.EVerticalLayout;
import com.bridge.ui.MainMenu;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class PersonalDataView extends EVerticalLayout implements View {

    public static final String name = "/user/personaldata";

    protected Label group1 = new Label("<b>Personal Information</b>");
    protected TextField firstName = new TextField("First name");
    protected TextField secondName = new TextField("Second name");
    protected TextField lastName = new TextField("Last name");
    protected TextField yearOfBirth = new TextField("Year of birth");
    protected TextField nationality = new TextField("Nationality");
    protected ComboBox language = new ComboBox("UI language"); // for user
                                                               // interface
    protected CheckBox alive = new CheckBox("Alive");

    protected Label group2 = new Label("<b>Contact Information");
    protected TextField username = new TextField("Username");
    protected TextField address = new TextField("Address");
    protected TextField town = new TextField("Town");
    protected TextField postalCode = new TextField("Postal code");
    protected TextField country = new TextField("Country");
    protected TextField email = new TextField("Email");
    protected TextField telephone = new TextField("Telephone");
    protected CheckBox emailPrivate = new CheckBox("Email hidden");
    protected CheckBox telephonePrivate = new CheckBox("Telephone nr. hidden");

    protected Label group3 = new Label("<b>Player Information</b>");
    protected CheckBox federationMember = new CheckBox("Federation member");
    protected CheckBox foreignWBFClubMember = new CheckBox(
            "Foreign club member");
    protected TextField federationCode = new TextField("Federation code");
    protected TextArea directorQualifications = new TextArea(
            "Director qualifications");
    protected TextField masterpoints = new TextField("Masterpoints");
    protected ComboBox club = new ComboBox("Club");

    protected Label group4 = new Label("<b>User Roles</b>");
    protected C<ShiroRole> shiroRoles = new C<>(ShiroRole.class);
    protected OptionGroup optionGroup = new OptionGroup("", shiroRoles.c());
    protected EntityItem<User> userItem;

    protected EVerticalLayout l1 = new EVerticalLayout(group1, firstName,
            secondName, lastName, yearOfBirth, nationality, language, alive);
    protected EVerticalLayout l2 = new EVerticalLayout(group2, username, email,
            emailPrivate, telephone, telephonePrivate, address, town,
            postalCode, country);
    protected EVerticalLayout l3 = new EVerticalLayout(group3, club,
            federationMember, foreignWBFClubMember, federationCode,
            directorQualifications, masterpoints);

    protected EVerticalLayout l4 = new EVerticalLayout(group4, optionGroup);
    protected EHorizontalLayout hl = new EHorizontalLayout(l1, l2, l3, l4);

    protected MainMenu menu;
    protected C<User> users = new C<>(User.class);
    protected C<Player> players = new C<>(Player.class);
    protected FieldGroup fgUser = null;
    protected FieldGroup fgPlayer = null;

    protected MenuBar submenu = new MenuBar();
    protected Object UserId = null;
    protected Object PlayerId = null;

    protected ComboBox search;
    protected boolean readOnlyStatus = true;

    public PersonalDataView(MainMenu mainMenu) {
        menu = mainMenu;

        group1.setContentMode(ContentMode.HTML);
        group2.setContentMode(ContentMode.HTML);
        group3.setContentMode(ContentMode.HTML);
        group4.setContentMode(ContentMode.HTML);
        optionGroup.setMultiSelect(false);
        optionGroup.setItemCaptionMode(ItemCaptionMode.ITEM);

        addMenuItems();
        optionGroup.setCaption(null);

        User u = BridgeUI.user.getCurrentUser();
        Player p = u.getPlayer();
        fgUser = new FieldGroup(users.item(u.getId()));
        fgUser.bindMemberFields(this);
        if (p != null) {
            fgPlayer = new FieldGroup(players.item(p.getId()));
            fgPlayer.buildAndBindMemberFields(this);
        }

        setFieldsReadOnly();
    }

    protected void addMenuItems() {
        submenu.addItem("Edit", command -> setFieldsReadOnlyOff());
        submenu.addItem("Cancel", command -> cancelEditing());
        submenu.addItem("Save", command -> editingDone());
        submenu.addItem("Change Club", command -> changeClub());
    }

    /***
     * changeClub shows an application window to switch club
     */

    protected void changeClub() {
        Window w = new Window("Send Club Membership Application");
        C<Club> cs = new C<>(Club.class);
        cs.filterNeq("id", BridgeUI.user.getCurrentClubId());
        ComboBox newClub = new ComboBox("New Club", cs.c());
        newClub.setItemCaptionMode(ItemCaptionMode.ITEM);
        newClub.setNullSelectionAllowed(false);
        Button apply = new Button("Apply");
        Button cancel = new Button("Cancel");
        Long oldId = (Long) BridgeUI.user.getCurrentClubId();
        // Long newId = (Long) newClub.getValue();
        apply.addClickListener(listener -> applyNewClub(oldId, newClub, w));
        cancel.addClickListener(listener -> w.close());
        HorizontalLayout hl = new HorizontalLayout(cancel, apply);
        hl.setSpacing(true);
        EVerticalLayout l = new EVerticalLayout(newClub, hl);
        l.setComponentAlignment(hl, Alignment.MIDDLE_RIGHT);
        w.setContent(l);
        w.center();
        w.setModal(true);
        w.setWidth("300px");
        getUI().addWindow(w);
    }

    protected void applyNewClub(Long oldId, ComboBox newClub, Window w) {
        ConfirmDialog.show(getUI(), "Please Confirm:", "Change my club", "Yes",
                "No", (org.vaadin.dialogs.ConfirmDialog.Listener) dialog -> {
                    Long newId = (Long) newClub.getValue();
                    if (dialog.isConfirmed() && oldId != null
                            && newId != null) {
                        // C<Club> cs = new C<>(Club.class);
                        // C<Player> ps = new C<>(Player.class);
                        C<MembershipApplication> as = new C<>(
                                MembershipApplication.class);

                        Long pid = BridgeUI.user.getCurrentUser().getPlayer()
                                .getId();
                        Calendar cal = Calendar.getInstance();
                        MembershipApplication a = new MembershipApplication(pid,
                                oldId, newId, cal.getTime());
                        as.add(a);
                    }
                });
        w.close();
    }

    protected void setFieldsReadOnly() {
        readOnlyStatus = true;
        boolean b = readOnlyStatus;
        firstName.setReadOnly(b);
        secondName.setReadOnly(b);
        lastName.setReadOnly(b);
        yearOfBirth.setReadOnly(b);
        nationality.setReadOnly(b);

        language.setReadOnly(b);
        alive.setReadOnly(b);

        username.setReadOnly(b);
        address.setReadOnly(b);
        town.setReadOnly(b);
        postalCode.setReadOnly(b);
        country.setReadOnly(b);
        email.setReadOnly(b);
        telephone.setReadOnly(b);

        federationCode.setReadOnly(b);
        federationMember.setReadOnly(b);
        foreignWBFClubMember.setReadOnly(b);
        directorQualifications.setReadOnly(b);
        masterpoints.setReadOnly(b);
        club.setReadOnly(b);

        emailPrivate.setReadOnly(b);
        telephonePrivate.setReadOnly(b);

        optionGroup.setReadOnly(b);
    }

    protected void setFieldsReadOnlyOff() {
        readOnlyStatus = false;
        boolean b = readOnlyStatus;

        firstName.setReadOnly(b);
        secondName.setReadOnly(b);
        lastName.setReadOnly(b);
        nationality.setReadOnly(b);
        language.setReadOnly(b);

        address.setReadOnly(b);
        town.setReadOnly(b);
        postalCode.setReadOnly(b);
        country.setReadOnly(b);
        email.setReadOnly(b);
        telephone.setReadOnly(b);

        foreignWBFClubMember.setReadOnly(b);
        directorQualifications.setReadOnly(b);

        emailPrivate.setReadOnly(b);
        telephonePrivate.setReadOnly(b);

    }

    protected void cancelEditing() {
        if (!readOnlyStatus) {
            if (fgUser != null) {
                fgUser.discard();
            }
            if (fgPlayer != null) {
                fgPlayer.discard();
            }
            setFieldsReadOnly();
        }
    }

    protected void editingDone() {
        if (!readOnlyStatus) {
            ConfirmDialog.show(getUI(), "Please Confirm:",
                    "Are you really sure?", "I am", "No, cancel changes",
                    (org.vaadin.dialogs.ConfirmDialog.Listener) dialog -> {
                        if (dialog.isConfirmed()) {

                            try {
                                fgUser.commit();
                                fgPlayer.commit();
                                setFieldsReadOnly();
                                // update search data
                                UserClubMembersView.refresh();
                            } catch (CommitException e) {
                            }

                        } else {
                        }
                    });
        }
    }

    @Override
    public void enter(ViewChangeEvent event) {
        addComponents(menu, submenu, hl);
    }

}
