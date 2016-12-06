package com.bridge.ui;

import com.bridge.ui.BasicUserContents.ROLE;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
abstract class InformationForm extends VerticalLayout {

    private Button edit = new Button("Edit");
    private Button cancel = new Button("Cancel");
    private Button ok = new Button("Ok");

    protected HorizontalLayout buttons = new HorizontalLayout();
    protected ROLE role;

    public InformationForm() {
        setStyleName("informationForm");
    }

    abstract void setAllFieldsReadOnly(boolean readOnly);

    abstract void okClicked();

    abstract void cancelClicked();

    /***
     * setReadOnlyState sets the buttons to be such that the user has to press
     * Edit button to enable editing
     */

    protected void setReadOnlyState(boolean readOnly, boolean showButtons) {
        setAllFieldsReadOnly(readOnly);
        buttons.setVisible(showButtons);
        cancel.setVisible(!readOnly);
        ok.setVisible(!readOnly);
        edit.setVisible(readOnly);
    }

    protected void addButtons() {

        edit.addClickListener(listener -> {
            cancel.setVisible(true);
            ok.setVisible(true);
            edit.setVisible(false);
            setReadOnlyState(false, true);
        });

        cancel.addClickListener(listener -> {
            cancelClicked();
        });

        ok.addClickListener(listener -> {
            okClicked();
        });

        buttons.addComponents(edit, cancel, ok);
        addComponent(buttons);

        edit.setVisible(true);
        cancel.setVisible(true);
        ok.setVisible(true);
    }

}
