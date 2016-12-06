package com.bridge.database;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.hibernate.validator.constraints.Email;

@Entity(name = "USERS")
public class User implements Serializable {

    private static final long serialVersionUID = 8784452154211849955L;
    @Transient
    public static final DefaultPasswordService passwordService = new DefaultPasswordService();

    @Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    @NotNull(message = "User id must not be null")
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    private Player player = null; // null if this user is not a bridge player

    private String firstName = new String();
    private String secondName = new String();
    private String lastName = new String();
    private String nationality = new String();
    // the user interface language
    private String language = new String();
    private String address = new String();
    private String town = new String();
    private String postalCode = new String();
    private String country = new String();
    private String yearOfBirth = new String();

    // @NotNull(message="Username must be nonnull")
    // @Size(min=3, max=30, message="username must be 6-30 characters long")
    @Pattern(regexp = "\\w+", message = "only English letters and numbers allowed")
    private String username = new String();

    // @Size(min=0, message="Password must be at least 3 characters long")
    @NotNull(message = "Password must not be null")
    private String password;

    @NotNull
    @Email(message = "Not valid email message address")
    private String email = new String("");

    private String telephone = new String();
    private Boolean alive = new Boolean(true);

    private Boolean emailPrivate = new Boolean(true);
    private Boolean telephonePrivate = new Boolean(true);

    @OneToMany
    private Set<ShiroRole> roles = new HashSet<>();

    public Set<ShiroRole> getRoles() {
        return roles;
    }

    public void setRoles(Set<ShiroRole> roles) {
        this.roles = roles;
    }

    public User() {
        emailPrivate = true;
        telephonePrivate = true;
    }

    public User(String username, String password, String firstName,
            String lastName, String email) {
        this.username = new String(username);
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;

        alive = new Boolean(true);
        emailPrivate = new Boolean(true);
        telephonePrivate = new Boolean(true);
    }

    @Override
    public String toString() {

        String s = "";

        if (firstName != null && !firstName.isEmpty()) {
            s = s + firstName + " ";
        }
        if (lastName != null && !lastName.isEmpty()) {
            s = s + lastName;
        }
        if (player != null && player.getFederationCode() != null) {
            s = s + " (" + player.getFederationCode() + ")";
        }

        return s;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getSecondName() {
        return secondName;
    }

    public void setSecondName(String secondName) {
        this.secondName = secondName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getNationality() {
        return nationality;
    }

    public void setNationality(String nationality) {
        this.nationality = nationality;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTown() {
        return town;
    }

    public void setTown(String town) {
        this.town = town;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public boolean isAlive() {
        return alive;
    }

    public void setAlive(boolean alive) {
        this.alive = alive;
    }

    public Boolean getAlive() {
        return alive;
    }

    public void setAlive(Boolean alive) {
        this.alive = alive;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getEmailPrivate() {
        return emailPrivate;
    }

    public void setEmailPrivate(Boolean emailPrivate) {
        this.emailPrivate = emailPrivate;
    }

    public Boolean getTelephonePrivate() {
        return telephonePrivate;
    }

    public void setTelephonePrivate(Boolean telephonePrivate) {
        this.telephonePrivate = telephonePrivate;
    }

    public boolean isTelephonePrivate() {
        return telephonePrivate;
    }

    public void setTelephonePrivate(boolean telephonePrivate) {
        this.telephonePrivate = telephonePrivate;
    }

    public String getYearOfBirth() {
        return yearOfBirth;
    }

    public void setYearOfBirth(String yearOfBirth) {
        this.yearOfBirth = yearOfBirth;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password; // Service.encryptPassword(password);
    }

    public ShiroRole getTopRole() {

        // if (roles.isEmpty()) throw new NullPointerException("Roles is
        // empty");
        // return roles.isEmpty() ? null : Collections.max(roles);
        ShiroRole r = Collections.max(roles);

        return r;
    }

}
