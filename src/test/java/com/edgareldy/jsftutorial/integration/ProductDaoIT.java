package com.edgareldy.jsftutorial.integration;

import com.edgareldy.jsftutorial.dao.BaseDao;
import com.edgareldy.jsftutorial.dao.CategoryDao;
import com.edgareldy.jsftutorial.dao.ProductDao;
import com.edgareldy.jsftutorial.dao.impl.BaseDaoImpl;
import com.edgareldy.jsftutorial.dao.impl.CategoryDaoImpl;
import com.edgareldy.jsftutorial.dao.impl.ProductDaoImpl;
import com.edgareldy.jsftutorial.entity.Category;
import com.edgareldy.jsftutorial.entity.Product;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import java.io.File;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Integration test for {@link ProductDaoImpl} against a real {@link javax.persistence.EntityManager},
 * covering the category-scoped queries {@code CategoryServiceImpl}'s business
 * rule and the products page's category filter both depend on.
 * <p>
 * Setup and cleanup happen entirely inside each {@code @Test} method rather
 * than {@code @BeforeEach}/{@code @AfterEach}: Arquillian's local protocol
 * invokes the {@code @Test} method on a different test class instance than
 * the one JUnit runs lifecycle callbacks on, so instance fields don't cross
 * that boundary (confirmed empirically: a category created in
 * {@code @BeforeEach} read back as {@code null} inside {@code @Test}).
 * <p>
 * Created by Edgar Muhamyangabo on 8/18/26
 * Author : Edgar Muhamyangabo
 * Date : 8/18/26
 * Project : jsf_tutorial
 */
@ExtendWith(ArquillianExtension.class)
class ProductDaoIT {

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addClasses(
                        BaseDao.class, BaseDaoImpl.class,
                        CategoryDao.class, CategoryDaoImpl.class,
                        ProductDao.class, ProductDaoImpl.class,
                        TestEntityManagerProducer.class,
                        Category.class, Product.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsManifestResource(new File("src/main/resources/META-INF/persistence.xml"), "persistence.xml");
    }

    @Inject
    private CategoryDao categoryDao;

    @Inject
    private ProductDao productDao;

    @Test
    void countByCategoryIsZeroForAnEmptyCategory() {
        Category category = categoryDao.save(newCategory());
        try {
            assertEquals(0, productDao.countByCategory(category));
        } finally {
            categoryDao.delete(category);
        }
    }

    @Test
    void savedProductIsFoundByCategoryAndCounted() {
        Category category = categoryDao.save(newCategory());
        Product product = new Product();
        product.setCategory(category);
        product.setProductName("Widget");
        product.setUnitPrice(9.99f);
        product = productDao.save(product);

        try {
            List<Product> found = productDao.findByCategory(category);

            assertEquals(1, found.size());
            assertEquals("Widget", found.get(0).getProductName());
            assertEquals(1, productDao.countByCategory(category));
        } finally {
            productDao.delete(product);
            categoryDao.delete(category);
        }
    }

    private Category newCategory() {
        Category category = new Category();
        category.setCategoryName("Arquillian-" + UUID.randomUUID());
        return category;
    }
}
