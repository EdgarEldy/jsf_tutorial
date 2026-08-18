package com.edgareldy.jsftutorial.bean;

import com.edgareldy.jsftutorial.service.UserService;
import com.edgareldy.jsftutorial.util.FacesMessageUtil;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

/**
 * Backs {@code /auth/forgot-password.xhtml}: triggers a password-reset
 * token/email via {@link UserService}, without revealing whether the email
 * is actually registered.
 * <p>
 * Created by Edgar Muhamyangabo on 8/17/26
 * Author : Edgar Muhamyangabo
 * Date : 8/17/26
 * Project : jsf_tutorial
 */
@Named
@RequestScoped
public class ForgotPasswordBean implements Serializable {

    @Inject
    private UserService userService;

    private String email;

    public String submit() {
        userService.forgotPassword(email);
        FacesMessageUtil.addInfo("If that email is registered, a reset link has been sent.");
        return "/auth/login.xhtml?faces-redirect=true";
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
