/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jsf_tutorial.controllers;

import com.jsf_tutorial.JPAImpl.ProfileFacadeLocal;
import com.jsf_tutorial.models.Profile;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;

/**
 *
 * @author EDGARELDY
 */
@Named(value = "profileController")
@SessionScoped
public class ProfileController implements Serializable {
@EJB
    private ProfileFacadeLocal profileFacade;
    private Profile profile = new Profile();
    /**
     * Creates a new instance of ProfilController
     */
    public ProfileController() {
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }
    public String add()
    {
        profileFacade.create(profile);
        profile = new Profile();
    return "/profiles/index.xhtml?faces-redirect=true";
    }
    
    public List<Profile> show()
    {
    return profileFacade.findAll();
    }
    
    public String edit(int id)
    {
        profile = profileFacade.find(id);
    return "/profiles/edit.xhtml?faces-redirect=true";
    }
    
    public String update()
    {
    profileFacade.edit(profile);
    profile = new Profile();
    return "/profiles/index.xhtml?faces-redirect=true";
    }
    
    public void delete(int id)
    {
    profile = profileFacade.find(id);
    profileFacade.remove(profile);
    }
    
}
