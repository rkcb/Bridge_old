package com.bridge.ui;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;

import com.bridge.database.C;
import com.bridge.database.Club;
import com.bridge.database.ShiroRole;
import com.bridge.database.User;

/***
 * ShiroLogin is used to get various information of the user
 */

public class ShiroLogin {

    public ShiroLogin() {

    }

    public boolean login(String username, String password, boolean rememberMe) {
        UsernamePasswordToken token = new UsernamePasswordToken(username,
                password);
        token.setRememberMe(rememberMe);
        try {
            SecurityUtils.getSubject().login(token);
        } catch (AuthenticationException e) {
            return false;
        }

        return true;
    }

    public void printRoles() {
        if (isSignedIn()) {
            User u = getCurrentUser();
            for (ShiroRole r : u.getRoles()) {
                BridgeUI.o(r.toString());
            }
        }
    }

    public boolean isSignedIn() {
        return SecurityUtils.getSubject().isAuthenticated();
    }

    public boolean hasRole(String roleName) {
        return SecurityUtils.getSubject().hasRole(roleName);
    }

    public String getPlayerClubName() {

        Subject s = SecurityUtils.getSubject();
        if (s.isAuthenticated()) {
            C<User> us = new C<>(User.class);
            us.filterEq("username", s.getPrincipal());
            String str = us.at(0).getPlayer().getClub().getName();
            return str;
        } else {
            return null;
        }
    }

    public Club getCurrentClub() {
        User u = getCurrentUser();
        return u == null ? null : u.getPlayer().getClub();
    }

    public User getCurrentUser() {
        Subject s = SecurityUtils.getSubject();
        if (s.isAuthenticated()) {
            C<User> us = new C<>(User.class);
            us.filterEq("username", s.getPrincipal());
            User u = us.at(0);
            return u;
        } else {
            return null;
        }
    }

    public void logout() {
        Subject s = SecurityUtils.getSubject();
        if (s.isAuthenticated()) {
            s.logout();
        }
    }

    /***
     * getCurrentClubId returns the entity id of the signed in user if possible
     * and otherwise null
     ***/

    public Object getCurrentClubId() {
        User u = getCurrentUser();
        return u == null ? null : u.getPlayer().getClub().getId();
    }

    public String getCurrentUsername() {
        User u = getCurrentUser();
        return u == null ? null : u.getUsername();
    }

}
