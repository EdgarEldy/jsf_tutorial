package com.edgareldy.jsftutorial.dao.impl;

import com.edgareldy.jsftutorial.dao.ProductDao;
import com.edgareldy.jsftutorial.entity.Category;
import com.edgareldy.jsftutorial.entity.Product;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * {@link ProductDao} implementation backed by the CDI-produced {@link EntityManager}.
 * <p>
 * Created by Edgar Muhamyangabo on 8/18/26
 * Author : Edgar Muhamyangabo
 * Date : 8/18/26
 * Project : jsf_tutorial
 */
@ApplicationScoped
public class ProductDaoImpl extends BaseDaoImpl<Product, Long> implements ProductDao {

    @Inject
    private EntityManager entityManager;

    public ProductDaoImpl() {
        super(Product.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }

    @Override
    public List<Product> findByCategory(Category category) {
        TypedQuery<Product> query = entityManager.createQuery(
                "SELECT p FROM Product p WHERE p.category = :category", Product.class);
        query.setParameter("category", category);
        return query.getResultList();
    }

    @Override
    public long countByCategory(Category category) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(p) FROM Product p WHERE p.category = :category", Long.class);
        query.setParameter("category", category);
        return query.getSingleResult();
    }
}
