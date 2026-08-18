package com.edgareldy.jsftutorial.service.impl;

import com.edgareldy.jsftutorial.dao.CategoryDao;
import com.edgareldy.jsftutorial.entity.Category;
import com.edgareldy.jsftutorial.service.CategoryService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

/**
 * {@link CategoryService} implementation, a thin pass-through to {@link CategoryDao}
 * on this branch.
 * <p>
 * Created by Edgar Muhamyangabo on 8/17/26
 * Author : Edgar Muhamyangabo
 * Date : 8/17/26
 * Project : jsf_tutorial
 */
@ApplicationScoped
public class CategoryServiceImpl implements CategoryService {

    @Inject
    private CategoryDao categoryDao;

    @Override
    public List<Category> findAll() {
        return categoryDao.findAll();
    }

    @Override
    public Category save(Category category) {
        return categoryDao.save(category);
    }

    @Override
    public void delete(Category category) {
        categoryDao.delete(category);
    }
}
