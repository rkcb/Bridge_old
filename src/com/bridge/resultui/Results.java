package com.bridge.resultui;

import java.util.List;

import com.bridge.database.C;
import com.bridge.database.PbnFile;
import com.bridge.database.Tournament;
import com.bridge.ui.EVerticalLayout;
import com.pbn.pbnjson.JsonEvents;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;

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
		C<Tournament> tournaments = new C<>(Tournament.class);
		List<PbnFile> pbns = tournaments.get(tourId).getPbnFiles();
		tabs = new Tab[pbns.size()];

		for (PbnFile pbn : pbns) {
			if (pbn.getFinalResults()) {

				// TableFactory factory = new TableFactory(pbn.getFileLines(),
				// false);
				JsonEvents jevents = new JsonEvents(pbn.getJson());

				String t = jevents.competion(); // factory.competitionType();
				// if (t.matches("team")) {
				if (t.matches("Teams")) {
					ResultsTabTeam tab = new ResultsTabTeam(jevents);
					tabs[0] = contents.addTab(tab);
					// } else if (t.matches("individual") || t.matches("pair"))
					// {
				} else if (t.matches("Individuals") || t.matches("Pairs")) {
					ResultsTabNotTeam tab = new ResultsTabNotTeam(jevents);
					tabs[0] = contents.addTab(tab);
				}
				// if (factory.eventDescription().length() > 0) {
				if (jevents.eventDescription().length() > 0) {
					tabs[0].setCaption(jevents.eventDescription());
				} else {
					tabs[0].setCaption("Main Results");
				}
			}
		}

		int s = 1;

		for (PbnFile pbn : pbns) {
			if (!pbn.getFinalResults()) {
				// TableFactory fac = new TableFactory(pbn.getFileLines(),
				// false);
				JsonEvents jevents = new JsonEvents(pbn.getJson());
				// String t = fac.competitionType();
				String t = jevents.competion();
				if (t.matches("team")) {
					ResultsTabTeam tab = new ResultsTabTeam(jevents);
					tabs[s] = contents.addTab(tab);
				} else if (t.matches("indi") || t.matches("pair")) {
					ResultsTabNotTeam tab = new ResultsTabNotTeam(jevents);
					tabs[s] = contents.addTab(tab);
				}
				if (jevents.eventDescription().length() > 0) {
					tabs[s].setCaption(jevents.eventDescription());
				} else {
					tabs[s].setCaption("Other Results " + s);
				}
				s++;
			}
		}

	}

}
