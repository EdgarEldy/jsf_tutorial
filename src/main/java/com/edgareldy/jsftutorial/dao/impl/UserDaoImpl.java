package com.edgareldy.jsftutorial.dao.impl;

import com.edgareldy.jsftutorial.dao.UserDao;
import com.edgareldy.jsftutorial.entity.User;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.Optional;

/**
 * {@link UserDao} implementation backed by the CDI-produced {@link EntityManager}.
 * <p>
 * Created by Edgar Muhamyangabo on 8/17/26
 * Author : Edgar Muhamyangabo
 * Date : 8/17/26
 * Project : jsf_tutorial
 */
@ApplicationScoped
public class UserDaoImpl extends BaseDaoImpl<User, Long> implements UserDao {

    @Inject
    private EntityManager entityManager;

    public UserDaoImpl() {
        super(User.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public Optional<User> findByEmail(String email) {
        TypedQuery<User> query = entityManager.createQuery(
                "SELECT u FROM User u WHERE u.email = :email", User.class);
        query.setParameter("email", email);
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
