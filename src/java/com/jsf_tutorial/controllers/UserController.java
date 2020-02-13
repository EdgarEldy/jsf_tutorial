/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jsf_tutorial.controllers;

import com.jsf_tutorial.JPAImpl.UserFacadeLocal;
import com.jsf_tutorial.models.Profile;
import com.jsf_tutorial.models.User;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.Query;

/**
 *
 * @author EDGARELDY
 */
@Named(value = "userController")
@SessionScoped
public class UserController implements Serializable {

    @EJB
    private UserFacadeLocal userFacade;
private User user = new User();
private Profile profile = new Profile();
EntityManager em;
Query stmt;

private String username,pwd;
private static final String PERSISTENCE_UNIT_NAME = "jsf_tutorialPU";

    /**
     * Creates a new instance of userController
     */
    public UserController() {
    }
    //For retrieving username typed in the login form
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    //For retrieving pwd typed in the login form
    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
    
    public String add()
    {
    this.userFacade.create(user);
    this.user = new User();
    return "/users/index.xhtml?faces-redirect=true";
    }
    
    public List<User> show()
    {
    return this.userFacade.findAll();
    }
    
    public String Login()
    {
    this.em = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME).createEntityManager();
    this.stmt = this.em.createNamedQuery("User.login", User.class);
    this.stmt.setParameter("username", this.username);
    this.stmt.setParameter("pwd", this.pwd);
    
        try {
            User u = (User) this.stmt.getSingleResult();
            if (u != null && (username.equalsIgnoreCase(u.getUsername())) && (pwd.equalsIgnoreCase(u.getPwd()))) {
                this.username = u.getUsername();
                return "/home/index.xhtml?faces-redirect=true";
            }
            else
            {
            return "/users/login.xhtml?faces-redirect=true";
            }
        } catch (Exception e) {
            FacesContext.getCurrentInstance().addMessage("loginForm:loginUsername", new FacesMessage("Username Incorrect"));            
            FacesContext.getCurrentInstance().addMessage("loginForm:loginPwd", new FacesMessage("Password Incorrect"));
            return "/users/login.xhtml?faces-redirect=true";
        }
    }
    
    public String edit(int id)
    {
        user = userFacade.find(id);
      return "/users/edit.xhtml?faces-redirect=true";
    }
    
    public String update()
    {
     userFacade.edit(user);
     user = new User();
     return "/users/index.xhtml?faces-redirect=true";
    }
    
    public String showProfile(int id)
    {
        user = userFacade.find(id);
      return "/users/profile.xhtml?faces-redirect=true";
    }
    
    public void delete(int id)
    {
    user = userFacade.find(id);
    userFacade.remove(user);
    }

    
}
