package com.edgareldy.jsftutorial.service.impl;

import com.edgareldy.jsftutorial.dao.CustomerDao;
import com.edgareldy.jsftutorial.entity.Customer;
import com.edgareldy.jsftutorial.entity.User;
import com.edgareldy.jsftutorial.service.CustomerService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Optional;

/**
 * {@link CustomerService} implementation, a thin pass-through to {@link CustomerDao}.
 * <p>
 * Created by Edgar Muhamyangabo on 8/18/26
 * Author : Edgar Muhamyangabo
 * Date : 8/18/26
 * Project : jsf_tutorial
 */
@ApplicationScoped
public class CustomerServiceImpl implements CustomerService {

    @Inject
    private CustomerDao customerDao;

    @Override
    public Optional<Customer> findByUser(User user) {
        return customerDao.findByUser(user);
    }

    @Override
    public Customer save(Customer customer) {
        return customerDao.save(customer);
    }
}
