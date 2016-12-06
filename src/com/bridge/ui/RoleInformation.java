package com.bridge.ui;

import java.util.ArrayList;
import java.util.List;

import com.bridge.database.C;
import com.bridge.database.ShiroRole;
import com.bridge.database.User;
import com.vaadin.addon.jpacontainer.EntityItem;
import com.vaadin.addon.jpacontainer.JPAContainer;
import com.vaadin.addon.jpacontainer.JPAContainerFactory;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Label;
import com.vaadin.ui.OptionGroup;

@SuppressWarnings("serial")
public class RoleInformation extends InformationForm {

    // this class is for system admins only to edit user userRoles

    protected Label group = new Label("<b>User userRoles</b>");
    protected OptionGroup optionGroup = new OptionGroup("");
    protected EntityItem<User> userItem;
    protected JPAContainer<ShiroRole> shiroRoles = JPAContainerFactory
            .make(ShiroRole.class, BridgeUI.unitName);
    protected List<ShiroRole> roles = new ArrayList<>(4);

    public RoleInformation() {

        addComponents(group, optionGroup);
        group.setContentMode(ContentMode.HTML);
        setComponentAlignment(group, Alignment.MIDDLE_CENTER);
        optionGroup.setContainerDataSource(shiroRoles);
        optionGroup.setMultiSelect(false);

        setMargin(true);
        setSpacing(true);

        addButtons();
        setReadOnlyState(true, false);
        setOptionCaptions();
    }

    private void setOptionCaptions() {
        for (Object id : shiroRoles.getItemIds()) {
            String desc = shiroRoles.getItem(id).getEntity().getDescription();
            if (desc != null && !desc.isEmpty()) {
                optionGroup.setItemCaption(id, desc);
            }
        }
    }

    public void setUser(EntityItem<User> ui) {

        C<User> us = new C<>(User.class);
        List<ShiroRole> l = new ArrayList<>(
                us.get(ui.getItemId()).getRoles());
        l.sort(null);
        optionGroup.setValue(l.get(l.size() - 1).getId());
        // userItem = ui;
        // User u = ui.getEntity();
        // Set<Object> ids = new HashSet<Object>();
        // Set<ShiroRole> rs = u.getRoles();
        // if (rs != null){
        // for (ShiroRole r: rs)
        // ids.add(r.getId());
        // }
        //
        // optionGroup.setReadOnly(false);
        // optionGroup.setValue(ids);
        //
        setReadOnlyState(true, true);
    }

    @Override
    void setAllFieldsReadOnly(boolean readOnly) {
        optionGroup.setReadOnly(readOnly);
    }

    @Override
    void okClicked() {
        Object id = optionGroup.getValue();
        C<ShiroRole> rs = new C<>(ShiroRole.class);
        ShiroRole.addRoles(userItem.getItemId(), rs.get(id).getName());

        setReadOnlyState(true, true);
    }

    @Override
    void cancelClicked() {
        userItem.discard();
    }

}
