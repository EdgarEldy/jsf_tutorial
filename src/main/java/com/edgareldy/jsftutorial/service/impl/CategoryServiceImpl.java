package com.edgareldy.jsftutorial.service.impl;

import com.edgareldy.jsftutorial.dao.CategoryDao;
import com.edgareldy.jsftutorial.dao.ProductDao;
import com.edgareldy.jsftutorial.entity.Category;
import com.edgareldy.jsftutorial.exception.BusinessRuleException;
import com.edgareldy.jsftutorial.service.CategoryService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

/**
 * {@link CategoryService} implementation. Enforces the non-empty-category
 * rule against {@link ProductDao}'s count query rather than duplicating that
 * count logic here.
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

    @Inject
    private ProductDao productDao;

    @Override
    public List<Category> findAll() {
        return categoryDao.findAll();
    }

    @Override
    public Category findById(Long id) {
        return categoryDao.findById(id);
    }

    @Override
    public Category save(Category category) {
        return categoryDao.save(category);
    }

    @Override
    public void delete(Category category) {
        if (productDao.countByCategory(category) > 0) {
            throw new BusinessRuleException("Cannot delete a category that still has products.");
        }
        categoryDao.delete(category);
    }
}
