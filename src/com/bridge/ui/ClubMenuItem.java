package com.bridge.ui;

import java.io.Serializable;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@SuppressWarnings("serial")

@Entity
public class ClubMenuItem implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;
    private boolean childrenAllowed;
    private boolean root; // root item only has siblings
    private boolean removable;
    private boolean editable;
    private String caption;
    private String html; // contents to show when clicked

    @ElementCollection
    private List<ClubMenuItem> siblings;
    @ElementCollection
    private List<ClubMenuItem> children;

    public ClubMenuItem() {
    }

    public boolean isRemovable() {
        return removable;
    }

    public void setRemovable(boolean removable) {
        this.removable = removable;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public List<ClubMenuItem> getSiblings() {
        return siblings;
    }

    public void setSiblings(List<ClubMenuItem> siblings) {
        this.siblings = siblings;
    }

    public List<ClubMenuItem> getChildren() {
        return children;
    }

    public void setChildren(List<ClubMenuItem> children) {
        this.children = children;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isRoot() {
        return root;
    }

    public void setRoot(boolean root) {
        this.root = root;
    }

    public boolean isChildrenAllowed() {
        return childrenAllowed;
    }

    public void setChildrenAllowed(boolean childrenAllowed) {
        this.childrenAllowed = childrenAllowed;
    }

}
