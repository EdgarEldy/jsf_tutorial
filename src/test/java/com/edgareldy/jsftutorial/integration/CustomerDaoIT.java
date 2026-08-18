package com.edgareldy.jsftutorial.integration;

import com.edgareldy.jsftutorial.dao.BaseDao;
import com.edgareldy.jsftutorial.dao.CustomerDao;
import com.edgareldy.jsftutorial.dao.UserDao;
import com.edgareldy.jsftutorial.dao.impl.BaseDaoImpl;
import com.edgareldy.jsftutorial.dao.impl.CustomerDaoImpl;
import com.edgareldy.jsftutorial.dao.impl.UserDaoImpl;
import com.edgareldy.jsftutorial.entity.BlacklistedToken;
import com.edgareldy.jsftutorial.entity.Customer;
import com.edgareldy.jsftutorial.entity.Permission;
import com.edgareldy.jsftutorial.entity.Role;
import com.edgareldy.jsftutorial.entity.User;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import java.io.File;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test for {@link CustomerDaoImpl} against a real {@link javax.persistence.EntityManager}.
 * Setup and cleanup happen entirely inside the {@code @Test} method, same
 * reason as every other Arquillian test in this project (see UserDaoIT).
 * <p>
 * Created by Edgar Muhamyangabo on 8/18/26
 * Author : Edgar Muhamyangabo
 * Date : 8/18/26
 * Project : jsf_tutorial
 */
@ExtendWith(ArquillianExtension.class)
class CustomerDaoIT {

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addClasses(
                        BaseDao.class, BaseDaoImpl.class,
                        UserDao.class, UserDaoImpl.class,
                        CustomerDao.class, CustomerDaoImpl.class,
                        TestEntityManagerProducer.class,
                        User.class, Role.class, Permission.class, BlacklistedToken.class, Customer.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsManifestResource(new File("src/main/resources/META-INF/persistence.xml"), "persistence.xml");
    }

    @Inject
    private UserDao userDao;

    @Inject
    private CustomerDao customerDao;

    @Test
    void savedCustomerIsFoundByUser() {
        User user = newUser();

        Customer customer = new Customer();
        customer.setUser(user);
        customer.setFirstName("Ada");
        customer.setLastName("Lovelace");
        customer.setTelephone("555-0100");
        customer.setEmail(user.getEmail());
        customer.setAddress("1 Analytical Engine Way");
        Customer savedCustomer = customerDao.save(customer);

        try {
            Optional<Customer> found = customerDao.findByUser(user);
            assertTrue(found.isPresent());
            assertEquals("Ada", found.get().getFirstName());
        } finally {
            customerDao.delete(savedCustomer);
            userDao.delete(user);
        }
    }

    @Test
    void findByUserIsEmptyWhenNoProfileExists() {
        User user = newUser();

        try {
            Optional<Customer> found = customerDao.findByUser(user);
            assertFalse(found.isPresent());
        } finally {
            userDao.delete(user);
        }
    }

    private User newUser() {
        User user = new User();
        user.setFirstName("Ada");
        user.setLastName("Lovelace");
        user.setEmail("arquillian-" + UUID.randomUUID() + "@example.com");
        user.setPassword("hashed-password");
        user.setEnabled(true);
        user.setAccountLocked(false);
        return userDao.save(user);
    }
}
