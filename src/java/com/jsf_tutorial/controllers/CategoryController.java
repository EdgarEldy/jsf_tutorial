/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jsf_tutorial.controllers;

import com.jsf_tutorial.JPAImpl.CategoryFacadeLocal;
import com.jsf_tutorial.models.Category;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.List;
import javax.ejb.EJB;

/**
 *
 * @author EDGARELDY
 */
@Named(value = "categoryController")
@SessionScoped
public class CategoryController implements Serializable {

    @EJB
    private CategoryFacadeLocal categoryFacade;
private Category category =new Category();
    /**
     * Creates a new instance of categoryController
     */
    public CategoryController() {
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
    
    public String add()
    {
    this.categoryFacade.create(category);
    this.category= new Category();
    return "/categories/index.xhtml?faces-redirect=true";
    }
    
    public List<Category> show()
    {
    return this.categoryFacade.findAll();
    }
    
    public String edit(int id)
    {
    this.category = this.categoryFacade.find(id);
    return "/categories/edit.xhtml?faces-redirect=true";
    }
    
    public String update()
    {
    this.categoryFacade.edit(category);
    this.category = new Category();
    return "/categories/index.xhtml?faces-redirect=true";
    }
    
    public void delete(int id)
    {
    category = this.categoryFacade.find(id);
    this.categoryFacade.remove(category);
    }
}
