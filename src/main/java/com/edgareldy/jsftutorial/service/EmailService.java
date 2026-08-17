package com.edgareldy.jsftutorial.service;

import com.edgareldy.jsftutorial.entity.User;

/**
 * Contract for sending account-related emails. Called by {@code UserService}
 * after generating an activation or password-reset token.
 * <p>
 * Created by Edgar Muhamyangabo on 8/17/26
 * Author : Edgar Muhamyangabo
 * Date : 8/17/26
 * Project : jsf_tutorial
 */
public interface EmailService {

    void sendActivationEmail(User user, String token);

    void sendPasswordResetEmail(User user, String token);
}
