package com.edgareldy.jsftutorial.dao;

import java.io.Serializable;
import java.util.List;

/**
 * Generic CRUD contract every entity-specific DAO of this project implements.
 * <p>
 * Created by Edgar Muhamyangabo on 8/17/26
 * Author : Edgar Muhamyangabo
 * Date : 8/17/26
 * Project : jsf_tutorial
 */
public interface BaseDao<T, ID extends Serializable> {

    T findById(ID id);

    List<T> findAll();

    T save(T entity);

    void delete(T entity);
}
