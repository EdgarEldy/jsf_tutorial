package com.edgareldy.jsftutorial.dao;

import com.edgareldy.jsftutorial.entity.Customer;
import com.edgareldy.jsftutorial.entity.User;

import java.util.Optional;

/**
 * CRUD plus lookup-by-user for {@link Customer}, the entry point
 * {@code CustomerProfileBean} resolves the current session user's profile through.
 * <p>
 * Created by Edgar Muhamyangabo on 8/18/26
 * Author : Edgar Muhamyangabo
 * Date : 8/18/26
 * Project : jsf_tutorial
 */
public interface CustomerDao extends BaseDao<Customer, Long> {

    Optional<Customer> findByUser(User user);
}
