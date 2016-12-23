package com.bridge.calendar;

import org.apache.shiro.SecurityUtils;
import org.vaadin.maddon.MBeanFieldGroup;
import org.vaadin.maddon.fields.MTextArea;
import org.vaadin.maddon.fields.MTextField;
import org.vaadin.maddon.layouts.MFormLayout;
import org.vaadin.maddon.layouts.MHorizontalLayout;
import org.vaadin.maddon.layouts.MVerticalLayout;

import com.bridge.database.BridgeEvent;
import com.bridge.database.C;
import com.bridge.database.Tournament;
import com.bridge.ui.BridgeUI;
import com.bridge.view.LoginView;
import com.bridge.view.ResultsView;
import com.vaadin.navigator.Navigator;
import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class BridgeCompetionReader extends MVerticalLayout {

    protected MTextArea description = new MTextArea("Description");
    protected MTextField caption = new MTextField("Caption"); // competition
                                                              // name
    protected MTextField town = new MTextField("Town");
    protected MTextField country = new MTextField("Country");
    protected DateField start = new DateField("Start"); // competion start time
    protected DateField end = new DateField("End"); // competion end time
    protected MTextField price = new MTextField("Price");
    protected DateField signInStart = new DateField("Sign in start");
    protected DateField signInEnd = new DateField("Sign in end");
    protected ComboBox type = new ComboBox("Type");
    protected CheckBox registration = new CheckBox("Registration required");
    protected CheckBox privateEvent = new CheckBox("Private event");
    protected CheckBox masterPoint = new CheckBox("Master point competion");
    protected MFormLayout fl1 = new MFormLayout();
    protected MFormLayout fl2 = new MFormLayout();
    // protected MVerticalLayout directorsLayout = new MVerticalLayout();
    // protected MVerticalLayout organizersLayout = new MVerticalLayout();
    private MBeanFieldGroup<BridgeEvent> bfg;

    private Window dialog;
    // private Button exit = new Button("Exit");
    // private Button results = new Button("Sign2 in to register");
    private Button results = new Button("Results");
    private Button signIn = new Button("Sign In");

    private VerticalLayout vLayout = new VerticalLayout();
    private MHorizontalLayout form = new MHorizontalLayout();
    private MHorizontalLayout buttons = new MHorizontalLayout();
    private Tournament tour = null;
    private BridgeEvent event;

    private Navigator navigator;

    public BridgeCompetionReader(Window win, Navigator navigator) {
        this.navigator = navigator;
        dialog = win;
        signInStart.setResolution(Resolution.MINUTE);
        signInEnd.setResolution(Resolution.MINUTE);
        start.setResolution(Resolution.MINUTE);
        end.setResolution(Resolution.MINUTE);

        caption.setRequired(true);

        start.setRequired(true);
        end.setRequired(true);
        type.setRequired(true);

        setFieldsReadOnly();
        insertFields();
    }

    private void insertFields() {
        form.withMargin(true).removeAllComponents();
        fl1 = new MFormLayout().withMargin(true);
        fl2 = new MFormLayout().withMargin(true);
        fl1.addComponents(caption, town, signInStart, type, registration,
                privateEvent, masterPoint);
        fl2.addComponents(description, country, signInEnd, end, price);
        form.addComponents(fl1, fl2);
        addButtonsAndActions();
        addComponents(form, buttons);
    }

    private void addButtonsAndActions() {
        results.addClickListener(listener -> {
            if (tour != null) {
                ResultsView.loadResults(tour.getId());
            }
            BridgeUI.getCurrent().getNavigator().navigateTo(ResultsView.name);
            ResultsView.loadResults(tour.getId());
            dialog.close();
        });

        signIn.addClickListener(listener -> {
            dialog.close();
            navigator.navigateTo(LoginView.name);
        });

        buttons.addComponents(results, signIn);
        vLayout.addComponent(buttons);
        vLayout.setComponentAlignment(buttons, Alignment.MIDDLE_CENTER);
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
        privateEvent.setReadOnly(s);
        masterPoint.setReadOnly(s);
    }

    /***
     * initialize initializes the dialog with the clicked event values
     */

    public void initialize(BridgeEvent e) {
        bfg = new MBeanFieldGroup<>(BridgeEvent.class);
        bfg.setItemDataSource(e);
        bfg.buildAndBindMemberFields(this);

        event = e;
        setFieldsReadOnly();

        tour = e.getTournament();
        setSignInButtonState();
        setResultsVisibility();
    }

    private void setResultsVisibility() {
        C<Tournament> ts = new C<>(Tournament.class);
        boolean isEmpty = ts.get(tour.getId()).getPbnFiles().isEmpty();
        results.setEnabled(!isEmpty);
    }

    private void setSignInButtonState() {
        boolean a = SecurityUtils.getSubject().isAuthenticated();
        signIn.setVisible(event.isRegistration() && !a);
    }
}
