package com.bridge.calendar;

import java.util.HashSet;
import java.util.Set;

import com.bridge.database.BridgeEvent;
import com.bridge.database.User;
import com.bridge.ui.BridgeUI;
import com.bridge.view.LoginView;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class BridgeEventReader extends BridgeEventEditor {

    private Window dialog;
    private Button exit = new Button("Exit");
    private Button loginToRegister = new Button("Login to register");
    private VerticalLayout vLayout = new VerticalLayout();
    private HorizontalLayout buttons = new HorizontalLayout();
    private User user = null;
    private BridgeEvent bridgeEvent = null;
    // private Object eventId = null;
    private State state = null;
    private JPAContainer<BridgeEvent> events = JPAContainerFactory
            .make(BridgeEvent.class, BridgeUI.unitName);
    private Table participantsTable = null;
    private BeanContainer<Long, User> participants = new BeanContainer<>(
            User.class);

    // NotLogged and Logged are used for the registration case only
    //
    private enum State {
        NoRegistration, NotLogged, Logged
    };

    public BridgeEventReader(Window parent) {
        super(false);

        events.setAutoCommit(true);
        participants.setBeanIdProperty("id");
        dialog = parent;
        setFieldsReadOnly();

        vLayout.addComponent(this);
        vLayout.setMargin(true);
        addButtons();

        registered.addValueChangeListener(event -> {
            // user variable should be nonzero because this clicking is possible
            if (registered.getValue()) {
                if (!participants.containsId(user.getId())) {
                    participants.addBean(user);
                    storeChanges();
                }
            } else {
                if (participants.containsId(user.getId())) {
                    participants.removeItem(user.getId());
                    storeChanges();
                }
            }
        });

        loginToRegister.addClickListener(event -> {
            getUI().getNavigator().navigateTo(LoginView.name);
            dialog.close();
        });
    }

    @SuppressWarnings("unchecked")
    protected void storeChanges() {
        Set<User> s = new HashSet<>();
        for (Object id : participants.getItemIds()) {
            s.add(participants.getItem(id).getBean());
        }
        events.getItem(bridgeEvent.getId()).getItemProperty("participants")
                .setValue(s);
    }

    public VerticalLayout getDialog() {
        return vLayout;
    }

    /***
     * setDialogState sets the state and hides some fields if needed
     */

    private void setDialogState() {

        type.setVisible(false);
        masterPoint.setVisible(false);

        if (registration.getValue() == false) {
            state = State.NoRegistration;
        } else {
            state = user == null ? State.NotLogged : State.Logged;
        }

        if (state == State.NoRegistration) {
            signInEnd.setVisible(false);
            signInStart.setVisible(false);
            registered.setVisible(false);
            loginToRegister.setVisible(false);
        } else if (state == State.NotLogged) {
            registered.setVisible(false);
            loginToRegister.setVisible(true);
        } else if (state == State.Logged) {
            registered.setVisible(true);
            registered.setReadOnly(false);
            loginToRegister.setVisible(false);
        }

    }

    private void setFieldsReadOnly() {
        final boolean s = true;

        caption.setReadOnly(s);
        description.setReadOnly(s);
        town.setReadOnly(s);
        country.setReadOnly(s);
        signInStart.setReadOnly(s);
        signInEnd.setReadOnly(s);
        start.setReadOnly(s);
        end.setReadOnly(s);
        type.setReadOnly(s);
        price.setReadOnly(s);
        registration.setReadOnly(s);
        registered.setReadOnly(s);
        privateEvent.setReadOnly(s);
    }

    public boolean isUserParticipant(User user) {
        return participants == null ? false
                : participants.containsId(user.getId());
    }

    /***
     * addButtons adds exit and registration buttons to the dialog
     */

    private void addButtons() {

        exit.addClickListener(event -> closeWindow());

        buttons.addComponents(loginToRegister, exit);
        vLayout.addComponent(buttons);
        vLayout.setComponentAlignment(buttons, Alignment.MIDDLE_CENTER);

    }

    /***
     * createParticipantsTable creates table listing the current participants in
     * the event
     */

    public Table createParticipantsTable(Set<User> ps) {

        participantsTable = new Table();
        participants.addAll(ps);

        // t.addContainerProperty("participants", String.class, null);
        // participants.addNestedContainerBean("player.club.name");
        participantsTable.setContainerDataSource(participants);
        participantsTable.setPageLength(0);
        participantsTable.setWidth("400px");
        participantsTable.setColumnHeader("firstName", "First name");
        participantsTable.setColumnHeader("lastName", "Last name");
        // participantsTable.setColumnHeader("player.club.name", "Club");
        participantsTable
                .setVisibleColumns(new Object[] { "firstName", "lastName" });

        return participantsTable;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private void closeWindow() {
        dialog.close();
    }

    public void initialize(BridgeEvent e, String username) {
        super.initialize(e);
        bridgeEvent = e;
        setFieldsReadOnly();
        // store event id to store a possible participation
        // eventId = e.getId();

        // if user is logged in find the corresponding entity
        // which is used for registering
        user = BridgeUI.user.getCurrentUser();
        if (user != null) {
            // set registration status
            registered.setValue(isUserParticipant(user));
        }
        // hide or show widgets based on the state, also set read only
        // statuses
        setDialogState();

    }
}
