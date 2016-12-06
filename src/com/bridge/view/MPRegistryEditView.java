package com.bridge.view;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import com.bridge.database.C;
import com.bridge.database.MasterPointMessage;
import com.bridge.database.Player;
import com.bridge.ui.BridgeUI;
import com.bridge.ui.EHorizontalLayout;
import com.bridge.ui.ETable;
import com.bridge.ui.EVerticalLayout;
import com.bridge.ui.MainMenu;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanContainer;
import com.vaadin.data.util.converter.StringToDoubleConverter;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.ErrorMessage;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.AbstractSelect.ItemCaptionMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

@SuppressWarnings("serial")
public class MPRegistryEditView extends EVerticalLayout implements View {

    public static final String name = "/admin/mpregistryeditor";

    private MainMenu mainMenu;
    private C<Player> players;
    private ComboBox search;
    private TextField masterPoints;
    private TextField message;
    private FormLayout messageFLayout;
    private Button add;
    private Button clear;
    private Button commit;
    private EHorizontalLayout hLayout;
    private HorizontalLayout hLayout2;
    private ETable selectedTable;
    private C<MasterPointMessage> mpsMessages;
    private BeanContainer<Long, Player> selected;

    public MPRegistryEditView(MainMenu mainMenu) {
        this.mainMenu = mainMenu;
        addComponent(this.mainMenu);
        players = new C<>(Player.class);
        mpsMessages = new C<>(MasterPointMessage.class);
        selected = new BeanContainer<>(Player.class);
        selected.setBeanIdProperty("id");
        addSearchWidgets();
        addMessageField();
        addMasterPointsField();
        addCommitButton();

    }

    private void addMessageField() {
        message = new TextField("Message for MPs");
        message.setRequired(true);
        message.setWidth("300px");
        message.addValidator(value -> {
            String msg = value != null ? (String) value : "";
            if (msg.trim().isEmpty()) {
                throw new InvalidValueException("Message must not be empty");
            }
        });
        message.addValueChangeListener(
                listener -> message.setComponentError(null));

        messageFLayout = new FormLayout(message);
    }

    protected void addSearchWidgets() {
        search = new ComboBox("MP Receiver");
        search.setFilteringMode(FilteringMode.CONTAINS);
        search.setItemCaptionMode(ItemCaptionMode.ITEM);
        search.setContainerDataSource(players.c());
        search.setTextInputAllowed(true);
        search.setNullSelectionAllowed(false);
        search.setWidth("300px");
        search.setRequired(true);
        search.setInputPrompt("Write a name or code");

        add = new Button("Add");
        add.addClickListener(event -> {
            if (search.getValue() != null) {
                selected.addBean(players.get(search.getValue()));
                search.clear();
            }
        });

        clear = new Button("Clear");
        clear.addClickListener(event -> {
            selected.removeAllItems();
            selectedTable.markAsDirty();
            search.clear();
        });

        FormLayout searchLayout = new FormLayout(search);

        hLayout = new EHorizontalLayout(searchLayout, add, clear);
        hLayout.setComponentAlignment(searchLayout, Alignment.MIDDLE_CENTER);
        hLayout.setComponentAlignment(add, Alignment.MIDDLE_CENTER);
        hLayout.setComponentAlignment(clear, Alignment.MIDDLE_CENTER);
        hLayout.setMargin(false);

        selectedTable = new ETable("Selected", selected);
        selectedTable.setVisibleColumns("fullName", "federationCode", "club");
        selectedTable.setImmediate(true);
        selectedTable.setPageLength(0);
        selectedTable.setColumnWidth("fullName", 300);
        selectedTable.addStyleName(ValoTheme.TABLE_NO_VERTICAL_LINES);
        selectedTable.addStyleName(ValoTheme.TABLE_COMPACT);
    }

    protected void addMasterPointsField() {
        masterPoints = new TextField("MPs per player (e.g. 1.2)");
        StringToDoubleConverter conv = new StringToDoubleConverter();
        masterPoints.setConverter(conv);
        masterPoints.setValidationVisible(true);
        masterPoints.addValueChangeListener(listener -> {
            masterPoints.setComponentError(null);
        });
        masterPoints.setNullRepresentation("");
        masterPoints.addValidator(value -> {
            Double pts = value == null ? new Double(0) : (Double) value;
            if (pts == 0) {
                throw new InvalidValueException("Master points must nonzero");
            }
        });
        masterPoints.setRequired(true);
        FormLayout masterPointsLayout = new FormLayout(masterPoints);

        hLayout2 = new HorizontalLayout(masterPointsLayout);
        hLayout2.setSpacing(true);
        hLayout2.setComponentAlignment(masterPointsLayout,
                Alignment.MIDDLE_CENTER);
    }

    protected void addCommitButton() {
        commit = new Button("Commit MPs");
        commit.addClickListener(listener -> commitMpsClick());
        hLayout2.addComponent(commit);
        hLayout2.setComponentAlignment(commit, Alignment.MIDDLE_CENTER);
    }

    private void commitMpsClick() {
        Player committer = BridgeUI.user.getCurrentUser().getPlayer();
        Subject subject = SecurityUtils.getSubject();

        // error messages
        // NOTE: every subject who has the role "admin" has also "clubadmin"
        if (masterPoints.isValid() && message.isValid()
                && subject.hasRole("clubadmin") && selected.size() > 0) {
            for (Object id : selected.getItemIds()) {
                // store message
                mpsMessages.add(new MasterPointMessage(players.get(id),
                        (Double) masterPoints.getConvertedValue(), committer,
                        message.getValue()));
                // add mps
                Double oldPts = (Double) players.get(id, "masterPoints");
                Double newPts = (Double) masterPoints.getConvertedValue();
                players.set(id, "masterPoints", oldPts + newPts);
                // MPRegistryView.refreshPlayer(id);
            }
            MPRegistryView.refreshPlayers();

            masterPoints.clear();
            selected.removeAllItems();
            message.clear();

            Notification.show("Master Points updated", Type.HUMANIZED_MESSAGE);

        } else {
            if (!masterPoints.isValid()) {
                masterPoints.setValidationVisible(true);
                masterPoints.setComponentError(new ErrorMessage() {
                    @Override
                    public String getFormattedHtmlMessage() {
                        return "Empty Master Point Field";
                    }

                    @Override
                    public ErrorLevel getErrorLevel() {
                        return ErrorLevel.ERROR;
                    }
                });
            }

            if (!message.isValid()) {
                message.setComponentError(new ErrorMessage() {

                    @Override
                    public String getFormattedHtmlMessage() {
                        return "Empty message";
                    }

                    @Override
                    public ErrorLevel getErrorLevel() {
                        return ErrorLevel.ERROR;
                    }
                });
            }

            if (!subject.hasRole("clubadmin")) {
                Notification.show(
                        "You are not authorized to change master points",
                        Type.ERROR_MESSAGE);
            }

            if (selected.size() == 0) {
                Notification.show("Select MP recipient(s)", Type.ERROR_MESSAGE);
                search.focus();
            }
        }
    }

    @Override
    public void enter(ViewChangeEvent event) {
        addComponents(mainMenu, hLayout, selectedTable, messageFLayout,
                hLayout2);
    }

}
