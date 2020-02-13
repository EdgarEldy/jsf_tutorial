/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.jsf_tutorial.JPAImpl;

import com.jsf_tutorial.models.Category;
import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author EDGARELDY
 */
@Local
public interface CategoryFacadeLocal {

    void create(Category category);

    void edit(Category category);

    void remove(Category category);

    Category find(Object id);

    List<Category> findAll();

    List<Category> findRange(int[] range);

    int count();
    
}
