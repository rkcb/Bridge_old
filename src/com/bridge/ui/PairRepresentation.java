package com.bridge.ui;

import java.io.Serializable;

import com.bridge.database.Player;

public class PairRepresentation implements Serializable {

	private static final long serialVersionUID = 3118501961864146655L;
	
	private Long id;
	private String name1;
	private String code1;
	private String club1;
	
	private String name2;
	private String club2;
	private String code2;
	
	private Player p1;
	private Player p2;
	
	public PairRepresentation(Player p1, Player p2, Long id) {
		if (p1 != null && p2 != null){
			this.id = id;
			
			this.p1 = p1;
			this.p2 = p2;
			
			name1 = p1.getFullName();
			name2 = p2.getFullName();
			code1 = p1.getFederationCode();
			code2 = p2.getFederationCode();
			club1 = p1.getClub().getName();
			club2 = p2.getClub().getName();
			
		} else throw new NullPointerException("Player can't be null");
	}

	public String getName1() {
		return name1;
	}

	public void setName1(String name1) {
		this.name1 = name1;
	}

	public String getCode1() {
		return code1;
	}

	public void setCode1(String code1) {
		this.code1 = code1;
	}

	public String getClub1() {
		return club1;
	}

	public void setClub1(String club1) {
		this.club1 = club1;
	}

	public String getName2() {
		return name2;
	}

	public void setName2(String name2) {
		this.name2 = name2;
	}

	public String getClub2() {
		return club2;
	}

	public void setClub2(String club2) {
		this.club2 = club2;
	}

	public String getCode2() {
		return code2;
	}

	public void setCode2(String code2) {
		this.code2 = code2;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
	
	
}
