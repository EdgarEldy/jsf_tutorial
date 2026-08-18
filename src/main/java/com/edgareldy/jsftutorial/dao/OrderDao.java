package com.edgareldy.jsftutorial.dao;

import com.edgareldy.jsftutorial.entity.Customer;
import com.edgareldy.jsftutorial.entity.Order;

import java.util.List;

/**
 * CRUD plus lookup-by-customer for {@link Order}, backing the current
 * customer's order history page.
 * <p>
 * Created by Edgar Muhamyangabo on 8/18/26
 * Author : Edgar Muhamyangabo
 * Date : 8/18/26
 * Project : jsf_tutorial
 */
public interface OrderDao extends BaseDao<Order, Long> {

    List<Order> findByCustomer(Customer customer);
}
