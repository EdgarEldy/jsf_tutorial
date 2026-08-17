package com.edgareldy.jsftutorial.dao.impl;

import com.edgareldy.jsftutorial.dao.BaseDao;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.io.Serializable;
import java.util.List;

/**
 * Generic {@link EntityManager}-based implementation of {@link BaseDao},
 * managing its own RESOURCE_LOCAL transactions around every write since
 * Tomcat has no JTA transaction manager to do it for us.
 * <p>
 * Created by Edgar Muhamyangabo on 8/17/26
 * Author : Edgar Muhamyangabo
 * Date : 8/17/26
 * Project : jsf_tutorial
 */
public abstract class BaseDaoImpl<T, ID extends Serializable> implements BaseDao<T, ID> {

    private final Class<T> entityClass;

    protected BaseDaoImpl(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager();

    @Override
    public T findById(ID id) {
        return getEntityManager().find(entityClass, id);
    }

    @Override
    public List<T> findAll() {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<T> cq = cb.createQuery(entityClass);
        cq.select(cq.from(entityClass));
        return em.createQuery(cq).getResultList();
    }

    @Override
    public T save(T entity) {
        EntityManager em = getEntityManager();
        return inTransaction(em, () -> {
            if (em.contains(entity)) {
                return em.merge(entity);
            }
            em.persist(entity);
            return entity;
        });
    }

    @Override
    public void delete(T entity) {
        EntityManager em = getEntityManager();
        inTransaction(em, () -> {
            em.remove(em.contains(entity) ? entity : em.merge(entity));
            return null;
        });
    }

    /**
     * RESOURCE_LOCAL persistence unit (Tomcat has no JTA transaction manager):
     * every write needs an explicit transaction around it.
     */
    private <R> R inTransaction(EntityManager em, java.util.function.Supplier<R> work) {
        EntityTransaction tx = em.getTransaction();
        boolean startedHere = !tx.isActive();
        if (startedHere) {
            tx.begin();
        }
        try {
            R result = work.get();
            if (startedHere) {
                tx.commit();
            }
            return result;
        } catch (RuntimeException e) {
            if (startedHere && tx.isActive()) {
                tx.rollback();
            }
            throw e;
        }
    }
}
