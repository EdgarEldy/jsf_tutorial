package com.edgareldy.jsftutorial.service.impl;

import com.edgareldy.jsftutorial.dao.ActivationTokenDao;
import com.edgareldy.jsftutorial.dao.PasswordResetTokenDao;
import com.edgareldy.jsftutorial.dao.UserDao;
import com.edgareldy.jsftutorial.entity.ActivationToken;
import com.edgareldy.jsftutorial.entity.PasswordResetToken;
import com.edgareldy.jsftutorial.entity.Permission;
import com.edgareldy.jsftutorial.entity.Role;
import com.edgareldy.jsftutorial.entity.User;
import com.edgareldy.jsftutorial.security.PasswordHasher;
import com.edgareldy.jsftutorial.service.EmailService;
import com.edgareldy.jsftutorial.service.UserService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * {@link UserService} implementation: registration, activation, login,
 * forgotten/reset password, all backed by {@link UserDao} and the token DAOs.
 * <p>
 * Created by Edgar Muhamyangabo on 8/17/26
 * Author : Edgar Muhamyangabo
 * Date : 8/17/26
 * Project : jsf_tutorial
 */
@ApplicationScoped
public class UserServiceImpl implements UserService {

    private static final int ACTIVATION_TOKEN_VALIDITY_HOURS = 24;
    private static final int PASSWORD_RESET_TOKEN_VALIDITY_HOURS = 1;
    private static final String PASSWORD_RESET_TOKEN_TYPE = "PASSWORD_RESET";

    @Inject
    private UserDao userDao;

    @Inject
    private ActivationTokenDao activationTokenDao;

    @Inject
    private PasswordResetTokenDao passwordResetTokenDao;

    @Inject
    private EmailService emailService;

    @Override
    public Optional<User> register(String firstName, String lastName, String email, String rawPassword) {
        if (userDao.findByEmail(email).isPresent()) {
            return Optional.empty();
        }

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword(PasswordHasher.hash(rawPassword));
        user.setEnabled(false);
        user.setAccountLocked(false);
        userDao.save(user);

        ActivationToken activationToken = new ActivationToken();
        activationToken.setUser(user);
        activationToken.setToken(UUID.randomUUID().toString());
        activationToken.setCreatedAt(LocalDateTime.now());
        activationToken.setExpiresAt(LocalDateTime.now().plusHours(ACTIVATION_TOKEN_VALIDITY_HOURS));
        activationTokenDao.save(activationToken);

        emailService.sendActivationEmail(user, activationToken.getToken());
        return Optional.of(user);
    }

    @Override
    public boolean activate(String token) {
        Optional<ActivationToken> activationToken = activationTokenDao.findByToken(token);
        if (!activationToken.isPresent()) {
            return false;
        }
        ActivationToken found = activationToken.get();
        if (found.getValidatedAt() != null) {
            return false;
        }
        if (found.getExpiresAt() != null && found.getExpiresAt().isBefore(LocalDateTime.now())) {
            return false;
        }

        found.setValidatedAt(LocalDateTime.now());
        activationTokenDao.save(found);

        User user = found.getUser();
        user.setEnabled(true);
        userDao.save(user);
        return true;
    }

    @Override
    public Optional<User> login(String email, String rawPassword) {
        return userDao.findByEmail(email)
                .filter(user -> user.isEnabled() && !user.isAccountLocked())
                .filter(user -> PasswordHasher.matches(rawPassword, user.getPassword()));
    }

    @Override
    public void forgotPassword(String email) {
        Optional<User> user = userDao.findByEmail(email);
        if (!user.isPresent()) {
            return;
        }

        PasswordResetToken resetToken = new PasswordResetToken();
        resetToken.setUser(user.get());
        resetToken.setToken(UUID.randomUUID().toString());
        resetToken.setType(PASSWORD_RESET_TOKEN_TYPE);
        resetToken.setExpiryDate(LocalDateTime.now().plusHours(PASSWORD_RESET_TOKEN_VALIDITY_HOURS));
        passwordResetTokenDao.save(resetToken);

        emailService.sendPasswordResetEmail(user.get(), resetToken.getToken());
    }

    @Override
    public boolean resetPassword(String token, String newPassword) {
        Optional<PasswordResetToken> resetToken = passwordResetTokenDao.findByToken(token);
        if (!resetToken.isPresent()) {
            return false;
        }
        PasswordResetToken found = resetToken.get();
        if (found.getExpiryDate().isBefore(LocalDateTime.now())) {
            return false;
        }

        User user = found.getUser();
        user.setPassword(PasswordHasher.hash(newPassword));
        userDao.save(user);

        // A password reset token has no value once consumed, unlike an
        // activation token whose history is worth keeping.
        passwordResetTokenDao.delete(found);
        return true;
    }

    @Override
    public Set<String> resolvePermissions(User user) {
        Set<String> permissions = new HashSet<>();
        for (Role role : user.getRoles()) {
            for (Permission permission : role.getPermissions()) {
                permissions.add(permission.getResource() + ":" + permission.getAction());
            }
        }
        return permissions;
    }
}
