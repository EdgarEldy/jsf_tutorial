package com.edgareldy.jsftutorial.unit;

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
import com.edgareldy.jsftutorial.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link UserServiceImpl}, with every DAO and
 * {@link EmailService} mocked.
 * <p>
 * Created by Edgar Muhamyangabo on 8/17/26
 * Author : Edgar Muhamyangabo
 * Date : 8/17/26
 * Project : jsf_tutorial
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserDao userDao;

    @Mock
    private ActivationTokenDao activationTokenDao;

    @Mock
    private PasswordResetTokenDao passwordResetTokenDao;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void registerCreatesADisabledAccountAndSendsAnActivationEmail() {
        when(userDao.findByEmail("ada@example.com")).thenReturn(Optional.empty());

        Optional<User> result = userService.register("Ada", "Lovelace", "ada@example.com", "s3cret");

        assertTrue(result.isPresent());
        assertFalse(result.get().isEnabled());
        verify(userDao).save(any(User.class));
        verify(activationTokenDao).save(any(ActivationToken.class));
        verify(emailService).sendActivationEmail(eq(result.get()), anyString());
    }

    @Test
    void registerRejectsAnAlreadyRegisteredEmail() {
        when(userDao.findByEmail("ada@example.com")).thenReturn(Optional.of(new User()));

        Optional<User> result = userService.register("Ada", "Lovelace", "ada@example.com", "s3cret");

        assertFalse(result.isPresent());
        verify(userDao, never()).save(any(User.class));
        verify(emailService, never()).sendActivationEmail(any(), anyString());
    }

    @Test
    void activateEnablesTheAccountForAValidToken() {
        User user = new User();
        user.setEnabled(false);
        ActivationToken token = new ActivationToken();
        token.setUser(user);
        token.setExpiresAt(LocalDateTime.now().plusHours(1));
        when(activationTokenDao.findByToken("valid-token")).thenReturn(Optional.of(token));

        boolean activated = userService.activate("valid-token");

        assertTrue(activated);
        assertTrue(user.isEnabled());
        assertEquals(token, captureSavedToken());
    }

    private ActivationToken captureSavedToken() {
        ArgumentCaptor<ActivationToken> captor = ArgumentCaptor.forClass(ActivationToken.class);
        verify(activationTokenDao).save(captor.capture());
        return captor.getValue();
    }

    @Test
    void activateRejectsAnUnknownToken() {
        when(activationTokenDao.findByToken("unknown")).thenReturn(Optional.empty());

        assertFalse(userService.activate("unknown"));
        verify(userDao, never()).save(any(User.class));
    }

    @Test
    void activateRejectsAnExpiredToken() {
        ActivationToken token = new ActivationToken();
        token.setUser(new User());
        token.setExpiresAt(LocalDateTime.now().minusMinutes(1));
        when(activationTokenDao.findByToken("expired")).thenReturn(Optional.of(token));

        assertFalse(userService.activate("expired"));
    }

    @Test
    void activateRejectsAnAlreadyValidatedToken() {
        ActivationToken token = new ActivationToken();
        token.setUser(new User());
        token.setValidatedAt(LocalDateTime.now().minusDays(1));
        when(activationTokenDao.findByToken("used")).thenReturn(Optional.of(token));

        assertFalse(userService.activate("used"));
    }

    @Test
    void loginSucceedsForAnEnabledUnlockedAccountWithTheRightPassword() {
        User user = new User();
        user.setEnabled(true);
        user.setAccountLocked(false);
        user.setPassword(PasswordHasher.hash("s3cret"));
        when(userDao.findByEmail("ada@example.com")).thenReturn(Optional.of(user));

        Optional<User> result = userService.login("ada@example.com", "s3cret");

        assertTrue(result.isPresent());
    }

    @Test
    void loginFailsForTheWrongPassword() {
        User user = new User();
        user.setEnabled(true);
        user.setPassword(PasswordHasher.hash("s3cret"));
        when(userDao.findByEmail("ada@example.com")).thenReturn(Optional.of(user));

        assertFalse(userService.login("ada@example.com", "wrong").isPresent());
    }

    @Test
    void loginFailsForADisabledAccount() {
        User user = new User();
        user.setEnabled(false);
        when(userDao.findByEmail("ada@example.com")).thenReturn(Optional.of(user));

        assertFalse(userService.login("ada@example.com", "s3cret").isPresent());
    }

    @Test
    void loginFailsForALockedAccount() {
        User user = new User();
        user.setEnabled(true);
        user.setAccountLocked(true);
        when(userDao.findByEmail("ada@example.com")).thenReturn(Optional.of(user));

        assertFalse(userService.login("ada@example.com", "s3cret").isPresent());
    }

    @Test
    void loginFailsForAnUnknownEmail() {
        when(userDao.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        assertFalse(userService.login("unknown@example.com", "s3cret").isPresent());
    }

    @Test
    void forgotPasswordCreatesATokenAndSendsAnEmailForAKnownAddress() {
        User user = new User();
        when(userDao.findByEmail("ada@example.com")).thenReturn(Optional.of(user));

        userService.forgotPassword("ada@example.com");

        verify(passwordResetTokenDao).save(any(PasswordResetToken.class));
        verify(emailService).sendPasswordResetEmail(eq(user), anyString());
    }

    @Test
    void forgotPasswordIsANoOpForAnUnknownAddress() {
        when(userDao.findByEmail("unknown@example.com")).thenReturn(Optional.empty());

        userService.forgotPassword("unknown@example.com");

        verify(passwordResetTokenDao, never()).save(any());
        verify(emailService, never()).sendPasswordResetEmail(any(), anyString());
    }

    @Test
    void resetPasswordUpdatesThePasswordAndConsumesTheToken() {
        User user = new User();
        user.setPassword(PasswordHasher.hash("old-password"));
        PasswordResetToken token = new PasswordResetToken();
        token.setUser(user);
        token.setExpiryDate(LocalDateTime.now().plusHours(1));
        when(passwordResetTokenDao.findByToken("valid-token")).thenReturn(Optional.of(token));

        boolean reset = userService.resetPassword("valid-token", "new-password");

        assertTrue(reset);
        assertTrue(PasswordHasher.matches("new-password", user.getPassword()));
        verify(passwordResetTokenDao, times(1)).delete(token);
    }

    @Test
    void resetPasswordRejectsAnExpiredToken() {
        PasswordResetToken token = new PasswordResetToken();
        token.setUser(new User());
        token.setExpiryDate(LocalDateTime.now().minusMinutes(1));
        when(passwordResetTokenDao.findByToken("expired")).thenReturn(Optional.of(token));

        assertFalse(userService.resetPassword("expired", "new-password"));
        verify(passwordResetTokenDao, never()).delete(any());
    }

    @Test
    void resetPasswordRejectsAnUnknownToken() {
        when(passwordResetTokenDao.findByToken("unknown")).thenReturn(Optional.empty());

        assertFalse(userService.resetPassword("unknown", "new-password"));
    }

    @Test
    void resolvePermissionsFlattensPermissionsAcrossAllRoles() {
        Permission readProducts = permission("products", "read");
        Permission writeProducts = permission("products", "write");
        Permission readOrders = permission("orders", "read");

        Role viewer = role("VIEWER", readProducts, readOrders);
        Role editor = role("EDITOR", writeProducts);

        User user = new User();
        user.setRoles(new HashSet<>(java.util.Arrays.asList(viewer, editor)));

        Set<String> permissions = userService.resolvePermissions(user);

        assertEquals(3, permissions.size());
        assertTrue(permissions.contains("products:read"));
        assertTrue(permissions.contains("products:write"));
        assertTrue(permissions.contains("orders:read"));
    }

    private Permission permission(String resource, String action) {
        Permission permission = new Permission();
        permission.setResource(resource);
        permission.setAction(action);
        return permission;
    }

    private Role role(String name, Permission... permissions) {
        Role role = new Role();
        role.setRoleName(name);
        role.setPermissions(new HashSet<>(java.util.Arrays.asList(permissions)));
        return role;
    }
}
