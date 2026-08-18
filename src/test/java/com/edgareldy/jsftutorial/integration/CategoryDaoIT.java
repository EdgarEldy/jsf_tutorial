package com.edgareldy.jsftutorial.integration;

import com.edgareldy.jsftutorial.dao.BaseDao;
import com.edgareldy.jsftutorial.dao.CategoryDao;
import com.edgareldy.jsftutorial.dao.impl.BaseDaoImpl;
import com.edgareldy.jsftutorial.dao.impl.CategoryDaoImpl;
import com.edgareldy.jsftutorial.entity.Category;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit5.ArquillianExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import javax.inject.Inject;
import java.io.File;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test for {@link CategoryDaoImpl} against a real {@link javax.persistence.EntityManager},
 * packaged as an Arquillian Weld-SE micro-deployment.
 * <p>
 * Created by Edgar Muhamyangabo on 8/17/26
 * Author : Edgar Muhamyangabo
 * Date : 8/17/26
 * Project : jsf_tutorial
 */
@ExtendWith(ArquillianExtension.class)
class CategoryDaoIT {

    @Deployment
    public static JavaArchive createDeployment() {
        return ShrinkWrap.create(JavaArchive.class)
                .addClasses(
                        BaseDao.class, BaseDaoImpl.class,
                        CategoryDao.class, CategoryDaoImpl.class,
                        TestEntityManagerProducer.class,
                        Category.class)
                .addAsManifestResource(EmptyAsset.INSTANCE, "beans.xml")
                .addAsManifestResource(new File("src/main/resources/META-INF/persistence.xml"), "persistence.xml");
    }

    @Inject
    private CategoryDao categoryDao;

    private Category savedCategory;

    @AfterEach
    void cleanUp() {
        if (savedCategory != null && savedCategory.getId() != null) {
            categoryDao.delete(savedCategory);
        }
    }

    @Test
    void savedCategoryIsFoundInFindAll() {
        Category category = new Category();
        category.setCategoryName("Arquillian-" + UUID.randomUUID());
        savedCategory = categoryDao.save(category);

        List<Category> all = categoryDao.findAll();

        assertTrue(all.stream().anyMatch(c -> c.getId().equals(savedCategory.getId())));
    }
}
