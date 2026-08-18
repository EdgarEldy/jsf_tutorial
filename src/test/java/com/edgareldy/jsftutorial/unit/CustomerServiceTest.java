package com.edgareldy.jsftutorial.unit;

import com.edgareldy.jsftutorial.dao.CustomerDao;
import com.edgareldy.jsftutorial.entity.Customer;
import com.edgareldy.jsftutorial.entity.User;
import com.edgareldy.jsftutorial.service.impl.CustomerServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link CustomerServiceImpl}, with {@link CustomerDao} mocked.
 * <p>
 * Created by Edgar Muhamyangabo on 8/18/26
 * Author : Edgar Muhamyangabo
 * Date : 8/18/26
 * Project : jsf_tutorial
 */
@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerDao customerDao;

    @InjectMocks
    private CustomerServiceImpl customerService;

    @Test
    void findByUserDelegatesToTheDao() {
        User user = new User();
        Customer customer = new Customer();
        customer.setUser(user);
        when(customerDao.findByUser(user)).thenReturn(Optional.of(customer));

        Optional<Customer> result = customerService.findByUser(user);

        assertTrue(result.isPresent());
        assertEquals(customer, result.get());
    }

    @Test
    void findByUserIsEmptyWhenNoProfileExists() {
        User user = new User();
        when(customerDao.findByUser(user)).thenReturn(Optional.empty());

        assertFalse(customerService.findByUser(user).isPresent());
    }

    @Test
    void saveDelegatesToTheDao() {
        Customer customer = new Customer();
        when(customerDao.save(customer)).thenReturn(customer);

        Customer result = customerService.save(customer);

        assertEquals(customer, result);
    }
}
