package com.bridge.database;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
	
import com.bridge.ui.BridgeUI;

@Entity
public class Team extends BasePart implements Serializable {

	private static final long serialVersionUID = -2885739242297862983L;

	private Player p0;
	private Player p1;
	private Player p2;
	private Player p3;
	private Player p4;
	private Player p5;
	
	private String name;
	
	public Team(){}
	
	public Team(String name, Player pl0, Player pl1, Player pl2, Player pl3, Player pl4,
			Player pl5, String password){
		this.name = name;
		p0 = pl0;
		p1 = pl1;
		p2 = pl2;
		p3 = pl3;
		p4 = pl4;
		p5 = pl5;
		this.password = BridgeUI.pwService.encryptPassword(password);
	}
	
	public Team(String teamName, String pw, Tournament tour, Player[] p) {
		name = teamName;
		this.password = BridgeUI.pwService.encryptPassword(pw);
		
		p0 = p[0];
		p1 = p[1];
		p2 = p[2];
		p3 = p[3];
		p4 = p[4];
		p5 = p[5];
		
		tournament = tour;
	}
	


	public Player getP1() {
		return p1;
	}

	public void setP1(Player p1) {
		this.p1 = p1;
	}
	public Player getP2() {
		return p2;
	}
	public void setP2(Player p2) {
		this.p2 = p2;
	}

	
	
	public Player getP3() {
		return p3;
	}

	public void setP3(Player p3) {
		this.p3 = p3;
	}

	public Player getP4() {
		return p4;
	}

	public void setP4(Player p4) {
		this.p4 = p4;
	}

	public Player getP5() {
		return p5;
	}

	public void setP5(Player p5) {
		this.p5 = p5;
	}

	public Player getP0() {
		return p0;
	}

	public void setP0(Player p0) {
		this.p0 = p0;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<Long> getPlayingIds() {
		HashSet<Long> s = new HashSet<Long>();
		if (p0 != null){ s.add(p0.getId()); }
		if (p1 != null){ s.add(p1.getId()); }
		if (p2 != null){ s.add(p2.getId()); }
		if (p3 != null){ s.add(p3.getId()); }
		if (p4 != null){ s.add(p4.getId()); }
		if (p5 != null){ s.add(p5.getId()); }
		
		return s;
	}
	
}
