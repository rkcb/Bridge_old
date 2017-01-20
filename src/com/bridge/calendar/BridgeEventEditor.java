package com.bridge.calendar;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import com.bridge.database.BridgeEvent;
import com.bridge.database.C;
import com.bridge.database.Club;
import com.bridge.database.Player;
import com.bridge.database.Tournament;
import com.bridge.input.ECheckBox;
import com.bridge.input.EComboBox;
import com.bridge.input.EDateField;
import com.bridge.input.ETextArea;
import com.bridge.input.ETextField;
import com.bridge.ui.BridgeUI;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.fieldfactory.SingleSelectConverter;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

/***
 * BridgeEventEditor is a dialog to edit or read a calendar event or a
 * tournament event Note: tournament needs more fields than calendar's including
 * tournament organizers and directors
 */

@SuppressWarnings("serial")
public class BridgeEventEditor extends HorizontalLayout {

    protected ETextArea description = new ETextArea("Description", false, true);
    protected ETextField caption = new ETextField("Caption", true, true); // competition
                                                                          // name
    protected ETextField town = new ETextField("Town", false, true);
    protected ETextField country = new ETextField("Country", false, true);
    protected EDateField start = new EDateField("Start", true); // competion
                                                                // start time
    protected EDateField end = new EDateField("End", false); // competion end
                                                             // time
    protected ETextField price = new ETextField("Price", false, true);
    protected EDateField signInStart = new EDateField("Sign in start", false);
    protected EDateField signInEnd = new EDateField("Sign in end", false);
    protected EComboBox type = new EComboBox("Type", true);
    protected ECheckBox registration = new ECheckBox("Registration required",
            false);
    protected ECheckBox privateEvent = new ECheckBox("Private event", false);
    protected ECheckBox registered = new ECheckBox("Participate", false);
    protected ECheckBox masterPoint = new ECheckBox("Master point competion",
            false);
    protected ComboBox owner;
    protected FormLayout fl1 = null;
    protected FormLayout fl2 = null;

    // used to glue tournament stuff to one form layout containing event fields
    protected VerticalLayout directorsLayout = new VerticalLayout();
    protected VerticalLayout organizersLayout = new VerticalLayout();

    private BeanFieldGroup<BridgeEvent> bfg = null;
    private FieldGroup fg = null;

    public BridgeEventEditor(boolean isTournament) {

        addOwnerComboBox();

        // visible only in BridgeEventReader
        registered.setVisible(false);
        description.setHeightUndefined();
        description.setHeight("70px");
        description.setWidth("200px");
        addTypeComboBox();

        setMargin(true);
        setSpacing(true);

        insertFields(isTournament);
        addComponents(fl1, fl2);
    }

    private void addOwnerComboBox() {
        C<Club> clubs = new C<>(Club.class);
        owner = new ComboBox("Owner", clubs.c());
        owner.setImmediate(true);
        owner.setRequired(true);
        owner.setConverter(new SingleSelectConverter<Club>(owner));
        owner.setItemCaptionMode(ItemCaptionMode.ITEM);
        owner.setNullSelectionAllowed(false);
        Object id = BridgeUI.user.getCurrentClubId();
        owner.setValue(id);

        if (BridgeUI.getCurrentRole().matches("admin")) {
            owner.setRequired(true);
        } else {
            owner.setReadOnly(true);
        }
    }

    public Date getStartDate() {
        return start.getValue();
    }

    public Object getOwnerId() {
        return owner.getValue();
    }

    protected void insertFields(boolean isTournament) {
        if (isTournament) {
            // tournament event
            fl1 = new FormLayout(caption, town, start, end, signInStart,
                    signInEnd, masterPoint);

            fl2 = new FormLayout(type, description, country, price, owner,
                    registration, privateEvent);
        } else {
            // calendar event
            fl1 = new FormLayout(caption, town, start, end, signInStart,
                    signInEnd);
            fl1.addComponent(registered);
            fl2 = new FormLayout(description, country, price, owner,
                    registration, privateEvent);
        }
    }

    // /***
    // * setEditingState sets the editor to editing status This state shows when
    // * to hide the "Participate" field and when not
    // */

    // public void setEditingState(boolean status) {
    // isEditing = status;
    // }

    /***
     * setEventEditor hides the fields which are not used in editor mode
     */

    public void setParticipateCheckBoxVisible(boolean state) {
        registered.setVisible(state);
    }

    /***
     * embedOrganizersAndDirectors adds the components to the same formlayout
     * with the event fields
     */
    public void embedOrganizersAndDirectors(PlayerList l1, PlayerList l2) {
        fl1.addComponent(l1);
        fl2.addComponent(l2);
    }

    /***
     * cancel discards changes made to the event which exists in the database
     * otherwise does nothing
     */

    public void cancel() {
        if (fg != null) {
            fg.discard();
        }
    }

    /***
     * embedOrganizersAndDirectors adds the sets for the event reading purposes
     * when the tournament event is the case
     */

    public void embedOrganizersAndDirectors(Tournament t) {

        fl2.removeComponent(directorsLayout);
        fl2.removeComponent(organizersLayout);
        directorsLayout.setCaption("Directors");
        organizersLayout.setCaption("Organizers");
        createList(directorsLayout, t.getDirectors());
        createList(organizersLayout, t.getDirectors());
        fl2.addComponent(directorsLayout);
        fl2.addComponent(organizersLayout);
        fl2.addComponent(owner);

    }

    protected VerticalLayout createList(VerticalLayout l, Set<Player> ps) {
        l.removeAllComponents();
        for (Player p : ps) {
            l.addComponent(new Label(p.toString()));
        }

        return l;
    }

    /***
     * isOwnerModified shows modification status of owner property
     */
    public boolean isOwnerModified() {
        return owner.isModified();
    }

    /***
     * initialize initializes the event with a cloned bean
     */

    public void initialize(BridgeEvent event) {
        bfg = new BeanFieldGroup<>(BridgeEvent.class);
        bfg.setBuffered(false);
        bfg.setItemDataSource(event);
        bfg.bindMemberFields(this);
        fg = null;
    }

    /***
     * setItemSource binds the item to the fields
     */

    public void setItemSource(EntityItem<BridgeEvent> item) {

        bfg = null;

        boolean isTournament = (boolean) item.getItemProperty("isTournament")
                .getValue();

        if (isTournament) { // is tournament
            fg = new FieldGroup(item);
        } else { // is calendar event
            type = null;
            registered = null;
            masterPoint = null;
            fg = new FieldGroup(item);
        }

        fg.bindMemberFields(this);
    }

    /***
     * areFieldsValid checks the values of the values against the validators
     */

    public boolean areTournamentFieldsValid() {

        return caption.isValid() && description.isValid() && town.isValid()
                && country.isValid() && signInEnd.isValid()
                && signInStart.isValid() && start.isValid() && end.isValid()
                && type.isValid() && price.isValid();
    }

    /***
     * areFieldsValid checks the values of the values against the validators
     */

    public boolean areEventFieldsValid() {

        return caption.isValid() && description.isValid() && town.isValid()
                && country.isValid() && signInEnd.isValid()
                && signInStart.isValid() && start.isValid() && end.isValid()
                && price.isValid();
    }

    /***
     * getFieldValues builds a bean of current field values
     */

    public BridgeEvent getFieldValues() {
        BridgeEvent e = new BridgeEvent();
        e.setCaption(caption.getValue());
        e.setDescription(description.getValue());
        e.setTown(town.getValue());
        e.setCountry(country.getValue());
        e.setSignInStart(signInStart.getValue());
        e.setSignInEnd(signInEnd.getValue());
        e.setStart(start.getValue());
        e.setEnd(end.getValue());
        e.setType((com.bridge.database.Tournament.Type) type.getValue());
        e.setPrice(price.getValue());

        return e;
    }

    /***
     * getValue returns non null event only if it is valid and currently edited
     * event is not in the database
     */

    public BridgeEvent getValue() {
        if (bfg != null && areEventFieldsValid()) {
            try {
                bfg.commit();
            } catch (CommitException e) {
                return null;
            }
            return bfg.getItemDataSource().getBean();
        } else {
            return null;
        }
    }

    private void addTypeComboBox() {
        type.addItem(Tournament.Type.Individual);
        type.setItemCaption(Tournament.Type.Individual, "Indy");

        type.addItem(Tournament.Type.Pair);
        type.setItemCaption(Tournament.Type.Pair, "Pair");

        type.addItem(Tournament.Type.Team);
        type.setItemCaption(Tournament.Type.Team, "Team");
    }

    /*
     * setDateWeekForward moves start date week forward iff date is not empty
     */

    private Date getWeekForward(Date date) {
        if (date != null) {
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            c.set(Calendar.DAY_OF_YEAR, c.get(Calendar.DAY_OF_YEAR) + 7);
            return c.getTime();
        } else {
            return null;
        }
    }

    public void setTimeFieldsWeekForward() {
        signInStart.setValue(getWeekForward(signInStart.getValue()));
        signInEnd.setValue(getWeekForward(signInEnd.getValue()));
        start.setValue(getWeekForward(start.getValue()));
        end.setValue(getWeekForward(end.getValue()));
    }

    /***
     * commit commits the values to the competion beeing edited note that the
     * method is valid only if editing a competion in DB
     *
     * @throws CommitException
     */

    public void commitTournamentEvent() throws CommitException {
        fg.commit();
    }

    public void commitCalendarEvent() throws CommitException {
        if (fg != null) {
            fg.commit();
        }
    }

    public void clear() {
        fg.clear();
    }

}
