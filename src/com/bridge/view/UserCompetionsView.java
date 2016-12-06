package com.bridge.view;

import com.bridge.calendar.BridgeCompetionReader;
import com.bridge.calendar.Competitors;
import com.bridge.database.BridgeEvent;
import com.bridge.newcalendar.WhiteUserCompetionCalendar;
import com.bridge.ui.BridgeUI;
import com.bridge.ui.EVerticalLayout;
import com.bridge.ui.MainMenu;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.Window;

@SuppressWarnings("serial")
public class UserCompetionsView extends EVerticalLayout implements View {

    public static final String name = "/user/competions";

    private MainMenu mainMenu;
    private WhiteUserCompetionCalendar calendar = new WhiteUserCompetionCalendar();
    private Window window;
    private BridgeCompetionReader reader;
    private Competitors competitors = null;
    private TabSheet contents = null;
    private Tab readerDialogTab = null;
    private Tab competitorsTab = null;
    private BridgeEvent ev;

    public UserCompetionsView(MainMenu menu) {
        setCaption("Competitors");
        mainMenu = menu;
        setEventClickHandler();
    }

    private void setEventClickHandler() {
        calendar.setEventHandler(event -> {
            if (event.getCalendarEvent() instanceof BridgeEvent) {
                window = new Window();
                reader = new BridgeCompetionReader(window,
                        getUI().getNavigator());
                ev = (BridgeEvent) event.getCalendarEvent();
                contents = new TabSheet();
                initEventTab();

                if (ev.isRegistration()) {
                    boolean anonymous = !BridgeUI.user.isSignedIn();
                    initCompetitorsTab(anonymous);
                }
                initWindow();
            }
        });
    }

    private void initEventTab() {
        readerDialogTab = contents.addTab(reader);
        readerDialogTab.setCaption("Event");
        reader.initialize(ev);
    }

    private void initCompetitorsTab(boolean readOnly) {
        if (competitorsTab != null) {
            contents.removeTab(competitorsTab);
        }

        competitors = new Competitors();
        competitors.initialize(ev, BridgeUI.user.getCurrentUser(), readOnly);
        competitorsTab = contents.addTab(competitors);
        competitorsTab.setCaption("Competitors");
        contents.setSelectedTab(readerDialogTab);
    }

    private void initWindow() {
        EVerticalLayout l = new EVerticalLayout(contents);
        l.setSizeUndefined();
        window.setContent(l);
        window.setModal(true);
        window.setPosition(30, 30);

        getUI().addWindow(window);
    }

    @Override
    public void enter(ViewChangeEvent event) {
        addComponents(mainMenu, calendar);
        calendar.addClubSelector();
        calendar.refreshSelectedClub();
        // Object id = BridgeUI.user.getCurrentClubId();
        // TODO: all competions shown -- filter some?
    }

}
