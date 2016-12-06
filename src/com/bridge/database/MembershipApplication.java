package com.bridge.database;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class MembershipApplication implements Serializable {

    private static final long serialVersionUID = 1745864314415541566L;
    protected User user = null;
    protected Player player = null;
    protected Club club = null; // target club
    @Temporal(TemporalType.TIMESTAMP)
    protected Date submitted = null;
    protected Long playerId = null;
    protected Long oldClubId = null;
    protected Long newClubId = null;

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private long id;

    public MembershipApplication() {
    }

    /***
     * MembershipApplication used for changing clubs
     */

    public MembershipApplication(Long playerId, Long oldId, Long newId,
            Date submitted) {
        this.playerId = playerId;
        oldClubId = oldId;
        newClubId = newId;
        this.submitted = submitted;

        C<Player> ps = new C<>(Player.class);
        player = ps.get(playerId);
        user = player.getUser();
        C<Club> cs = new C<>(Club.class);
        club = cs.get(newId);
    }

    public MembershipApplication(User u, Player p, Date submitted) {
        user = u;
        player = p;
        club = p.getClub();
        this.submitted = submitted;
    }

    public Date getSubmitted() {
        return submitted;
    }

    public void setSubmitted(Date submitted) {
        this.submitted = submitted;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Long getOldClubId() {
        return oldClubId;
    }

    public void setOldClubId(Long oldClubId) {
        this.oldClubId = oldClubId;
    }

    public Long getNewClubId() {
        return newClubId;
    }

    public void setNewClubId(Long newClubId) {
        this.newClubId = newClubId;
    }

    public Club getClub() {
        return club;
    }

    public void setClub(Club club) {
        this.club = club;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long playerId) {
        this.playerId = playerId;
    }

}
