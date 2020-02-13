/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jsf_tutorial.controllers;

import com.jsf_tutorial.JPAImpl.CustomerFacadeLocal;
import com.jsf_tutorial.models.Customer;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;

/**
 *
 * @author EDGARELDY
 */
@Named(value = "customerController")
@SessionScoped
public class CustomerController implements Serializable {

    @EJB
    private CustomerFacadeLocal customerFacade;
private Customer customer = new Customer();
    /**
     * Creates a new instance of customerController
     */
    public CustomerController() {
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
    public String add()
    {
    this.customerFacade.create(customer);
    this.customer = new Customer();
    return "/home/index.xhtml?faces-redirect=true";
    }
    
    public List<Customer> show()
    {
    return this.customerFacade.findAll();
    }
    
    public String edit(int id)
    {
    this.customerFacade.find(id);
     return "/customers/edit.xhtml?faces-redirect=true";
    }
    
    public String update()
    {
    this.customerFacade.edit(customer);
    this.customer = new Customer();    
    return "/home/index.xhtml?faces-redirect=true";
    }
    
    public void delete(int id)
    {
        this.customer = this.customerFacade.find(id);
        this.customerFacade.remove(customer);
    }
    
}
