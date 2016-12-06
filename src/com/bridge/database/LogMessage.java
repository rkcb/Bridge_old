package com.bridge.database;

import java.util.Calendar;
import java.util.Date;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

/***
 * LogMessage is used to keep record of actions (club)admins do. Note that
 * MasterPointMessage records master point changes
 */

public class LogMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @NotNull
    private Player committer; // player who did the action
    @NotNull
    private String message;
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date date;

    public LogMessage(String message) {
        this.message = message;
    }

    public static Date now() {
        Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
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

}
