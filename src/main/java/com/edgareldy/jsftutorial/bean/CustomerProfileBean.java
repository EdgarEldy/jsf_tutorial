package com.edgareldy.jsftutorial.bean;

import com.edgareldy.jsftutorial.entity.Customer;
import com.edgareldy.jsftutorial.security.SessionUserHolder;
import com.edgareldy.jsftutorial.service.CustomerService;
import com.edgareldy.jsftutorial.util.FacesMessageUtil;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

/**
 * Backs {@code /customer/profile.xhtml}: loads the current session user's
 * {@link Customer} profile, creating a blank one bound to that user if none
 * exists yet.
 * <p>
 * Created by Edgar Muhamyangabo on 8/18/26
 * Author : Edgar Muhamyangabo
 * Date : 8/18/26
 * Project : jsf_tutorial
 */
@Named
@ViewScoped
public class CustomerProfileBean implements Serializable {

    @Inject
    private CustomerService customerService;

    @Inject
    private SessionUserHolder sessionUserHolder;

    private Customer customer;

    @PostConstruct
    public void init() {
        customer = customerService.findByUser(sessionUserHolder.getUser())
                .orElseGet(() -> {
                    Customer created = new Customer();
                    created.setUser(sessionUserHolder.getUser());
                    return created;
                });
    }

    public void save() {
        customer = customerService.save(customer);
        FacesMessageUtil.addInfo("Profile saved.");
    }

    public Customer getCustomer() {
        return customer;
    }
}
