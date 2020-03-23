/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jsf_tutorial.controllers;

import com.jsf_tutorial.JPAImpl.CategoryFacadeLocal;
import com.jsf_tutorial.JPAImpl.ProductFacadeLocal;
import com.jsf_tutorial.models.Category;
import com.jsf_tutorial.models.Product;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;

/**
 *
 * @author EDGARELDY
 */
@Named(value = "productController")
@SessionScoped
public class ProductController implements Serializable {

    @EJB
    private CategoryFacadeLocal categoryFacade;

    @EJB
    private ProductFacadeLocal productFacade;
private Product product = new Product();
private Category category = new Category();

    /**
     * Creates a new instance of productController
     */
    public ProductController() {
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
    
    public List<Category> showCategories()
    {
    return this.categoryFacade.findAll();
    }
    
    public String add()
    {
    this.productFacade.create(product);
    this.product= new Product();
    return "/products/index.xhtml?faces-redirect=true";
    }
    
    public List<Product> show()
    {
        return this.productFacade.findAll();
    }
    
    public String edit(int id)
    {
    this.product = this.productFacade.find(id);
    return "/products/edit.xhtml?faces-redirect=true";
    }
    
    public String update()
    {
    this.productFacade.edit(product);
    this.product = new Product();
    return "/products/index.xhtml?faces-redirect=true";
    }
    
    public void delete(int id)
    {
    this.product = this.productFacade.find(id);
    this.productFacade.remove(product);
    }
}
