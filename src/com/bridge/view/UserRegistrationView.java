package com.bridge.view;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

import com.bridge.database.C;
import com.bridge.database.Club;
import com.bridge.database.MembershipApplication;
import com.bridge.database.Player;
import com.bridge.database.User;
import com.bridge.ui.BridgeUI;
import com.bridge.ui.MainMenu;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.data.validator.EmailValidator;
import com.vaadin.data.validator.StringLengthValidator;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.PasswordField;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class UserRegistrationView extends UserManagementView {

    public static final String name = "/user/registration";
    public final static String[] rls = new String[] { "anon" };
    @SuppressWarnings("unchecked")
    public final static HashSet<String> roles = new HashSet<>(
            Arrays.asList(rls));

    static final String usernameQuery = "SELECT username FROM USERS WHERE username = ?";

    protected Label group1 = new Label("<b>Personal Information</b>");
    protected TextField firstName = new TextField("First name");
    protected TextField secondName = new TextField("Second name");
    protected TextField lastName = new TextField("Last name");
    protected TextField yearOfBirth = new TextField("Year of birth");
    protected TextField nationality = new TextField("Nationality");
    protected ComboBox language = new ComboBox("UI language"); // for user
                                                               // interface
    protected VerticalLayout group1layout = new VerticalLayout();

    protected Label group2 = new Label("<b>Contact Information</b>");
    protected TextField address = new TextField("Address");
    protected TextField postalCode = new TextField("Postal code");
    protected TextField town = new TextField("Town");
    protected TextField telephone = new TextField("Telephone");
    protected TextField email = new TextField("Email");
    protected CheckBox telephonePrivate = new CheckBox("telephone hidden");
    protected CheckBox emailPrivate = new CheckBox("Email hidden");
    protected VerticalLayout group2layout = new VerticalLayout();

    protected Label group3 = new Label("<b>Login information</b>");
    protected ComboBox club = new ComboBox("Club");
    protected TextField username = new TextField("Username");
    protected PasswordField password = new PasswordField("Password");
    protected PasswordField passwordVerification = new PasswordField(
            "Password again");
    protected CheckBox foreignWBFClubMember = new CheckBox(
            "Member of a non-Finnish club");
    protected VerticalLayout group3layout = new VerticalLayout();

    protected Button submitButton = new Button("Submit");
    protected HorizontalLayout forms = new HorizontalLayout();

    protected BeanFieldGroup<User> userFg;
    protected BeanFieldGroup<Player> playerFg;
    protected BeanItem<User> userItem;
    protected BeanItem<Player> playerItem;

    protected C<User> users = new C<>(User.class);
    protected C<Player> players = new C<>(Player.class);
    protected C<Club> clubs = new C<>(Club.class);
    protected C<MembershipApplication> as = new C<>(
            MembershipApplication.class);

    public UserRegistrationView(MainMenu menu) {
        super(menu);
        resetForm();
        prefill();
        addFormsAndWidgets();
        addValidators();

        club.setConverter(new SingleSelectConverter<Club>(club));

        // hide Bridge Federation as membership club
        clubs.filterNeq("name", "Federation");
    }

    private void prefill() {
        String s = "esaesaesa";
        username.setValue(s);
        firstName.setValue(s);
        lastName.setValue(s);
        password.setValue(s);
        passwordVerification.setValue(s);
        email.setValue("esa@esa.fi");
    }

    /***
     * Used only for testing purposes
     */

    @SuppressWarnings("unused")
    private void setTestValues() {
        firstName.setValue("FirstName");
        lastName.setValue("LastName");
        email.setValue("rkcb0306@countermail.com");
        username.setValue("username");
        password.setValue("12345678");
        passwordVerification.setValue("12345678");
    }

    /***
     * resetForm creates a new form and cleans the fields
     */

    protected void resetForm() {

        userItem = new BeanItem<>(new User());
        playerItem = new BeanItem<>(new Player());

        userFg = new BeanFieldGroup<>(User.class);
        playerFg = new BeanFieldGroup<>(Player.class);

        userFg.bindMemberFields(this);
        playerFg.bindMemberFields(this);

        userFg.setItemDataSource(userItem);
        playerFg.setItemDataSource(playerItem);

        passwordVerification.clear();
        password.clear();
        club.clear();
    }

    protected void addValidators() {

        // because has hashing the plain password must be validated here
        StringLengthValidator v = new StringLengthValidator(
                "Password must be 8-30 letters long", 8, 30, false);
        password.addValidator(v);

        passwordVerification.addValidator(value -> {
            if (!password.getValue().equals(value)) {
                throw new InvalidValueException(
                        "Password fields are different!");
            }
        });

        username.addValidator(value -> {
            if (usernameExists((String) value)) {
                throw new InvalidValueException("The username is in use");
            }
        });

        email.addValidator(new EmailValidator("Not a valid email address"));

    }

    protected void addFormsAndWidgets() {

        club.setContainerDataSource(clubs.c());
        club.setItemCaptionMode(ItemCaptionMode.ITEM);

        group1.setContentMode(ContentMode.HTML);
        group2.setContentMode(ContentMode.HTML);
        group3.setContentMode(ContentMode.HTML);

        group1layout.addComponents(group1, firstName, secondName, lastName,
                yearOfBirth, nationality, language);
        group1layout.setMargin(true);
        group1layout.setSpacing(true);

        group2layout.addComponents(group2, address, postalCode, town, telephone,
                email, telephonePrivate, emailPrivate);
        group2layout.setMargin(true);
        group2layout.setSpacing(true);

        group3layout.addComponents(group3, club, username, password,
                passwordVerification, foreignWBFClubMember);
        group3layout.setMargin(true);
        group3layout.setSpacing(true);

        forms.addComponents(group1layout, group2layout, group3layout);

        setMargin(true);
        setSpacing(true);

        firstName.setRequired(true);
        lastName.setRequired(true);
        email.setRequired(true);
        username.setRequired(true);
        password.setRequired(true);
        passwordVerification.setRequired(true);

        username.setImmediate(true);
        password.setImmediate(true);
        email.setImmediate(true);
        passwordVerification.setImmediate(true);

        emailPrivate.setValue(true);
        telephonePrivate.setValue(true);

        foreignWBFClubMember.setDescription(
                "The club must be a member of the World Bridge Federation");

        submitButton.addClickListener(new Button.ClickListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void buttonClick(ClickEvent event) {

                if (requiredFieldsValid()) {

                    // commit the bean changes and add the default role: owner

                    try {
                        userFg.commit();
                        String s = BridgeUI.pwService
                                .encryptPassword(password.getValue());
                        userItem.getItemProperty("password").setValue(s); // encrypt
                    } catch (CommitException e) {
                        e.printStackTrace();
                    }

                    try {
                        // Object cid = club.getValue();
                        // if (cid != null) {
                        // // playerFg.unbind(club); ??? Why exists?
                        // playerItem.getItemProperty("club")
                        // .setValue(clubs.get(cid, "club"));
                        // }
                        // BridgeUI.o("valid? :" + playerFg.isValid());
                        playerFg.commit();
                    } catch (CommitException e) {
                        e.printStackTrace();
                    }

                    User u = userItem.getBean();
                    Player p = playerItem.getBean();

                    Calendar cl = Calendar.getInstance();
                    Date now = cl.getTime();

                    Object aid = as.add(new MembershipApplication(u, p, now));
                    BridgeUI.o(
                            "email put: " + as.get(aid).getUser().getEmail());

                    resetForm();
                } else {
                    if (!foreignWBFClubMember.getValue()
                            && club.getValue() == null) {
                        Notification.show(
                                "The player must be a member of a Finnish club or a foreign club",
                                "", Notification.Type.ERROR_MESSAGE);
                    } else {
                        Notification.show(
                                "A field with * is empty or contains an error;",
                                "", Notification.Type.ERROR_MESSAGE);
                    }
                }
            }

        });
    }

    protected boolean requiredFieldsValid() {

        boolean valid = true;

        try {
            firstName.validate();
        } catch (InvalidValueException e) {
            valid = false;
        }
        try {
            lastName.validate();
        } catch (InvalidValueException e) {
            valid = false;
        }
        try {
            email.validate();
        } catch (InvalidValueException e) {
            valid = false;
        }
        try {
            username.validate();
        } catch (InvalidValueException e) {
            valid = false;
        }
        try {
            password.validate();
        } catch (InvalidValueException e) {
            valid = false;
        }
        try {
            passwordVerification.validate();
        } catch (InvalidValueException e) {
            valid = false;
        }

        if (!foreignWBFClubMember.getValue() && club.getValue() == null) {
            valid = false;
        }

        return valid;
    }

    /***
     * usernameExists checks that the username is not occupied by existing users
     * or in the applications waiting for processing
     */

    protected boolean usernameExists(String username) {
        C<User> ps = new C<>(User.class);
        C<MembershipApplication> as = new C<>(MembershipApplication.class);
        as.nest("user.username");

        return ps.exists("username", username)
                && as.exists("user.username", username);
    }

    @Override
    public void enter(ViewChangeEvent event) {
        addComponents(mainMenu, forms, submitButton);
    }

}
