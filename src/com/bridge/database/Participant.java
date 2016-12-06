package com.bridge.database;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.bridge.ui.BridgeUI;

/**
 * Participant represents the playing unit in a tournament. In a team match
 * Participant would describe a team, in a pair contest a pair and and
 * individual contest the player
 */

@Entity
public class Participant implements Serializable {

    private static final long serialVersionUID = -8085573353116617661L;

    // this field should be used only for the team name
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;
    private String password; // encrypted

    @ManyToOne
    private Tournament tournament;

    public Participant() {
    }

    public Participant(Object o, String plainPassword) {
        password = BridgeUI.pwService.encryptPassword(plainPassword);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String plainPassword) {
        password = BridgeUI.pwService.encryptPassword(plainPassword);
    }

}
