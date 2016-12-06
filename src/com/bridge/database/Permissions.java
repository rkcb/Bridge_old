package com.bridge.database;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Permissions implements Serializable {
	
	private static final long serialVersionUID = -2505539724821468445L;

	@Id
	@GeneratedValue(strategy=GenerationType.TABLE)
	private long id;
	
	@Column(name="NAME")
	private String name;
	
	@Column(name="DESCRIPTIONS")
	private String descriptions;
	
	public Permissions() {}
	public Permissions(String name, String descriptions){
		this.name = name;
		this.descriptions = descriptions;
	}
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescriptions() {
		return descriptions;
	}
	public void setDescriptions(String descriptions) {
		this.descriptions = descriptions;
	}
	
	public String toString(){
		return name;
	}
	
	

}
