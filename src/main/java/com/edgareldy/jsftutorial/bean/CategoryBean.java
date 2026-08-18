package com.edgareldy.jsftutorial.bean;

import com.edgareldy.jsftutorial.entity.Category;
import com.edgareldy.jsftutorial.service.CategoryService;
import com.edgareldy.jsftutorial.util.FacesMessageUtil;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

/**
 * Backs {@code /admin/categories.xhtml}: list/create/edit/delete categories
 * through {@link CategoryService}. {@code @ViewScoped} so the {@code p:dataTable}
 * selection and dialog state survive AJAX postbacks within the page.
 * <p>
 * Created by Edgar Muhamyangabo on 8/17/26
 * Author : Edgar Muhamyangabo
 * Date : 8/17/26
 * Project : jsf_tutorial
 */
@Named
@ViewScoped
public class CategoryBean implements Serializable {

    @Inject
    private CategoryService categoryService;

    private List<Category> categories;
    private Category category;

    @PostConstruct
    public void init() {
        categories = categoryService.findAll();
    }

    public void openNew() {
        category = new Category();
    }

    public void edit(Category selected) {
        category = selected;
    }

    public void save() {
        categoryService.save(category);
        categories = categoryService.findAll();
        category = null;
        FacesMessageUtil.addInfo("Category saved.");
    }

    public void delete(Category selected) {
        categoryService.delete(selected);
        categories = categoryService.findAll();
        FacesMessageUtil.addInfo("Category deleted.");
    }

    public List<Category> getCategories() {
        return categories;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
