package com.bridge.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.parboiled.errors.ParseError;
import org.parboiled.support.ParsingResult;
import org.vaadin.easyuploads.MultiFileUpload;

import com.bridge.calendar.WhiteCalendar;
import com.bridge.database.BridgeEvent;
import com.bridge.database.C;
import com.bridge.database.MasterPointMessage;
import com.bridge.database.PbnFile;
import com.bridge.database.Player;
import com.bridge.database.Tournament;
import com.bridge.resultui.TotalScoreTable;
import com.bridge.ui.BridgeUI;
import com.bridge.ui.ETable;
import com.bridge.ui.EVerticalLayout;
import com.bridge.ui.MainMenu;
import com.pbn.ast.Pbn;
import com.pbn.pbnjson.JsonEvents;
import com.pbn.tools.Tools;
import com.vaadin.data.Container.Filter;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.calendar.CalendarComponentEvents.EventClickHandler;

import scala.bridge.MPTools;
import scala.bridge.TableFactory;

@SuppressWarnings("serial")
public class UploadView extends EVerticalLayout implements View {

    public static final String name = "/admin/upload";

    protected WhiteCalendar calendar = WhiteCalendar.getUploadCalendar();
    protected C<Player> players = new C<>(Player.class);
    protected C<MasterPointMessage> mpMessages = new C<>(
            MasterPointMessage.class);

    protected Window window = new Window();
    protected MainMenu mainMenu;
    protected MenuBar calendarMenubar = new MenuBar();
    protected MultiFileUpload up;
    protected Button setFinalResults = new Button("Set Final Result File");
    protected Button remove = new Button("Remove File");
    protected Button cancel = new Button("Cancel");
    protected Button done = new Button("Done");
    protected HorizontalLayout hl = new HorizontalLayout();
    protected BeanItemContainer<PbnFile> pbnc = new BeanItemContainer<>(
            PbnFile.class);
    protected ETable table = new ETable("Result Files", pbnc);
    protected Object tourId = null;
    protected Object tableSelection = null;
    protected ComboBox clubs;
    protected EVerticalLayout winLayout = new EVerticalLayout();
    protected TotalScoreTable fileResults = null;

    public UploadView(MainMenu mainMenu) {
        this.mainMenu = mainMenu;
        addFileClickListeners();
        addEventClickHandler();
        addFileUpload();
        doTableSettings();
    }

    /***
     * addPbnFilePreview adds a table to view TotalScoreTable
     */

    protected TotalScoreTable addPbnFilePreview() {
        if (tableSelection != null) {
            PbnFile file = pbnc.getItem(tableSelection).getBean();
            TableFactory f = new TableFactory(file.getFileLines(), true);
            return new TotalScoreTable(f);
        } else {
            return null;
        }
    }

    protected void doTableSettings() {
        table.with("name", "masterPoints", "finalResults").headers("File Name",
                "Contains MPs", "Final Results");
        table.setWidth("570px");
        table.setSelectable(true);
        table.addStyleName("pbntable");
        table.setPageLength(0);
        table.addItemClickListener(event -> {
            if (tableSelection != event.getItemId()) {
                tableSelection = event.getItemId();

                if (fileResults != null) {
                    winLayout.removeComponent(fileResults);
                }
                PbnFile file = pbnc.getItem(tableSelection).getBean();
                TableFactory fac = new TableFactory(file.getFileLines(), true);
                fileResults = new TotalScoreTable(fac);
                fileResults.setPageLength(7);
                winLayout.addComponentAsFirst(fileResults);
                winLayout.setComponentAlignment(fileResults,
                        Alignment.MIDDLE_CENTER);
                window.center();
            }
        });
    }

    // private String pbnToString(List<String> pbn) {
    // StringBuilder builder = new StringBuilder("");
    // for (String line : pbn) {
    // builder.append(line + "\r\n");
    // }
    // return builder.toString();
    // }

    private String errorMessage(ParsingResult<Pbn> result, String fileName) {
        ParseError error = result.parseErrors.get(0);
        int s = error.getStartIndex();
        int line = error.getInputBuffer().getPosition(s).line;
        StringBuilder builder = new StringBuilder("");
        builder.append("The result file ***" + fileName
                + "*** contains an error at line " + line + " and column "
                + error.getInputBuffer().getPosition(s).column
                + ". The erroneous text may occur earlier in the line.");
        return builder.toString();
    }

    protected void addFileUpload() {

        up = new MultiFileUpload() {
            @Override
            protected void handleFile(File file, String fileName,
                    String mimeType, long length) {
                if (length <= 1000000) {
                    try {
                        if (pbnc.size() > 10) {
                            table.setPageLength(10);
                        }
                        BufferedReader breader = new BufferedReader(
                                new InputStreamReader(new FileInputStream(file),
                                        "iso-8859-1"));
                        boolean end = false;
                        List<String> pbnList = new ArrayList<>();
                        StringBuilder builder = new StringBuilder("");
                        // build a pbn list and string
                        while (!end) {
                            String line = breader.readLine();
                            if (line != null) {
                                pbnList.add(line);
                                builder.append(line + "\r\n");
                            }
                            end = line == null;
                        }

                        ParsingResult<Pbn> result = Tools
                                .getPbnResult(builder.toString());
                        // PBN syntax check
                        if (!result.matched) {
                            Notification.show(errorMessage(result, fileName),
                                    Type.ERROR_MESSAGE);
                        } else {
                            String json = Tools.toJson(result);
                            JsonEvents jevents = new JsonEvents(json);
                            // this is accomplished in the pbnparser project
                            // TableFactory factory = new TableFactory(pbnList,
                            // true);
                            if (jevents.totalScoreTableExists()) {
                                pbnc.addBean(new PbnFile(fileName,
                                        jevents.hasMasterPoints(), pbnList,
                                        jevents.masterpointRegistry(), json));
                            } else {
                                Notification.show(
                                        "The file did not contain TotalScoreTable",
                                        Type.ERROR_MESSAGE);
                            }

                            // if (factory.totalScoreSupported()) {
                            // MPTools tools = new MPTools(
                            // factory.events().head());
                            // @SuppressWarnings("unused")
                            // Object id = pbnc.addBean(new PbnFile(fileName,
                            // factory.hasMP(), pbnList,
                            // tools.posMpsEarned(), json));
                            // } else {
                            // Notification.show(
                            // "The file did not contain TotalScoreTable",
                            // Type.ERROR_MESSAGE);
                            // }

                        }
                        breader.close();
                    } catch (FileNotFoundException e) {
                    } catch (IOException e) {
                    }
                } else {
                    Notification.show("Too big file (>1000kB)",
                            Type.ERROR_MESSAGE);
                }
            }

        };
    }

    protected void commitMPMessage(Player receiver, Float mps,
            Player committer) {

    }

    /***
     * updatePlayerMps adds or subtracts the master points (if) found in the
     * result file and commits a message of the transaction. This method also
     * adds MasterPointMessage for each update
     */

    protected void updatePlayerMps() {
        // subtract first removed players' mps and then add mps to new players
        C<Tournament> ts = new C<>(Tournament.class);
        C<MasterPointMessage> messages = new C<>(MasterPointMessage.class);
        messages.c().setReadOnly(true);
        Player committer = BridgeUI.user.getCurrentUser().getPlayer();

        List<PbnFile> oldFiles = ts.get(tourId).getPbnFiles();
        // find old final results file if any and remove the master points
        for (PbnFile f : oldFiles) {
            if (f.getMasterPoints() && !pbnc.getItemIds().contains(f)
                    && f.getFinalResults()) {
                for (Object key : f.getFedCodeToMps().keySet()) {
                    // federation code is unique and must exist
                    Filter fil = players.filterEq("federationCode", key);
                    // update master points
                    if (players.size() == 1) {
                        Double oldPts = players.at(0).getMasterPoints();
                        // NOTE negative sign!
                        Double addedMPs = f.getFedCodeToMps().get(key);
                        players.set(players.at(0).getId(), "masterPoints",
                                oldPts - f.getFedCodeToMps().get(key));
                        // commit master point update message
                        messages.add(new MasterPointMessage(players.get(0),
                                -addedMPs, committer, ""));
                        // refresh MPRegistryView container
                        // (players.at(0).getId());
                        players.removeFilter(fil);
                    }
                }
                break; // at most one final score file
            }
        }

        // find new final result file if any and add master points
        for (PbnFile f : pbnc.getItemIds()) {
            if (f.getMasterPoints() && !oldFiles.contains(f)
                    && f.getFinalResults()) {
                for (Object key : f.getFedCodeToMps().keySet()) {
                    // federation code is unique and must exist
                    Filter fil = players.filterEq("federationCode", key);
                    if (players.size() == 1) {
                        Double oldPts = players.at(0).getMasterPoints();
                        Double newMPs = f.getFedCodeToMps().get(key);
                        players.set(players.at(0).getId(), "masterPoints",
                                oldPts + newMPs);
                        // commit master point update messsage
                        // TODO: add competion name
                        messages.add(new MasterPointMessage(players.get(0),
                                newMPs, committer, ""));
                        // MPRegistryView.refreshPlayer(players.at(0).getId());
                        players.removeFilter(fil);
                    }
                }
                break; // at most one final score file
            }
        }
        // update players container to reflect the mp changes
        MPRegistryView.refreshPlayers();
    }

    /***
     * getPlayerFedCodes returns the found federation code
     */

    protected HashSet<String> getPlayerFedCodes(PbnFile f) {
        if (f != null) {
            HashSet<String> ids = new HashSet<>();
            TableFactory fac = new TableFactory(f.getFileLines(), true);
            MPTools tools = new MPTools(fac.events().head());
            ids.addAll(tools.allIds());
            return ids;
        } else {
            return new HashSet<>();
        }
    }

    /***
     * getCurrentTotalResultsFile returns the first found total results file in
     * pbnc if found and otherwise returns null
     */

    protected PbnFile getNewFinalResultsFile() {
        PbnFile file = null;
        for (PbnFile f : pbnc.getItemIds()) {
            if (f.getFinalResults()) {
                file = f;
                break;
            }
        }
        return file;
    }

    /***
     * getOldTotalResultsFile returns the first found total results file from
     * the DB
     */

    protected PbnFile getOldFinalResultsFile() {
        PbnFile file = null;
        C<Tournament> ts = new C<>(Tournament.class);
        for (PbnFile f : ts.get(tourId).getPbnFiles()) {
            if (f.getFinalResults()) {
                file = f;
                break;
            }
        }
        return file;
    }

    /***
     * updatePlayerParticipations checks which players played in the tournament
     * and updates the DB connections accordingly for example when new players
     * are added or old players removed NOTE: only the players in the total
     * results file are used
     */

    protected void updatePlayerParticipations() {
        HashSet<String> oldFeds = getPlayerFedCodes(getOldFinalResultsFile());
        HashSet<String> newFeds = getPlayerFedCodes(getNewFinalResultsFile());
        HashSet<String> add = new HashSet<>(); // new not in old codes

        for (String code : newFeds) {
            if (!oldFeds.contains(code)) {
                add.add(code);
            }
            oldFeds.remove(code);
        }

        // oldFeds contains now fed codes not in newFeds

        removePlayerParticipation((Long) tourId, oldFeds);
        List<Player> pls = addPlayerParticipation((Long) tourId, add);

        // update Tournament entity accordingly
        removeTournamentParticipation(oldFeds);
        addTournamentParticipation(pls);
    }

    /***
     * remove players with these fed codes from the tournament
     */

    protected void removeTournamentParticipation(HashSet<String> oldFeds) {
        C<Tournament> ts = new C<>(Tournament.class);
        List<Player> pls = ts.get(tourId).getParticipatedPlayers();
        pls.stream().filter(p -> !oldFeds.contains(p.getFederationCode()))
                .collect(Collectors.toList());
        ts.set(tourId, "participatedPlayers", pls);
    }

    /***
     * addTournamentParticipation adds the players to the tournament
     */

    protected void addTournamentParticipation(List<Player> add) {
        C<Tournament> ts = new C<>(Tournament.class);
        List<Player> old = ts.get(tourId).getParticipatedPlayers();
        old.addAll(add);
        ts.set(tourId, "participatedPlayers", old);
    }

    /***
     * removeTournament removes this tournament from the players
     */

    protected void removePlayerParticipation(Long tourId,
            HashSet<String> fedCodes) {
        BridgeUI.o("removeParticipation size: " + fedCodes.size());
        for (String code : fedCodes) {
            Filter f = players.filterEq("federationCode", code);
            if (players.size() == 1) {
                Set<Tournament> played = players.at(0).getPlayedTournaments();

                // preserve only tours whose id differ from tourId
                Set<Tournament> s = played.stream()
                        .filter(t -> t.getId() != tourId)
                        .collect(Collectors.toSet());
                players.set(0, "playedTournaments", s);
            }
            players.removeFilter(f);
        }
    }

    /***
     * addParticipation adds the tour to player entities that were found by fed
     * codes; returns the found players
     */

    protected List<Player> addPlayerParticipation(Long tourId,
            HashSet<String> fedCodes) {
        List<Player> pls = new ArrayList<>();
        C<Tournament> ts = new C<>(Tournament.class);
        BridgeUI.o("addParticipation size: " + fedCodes.size());
        for (String code : fedCodes) {
            Filter f = players.filterEq("federationCode", code);
            if (players.size() == 1) {
                pls.add(players.at(0));
                Set<Tournament> played = players.at(0).getPlayedTournaments();
                played.add(ts.get(tourId));
                // preserve only tours whose id differ from tourId
                players.set(0, "playedTournaments", played);
            }
            players.removeFilter(f);
        }
        return pls;
    }

    @SuppressWarnings("unchecked")
    protected void addFileClickListeners() {

        setFinalResults.addClickListener(listener -> {
            if (tableSelection != null) {
                for (Object f : pbnc.getItemIds()) {
                    pbnc.getItem(f).getItemProperty("finalResults")
                            .setValue(f == tableSelection);
                }

            } else {
                Notification.show(
                        "To select final result file "
                                + "click the file in the table",
                        Type.ERROR_MESSAGE);
            }
        });

        // remove selected file from table
        remove.addClickListener(listener -> {
            if (tableSelection != null) {
                pbnc.removeItem(tableSelection);
                tableSelection = null;
                winLayout.removeComponent(fileResults);
                tableSelection = null;
            }
        });

        cancel.addClickListener(listener -> {
            window.close();
        });

        // update results
        done.addClickListener(event -> {

            if (finalResultsFound()) {
                // update Player and Tournament entities to reflect
                // possibly changed situation
                updatePlayerMps();
                updatePlayerParticipations();
                // set new pbn file(s)

                C<Tournament> ts = new C<>(Tournament.class);
                List<PbnFile> list = new ArrayList<>();
                if (pbnc.size() > 0) {
                    list = pbnc.getItemIds();
                }

                ts.set(tourId, "pbnFiles", list);
                // update json strings of the corresponding pbn files

                BridgeUI.o("ts size after commit and refresh in done "
                        + ts.get(tourId).getPbnFiles().size());
                BridgeUI.o("pbnc size in tournament setting: "
                        + pbnc.getItemIds().size());

                pbnc.removeAllItems();
                window.close();
            } else {
                Notification.show("Set Final Results File", Type.ERROR_MESSAGE);
            }
        });
    }

    // check that exactly one file is total result file
    private boolean finalResultsFound() {
        boolean found = false;
        for (PbnFile f : pbnc.getItemIds()) {
            if (f.getFinalResults()) {
                found = true;
            }
        }
        return found || pbnc.size() == 0;
    }

    /***
     * createFilesWindowContents creates a dialog widgets for the uploading
     ***/

    protected void createFilesWindowContents(BridgeEvent e) {
        C<Tournament> ts = new C<>(Tournament.class);
        tourId = e.getTournament().getId();
        ts = new C<>(Tournament.class);
        List<PbnFile> ls = ts.get(tourId).getPbnFiles();
        table.removeAllItems();
        pbnc.addAll(ls);
        HorizontalLayout buttons = new HorizontalLayout(setFinalResults, remove,
                cancel, done);
        buttons.setSpacing(true);
        winLayout = new EVerticalLayout(table, up, buttons);
        winLayout.setComponentAlignment(buttons, Alignment.MIDDLE_RIGHT);
        winLayout.setComponentAlignment(table, Alignment.MIDDLE_CENTER);
        hl.setSpacing(true);

        window.setModal(true);
        window.setContent(winLayout);
        window.center();
        window.setClosable(false);
    }

    protected void addEventClickHandler() {
        calendar.setHandler((EventClickHandler) event -> {
            window.setClosable(false);
            window.addStyleName("uploadWindow");
            window.setCaption("File Upload Window");
            window.setWidth("900px");
            BridgeEvent e = (BridgeEvent) event.getCalendarEvent();
            createFilesWindowContents(e);
            getUI().addWindow(window);
        });
    }

    @SuppressWarnings("unused")
    private void resetPbnContainer() {
        pbnc.removeAllContainerFilters();
        pbnc.removeAllItems();
    }

    @Override
    public void enter(ViewChangeEvent event) {
        addComponents(mainMenu, calendar.getCompositeCalendar());
    }

}
