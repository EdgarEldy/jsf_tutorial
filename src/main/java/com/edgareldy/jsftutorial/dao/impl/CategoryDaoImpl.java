package com.edgareldy.jsftutorial.dao.impl;

import com.edgareldy.jsftutorial.dao.CategoryDao;
import com.edgareldy.jsftutorial.entity.Category;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;

/**
 * {@link CategoryDao} implementation backed by the CDI-produced {@link EntityManager}.
 * <p>
 * Created by Edgar Muhamyangabo on 8/17/26
 * Author : Edgar Muhamyangabo
 * Date : 8/17/26
 * Project : jsf_tutorial
 */
@ApplicationScoped
public class CategoryDaoImpl extends BaseDaoImpl<Category, Long> implements CategoryDao {

    @Inject
    private EntityManager entityManager;

    public CategoryDaoImpl() {
        super(Category.class);
    }

    @Override
    protected EntityManager getEntityManager() {
        return entityManager;
    }
}
