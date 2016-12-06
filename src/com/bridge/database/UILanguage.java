package com.bridge.database;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class UILanguage implements Serializable {

	private static final long serialVersionUID = 4531387750972363374L;
	
	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private Long id;
	
	// options must be written in English
	private String language;
	
	public UILanguage() { }
	
	public UILanguage(String language) { 
		this.language = language;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
}
