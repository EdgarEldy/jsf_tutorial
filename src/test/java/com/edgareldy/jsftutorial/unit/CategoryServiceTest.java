package com.edgareldy.jsftutorial.unit;

import com.edgareldy.jsftutorial.dao.CategoryDao;
import com.edgareldy.jsftutorial.entity.Category;
import com.edgareldy.jsftutorial.service.impl.CategoryServiceImpl;
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
 * Unit tests for {@link CategoryServiceImpl}, with {@link CategoryDao} mocked.
 * Plain pass-through on this branch, no business rule yet since Product
 * doesn't exist until feature/products.
 * <p>
 * Created by Edgar Muhamyangabo on 8/17/26
 * Author : Edgar Muhamyangabo
 * Date : 8/17/26
 * Project : jsf_tutorial
 */
@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryDao categoryDao;

    @InjectMocks
    private CategoryServiceImpl categoryService;

    @Test
    void findAllDelegatesToTheDao() {
        Category category = new Category();
        category.setCategoryName("Electronics");
        when(categoryDao.findAll()).thenReturn(Collections.singletonList(category));

        List<Category> result = categoryService.findAll();

        assertEquals(1, result.size());
        assertEquals("Electronics", result.get(0).getCategoryName());
    }

    @Test
    void saveDelegatesToTheDao() {
        Category category = new Category();
        category.setCategoryName("Electronics");
        when(categoryDao.save(category)).thenReturn(category);

        Category result = categoryService.save(category);

        assertEquals(category, result);
        verify(categoryDao).save(category);
    }

    @Test
    void deleteDelegatesToTheDao() {
        Category category = new Category();

        categoryService.delete(category);

        verify(categoryDao).delete(category);
    }
}
