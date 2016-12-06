package com.bridge.database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.vaadin.server.VaadinService;

@Entity(name = "Club")
public class Club implements Serializable {
    private static final long serialVersionUID = -7263413424719546691L;

    public static Object findId(String clubName) {

        if (clubName != null) {
            C<Club> cs = new C<>(Club.class);
            cs.filterEq("name", clubName);
            if (cs.size() == 1) {
                return cs.at(0).getId();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    public static String resourcePath(Object clubEntityId) {

        String filePath = VaadinService.getCurrent().getBaseDirectory()
                .getAbsolutePath();
        if (clubEntityId != null) {
            return filePath + "/WEB-INF/clubNews/" + clubEntityId.toString()
                    + "/";
        } else {
            return null;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    private String name = new String("");
    private String town = new String("");
    private String founded = new String("");
    private Set<String> topics = new HashSet<>();

    /*
     * if club is not active it does not have club admins or members; setting
     * club to inactive state causes club admins and memberships removed
     */
    private boolean active = true;

    @OneToMany(mappedBy = "owner")
    private Set<CompositeCompetion> compositeCompetions = new HashSet<>();

    @OneToMany(mappedBy = "owner")
    private Set<Tournament> tournaments = new HashSet<>();

    @OneToMany(mappedBy = "owner")
    private Set<BridgeEvent> calendarEvents = new HashSet<>();

    @OneToMany(mappedBy = "club", orphanRemoval = false, cascade = CascadeType.ALL)
    private Set<Player> members = new HashSet<>();

    @OneToMany(mappedBy = "club", cascade = CascadeType.ALL)
    private List<HtmlItem> news = new ArrayList<>();

    public Club() {
    }

    public Club(String name, String town) {
        this.name = name;
        this.town = town;
    }

    @Override
    public String toString() {
        return name;
    }

    public boolean ownsTournament(Object tournamentId) {

        if (tournaments != null && tournamentId != null) {
            Long id = (Long) tournamentId;
            boolean contains = false;
            Iterator<Tournament> i = tournaments.iterator();
            while (i.hasNext() && !contains) {
                Tournament t = i.next();
                contains = t.getId() == id;
            }
            return contains;
        } else {
            return false;
        }
    }

    /***
     * removeTournament removes the tournament if it is found in the club's
     * tournaments
     */

    public boolean removeTournament(Object tournamentId) {
        if (tournamentId != null) {
            Iterator<Tournament> i = tournaments.iterator();
            boolean found = false;

            while (!found && i.hasNext()) {
                Tournament t = i.next();
                found = t.getId() == null ? false : t.getId() == tournamentId;
            }

            if (found) {
                i.remove();
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean ownsCompositeCompetion(Object compositeId) {

        if (compositeCompetions != null && compositeId != null) {
            Long id = (Long) compositeId;
            boolean contains = false;
            Iterator<CompositeCompetion> i = compositeCompetions.iterator();
            while (i.hasNext() && !contains) {
                CompositeCompetion t = i.next();
                contains = t.getId() == id;
            }
            return contains;
        } else {
            return false;
        }
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    /***
     * removePlayer remove the player and return the remaining members of this
     * club
     */

    public Set<Player> removePlayer(Object pid) {
        Long id = (Long) pid;
        return members.stream().filter(p -> p.getId().equals(id))
                .collect(Collectors.toSet());
    }

    /***
     * addPlayer adds the player with the id and returns members after adding
     * the player
     */

    public Set<Player> addPlayer(Object pid) {
        C<Player> ps = new C<>(Player.class);
        if (pid != null && ps.c().containsId(pid)) {
            members.add(ps.get(pid));
            return members;
        } else {
            return members;
        }
    }

    public Set<Player> getMembers() {
        return members;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setMembers(Set<Player> members) {
        this.members = members;
    }

    public void testAddMembers() {

    }

    public Set<CompositeCompetion> getCompositeCompetions() {
        return compositeCompetions;
    }

    public void setCompositeCompetions(
            Set<CompositeCompetion> compositeCompetions) {
        this.compositeCompetions = compositeCompetions;
    }

    public Set<Tournament> getTournaments() {
        return tournaments;
    }

    public void setTournaments(Set<Tournament> tournaments) {
        this.tournaments = tournaments;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getFounded() {
        return founded;
    }

    public void setFounded(String founded) {
        this.founded = founded;
    }

    public Set<BridgeEvent> getCalendarEvents() {
        return calendarEvents;
    }

    public void setCalendarEvents(Set<BridgeEvent> calendarEvents) {
        this.calendarEvents = calendarEvents;
    }

    public List<HtmlItem> getNews() {
        return news;
    }

    public void setNews(List<HtmlItem> news) {
        this.news = news;
    }

    public Set<String> getTopics() {
        return topics;
    }

    public void setTopics(Set<String> topics) {
        this.topics = topics;
    }

}
