package com.edgareldy.jsftutorial.security;

import com.edgareldy.jsftutorial.entity.Role;
import com.edgareldy.jsftutorial.entity.User;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Holds the authenticated {@link User} (or none) and their resolved
 * {@code "resource:action"} permissions for the duration of the HTTP
 * session. The only bean in this project allowed {@code @SessionScoped}.
 * <p>
 * Created by Edgar Muhamyangabo on 8/17/26
 * Author : Edgar Muhamyangabo
 * Date : 8/17/26
 * Project : jsf_tutorial
 */
@Named
@SessionScoped
public class SessionUserHolder implements Serializable {

    private User user;
    private Set<String> permissions = Collections.emptySet();

    public void login(User user, Set<String> permissions) {
        this.user = user;
        this.permissions = new HashSet<>(permissions);
    }

    public void logout() {
        this.user = null;
        this.permissions = Collections.emptySet();
    }

    public boolean isAuthenticated() {
        return user != null;
    }

    public boolean hasRole(String roleName) {
        if (user == null) {
            return false;
        }
        for (Role role : user.getRoles()) {
            if (role.getRoleName().equalsIgnoreCase(roleName)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasPermission(String resource, String action) {
        return permissions.contains(resource + ":" + action);
    }

    public User getUser() {
        return user;
    }
}
