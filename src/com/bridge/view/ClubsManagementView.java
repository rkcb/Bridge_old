package com.bridge.view;

import java.util.Arrays;
import java.util.HashSet;

import org.vaadin.dialogs.ConfirmDialog;

import com.bridge.database.C;
import com.bridge.database.Club;
import com.bridge.database.Player;
import com.bridge.ui.EFormLayout;
import com.bridge.ui.EHorizontalLayout;
import com.bridge.ui.EVerticalLayout;
import com.bridge.ui.MainMenu;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.validator.AbstractStringValidator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;

@SuppressWarnings("serial")
public class ClubsManagementView extends EVerticalLayout implements View {

    public static final String name = "/clubsManagement";
    public final static String[] rls = new String[] { "anon" };
    public final static HashSet<String> roles = new HashSet<>(
            Arrays.asList(rls));

    /***
     * validates that new club has a nonexisting and nonempty name; whitespace
     * does not qualify
     */
    class NewV extends AbstractStringValidator {
        private final HashSet<String> names;

        public NewV(String errorMessage, HashSet<String> names) {
            super(errorMessage);
            this.names = names;
        }

        @Override
        protected boolean isValidValue(String value) {

            String s = value.trim();

            return s != null && s.length() > 0 && !names.contains(s);
        }
    }

    /***
     * as above but can use its old name
     */

    class OldV extends AbstractStringValidator {

        private final String old;
        private HashSet<String> names;

        public OldV(String errorMessage, HashSet<String> names,
                String oldName) {
            super(errorMessage);
            old = oldName.toLowerCase();
            this.names = names;
            names.remove(old);
        }

        public HashSet<String> update(String newName) {
            names.add(newName);
            return names;
        }

        public HashSet<String> restore() {
            names.add(old);
            return names;
        }

        @Override
        protected boolean isValidValue(String value) {
            String s = value.trim().toLowerCase();
            return s != null && s.length() > 0 && !names.contains(s);
        }
    }

    private MainMenu menu;
    private C<Club> clubs = new C<>(Club.class);
    private C<Player> ps = new C<>(Player.class);
    // private ETable table = new ETable("Clubs", clubSelector.c());
    private ComboBox clubId = new ComboBox("Club", clubs.c());
    private EHorizontalLayout hl = new EHorizontalLayout(clubId);
    private EFormLayout fl = new EFormLayout();
    private EVerticalLayout vl1 = new EVerticalLayout();
    @SuppressWarnings("unused")
    private MenuItem newClub;
    @SuppressWarnings("unused")
    private MenuItem remove;
    @SuppressWarnings("unused")
    private MenuItem edit;
    @SuppressWarnings("unused")
    private MenuItem deactivation;
    private CheckBox active;
    private MenuBar submenu = new MenuBar();
    private TextField clubName;
    private TextField town;
    private ComboBox admin;
    private Object id = null; // table selection
    private FieldGroup fg;
    private BeanFieldGroup<Club> bfg;
    // private boolean creatingNewClub = false;
    private HashSet<String> names = new HashSet<>();

    public ClubsManagementView(MainMenu menu) {
        this.menu = menu;

        clubId.setItemCaptionMode(ItemCaptionMode.ITEM);
        clubId.setImmediate(true);
        clubId.setFilteringMode(FilteringMode.CONTAINS);
        clubId.setContainerDataSource(clubs.c());

        vl1.addComponents(clubId);
        hl.addComponents(vl1, fl);
        createButtons();

    }

    public boolean newName(String s) {
        return names.contains(s);
    }

    /***
     * bindFg binds existing club data
     */
    // private void bindFg() {
    // fg = new FieldGroup(clubs.item(id));
    // fg.bindMemberFields(this);
    // fg.bind(clubName, "name");
    // }

    private void createButtons() {

        newClub = submenu.addItem("Add club",
                selectedItem -> createClubCreator());

        edit = submenu.addItem("Edit Club", selectedItem -> editClub());

        remove = submenu.addItem("Remove Club", selectedItem -> {
            id = clubId.getValue();
            if (id != null) {
                Club cl = clubs.get(id);
                if (cl.getCalendarEvents().isEmpty()
                        && cl.getTournaments().isEmpty()) {
                    String s = clubs.get(id).getName().toLowerCase();
                    clubs.rem(id);
                    names.remove(s);
                    Notification.show(s + " succesfully removed",
                            Type.HUMANIZED_MESSAGE);
                } else {
                    Notification.show(
                            "Only a club without events or tournaments "
                                    + "accociated with it can be removed",
                            Type.ERROR_MESSAGE);
                }
            } else {
                Notification.show("Choose a club to edit", Type.ERROR_MESSAGE);
            }
        });

    }

    protected void o(String string) {
        System.out.println(string);
    }

    private void initNames() {
        for (int i = 0; i < clubs.size(); i++) {
            String s = clubs.at(i).getName().toLowerCase();
            names.add(s);
        }
    }

    private void editClub() {
        id = clubId.getValue();

        if (id != null) {
            String oldName = clubs.get(id).getName();
            fl.removeAllComponents();
            createFields();
            clubName.addValidator(value -> {
                String name = (String) value;
                if (clubs.exists("name", name) && !oldName.matches(name)) {
                    throw new InvalidValueException("The name is in use");
                }
            });
            active = new CheckBox("Active");
            fg = new FieldGroup(clubs.item(id));
            fg.bindMemberFields(this);
            fg.bind(clubName, "name");

            // if (clubs.get(id).getMembers().isEmpty()) {
            // admin = new ComboBox("Club administrator");
            // admin.setImmediate(true);
            // admin.setRequired(true);
            // admin.addFocusListener(new FocusListener() {
            // @Override
            // public void focus(FocusEvent event) {
            // admin.setRequiredError("A club must have an admin");
            // }
            // });
            // admin.setContainerDataSource(ps.c());
            // admin.setItemCaptionMode(ItemCaptionMode.ITEM);
            // }

            active.addValueChangeListener(event -> {
                boolean a = (boolean) event.getProperty().getValue();
                if (!a) {
                    ConfirmDialog.show(getUI(), "Confirmation",
                            "Inactive club cannot create tournaments or events or any other",
                            "I understand", "Oops, cancel",
                            (org.vaadin.dialogs.ConfirmDialog.Listener) dialog -> {
                                if (dialog.isCanceled()) {
                                    active.setValue(true);
                                }
                            });
                }
            });

            Button ok = new Button("Ok");
            ok.addClickListener(event -> {
                boolean valid = true;
                try {
                    clubName.validate();
                } catch (InvalidValueException e1) {
                    valid = false;
                }
                if (valid) {
                    try {
                        if (!active.getValue()) {
                            ps.refresh();
                            ps.removeFilters();
                        }
                        fg.commit();
                        fl.removeAllComponents();
                        clubName.setValue(null);
                    } catch (CommitException e2) {
                    }
                }
            });
            Button cancel = new Button("Cancel");
            cancel.addClickListener(event -> {
                bfg = null;
                fl.removeAllComponents();
                clubName = null;
                town = null;
                admin = null;
                clubId.setValue(null);
            });
            EHorizontalLayout buttons = new EHorizontalLayout(cancel, ok);
            buttons.setSpacing(true);
            fl.addComponents(clubName, town, active, buttons);
            if (admin != null) {
                fl.addComponent(admin, 3);
            }

            clubId.setValue(null);
        } else {
            Notification.show("Choose a club to edit", Type.ERROR_MESSAGE);
        }
    }

    private void createClubCreator() {
        fl.removeAllComponents();
        // creatingNewClub = true; // used in ok button
        createFields();
        clubName.addValidator(value -> {
            String name = (String) value;
            if (clubs.exists("name", name)) {
                throw new InvalidValueException("The name is in use");
            }
        });

        fl.addComponents(clubName, town);
        bfg = new BeanFieldGroup<>(Club.class);
        bfg.setBuffered(false);
        bfg.setItemDataSource(new Club());
        bfg.bindMemberFields(this);
        bfg.bind(clubName, "name");
        Button ok = new Button("Ok");
        ok.addClickListener(event -> {
            if (clubName.isValid()) {
                try {
                    bfg.commit();
                    names.add(clubName.getValue());
                    clubs.add(bfg.getItemDataSource().getBean());
                    clubName = null;
                    town = null;
                    fl.removeAllComponents();
                } catch (CommitException e) {
                    e.printStackTrace();
                }
            } else {
                Notification.show("Check that starred fields are valid",
                        Type.ERROR_MESSAGE);
            }
        });
        Button cancel = new Button("Cancel");
        cancel.addClickListener(event -> {
            fl.removeAllComponents();
            clubName = null;
            town = null;
            admin = null;
            clubId.setValue(null);
        });
        HorizontalLayout buttons = new HorizontalLayout(cancel, ok);
        buttons.setSpacing(true);
        fl.addComponent(buttons);
    }

    private void createFields() {
        clubName = new TextField("Club name");
        clubName.setRequired(true);
        clubName.setImmediate(true);

        town = new TextField("Town");
    }

    @Override
    public void enter(ViewChangeEvent event) {
        clubs.refresh();
        names.clear();
        initNames();
        addComponents(menu, submenu, hl);
    }

}
