package com.bridge.ui;

import com.bridge.database.User;
import com.bridge.ui.BasicUserContents.ROLE;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

@SuppressWarnings("serial")
public class ContactInformationForm extends InformationForm {

    private Label header = new Label("Contact information");
    // @PropertyId("address") // should be useless because automatically scanned
    private TextField address;
    // @PropertyId("town")
    private TextField town;
    // @PropertyId("postalCode")
    private TextField postalCode;
    // @PropertyId("country")
    private TextField country;

    // @PropertyId("email")
    private TextField email;
    // @PropertyId("telephone")
    private TextField telephone;

    // @PropertyId("emailPrivate")
    private CheckBox emailPrivate = new CheckBox("Private email"); //

    // @PropertyId("telephonePrivate")
    private CheckBox telephonePrivate = new CheckBox("Private telephone");

    private User targetUser;
    private ROLE role;
    private boolean showButtons = false;

    public ContactInformationForm() {
    }

    public ContactInformationForm(EntityItem<User> userItem, ROLE role) {
        if (userItem != null) {
            super.addComponent(header);
            super.setSpacing(true);
            super.setMargin(true);

            this.role = role;
            targetUser = userItem.getEntity();

            // connect fields to DB
            FieldGroup fg = new FieldGroup(userItem);
            fg.buildAndBindMemberFields(this);

            // if (role == ROLE.OWNERUSER || role == ROLE.SYSTEMADMIN) {
            // form.addComponents( address, town, postalCode, country, email,
            // telephone,
            // emailPrivate, telephonePrivate);
            // showButtons = true;
            // } else if (role == ROLE.USER || role == ROLE.CLUBADMIN) {
            // System.out.println("User or clubadmin");
            // Boolean emailPrivate = targetUser.getEmailPrivate();
            // if (emailPrivate == false) {
            // form.addComponent(email);
            // }
            //
            // Boolean telephonePrivate = targetUser.getTelephonePrivate();
            // if (telephonePrivate == false) {
            // form.addComponent(telephone);
            // }
            // showButtons = false;
            //
            // }
            // super.addComponents(form);
            addButtons();
            setReadOnlyState(true, showButtons);
        }
    }

    @Override
    public void setAllFieldsReadOnly(boolean readOnly) {
        if (role != ROLE.SYSTEMADMIN && role != ROLE.OWNERUSER) {
            readOnly = true;
        }

        address.setReadOnly(readOnly);
        postalCode.setReadOnly(readOnly);
        town.setReadOnly(readOnly);
        country.setReadOnly(readOnly);
        email.setReadOnly(readOnly);
        telephone.setReadOnly(readOnly);
        telephonePrivate.setReadOnly(readOnly);
        emailPrivate.setReadOnly(readOnly);
    }

    /***
     * okClicked: when ok button is clicked then all changes are committed to
     * the database
     */
    @Override
    void okClicked() {
        if (role == ROLE.SYSTEMADMIN) {
            address.commit();
            postalCode.commit();
            town.commit();
            country.commit();
            email.commit();
            telephone.commit();
            emailPrivate.commit();
            telephonePrivate.commit();
        }
        setReadOnlyState(true, showButtons);
    }

    /***
     * cancelClicked: when Cancel button is clicked all changes are cancelled
     */

    @Override
    void cancelClicked() {
        address.setValue(targetUser.getAddress());
        postalCode.setValue(targetUser.getPostalCode());
        town.setValue(targetUser.getTown());
        country.setValue(targetUser.getCountry());
        email.setValue(targetUser.getEmail());
        telephone.setValue(targetUser.getTelephone());
        emailPrivate.setValue(targetUser.getEmailPrivate());
        telephonePrivate.setValue(targetUser.getTelephonePrivate());

        setReadOnlyState(true, showButtons);
    }

}
