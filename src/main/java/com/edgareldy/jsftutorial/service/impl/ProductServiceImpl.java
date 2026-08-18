package com.edgareldy.jsftutorial.service.impl;

import com.edgareldy.jsftutorial.dao.ProductDao;
import com.edgareldy.jsftutorial.entity.Category;
import com.edgareldy.jsftutorial.entity.Product;
import com.edgareldy.jsftutorial.service.ProductService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

/**
 * {@link ProductService} implementation, a thin pass-through to {@link ProductDao}.
 * <p>
 * Created by Edgar Muhamyangabo on 8/18/26
 * Author : Edgar Muhamyangabo
 * Date : 8/18/26
 * Project : jsf_tutorial
 */
@ApplicationScoped
public class ProductServiceImpl implements ProductService {

    @Inject
    private ProductDao productDao;

    @Override
    public List<Product> findAll() {
        return productDao.findAll();
    }

    @Override
    public Product findById(Long id) {
        return productDao.findById(id);
    }

    @Override
    public List<Product> findByCategory(Category category) {
        return productDao.findByCategory(category);
    }

    @Override
    public Product save(Product product) {
        return productDao.save(product);
    }

    @Override
    public void delete(Product product) {
        productDao.delete(product);
    }
}
