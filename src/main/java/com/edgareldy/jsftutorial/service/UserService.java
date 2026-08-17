package com.edgareldy.jsftutorial.service;

import com.edgareldy.jsftutorial.entity.User;

import java.util.Optional;
import java.util.Set;

/**
 * Registration, activation, login, and password-reset business logic for
 * {@link User} accounts. All password comparisons go through
 * {@code PasswordHasher}, never a plain-text comparison.
 * <p>
 * Created by Edgar Muhamyangabo on 8/17/26
 * Author : Edgar Muhamyangabo
 * Date : 8/17/26
 * Project : jsf_tutorial
 */
public interface UserService {

    /**
     * Creates a disabled account and an activation token, and triggers the
     * activation email. Empty if the email is already registered.
     */
    Optional<User> register(String firstName, String lastName, String email, String rawPassword);

    /**
     * Validates the activation token and enables the account. False if the
     * token doesn't exist, is already validated, or has expired.
     */
    boolean activate(String token);

    /**
     * Empty unless the email/password pair matches an enabled, unlocked account.
     */
    Optional<User> login(String email, String rawPassword);

    /**
     * Creates a password-reset token and triggers the reset email. No-op
     * (not an error) if the email doesn't match any account, to avoid
     * revealing which emails are registered.
     */
    void forgotPassword(String email);

    /**
     * Validates the reset token, updates the password, and consumes the
     * token. False if the token doesn't exist or has expired.
     */
    boolean resetPassword(String token, String newPassword);

    /**
     * All {@code "resource:action"} permission strings granted to the user
     * through its roles, for {@code SessionUserHolder}.
     */
    Set<String> resolvePermissions(User user);
}
