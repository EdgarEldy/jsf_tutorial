package com.edgareldy.jsftutorial.unit;

import com.edgareldy.jsftutorial.dao.ProductDao;
import com.edgareldy.jsftutorial.entity.Category;
import com.edgareldy.jsftutorial.entity.Product;
import com.edgareldy.jsftutorial.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link ProductServiceImpl}, with {@link ProductDao} mocked.
 * <p>
 * Created by Edgar Muhamyangabo on 8/18/26
 * Author : Edgar Muhamyangabo
 * Date : 8/18/26
 * Project : jsf_tutorial
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductDao productDao;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void findByCategoryDelegatesToTheDao() {
        Category category = new Category();
        Product product = new Product();
        product.setCategory(category);
        when(productDao.findByCategory(category)).thenReturn(Collections.singletonList(product));

        List<Product> result = productService.findByCategory(category);

        assertEquals(1, result.size());
        verify(productDao).findByCategory(category);
    }

    @Test
    void saveDelegatesToTheDao() {
        Product product = new Product();
        when(productDao.save(product)).thenReturn(product);

        Product result = productService.save(product);

        assertEquals(product, result);
    }

    @Test
    void deleteDelegatesToTheDao() {
        Product product = new Product();

        productService.delete(product);

        verify(productDao).delete(product);
    }
}
