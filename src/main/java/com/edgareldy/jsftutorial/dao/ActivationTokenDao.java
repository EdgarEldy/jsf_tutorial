package com.edgareldy.jsftutorial.dao;

import com.edgareldy.jsftutorial.entity.ActivationToken;

import java.util.Optional;

/**
 * CRUD plus lookup-by-token for {@link ActivationToken}.
 * <p>
 * Created by Edgar Muhamyangabo on 8/17/26
 * Author : Edgar Muhamyangabo
 * Date : 8/17/26
 * Project : jsf_tutorial
 */
public interface ActivationTokenDao extends BaseDao<ActivationToken, Long> {

    Optional<ActivationToken> findByToken(String token);
}
