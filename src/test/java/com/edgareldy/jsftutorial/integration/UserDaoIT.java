package com.edgareldy.jsftutorial.integration;

import com.edgareldy.jsftutorial.dao.BaseDao;
import com.edgareldy.jsftutorial.dao.UserDao;
import com.edgareldy.jsftutorial.dao.impl.BaseDaoImpl;
import com.edgareldy.jsftutorial.dao.impl.UserDaoImpl;
import com.edgareldy.jsftutorial.entity.BlacklistedToken;
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

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test for {@link UserDaoImpl} against a real {@link javax.persistence.EntityManager},
 * packaged as an Arquillian Weld-SE micro-deployment (no servlet container
 * needed to exercise a plain CDI + JPA DAO).
 * <p>
 * Setup and cleanup happen entirely inside each {@code @Test} method rather
 * than {@code @AfterEach}: Arquillian's local protocol invokes the
 * {@code @Test} method on a different test class instance than the one
 * JUnit runs lifecycle callbacks on, so an instance field set in
 * {@code @Test} reads back as {@code null} in {@code @AfterEach}, making a
 * cleanup guarded by {@code if (field != null)} there a silent no-op.
 * <p>
 * Created by Edgar Muhamyangabo on 8/17/26
 * Author : Edgar Muhamyangabo
 * Date : 8/17/26
 * Project : jsf_tutorial
 */
@ExtendWith(ArquillianExtension.class)
class UserDaoIT {

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addClasses(
                        BaseDao.class, BaseDaoImpl.class,
                        UserDao.class, UserDaoImpl.class,
                        TestEntityManagerProducer.class,
                        User.class, Role.class, Permission.class, BlacklistedToken.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsManifestResource(new File("src/main/resources/META-INF/persistence.xml"), "persistence.xml");
    }

    @Inject
    private UserDao userDao;

    @Test
    void findsAPersistedUserByEmail() {
        String email = "arquillian-" + UUID.randomUUID() + "@example.com";
        User user = new User();
        user.setFirstName("Ada");
        user.setLastName("Lovelace");
        user.setEmail(email);
        user.setPassword("hashed-password");
        user.setEnabled(true);
        user.setAccountLocked(false);
        User savedUser = userDao.save(user);

        try {
            Optional<User> found = userDao.findByEmail(email);
            assertTrue(found.isPresent());
        } finally {
            userDao.delete(savedUser);
        }
    }

    @Test
    void findByEmailIsEmptyForAnUnknownEmail() {
        Optional<User> found = userDao.findByEmail("no-such-user-" + UUID.randomUUID() + "@example.com");

        assertFalse(found.isPresent());
    }
}
