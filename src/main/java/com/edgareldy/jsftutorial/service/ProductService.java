package com.edgareldy.jsftutorial.service;

import com.edgareldy.jsftutorial.entity.Category;
import com.edgareldy.jsftutorial.entity.Product;

import java.util.List;

/**
 * CRUD for {@link Product}, plus listing by {@link Category} for the products
 * page's category filter.
 * <p>
 * Created by Edgar Muhamyangabo on 8/18/26
 * Author : Edgar Muhamyangabo
 * Date : 8/18/26
 * Project : jsf_tutorial
 */
public interface ProductService {

    List<Product> findAll();

    Product findById(Long id);

    List<Product> findByCategory(Category category);

    Product save(Product product);

    void delete(Product product);
}
