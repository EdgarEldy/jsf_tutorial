package com.edgareldy.jsftutorial.dao;

import com.edgareldy.jsftutorial.entity.Category;
import com.edgareldy.jsftutorial.entity.Product;

import java.util.List;

/**
 * CRUD plus category-scoped queries for {@link Product}: listing a category's
 * products (for the products page's category filter) and counting them (for
 * {@code CategoryService}'s non-empty-category business rule).
 * <p>
 * Created by Edgar Muhamyangabo on 8/18/26
 * Author : Edgar Muhamyangabo
 * Date : 8/18/26
 * Project : jsf_tutorial
 */
public interface ProductDao extends BaseDao<Product, Long> {

    List<Product> findByCategory(Category category);

    long countByCategory(Category category);
}
