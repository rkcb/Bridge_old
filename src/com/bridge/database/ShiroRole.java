package com.bridge.database;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity(name = "ROLES")
public class ShiroRole implements Serializable, Comparable<ShiroRole> {

    private static final long serialVersionUID = -2099466806012403271L;

    public static final String[] r = new String[] { "anon", "basic",
            "clubadmin", "admin" };
    public static final ArrayList<String> roles = new ArrayList<>(
            Arrays.asList(r));
    public static final HashSet<String> roleSet = new HashSet<>(roles);
    private static ArrayList<ShiroRole> roleList = null;

    /***
     * addRoles add for the user all roles that are less than or equal to role.
     * E.g. "clubadmin" adds the roles "basic" and "clubadmin" but not "admin"
     * Precondition: role must match "basic", "clubadmin" or "admin"
     */

    public static void addRoles(Object userId, String role) {
        if (roleList == null) {
            C<ShiroRole> c = new C<>(ShiroRole.class);
            roleList = new ArrayList<>(c.all());
            roleList.sort(null);
        }
        if (userId != null) {
            C<User> us = new C<>(User.class);
            HashSet<ShiroRole> rs = new HashSet<>();
            int index = roles.indexOf(role) - 1;
            for (int j = 0; j <= index; j++) {
                rs.add(roleList.get(j));
            }
            us.set(userId, "roles", rs);

        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;

    @Column(name = "NAME")
    private String name = new String();
    @Column(name = "DESCRIPTION")
    private String description = new String();

    @OneToMany
    private Set<Permissions> permissions = new HashSet<>();

    public ShiroRole() {
    }

    public ShiroRole(String name, String description) {
        this.name = name;
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public Set<Permissions> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permissions> permissions) {
        this.permissions = permissions;
    }

    @Override
    public int compareTo(ShiroRole o) {
        if (o == null || name == null || o.getName() == null) {
            throw new NullPointerException("Null cannot be compared");
        } else {
            int i = roles.indexOf(name);
            int j = roles.indexOf(o.getName());
            if (i == j) {
                return 0;
            } else {
                return i < j ? -1 : 1;
            }
        }
    }

}
