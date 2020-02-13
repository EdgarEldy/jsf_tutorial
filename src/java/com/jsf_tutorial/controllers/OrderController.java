/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jsf_tutorial.controllers;

import com.jsf_tutorial.JPAImpl.OrdersFacadeLocal;
import com.jsf_tutorial.models.Orders;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;

/**
 *
 * @author EDGARELDY
 */
@Named(value = "orderController")
@SessionScoped
public class OrderController implements Serializable {

    @EJB
    private OrdersFacadeLocal ordersFacade;
private Orders orders = new Orders();
    /**
     * Creates a new instance of orderController
     */
    public OrderController() {
    }

    public Orders getOrders() {
        return orders;
    }

    public void setOrders(Orders orders) {
        this.orders = orders;
    }
    
    public List<Orders> show()
    {
    return this.ordersFacade.findAll();
    }
    
    public String add()
    {
        this.ordersFacade.create(orders);
        this.orders = new Orders();
    return "/orders/index.xhtml?faces-redirect=true";
    }
    
    public String edit(int id)
    {
    this.orders = this.ordersFacade.find(id);
    return "/orders/index.xhtml?faces-redirect-true";
    }
    
    public String update()
    {
    this.ordersFacade.edit(orders);
    this.orders = new Orders();
    return "/orders/index.xhtml?faces-redirect=true";
    }
    
    public void delete(int id)
    {
    this.orders = this.ordersFacade.find(id);
    this.ordersFacade.remove(orders);
    }
}
