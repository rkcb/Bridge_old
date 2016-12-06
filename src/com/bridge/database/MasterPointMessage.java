package com.bridge.database;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/***
 * MasterPointMessage is a "receipt" of a master point transaction and it is
 * intended to remain always same and never change to keep record of master
 * point registry changes
 */

@Entity
public class MasterPointMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;
    @NotNull
    private Player receiver;
    @NotNull
    private Double masterPoints; // positive if added and negative otherwise
    @NotNull
    private Player committer;
    @NotNull
    private String message;

    @NotNull
    @Temporal(TemporalType.TIMESTAMP)
    private Date date; // date of granting

    /***
     * stamps this message with server's "now" time
     */
    public MasterPointMessage() {
        date = LogMessage.now();
    }

    /***
     * stamps this message with server's "now" time
     */
    public MasterPointMessage(Player receiver, Double mps, Player committer,
            String message) {
        this.receiver = receiver;
        masterPoints = mps;
        this.committer = committer;
        this.message = message;
        date = LogMessage.now();
    }

    public Player getReceiver() {
        return receiver;
    }

    public void setReceiver(Player receiver) {
        this.receiver = receiver;
    }

    public Double getMasterPoints() {
        return masterPoints;
    }

    public void setMasterPoints(Double masterPoints) {
        this.masterPoints = masterPoints;
    }

    public Player getGranted() {
        return committer;
    }

    public void setGranted(Player granted) {
        committer = granted;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Player getCommitter() {
        return committer;
    }

    public void setCommitter(Player committer) {
        this.committer = committer;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return receiver.getFullName() + ", " + receiver.getFederationCode()
                + ", earned" + masterPoints + ", from" + committer.getFullName()
                + "at " + date;
    }

}
