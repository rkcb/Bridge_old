package com.bridge.ui;
import com.bridge.database.Player;
public class IndividualRepresentation {

	private Long id;
	private String name;
	private String club;
	private String code;
	private String password; // encrypted
	private Long pid;
	public IndividualRepresentation(Player p, Long id) {
		this.id = id;
		name = p.getFullName();
		club = p.getClub().getName();
		code = p.getFederationCode();
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getClub() {
		return club;
	}
	public void setClub(String club) {
		this.club = club;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	public Long getPid() {
		return pid;
	}
	public void setPid(Long pid) {
		this.pid = pid;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	

}
