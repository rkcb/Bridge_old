package com.bridge.database;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;

import com.bridge.ui.BridgeUI;

@Entity
public class Indy extends BasePart implements Serializable {

    private static final long serialVersionUID = -2812610435339256599L;

    private Player p0; // player entity id

    public Indy() {
    }

    public Indy(Player p, Tournament t, String password) {
        p0 = p;
        tournament = t;
        this.password = BridgeUI.pwService.encryptPassword(password);
    }

    public Indy(String pw, Tournament t, Player... p) {
        setPassword(pw);
        setTournament(t);
        p0 = p[0];
    }

    public Player getP0() {
        return p0;
    }

    public void setP0(Player p0) {
        this.p0 = p0;
    }

    public Set<Long> getPlayingIds() {
        HashSet<Long> s = new HashSet<>();
        if (p0.getId() != null) {
            s.add(p0.getId());
        }

        return s;
    }

}
