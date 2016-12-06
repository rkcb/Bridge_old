package com.bridge.database;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Entity
public class Player implements Serializable {

    private static final long serialVersionUID = 5869544743061188413L;

    /***
     * getNewFedCode creates a new federation code by choosing the smallest
     * number such that it is greater than the number of the players and is not
     * used and then converts this integer to string
     */

    public static String getNewFedCode() {
        C<Player> ps = new C<>(Player.class);
        Integer c = ps.size();
        boolean free = false;
        String code = null;

        do {
            c++;
            code = Integer.toString(c);
            free = !ps.exists("federationCode", code);
        } while (!free);

        return code;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @NotNull(message = "Player id must not be null")
    private Long id;

    @OneToOne(mappedBy = "player", cascade = CascadeType.ALL)
    @Valid
    private User user;

    private String federationCode;

    @NotNull(message = "OldMasterPoints  must not be null")
    private Double oldMasterPoints = new Double(0); // points earned before this
                                                    // system
    @NotNull
    private Double masterPoints = new Double(0);

    @NotNull
    private Double valueMasterPoints = new Double(0);

    private String directorQualifications = new String();

    @ManyToOne(cascade = CascadeType.ALL)
    private Club club;

    private Boolean federationMember = new Boolean(false);
    private Boolean foreignWBFClubMember = new Boolean(false);

    @Temporal(TemporalType.TIMESTAMP)
    private Date suspensionStart;
    @Temporal(TemporalType.TIMESTAMP)
    private Date suspensionEnd;

    @ManyToMany(mappedBy = "participatedPlayers")
    private Set<Tournament> playedTournaments = new HashSet<>();

    private List<String> masterpointMessages;

    public Player() {
    }

    /***
     * hasPlayedTournament checks if the player has played the tournament
     */

    public boolean hasPlayedTournament(Object tournamentId) {
        Long id = (Long) tournamentId;
        if (playedTournaments != null) {
            boolean found = false;
            Iterator<Tournament> i = playedTournaments.iterator();
            while (!found && i.hasNext()) {
                found = i.next().getId() == id;
            }
            return found;
        }
        return false;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @OneToOne(mappedBy = "player")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public static boolean federationCodeExists(String c) {
        C<Player> ps = new C<>(Player.class);
        ps.filterEq("federationCode", c);
        return ps.size() == 1;
    }

    public Player(String federationCode) {
        this.federationCode = federationCode;
    }

    @Override
    public String toString() {
        String s = getFullName();
        if (federationCode != null) {
            s += ", " + federationCode;
        }
        return s;
    }

    public String getFederationCode() {
        return federationCode;
    }

    public void setFederationCode(String federationCode) {
        this.federationCode = federationCode;
    }

    public Double getMasterPoints() {
        return masterPoints;
    }

    public void setMasterPoints(Double masterPoints) {
        this.masterPoints = masterPoints;
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public Set<Tournament> getPlayedTournaments() {
        return playedTournaments;
    }

    public void setPlayedTournaments(Set<Tournament> playedTournaments) {
        this.playedTournaments = playedTournaments;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public boolean isFederationMember() {
        return federationMember;
    }

    public void setFederationMember(boolean federationMember) {
        this.federationMember = federationMember;
    }

    public String getDirectorQualifications() {
        return directorQualifications;
    }

    public void setDirectorQualifications(String directorQualifications) {
        this.directorQualifications = directorQualifications;
    }

    public Boolean getFederationMember() {
        return federationMember;
    }

    public void setFederationMember(Boolean federationMember) {
        this.federationMember = federationMember;
    }

    public String getFullName() {
        if (user != null) {
            return user.getFirstName() + " " + user.getLastName();
        } else {
            return "";
        }
    }

    public Boolean getForeignWBFClubMember() {
        return foreignWBFClubMember;
    }

    public void setForeignWBFClubMember(Boolean foreignWBFClub) {
        foreignWBFClubMember = foreignWBFClub;
    }

    public Double getValueMasterPoints() {
        return valueMasterPoints;
    }

    public void setValueMasterPoints(Double valueMasterPoints) {
        this.valueMasterPoints = valueMasterPoints;
    }

    public Date getSuspensionStart() {
        return suspensionStart;
    }

    public void setSuspensionStart(Date suspensionStart) {
        this.suspensionStart = suspensionStart;
    }

    public Date getSuspensionEnd() {
        return suspensionEnd;
    }

    public void setSuspensionEnd(Date suspensionEnd) {
        this.suspensionEnd = suspensionEnd;
    }

    public List<String> getMasterpointMessages() {
        return masterpointMessages;
    }

    public void setMasterpointMessages(List<String> masterpointMessages) {
        this.masterpointMessages = masterpointMessages;
    }

}
