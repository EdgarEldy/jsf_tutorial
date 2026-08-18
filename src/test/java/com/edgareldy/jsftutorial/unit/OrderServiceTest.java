package com.edgareldy.jsftutorial.unit;

import com.edgareldy.jsftutorial.dao.OrderDao;
import com.edgareldy.jsftutorial.entity.Customer;
import com.edgareldy.jsftutorial.entity.Order;
import com.edgareldy.jsftutorial.entity.Product;
import com.edgareldy.jsftutorial.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link OrderServiceImpl}, with {@link OrderDao} mocked.
 * <p>
 * Created by Edgar Muhamyangabo on 8/18/26
 * Author : Edgar Muhamyangabo
 * Date : 8/18/26
 * Project : jsf_tutorial
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderDao orderDao;

    @InjectMocks
    private OrderServiceImpl orderService;

    @Test
    void createComputesTotalAsQuantityTimesUnitPrice() {
        Product product = new Product();
        product.setUnitPrice(9.99f);
        Order order = new Order();
        order.setProduct(product);
        order.setQuantity(3);
        when(orderDao.save(order)).thenReturn(order);

        orderService.create(order);

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderDao).save(captor.capture());
        assertEquals(29.97, captor.getValue().getTotal(), 0.001);
    }

    @Test
    void findByCustomerDelegatesToTheDao() {
        Customer customer = new Customer();
        Order order = new Order();
        order.setCustomer(customer);
        when(orderDao.findByCustomer(customer)).thenReturn(Collections.singletonList(order));

        List<Order> result = orderService.findByCustomer(customer);

        assertEquals(1, result.size());
    }

    @Test
    void findAllDelegatesToTheDao() {
        when(orderDao.findAll()).thenReturn(Collections.singletonList(new Order()));

        assertEquals(1, orderService.findAll().size());
    }
}
