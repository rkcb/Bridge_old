package com.bridge.ui;

import com.bridge.database.Player;
import com.bridge.ui.BasicUserContents.ROLE;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.PropertyId;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

@SuppressWarnings("serial")
public class PlayerInformationForm extends InformationForm {

    private Label header = new Label("Player information");
    @PropertyId("federationMember")
    private CheckBox federationMember = new CheckBox("Federation member");
    @PropertyId("federationCode")
    private TextField federationCode = new TextField("Federation code");
    @PropertyId("directorQualifications")
    private TextArea directorQualifications = new TextArea(
            "Director qualifications");
    @PropertyId("masterpoints")
    private TextField masterpoints = new TextField("Masterpoints");

    // private TextField clubMemberships = new TextField("Memberships");

    // private Player targetPlayer;
    private EntityItem<Player> playerItem;

    public PlayerInformationForm() {
    }

    public PlayerInformationForm(EntityItem<Player> playerItem, ROLE role) {
        if (playerItem != null) {
            super.addComponent(header);
            super.setMargin(true);
            super.setSpacing(true);

            this.playerItem = playerItem;
            this.role = role;

            // targetPlayer = playerItem.getEntity();
            addClubMemberships();

            FieldGroup fg = new FieldGroup(playerItem);
            fg.buildAndBindMemberFields(this);
            //
            // form.addComponents(masterpoints, federationCode,
            // federationMember, clubMemberships, directorQualifications);
            // super.addComponent(form);
            // addButtons();

            setReadOnlyState(true, role == ROLE.SYSTEMADMIN);
        }
    }

    /**
     * addClubMemberships:
     */
    private void addClubMemberships() {

        // if (targetPlayer != null && targetPlayer.getClubs() != null) {
        // String memberships = new String("");
        // Set<Club> clubSelector = targetPlayer.getClubs();
        // for (Club c: clubSelector) {
        // memberships = memberships + c;
        // if (clubSelector.size() > 1)
        // memberships = memberships + ", ";
        // }
        // clubMemberships.setValue(memberships);
        // clubMemberships.setReadOnly(true);
        // }
    }

    @Override
    protected void setAllFieldsReadOnly(boolean readOnly) {

        if (role != ROLE.SYSTEMADMIN) {
            readOnly = true;
        }

        federationMember.setReadOnly(readOnly);
        federationCode.setReadOnly(readOnly);
        directorQualifications.setReadOnly(readOnly);
        masterpoints.setReadOnly(readOnly);
    }

    @Override
    void okClicked() {
        // commit changes to the database
        federationCode.commit();
        federationMember.commit();
        masterpoints.commit();
        directorQualifications.commit();
        setReadOnlyState(true, true);
    }

    @Override
    void cancelClicked() {
        if (playerItem != null) {
            playerItem.refresh();
            // get old values back
            Player p = playerItem.getEntity();
            federationCode.setValue(p.getFederationCode());
            federationMember.setValue(p.getFederationMember());
            // masterpoints.setValue(p.getMasterPoints());
            directorQualifications.setValue(p.getDirectorQualifications());

        }
    }

}
