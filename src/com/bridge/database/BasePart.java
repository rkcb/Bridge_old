package com.bridge.database;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

import com.bridge.ui.BridgeUI;

@Entity
public abstract class BasePart implements Serializable {

	private static final long serialVersionUID = 2794034454942770348L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	@NotNull(message="BasePart id must not be null")
	protected Long id;
	
	@NotNull(message="Basepart password must not be null")
	protected String password; // encrypted
	
	@ManyToOne
	protected Tournament tournament;
	
	public BasePart() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Tournament getTournament() {
		return tournament;
	}

	public void setTournament(Tournament tournament) {
		this.tournament = tournament;
	}

	/***
	 * getPassword returns the encrypted password
	 * */
	public String getPassword() {
		return password;
	}

	public void setPassword(String plainPassword) {
		this.password = BridgeUI.pwService.encryptPassword(plainPassword);
	}

}
