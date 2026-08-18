package com.edgareldy.jsftutorial.bean;

import com.edgareldy.jsftutorial.service.UserService;
import com.edgareldy.jsftutorial.util.FacesMessageUtil;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

/**
 * Backs {@code /auth/reset-password.xhtml?token=...}: consumes the
 * {@code token} query parameter and updates the password via
 * {@link UserService}.
 * <p>
 * Created by Edgar Muhamyangabo on 8/17/26
 * Author : Edgar Muhamyangabo
 * Date : 8/17/26
 * Project : jsf_tutorial
 */
@Named
@RequestScoped
public class ResetPasswordBean implements Serializable {

    @Inject
    private UserService userService;

    private String token;
    private String newPassword;
    private String confirmPassword;

    @PostConstruct
    public void init() {
        token = FacesContext.getCurrentInstance().getExternalContext()
                .getRequestParameterMap().get("token");
    }

    public String submit() {
        if (newPassword == null || !newPassword.equals(confirmPassword)) {
            FacesMessageUtil.addError("Passwords do not match.");
            return null;
        }

        boolean success = token != null && userService.resetPassword(token, newPassword);
        if (!success) {
            FacesMessageUtil.addError("This reset link is invalid or has expired.");
            return null;
        }

        FacesMessageUtil.addInfo("Your password has been updated. You can now log in.");
        return "/auth/login.xhtml?faces-redirect=true";
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
