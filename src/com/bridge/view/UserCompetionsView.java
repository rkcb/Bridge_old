package com.bridge.view;

import java.util.Calendar;
import java.util.Date;

import com.bridge.calendar.BridgeCompetionReader;
import com.bridge.calendar.Competitors;
import com.bridge.calendar.WhiteCalendar;
import com.bridge.database.BridgeEvent;
import com.bridge.ui.BridgeUI;
import com.bridge.ui.EVerticalLayout;
import com.bridge.ui.MainMenu;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.EventClickHandler;

@SuppressWarnings("serial")
public class UserCompetionsView extends EVerticalLayout implements View {

    public static final String name = "/user/competions";

    private MainMenu mainMenu;
    private WhiteCalendar calendar = WhiteCalendar.getTournamentCalendar();
    private Window window;
    private BridgeCompetionReader reader;
    private Competitors competitors = null;
    private TabSheet contents = null;
    private Tab readerDialogTab = null;
    private Tab competitorsTab = null;
    private BridgeEvent bridgeEvent;

    public UserCompetionsView(MainMenu menu) {
        setCaption("Competitors");
        mainMenu = menu;
        setEventClickHandler();
    }

    /***
     * setEventClickHandler defines action for an event click
     */
    private void setEventClickHandler() {
        calendar.setHandler((EventClickHandler) event -> {
            if (event.getCalendarEvent() instanceof BridgeEvent) {
                window = new Window();
                reader = new BridgeCompetionReader(window,
                        getUI().getNavigator());
                bridgeEvent = (BridgeEvent) event.getCalendarEvent();
                contents = new TabSheet();
                initEventTab();

                if (bridgeEvent.isRegistration()
                        && !registrationExpired(bridgeEvent)) {
                    boolean anonymous = !BridgeUI.user.isSignedIn();
                    initCompetitorsTab(anonymous);
                }
                initWindow();
            }
        });
    }

    private boolean registrationExpired(BridgeEvent e) {
        java.util.Calendar cal = Calendar.getInstance();
        Date date = e.getSignInEnd();
        date = date == null ? e.getEnd() : date;

        boolean isExpired = date == null ? true : date.before(cal.getTime());
        return isExpired;
    }

    private void initEventTab() {
        readerDialogTab = contents.addTab(reader);
        readerDialogTab.setCaption("Event");
        reader.initialize(bridgeEvent);
    }

    private void initCompetitorsTab(boolean readOnly) {
        if (competitorsTab != null) {
            contents.removeTab(competitorsTab);
        }

        competitors = new Competitors();
        competitors.initialize(bridgeEvent, BridgeUI.user.getCurrentUser(),
                readOnly);
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
        addComponents(mainMenu, calendar.getCompositeCalendar());
        calendar.addSearchFilters();
    }

}
