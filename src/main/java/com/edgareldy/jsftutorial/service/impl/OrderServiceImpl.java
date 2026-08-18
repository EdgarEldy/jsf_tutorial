package com.edgareldy.jsftutorial.service.impl;

import com.edgareldy.jsftutorial.dao.OrderDao;
import com.edgareldy.jsftutorial.entity.Customer;
import com.edgareldy.jsftutorial.entity.Order;
import com.edgareldy.jsftutorial.service.OrderService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

/**
 * {@link OrderService} implementation. The total is computed here, in the
 * service, rather than in {@code OrderBean} or left for the database:
 * business calculations belong in the service layer regardless of how small
 * they are, consistent with every other rule in this project.
 * <p>
 * Created by Edgar Muhamyangabo on 8/18/26
 * Author : Edgar Muhamyangabo
 * Date : 8/18/26
 * Project : jsf_tutorial
 */
@ApplicationScoped
public class OrderServiceImpl implements OrderService {

    @Inject
    private OrderDao orderDao;

    @Override
    public List<Order> findAll() {
        return orderDao.findAll();
    }

    @Override
    public List<Order> findByCustomer(Customer customer) {
        return orderDao.findByCustomer(customer);
    }

    @Override
    public Order create(Order order) {
        order.setTotal(order.getQuantity() * order.getProduct().getUnitPrice());
        return orderDao.save(order);
    }
}
