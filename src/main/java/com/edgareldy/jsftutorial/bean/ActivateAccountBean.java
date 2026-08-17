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
 * Backs {@code /auth/activate.xhtml?token=...}: consumes the {@code token}
 * query parameter on page load and enables the account via
 * {@link UserService}.
 * <p>
 * Created by Edgar Muhamyangabo on 8/17/26
 * Author : Edgar Muhamyangabo
 * Date : 8/17/26
 * Project : jsf_tutorial
 */
@Named
@RequestScoped
public class ActivateAccountBean implements Serializable {

    @Inject
    private UserService userService;

    private boolean activated;

    @PostConstruct
    public void activate() {
        String token = FacesContext.getCurrentInstance().getExternalContext()
                .getRequestParameterMap().get("token");

        activated = token != null && userService.activate(token);

        if (activated) {
            FacesMessageUtil.addInfo("Your account has been activated. You can now log in.");
        } else {
            FacesMessageUtil.addError("This activation link is invalid or has expired.");
        }
    }

    public boolean isActivated() {
        return activated;
    }
}
