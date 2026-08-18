package com.edgareldy.jsftutorial.service;

import com.edgareldy.jsftutorial.entity.Customer;
import com.edgareldy.jsftutorial.entity.User;

import java.util.Optional;

/**
 * Loading and saving the current user's {@link Customer} profile.
 * <p>
 * Created by Edgar Muhamyangabo on 8/18/26
 * Author : Edgar Muhamyangabo
 * Date : 8/18/26
 * Project : jsf_tutorial
 */
public interface CustomerService {

    Optional<Customer> findByUser(User user);

    Customer save(Customer customer);
}
