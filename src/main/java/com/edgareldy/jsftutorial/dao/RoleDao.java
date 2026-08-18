package com.edgareldy.jsftutorial.dao;

import com.edgareldy.jsftutorial.entity.Role;

import java.util.Optional;

/**
 * CRUD plus lookup-by-name for {@link Role}.
 * <p>
 * Created by Edgar Muhamyangabo on 8/17/26
 * Author : Edgar Muhamyangabo
 * Date : 8/17/26
 * Project : jsf_tutorial
 */
public interface RoleDao extends BaseDao<Role, Long> {

    Optional<Role> findByName(String roleName);
}
