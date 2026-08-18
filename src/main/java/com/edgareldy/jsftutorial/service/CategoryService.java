package com.edgareldy.jsftutorial.service;

import com.edgareldy.jsftutorial.entity.Category;
import com.edgareldy.jsftutorial.exception.BusinessRuleException;

import java.util.List;

/**
 * CRUD for {@link Category}, including the rule that a category still
 * holding products can't be deleted.
 * <p>
 * Created by Edgar Muhamyangabo on 8/17/26
 * Author : Edgar Muhamyangabo
 * Date : 8/17/26
 * Project : jsf_tutorial
 */
public interface CategoryService {

    List<Category> findAll();

    Category findById(Long id);

    Category save(Category category);

    /**
     * @throws BusinessRuleException if the category still has products
     */
    void delete(Category category);
}
