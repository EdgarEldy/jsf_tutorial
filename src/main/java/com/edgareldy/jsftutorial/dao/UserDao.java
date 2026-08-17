package com.edgareldy.jsftutorial.dao;

import com.edgareldy.jsftutorial.entity.User;

import java.util.Optional;

/**
 * CRUD plus lookup-by-email for {@link User}, the entry point login and
 * registration resolve accounts through.
 * <p>
 * Created by Edgar Muhamyangabo on 8/17/26
 * Author : Edgar Muhamyangabo
 * Date : 8/17/26
 * Project : jsf_tutorial
 */
public interface UserDao extends BaseDao<User, Long> {

    Optional<User> findByEmail(String email);
}
