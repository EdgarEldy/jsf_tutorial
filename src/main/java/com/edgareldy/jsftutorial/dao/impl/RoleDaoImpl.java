package com.edgareldy.jsftutorial.dao.impl;

import com.edgareldy.jsftutorial.dao.RoleDao;
import com.edgareldy.jsftutorial.entity.Role;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import java.util.Optional;

/**
 * {@link RoleDao} implementation backed by the CDI-produced {@link EntityManager}.
 * <p>
 * Created by Edgar Muhamyangabo on 8/17/26
 * Author : Edgar Muhamyangabo
 * Date : 8/17/26
 * Project : jsf_tutorial
 */
@ApplicationScoped
public class RoleDaoImpl extends BaseDaoImpl<Role, Long> implements RoleDao {

    @Inject
    private EntityManager entityManager;

    public RoleDaoImpl() {
        super(Role.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public Optional<Role> findByName(String roleName) {
        TypedQuery<Role> query = entityManager.createQuery(
                "SELECT r FROM Role r WHERE r.roleName = :roleName", Role.class);
        query.setParameter("roleName", roleName);
        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }
}
