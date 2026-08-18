package com.edgareldy.jsftutorial.unit;

import com.edgareldy.jsftutorial.entity.User;
import com.edgareldy.jsftutorial.service.impl.EmailServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for {@link EmailServiceImpl}'s message-building logic. Only the
 * pure "build a message" methods are exercised here — no SMTP call, no
 * mocking needed, since {@code send} is a separate method entirely.
 * <p>
 * Created by Edgar Muhamyangabo on 8/17/26
 * Author : Edgar Muhamyangabo
 * Date : 8/17/26
 * Project : jsf_tutorial
 */
class EmailServiceImplTest {

    private EmailServiceImpl emailService;
    private User user;

    @BeforeEach
    void setUp() {
        emailService = new EmailServiceImpl();
        user = new User();
        user.setFirstName("Ada");
        user.setEmail("ada@example.com");
    }

    @Test
    void activationMessageIncludesTheTokenLink() {
        EmailServiceImpl.MailMessage message = emailService.buildActivationMessage(user, "raw-token");

        assertEquals("ada@example.com", message.to);
        assertEquals("Activate your account", message.subject);
        assertTrue(message.body.contains("/auth/activate.xhtml?token=raw-token"));
    }

    @Test
    void passwordResetMessageIncludesTheTokenLink() {
        EmailServiceImpl.MailMessage message = emailService.buildPasswordResetMessage(user, "reset-token");

        assertEquals("ada@example.com", message.to);
        assertEquals("Reset your password", message.subject);
        assertTrue(message.body.contains("/auth/reset-password.xhtml?token=reset-token"));
    }
}
