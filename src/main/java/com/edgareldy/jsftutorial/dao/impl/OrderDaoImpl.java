package com.edgareldy.jsftutorial.dao.impl;

import com.edgareldy.jsftutorial.dao.OrderDao;
import com.edgareldy.jsftutorial.entity.Customer;
import com.edgareldy.jsftutorial.entity.Order;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * {@link OrderDao} implementation backed by the CDI-produced {@link EntityManager}.
 * <p>
 * Created by Edgar Muhamyangabo on 8/18/26
 * Author : Edgar Muhamyangabo
 * Date : 8/18/26
 * Project : jsf_tutorial
 */
@ApplicationScoped
public class OrderDaoImpl extends BaseDaoImpl<Order, Long> implements OrderDao {

    @Inject
    private EntityManager entityManager;

    public OrderDaoImpl() {
        super(Order.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public List<Order> findByCustomer(Customer customer) {
        TypedQuery<Order> query = entityManager.createQuery(
                "SELECT o FROM OrderEntity o WHERE o.customer = :customer", Order.class);
        query.setParameter("customer", customer);
        return query.getResultList();
    }
}
