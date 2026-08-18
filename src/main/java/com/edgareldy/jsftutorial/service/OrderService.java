package com.edgareldy.jsftutorial.service;

import com.edgareldy.jsftutorial.entity.Customer;
import com.edgareldy.jsftutorial.entity.Order;

import java.util.List;

/**
 * Creating and listing {@link Order}s.
 * <p>
 * Created by Edgar Muhamyangabo on 8/18/26
 * Author : Edgar Muhamyangabo
 * Date : 8/18/26
 * Project : jsf_tutorial
 */
public interface OrderService {

    List<Order> findAll();

    List<Order> findByCustomer(Customer customer);

    /**
     * Computes {@code total = quantity * product.unitPrice} and persists the
     * order; {@code order.customer}, {@code order.product} and
     * {@code order.quantity} must already be set.
     */
    Order create(Order order);
}
