package com.bridge.view;

import org.vaadin.dialogs.ConfirmDialog;

import com.bridge.database.C;
import com.bridge.database.Club;
import com.bridge.database.Player;
import com.bridge.database.ShiroRole;
import com.bridge.database.User;
import com.bridge.ui.EHorizontalLayout;
import com.bridge.ui.EVerticalLayout;
import com.bridge.ui.MainMenu;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.RegexpValidator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

@SuppressWarnings("serial")
public class AdminPlayerDataEditor extends EVerticalLayout implements View {

    public static final String name = "/admin/userdata";

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
    protected ComboBox club = new ComboBox("Club");
    protected CheckBox federationMember = new CheckBox("Federation member");
    protected CheckBox foreignWBFClubMember = new CheckBox(
            "Foreign club member");
    protected TextField federationCode = new TextField("Federation code");
    protected TextArea directorQualifications = new TextArea(
            "Director qualifications");
    protected TextField masterpoints = new TextField("Masterpoints");

    protected Label group4 = new Label("<b>User Roles</b>");
    protected C<ShiroRole> shiroRoles = new C<>(ShiroRole.class);
    protected OptionGroup optionGroup = new OptionGroup("", shiroRoles.c());
    protected EntityItem<User> userItem;

    protected EVerticalLayout l1 = new EVerticalLayout(group1, firstName,
            secondName, lastName, yearOfBirth, nationality, language, alive);
    protected EVerticalLayout l2 = new EVerticalLayout(group2, username, email,
            telephone, address, town, postalCode, country);
    protected EVerticalLayout l3 = new EVerticalLayout(group3, club,
            federationMember, foreignWBFClubMember, federationCode,
            directorQualifications, masterpoints);

    protected EVerticalLayout l4 = new EVerticalLayout(group4, optionGroup);
    protected EHorizontalLayout hl = new EHorizontalLayout(l1, l2, l3, l4);

    protected MainMenu menu;
    protected C<User> users = new C<>(User.class);
    protected C<Player> players = new C<>(Player.class);
    protected C<Club> clubs = new C<>(Club.class);
    protected FieldGroup fgu = null; // User
    protected FieldGroup fgp = null; // Player

    protected MenuBar submenu = new MenuBar();
    protected Object UserId = null;
    protected Object PlayerId = null;

    protected ComboBox search;
    protected boolean readOnlyStatus = true;

    public AdminPlayerDataEditor(MainMenu mainMenu) {
        menu = mainMenu;
        addUsernameValidator();
        addSearch();
        setFieldsReadOnly();
        group1.setContentMode(ContentMode.HTML);
        group2.setContentMode(ContentMode.HTML);
        group3.setContentMode(ContentMode.HTML);
        group4.setContentMode(ContentMode.HTML);
        optionGroup.setMultiSelect(false);
        optionGroup.setItemCaptionMode(ItemCaptionMode.ITEM);

        club.setConverter(Club.class);

        addMenuItems();
        email.addValidator(new EmailValidator("Not a valid email address"));
    }

    protected void addMenuItems() {
        submenu.addItem("Edit", command -> setFieldsReadOnlyOff());
        submenu.addItem("Cancel", command -> cancelEditing());
        submenu.addItem("Delete", command -> deleteAccount());
        submenu.addItem("Done", command -> editingDone());
    }

    /***
     * deleteAccount deletes the account IMPORTANT NOTE: since the relation
     * Player <-> Club is "ALL" cascaded a player must be removed by setting
     * his/her club property to null first; if this null value is not set the
     * club will be deleted
     */

    protected void deleteAccount() {
        if (search.getValue() != null) {
            ConfirmDialog.show(getUI(), "Please Confirm:",
                    "Delete this Account", "Yes", "No, cancel changes",
                    (org.vaadin.dialogs.ConfirmDialog.Listener) dialog -> {
                        if (dialog.isConfirmed()) {
                            Object uid = search.getValue();
                            Player p = users.get(uid).getPlayer();
                            C<Player> ps = new C<>(Player.class);
                            ps.set(p.getId(), "club", null);
                            users.rem(uid);
                        } else {
                        }
                    });
        } else {
            Notification.show("No user selected", Type.ERROR_MESSAGE);
        }
    }

    protected void addSearch() {
        search = new ComboBox("Search", users.c());
        search.setItemCaptionMode(ItemCaptionMode.ITEM);
        search.setFilteringMode(FilteringMode.CONTAINS);
        search.setNullSelectionAllowed(false);
        search.addValueChangeListener(listener -> {
            Object uid = search.getValue();
            User u = users.get(search.getValue());
            fgu = new FieldGroup(users.item(uid));
            if (u.getPlayer() != null) {
                fgp = new FieldGroup(players.item(u.getPlayer().getId()));
                fgp.bindMemberFields(this);
                club.setReadOnly(true);
            }
            fgu.bindMemberFields(this);

            optionGroup.setReadOnly(false);
            optionGroup.setValue(u.getTopRole().getId());
            setFieldsReadOnly();
        });
        search.setWidth("300px");
    }

    protected void cancelEditing() {
        if (!readOnlyStatus) {
            if (fgu != null) {
                fgu.discard();
            }
            if (fgp != null) {
                fgp.discard();
            }
            setFieldsReadOnly();
        }
    }

    protected void editingDone() {
        if (search.getValue() != null && !readOnlyStatus) {
            ConfirmDialog.show(getUI(), "Please Confirm:",
                    "Are you really sure?", "I am", "No, cancel changes",
                    (org.vaadin.dialogs.ConfirmDialog.Listener) dialog -> {
                        if (dialog.isConfirmed()) {

                            try {
                                fgu.commit();
                                fgp.commit();
                                Object rid = optionGroup.getValue();
                                ShiroRole.addRoles(search.getValue(),
                                        shiroRoles.get(rid).getName());
                                setFieldsReadOnly();

                            } catch (CommitException e) {
                            }

                        } else {
                        }
                    });
        }
    }

    protected void setFieldsReadOnly() {
        readOnlyStatus = true;
        boolean b = true;
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

        club.setReadOnly(b);
        federationCode.setReadOnly(b);
        federationMember.setReadOnly(b);
        foreignWBFClubMember.setReadOnly(b);
        directorQualifications.setReadOnly(b);
        masterpoints.setReadOnly(b);

        optionGroup.setReadOnly(b);
    }

    protected void setFieldsReadOnlyOff() {
        if (search.getValue() != null) {
            readOnlyStatus = false;
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

            federationMember.setReadOnly(b);
            foreignWBFClubMember.setReadOnly(b);
            directorQualifications.setReadOnly(b);

            optionGroup.setReadOnly(b);
        }
    }

    protected void addUsernameValidator() {

        username.addValidator(new RegexpValidator("\\w{5,30}",
                "Username must be 5-30 characters long and consist of a-z and 0-9"));
        username.addValidator(value -> {
            Filter f = users.filterEq("username", value);
            if (users.size() >= 1
                    && users.at(0).getId() != (Long) search.getValue()) {
                throw new InvalidValueException("Username is already in use");
            }
            users.removeFilter(f);
        });

    }

    @Override
    public void enter(ViewChangeEvent event) {
        addComponents(menu, submenu, search, hl);
    }

}
