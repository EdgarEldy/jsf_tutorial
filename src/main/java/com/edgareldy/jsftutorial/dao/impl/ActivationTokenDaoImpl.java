package com.edgareldy.jsftutorial.dao.impl;

import com.edgareldy.jsftutorial.dao.ActivationTokenDao;
import com.edgareldy.jsftutorial.entity.ActivationToken;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.Optional;

/**
 * {@link ActivationTokenDao} implementation backed by the CDI-produced
 * {@link EntityManager}.
 * <p>
 * Created by Edgar Muhamyangabo on 8/17/26
 * Author : Edgar Muhamyangabo
 * Date : 8/17/26
 * Project : jsf_tutorial
 */
@ApplicationScoped
public class ActivationTokenDaoImpl extends BaseDaoImpl<ActivationToken, Long> implements ActivationTokenDao {

    @Inject
    private EntityManager entityManager;

    public ActivationTokenDaoImpl() {
        super(ActivationToken.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public Optional<ActivationToken> findByToken(String token) {
        TypedQuery<ActivationToken> query = entityManager.createQuery(
                "SELECT t FROM ActivationToken t WHERE t.token = :token", ActivationToken.class);
        query.setParameter("token", token);
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
