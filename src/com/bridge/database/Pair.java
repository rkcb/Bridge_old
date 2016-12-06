package com.bridge.database;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;

import com.bridge.ui.BridgeUI;

@Entity
public class Pair extends BasePart implements Serializable {

    private static final long serialVersionUID = -2885739242297862983L;

    private Player p0;
    private Player p1;

    public Pair() {
    }

    public Pair(Player pl0, Player pl1, Tournament t, String password) {
        p0 = pl0;
        p1 = pl1;
        tournament = t;
        this.password = BridgeUI.pwService.encryptPassword(password);
    }

    public Pair(String pw, Tournament t, Player... p) {
        setPassword(pw);
        setTournament(t);
        p0 = p[0];
        p1 = p[1];
    }

    public Player getP1() {
        return p1;
    }

    public void setP1(Player p1) {
        this.p1 = p1;
    }

    public Player getP0() {
        return p0;
    }

    public void setP0(Player p0) {
        this.p0 = p0;
    }

    public Set<Long> getPlayingIds() {
        HashSet<Long> s = new HashSet<>();
        if (p1.getId() != null && p0.getId() != null) {
            s.add(p1.getId());
        }
        s.add(p0.getId());
        return s;
    }

}
