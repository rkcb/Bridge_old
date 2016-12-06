package com.bridge.database;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class HtmlItem {
    
    @Id
    @GeneratedValue(strategy=GenerationType.TABLE)
    private Long id = null;
    private String markdown = "";
    // file names which are all used in the new    
    @ElementCollection
    private Set<String> fileNames = new HashSet<String>(); 
    @Temporal(TemporalType.TIMESTAMP)
    private Date date = null;
    @ManyToOne(cascade=CascadeType.ALL)
    private Club club = null; // owner
    private String topic = null;
    
    public static String getResourcePath(Object clubEntityId, Object htmlItemId) {
    	if (clubEntityId != null && htmlItemId != null) {
    		String clubPath = Club.resourcePath(clubEntityId);
    		String itemPath = htmlItemId.toString() + "/";
    		return htmlItemId != null ? clubPath + itemPath : null;
    	} return null;
    }
    
    public HtmlItem() { }
    public HtmlItem(Club owner) { club = owner; }
    
    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
	public Set<String> getFileNames() {
		return fileNames;
	}
	public void setFileNames(Set<String> fileNames) {
		this.fileNames = fileNames;
	}
	public String getMarkdown() {
		return markdown;
	}
	public void setMarkdown(String markdown) {
		this.markdown = markdown;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}

	public Club getClub() {
		return club;
	}

	public void setClub(Club club) {
		this.club = club;
	}

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}
    
}
