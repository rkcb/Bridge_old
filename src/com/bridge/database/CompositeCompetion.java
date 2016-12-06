package com.bridge.database;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;


@Entity
public class CompositeCompetion implements Serializable {

	private static final long serialVersionUID = 5657373612431845709L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.TABLE)
	private Long    id;
	
	@OneToMany(cascade=CascadeType.ALL, orphanRemoval=true)
	private Set<Tournament>  parts= new HashSet<Tournament>();
	
	@ManyToOne
	private Club owner;
	
	public CompositeCompetion() {
		super();
	}

	public Club getOwner() {
		return owner;
	}


	public void setOwner(Club owner) {
		this.owner = owner;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	

	public Set<Tournament> getParts() {
		return parts;
	}

	public void setParts(Set<Tournament> parts) {
		this.parts = parts;
	}


	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	public boolean containsTournament(Object tournamentId) {
		if (parts != null && tournamentId != null) {
			boolean contains = false;
			Long id = (Long) tournamentId;
			Iterator<Tournament> i = parts.iterator();
			while (!contains && i.hasNext()) {
				Tournament t = i.next();
				contains = t.getId() == id;
			}
			return contains;
		} else return false; 
	}

	
	
}
