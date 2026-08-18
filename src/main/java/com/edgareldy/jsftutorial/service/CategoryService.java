package com.edgareldy.jsftutorial.service;

import com.edgareldy.jsftutorial.entity.Category;

import java.util.List;

/**
 * Plain CRUD for {@link Category} on this branch; feature/products adds the
 * business rule rejecting deletion of a non-empty category once {@code Product}
 * exists to make a category non-empty.
 * <p>
 * Created by Edgar Muhamyangabo on 8/17/26
 * Author : Edgar Muhamyangabo
 * Date : 8/17/26
 * Project : jsf_tutorial
 */
public interface CategoryService {

    List<Category> findAll();

    Category save(Category category);

    void delete(Category category);
}
