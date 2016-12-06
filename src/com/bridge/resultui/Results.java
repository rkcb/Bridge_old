package com.bridge.resultui;

import java.util.List;

import com.bridge.database.C;
import com.bridge.database.PbnFile;
import com.bridge.database.Tournament;
import com.bridge.ui.EVerticalLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;

import scala.bridge.TableFactory;

@SuppressWarnings("serial")
public class Results extends EVerticalLayout {

    public static final String name = "/result";

    private TabSheet contents = new TabSheet();

    private Long tourId = null;
    private Tab[] tabs = null;

    public Results(Long tourId) {
        this.tourId = tourId;
        createTabs();
        addComponent(contents);
    }

    /***
     * createTabs creates a tab for each pbn file
     */

    protected void createTabs() {
        C<Tournament> ts = new C<>(Tournament.class);
        List<PbnFile> pbns = ts.get(tourId).getPbnFiles();
        tabs = new Tab[pbns.size()];

        for (PbnFile pbn : pbns) {
            if (pbn.getFinalResults()) {
                TableFactory fac = new TableFactory(pbn.getFileLines(), false);
                String t = fac.competitionType();
                if (t.matches("team")) {
                    ResultsTabTeam tab = new ResultsTabTeam(fac);
                    tabs[0] = contents.addTab(tab);
                } else if (t.matches("individual") || t.matches("pair")) {
                    ResultsTabNotTeam tab = new ResultsTabNotTeam(fac);
                    tabs[0] = contents.addTab(tab);
                }
                if (fac.eventDescription().length() > 0) {
                    tabs[0].setCaption(fac.eventDescription());
                } else {
                    tabs[0].setCaption("Main Results");
                }
            }
        }

        int s = 1;

        for (PbnFile pbn : pbns) {
            if (!pbn.getFinalResults()) {
                TableFactory fac = new TableFactory(pbn.getFileLines(), false);
                String t = fac.competitionType();
                if (t.matches("team")) {
                    ResultsTabTeam tab = new ResultsTabTeam(fac);
                    tabs[s] = contents.addTab(tab);
                } else if (t.matches("indi") || t.matches("pair")) {
                    ResultsTabNotTeam tab = new ResultsTabNotTeam(fac);
                    tabs[s] = contents.addTab(tab);
                }
                if (fac.eventDescription().length() > 0) {
                    tabs[s].setCaption(fac.eventDescription());
                } else {
                    tabs[s].setCaption("Other Results " + s);
                }
                s++;
            }
        }

    }

}
