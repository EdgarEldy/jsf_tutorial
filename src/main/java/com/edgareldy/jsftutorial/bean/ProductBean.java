package com.edgareldy.jsftutorial.bean;

import com.edgareldy.jsftutorial.entity.Category;
import com.edgareldy.jsftutorial.entity.Product;
import com.edgareldy.jsftutorial.service.CategoryService;
import com.edgareldy.jsftutorial.service.ProductService;
import com.edgareldy.jsftutorial.util.FacesMessageUtil;

import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;

/**
 * Backs {@code /admin/products.xhtml}: list (optionally filtered by category)
 * /create/edit/delete products through {@link ProductService}. {@code @ViewScoped}
 * for the same reason as {@code CategoryBean}: the AJAX postbacks from the
 * data table and dialog need the loaded state to survive between them.
 * <p>
 * Created by Edgar Muhamyangabo on 8/18/26
 * Author : Edgar Muhamyangabo
 * Date : 8/18/26
 * Project : jsf_tutorial
 */
@Named
@ViewScoped
public class ProductBean implements Serializable {

    @Inject
    private ProductService productService;

    @Inject
    private CategoryService categoryService;

    private List<Category> categories;
    private List<Product> products;
    private Long categoryFilterId;
    private Product product;
    private Long productCategoryId;

    @PostConstruct
    public void init() {
        categories = categoryService.findAll();
        products = productService.findAll();
    }

    public void filterByCategory() {
        products = categoryFilterId == null
                ? productService.findAll()
                : productService.findByCategory(categoryService.findById(categoryFilterId));
    }

    public void openNew() {
        product = new Product();
        productCategoryId = null;
    }

    public void edit(Product selected) {
        product = selected;
        productCategoryId = selected.getCategory().getId();
    }

    public void save() {
        product.setCategory(categoryService.findById(productCategoryId));
        productService.save(product);
        filterByCategory();
        product = null;
        FacesMessageUtil.addInfo("Product saved.");
    }

    public void delete(Product selected) {
        productService.delete(selected);
        filterByCategory();
        FacesMessageUtil.addInfo("Product deleted.");
    }

    public List<Category> getCategories() {
        return categories;
    }

    public List<Product> getProducts() {
        return products;
    }

    public Long getCategoryFilterId() {
        return categoryFilterId;
    }

    public void setCategoryFilterId(Long categoryFilterId) {
        this.categoryFilterId = categoryFilterId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Long getProductCategoryId() {
        return productCategoryId;
    }

    public void setProductCategoryId(Long productCategoryId) {
        this.productCategoryId = productCategoryId;
    }
}
