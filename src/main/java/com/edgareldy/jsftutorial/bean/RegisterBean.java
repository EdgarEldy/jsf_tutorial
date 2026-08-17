package com.edgareldy.jsftutorial.bean;

import com.edgareldy.jsftutorial.service.UserService;
import com.edgareldy.jsftutorial.util.FacesMessageUtil;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

/**
 * Backs {@code /auth/register.xhtml}: creates a disabled account plus an
 * activation token/email via {@link UserService}.
 * <p>
 * Created by Edgar Muhamyangabo on 8/17/26
 * Author : Edgar Muhamyangabo
 * Date : 8/17/26
 * Project : jsf_tutorial
 */
@Named
@RequestScoped
public class RegisterBean implements Serializable {

    @Inject
    private UserService userService;

    private String firstName;
    private String lastName;
    private String email;
    private String password;

    public String register() {
        boolean created = userService.register(firstName, lastName, email, password).isPresent();
        if (!created) {
            FacesMessageUtil.addError("This email is already registered.");
            return null;
        }

        FacesMessageUtil.addInfo("Check your email to activate your account.");
        return "/auth/login.xhtml?faces-redirect=true";
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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
