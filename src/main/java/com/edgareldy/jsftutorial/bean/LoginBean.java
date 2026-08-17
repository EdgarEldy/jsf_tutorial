package com.edgareldy.jsftutorial.bean;

import com.edgareldy.jsftutorial.entity.User;
import com.edgareldy.jsftutorial.security.SessionUserHolder;
import com.edgareldy.jsftutorial.service.UserService;
import com.edgareldy.jsftutorial.util.FacesMessageUtil;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

/**
 * Backs {@code /auth/login.xhtml}: authenticates against {@link UserService}
 * and starts the session via {@link SessionUserHolder}.
 * <p>
 * Created by Edgar Muhamyangabo on 8/17/26
 * Author : Edgar Muhamyangabo
 * Date : 8/17/26
 * Project : jsf_tutorial
 */
@Named
@RequestScoped
public class LoginBean implements Serializable {

    @Inject
    private UserService userService;

    @Inject
    private SessionUserHolder sessionUserHolder;

    private String email;
    private String password;

    public String login() {
        Optional<User> user = userService.login(email, password);
        if (!user.isPresent()) {
            FacesMessageUtil.addError("Invalid email or password, or account not yet activated.");
            return null;
        }

        Set<String> permissions = userService.resolvePermissions(user.get());
        sessionUserHolder.login(user.get(), permissions);
        return "/index.xhtml?faces-redirect=true";
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
