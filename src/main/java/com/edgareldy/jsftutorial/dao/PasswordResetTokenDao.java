package com.edgareldy.jsftutorial.dao;

import com.edgareldy.jsftutorial.entity.PasswordResetToken;

import java.util.Optional;

/**
 * CRUD plus lookup-by-token for {@link PasswordResetToken}.
 * <p>
 * Created by Edgar Muhamyangabo on 8/17/26
 * Author : Edgar Muhamyangabo
 * Date : 8/17/26
 * Project : jsf_tutorial
 */
public interface PasswordResetTokenDao extends BaseDao<PasswordResetToken, Long> {

    Optional<PasswordResetToken> findByToken(String token);
}
