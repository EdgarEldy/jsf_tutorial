package com.edgareldy.jsftutorial.dao.impl;

import com.edgareldy.jsftutorial.dao.PasswordResetTokenDao;
import com.edgareldy.jsftutorial.entity.PasswordResetToken;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.Optional;

/**
 * {@link PasswordResetTokenDao} implementation backed by the CDI-produced
 * {@link EntityManager}.
 * <p>
 * Created by Edgar Muhamyangabo on 8/17/26
 * Author : Edgar Muhamyangabo
 * Date : 8/17/26
 * Project : jsf_tutorial
 */
@ApplicationScoped
public class PasswordResetTokenDaoImpl extends BaseDaoImpl<PasswordResetToken, Long> implements PasswordResetTokenDao {

    @Inject
    private EntityManager entityManager;

    public PasswordResetTokenDaoImpl() {
        super(PasswordResetToken.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public Optional<PasswordResetToken> findByToken(String token) {
        TypedQuery<PasswordResetToken> query = entityManager.createQuery(
                "SELECT t FROM PasswordResetToken t WHERE t.token = :token", PasswordResetToken.class);
        query.setParameter("token", token);
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
