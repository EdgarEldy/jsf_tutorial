/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jsf_tutorial.controllers;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;

/**
 *
 * @author EDGARELDY
 */
@Named(value = "customerController")
@SessionScoped
public class customerController implements Serializable {

    /**
     * Creates a new instance of customerController
     */
    public customerController() {
    }
    
}
