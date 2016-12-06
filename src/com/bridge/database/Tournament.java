package com.bridge.database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.validation.Valid;

import org.hibernate.validator.constraints.NotEmpty;

/***
 * Tournament is owned by a club and is in 1-1 relation to calendar event.
 */

@Entity
public class Tournament implements Serializable {

    private static final long serialVersionUID = 5334646092133026830L;

    public enum Type {
        Individual, Pair, Team
    };

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @Column(name = "TOURNAMENT_ID")
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @Valid
    private BridgeEvent calendarEvent;

    @ManyToOne(cascade = CascadeType.ALL)
    @Valid
    private Club owner;

    @ElementCollection
    private List<PbnFile> pbnFiles = new ArrayList<>();

    @NotEmpty
    @ElementCollection
    private Set<Player> organizers = new HashSet<>();

    // @OneToMany
    // @JoinColumn(name="PLAYER_ID", referencedColumnName="TOURNAMENT_ID")
    @NotEmpty
    @ElementCollection
    private Set<Player> directors = new HashSet<>();

    // key is federation code and the value earned masterpoints
    // Using Player class masterpoints can be handled even if
    // federation code changes
    // private HashMap<Long, Float> masterPoints2; // key is federation code

    @ManyToOne(cascade = CascadeType.ALL)
    private CompositeCompetion compositeCompetion;

    @ManyToMany(cascade = CascadeType.ALL)
    @Valid
    private List<Player> participatedPlayers = new ArrayList<>();

    // participant is invidual player, pair or team
    // @OneToMany(orphanRemoval=true, mappedBy="tournament",
    // cascade=CascadeType.ALL)

    // the following store registered "units" in the tournament
    // exactly one of these should be used
    @OneToMany(orphanRemoval = true, mappedBy = "tournament", cascade = CascadeType.ALL)
    private Set<Indy> individuals = new HashSet<>();
    @OneToMany(orphanRemoval = true, mappedBy = "tournament", cascade = CascadeType.ALL)
    private Set<Pair> pairs = new HashSet<>();
    @OneToMany(orphanRemoval = true, mappedBy = "tournament", cascade = CascadeType.ALL)
    private Set<Team> teams = new HashSet<>();

    public CompositeCompetion getCompositeCompetion() {
        return compositeCompetion;
    }

    public void setCompositeCompetion(CompositeCompetion compositeCompetion) {
        this.compositeCompetion = compositeCompetion;
    }

    public List<PbnFile> getPbnFiles() {
        return pbnFiles;
    }

    public void setPbnFiles(ArrayList<PbnFile> pbnFiles) {
        this.pbnFiles = pbnFiles;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public Tournament() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BridgeEvent getCalendarEvent() {
        return calendarEvent;
    }

    public void setCalendarEvent(BridgeEvent calendarEvent) {
        this.calendarEvent = calendarEvent;
    }

    @OneToMany
    public Set<Player> getOrganizers() {
        return organizers;
    }

    public void setOrganizers(Set<Player> organizers) {
        this.organizers = organizers;
    }

    @OneToMany
    public Set<Player> getDirectors() {
        return directors;
    }

    public void setDirectors(Set<Player> directors) {
        this.directors = directors;
    }

    public List<Player> getParticipatedPlayers() {
        return participatedPlayers;
    }

    public void setParticipatedPlayers(ArrayList<Player> participatedPlayers) {
        this.participatedPlayers = participatedPlayers;
    }

    // public HashMap<Long, Float> getMasterPoints() {
    // return masterPoints;
    // }
    //
    // public void setMasterPoints(HashMap<Long, Float> masterPoints) {
    // this.masterPoints = masterPoints;
    // }

    public Club getOwner() {
        return owner;
    }

    public void setOwner(Club owner) {
        this.owner = owner;
    }

    public void setPbnFiles(List<PbnFile> pbnFiles) {
        this.pbnFiles = pbnFiles;
    }

    public void setParticipatedPlayers(List<Player> participatedPlayers) {
        this.participatedPlayers = participatedPlayers;
    }

    public Set<Indy> getIndividuals() {
        return individuals;
    }

    public void setIndividuals(Set<Indy> individuals) {
        this.individuals = individuals;
    }

    public Set<Pair> getPairs() {
        return pairs;
    }

    public void setPairs(Set<Pair> pairs) {
        this.pairs = pairs;
    }

    public Set<Team> getTeams() {
        return teams;
    }

    public void setTeams(Set<Team> teams) {
        this.teams = teams;
    }

}
