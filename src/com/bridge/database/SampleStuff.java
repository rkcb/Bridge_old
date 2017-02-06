package com.bridge.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import com.bridge.calendar.TournamentManager;
import com.bridge.database.Tournament.Type;
import com.bridge.newcalendar.TournamentCalendar;
import com.bridge.ui.BridgeUI;

/***
 * SampleStuff creates various database items -- note that a user must be signed
 * in to create a BridgeEvent or Tournament because
 */

public class SampleStuff {

    private static Hashtable<String, ShiroRole> roleMap = new Hashtable<>(3);
    private static C<ShiroRole> roles = new C<>(ShiroRole.class);
    private static C<Player> players = new C<>(Player.class);
    private static C<Club> clubs = new C<>(Club.class);
    private static C<User> users = new C<>(User.class);

    public static void create() {

        createRoles();
        createClubs();

        // createSamplePlayers(0);
        createUser("basic", "basic", 0);
        createUser("clubadmin", "clubadmin", 0);
        createUser("admin", "admin", 0);
        createUser("admin2", "admin", 1);
        // createEscobar("admin2", "admin");
        // createFromPbns("team.pbn");
        // createFromPbns("sm1.pbn");

        // createFromPbns("impcross.pbn");
        // createFromPbns("indi.pbn");
        // createFromPbns("div2.pbn");
        // createFromPbns("butler.pbn");
        // // A user must be signed in to create a tournament

        createBridgeEvent();
        createTeamTour();
        // BridgeUI.user.logout();
        // createIndy();
        // createMembershipApplications();
        //
    }

    public static void adminlogin() {
        UsernamePasswordToken token = new UsernamePasswordToken("admin", "xxx");
        Subject subject = SecurityUtils.getSubject();
        try {
            subject.login(token);
        } catch (AuthenticationException e) {
        }
    }

    // private static void createFromPbns(String fileName) {
    // String pre = "/home/esa/pbn/";
    // File f = new File(pre + fileName);
    // List<String> l = null;
    // try {
    // BufferedReader br = null;
    // try {
    // br = new BufferedReader(new InputStreamReader(
    // new FileInputStream(f), "iso-8859-1"));
    // } catch (UnsupportedEncodingException e1) {
    // e1.printStackTrace();
    // } catch (FileNotFoundException e1) {
    // e1.printStackTrace();
    // }
    //
    // boolean end = false;
    // l = new ArrayList<>();
    // while (!end) {
    // String line = null;
    // try {
    // line = br.readLine();
    //
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // if (line != null) {
    // l.add(line);
    // }
    // end = line == null;
    // }
    // try {
    // br.close();
    // } catch (IOException e) {
    // e.printStackTrace();
    // }
    // } finally {
    // }
    //
    // createPlayersFromPbnFile(l);
    //
    // }

    //
    // try {
    // br.close();
    // } catch (IOException e) { e.printStackTrace(); }
    // } catch (UnsupportedEncodingException e) {
    // e.printStackTrace();
    // } catch (FileNotFoundException e) {
    // e.printStackTrace();
    // }
    //
    // final String pw = BridgeUI.pwService.encryptPassword("xxx");

    // }
    // private static void createPlayersFromPbnFile(List<String> l) {
    //
    // final String pw = BridgeUI.pwService.encryptPassword("xxx");
    // if (l.size() > 0) {
    // TableFactory fac = new TableFactory(l, true);
    // MPTools tools = new MPTools(fac.events().head());
    // for (String id : tools.allIds()) {
    // Pattern p = Pattern.compile("\\d+");
    // Matcher m = p.matcher(id);
    // if (m.find()) {
    // String s = m.group(0);
    // SampleStuff.createPlayer(s, pw);
    // }
    // }
    // }
    // }

    @SuppressWarnings("unused")
    private static void createIndy() {
        C<Indy> c = new C<>(Indy.class);
        if (c.size() == 0) {
            C<Player> ps = new C<>(Player.class);
            C<Tournament> ts = new C<>(Tournament.class);

            Indy i = new Indy(ps.at(0), ts.at(0), "");
            c.add(i);
            c.commit();
        }
    }

    @SuppressWarnings("unused")
    private static void createPair() {
        C<Team> c = new C<>(Team.class);
        if (c.size() == 0) {
            C<Player> ps = new C<>(Player.class);
            C<Tournament> ts = new C<>(Tournament.class);

            Pair p = new Pair(ps.at(0), ps.at(1), ts.at(0), "");
        }
    }

    public static void createTeamTour(/* String fileName */) {

        // UsernamePasswordToken token = new UsernamePasswordToken("admin",
        // "xxx");
        // Subject subject = SecurityUtils.getSubject();
        // try {
        // subject.login(token);
        // } catch (AuthenticationException e) {
        // }

        C<Tournament> tournaments = new C<>(Tournament.class);
        C<BridgeEvent> events = new C<>(BridgeEvent.class);

        if (tournaments.size() == 0) {

            TournamentCalendar cal = new TournamentCalendar();

            BridgeEvent event = new BridgeEvent("Tour", "Description", true);
            event.setRegistration(true);
            event.setType(Type.Team);

            TournamentManager m = new TournamentManager(cal);
            Tournament tournament = new Tournament();
            C<Player> players = new C<>(Player.class);

            Set<Player> s = new HashSet<>();

            s.add(players.at(0));
            s.add(players.at(1));

            tournament.setDirectors(s);
            tournament.setOrganizers(s);

            Object tid = m.createTournament(event, tournament, clubs.at(0));

            tournaments.set(tid, "owner", clubs.at(0));

            // add pbn lines

            // PbnFile file = new PbnFile();
            // file.setFinalResults(true);
            // file.setName(fileName);
            //
            // TableFactory fac = new TableFactory(getLines(fileName), true);
            // MPTools tools = new MPTools(fac.events().head());
            // file.setMasterPoints(fac.hasMP());
            // file.setFileLines(getLines(fileName));
            // // pbn file lines
            //
            //
            //
            // ArrayList<PbnFile> fileList = new ArrayList<PbnFile>();
            // fileList.add(file);
            // ts.set(tid, "pbnFiles", fileList);

            Object eid = tournaments.get(tid).getCalendarEvent().getId();
            events.set(eid, "owner", clubs.at(0));

            // subject.logout();

            // ts.set(tid, "pbnFiles", pbns);

        }
    }

    /***
     * createBridgeEvent creates a calendar event for a club NOTE: clubs must be
     * nonempty
     */
    public static void createBridgeEvent() {
        C<BridgeEvent> events = new C<>(BridgeEvent.class);
        if (events.size() == 0) {
            Calendar c = Calendar.getInstance();
            Object id = events.add(new BridgeEvent("Caption", "description",
                    c.getTime(), c.getTime()));
            Club club = clubs.at(0);

            events.set(id, "owner", club);
            events.set(id, "registration", true);
        }

    }

    private static void createClubs() {
        C<Club> cs = new C<>(Club.class);
        if (cs.size() == 0) {
            cs.add(new Club("B52", "Helsinki", false));
            cs.add(new Club("H6", "Turku", false));
            cs.add(new Club("Federation", "Helsinki", true));
        }
    }

    private static void createRoles() {
        C<ShiroRole> rs = new C<>(ShiroRole.class);

        if (rs.size() == 0) {
            Object id = rs.add(new ShiroRole("basic", "Basic User"));
            roleMap.put("basic", rs.get(id));
            id = rs.add(new ShiroRole("clubadmin", "Club Administrator"));
            roleMap.put("clubadmin", rs.get(id));
            id = rs.add(new ShiroRole("admin", "System Administrator"));
            roleMap.put("admin", rs.get(id));
        }
    }

    private static Set<ShiroRole> roles(String... r) {
        Set<ShiroRole> s = new HashSet<>();
        for (String x : r) {
            s.add(roleMap.get(x));
        }
        return s;
    }

    @SuppressWarnings("unused")
    private static List<String> getLines(String fileName) {
        Path p = Paths.get("/home/esa/pbn/" + fileName);
        Charset charset = Charset.forName("ISO-8859-1");
        List<String> contents = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(p, charset)) {
            String line = null;

            while ((line = reader.readLine()) != null) {
                contents.add(line);
            }
        } catch (IOException x) {
            System.err.format("IOException: %s%n", x);
        }
        return contents;
    }

    @SuppressWarnings("unused")
    private static void createSamplePlayers(int n) {
        C<ShiroRole> rs = new C<>(ShiroRole.class);
        C<Player> ps = new C<>(Player.class);
        C<Club> cs = new C<>(Club.class);
        C<User> us = new C<>(User.class);

        if (n > 0 && !rs.empty() && !cs.empty()) {
            for (int j = 0; j < n; j++) {
                String un = "user0" + j;
                String pw = un;
                String fn = "firstname" + j;
                String ln = "lastname" + j;
                String email = "mailme" + j + "@br.fi";
                String fedNr = "000" + j;

                Object pid = ps.add(new Player(fedNr));
                Object uid = us.add(new User(un, pw, fn, ln, email));

                us.set(uid, "player", ps.get(pid));
                ps.set(pid, "user", us.get(uid));

                ps.set(pid, "club", cs.at(0));

                us.set(uid, "roles", roles("user"));

            }
        } else {
            BridgeUI.o("SampleStuff.createSamplePlayers: "
                    + "at least one container null or empty");
        }
    }

    private static boolean unique(String username) {
        users.removeFilters();
        users.filterEq("username", username);
        int s = users.size();
        users.removeFilters();
        return s == 0;
    }

    public static void createUser(String userName, String role, int clubIndex) {

        if (unique(userName) && roles.size() > 0) {
            String password = "xxx";
            String fn = userName + "Name";
            String ln = "Franzen";
            String email = userName + "@br.fi";
            String fedNr = Player.getNewFedCode();

            Object pid = players.add(new Player(fedNr));
            String enc = BridgeUI.pwService.encryptPassword(password);
            Object uid = users.add(new User(userName, enc, fn, ln, email));

            users.set(uid, "player", players.get(pid));
            players.set(pid, "user", users.get(uid));
            players.set(pid, "club", clubs.at(clubIndex));
            ShiroRole.addRoles(uid, role);
        } else {
            BridgeUI.o("username " + userName + " already exists");
        }
        users.removeFilters();
    }

    @SuppressWarnings("unused")
    public static void createPlayer(String fedCode, String encPw) {
        BridgeUI.o("fedCode: " + fedCode);
        if (unique(fedCode) && roles.size() > 0) {
            String password = "xxx";
            String fn = "first" + fedCode;
            String ln = "last" + fedCode;
            String email = "esco" + "@br.fi";
            String fedNr = fedCode;

            Object pid = players.add(new Player(fedCode));
            String enc = fedCode;
            Object uid = users.add(new User(fedCode, enc, fn, ln, email));
            //
            users.set(uid, "player", players.get(pid));
            players.set(pid, "user", users.get(uid));
            //
            players.set(pid, "club", clubs.at(0));
            ShiroRole.addRoles(uid, "basic");
        } else {
            BridgeUI.o("username " + fedCode + " already exists");
        }
        users.removeFilters();
    }

    @SuppressWarnings("unused")
    private static void createMembershipApplications() {
        // create two applications

        C<MembershipApplication> as = new C<>(MembershipApplication.class);
        Calendar c = Calendar.getInstance();
        Date d = c.getTime();
        MembershipApplication a = new MembershipApplication(users.at(0),
                players.at(0), d);
        as.add(a);
    }

}
