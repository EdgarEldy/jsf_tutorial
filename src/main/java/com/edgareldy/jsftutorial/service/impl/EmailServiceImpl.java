package com.edgareldy.jsftutorial.service.impl;

import com.edgareldy.jsftutorial.entity.User;
import com.edgareldy.jsftutorial.service.EmailService;

import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Default {@link EmailService} implementation using plain JavaMail. Each send
 * is dispatched to a background thread so a slow SMTP call never blocks the
 * JSF request that triggered it — Tomcat has no {@code @Asynchronous}/EJB
 * container to do that for us.
 * <p>
 * Created by Edgar Muhamyangabo on 8/17/26
 * Author : Edgar Muhamyangabo
 * Date : 8/17/26
 * Project : jsf_tutorial
 */
@ApplicationScoped
public class EmailServiceImpl implements EmailService {

    private static final Logger LOGGER = Logger.getLogger(EmailServiceImpl.class.getName());

    private final String mailHost = System.getenv().getOrDefault("MAIL_HOST", "localhost");
    private final String mailPort = System.getenv().getOrDefault("MAIL_PORT", "1025");
    private final boolean smtpAuth = Boolean.parseBoolean(System.getenv().getOrDefault("MAIL_SMTP_AUTH", "false"));
    private final boolean smtpStartTls = Boolean.parseBoolean(System.getenv().getOrDefault("MAIL_SMTP_STARTTLS", "false"));
    private final String mailFrom = System.getenv().getOrDefault("MAIL_FROM", "no-reply@jsf-tutorial.local");
    private final String baseUrl = System.getenv().getOrDefault("APP_BASE_URL", "http://localhost:8080/jsf_tutorial");

    private final ExecutorService executor = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "email-sender");
        thread.setDaemon(true);
        return thread;
    });

    @Override
    public void sendActivationEmail(User user, String token) {
        dispatch(buildActivationMessage(user, token));
    }

    @Override
    public void sendPasswordResetEmail(User user, String token) {
        dispatch(buildPasswordResetMessage(user, token));
    }

    public MailMessage buildActivationMessage(User user, String token) {
        String link = baseUrl + "/auth/activate.xhtml?token=" + token;
        return new MailMessage(user.getEmail(), "Activate your account",
                "Hello " + user.getFirstName() + ",\n\nActivate your account by visiting:\n" + link);
    }

    public MailMessage buildPasswordResetMessage(User user, String token) {
        String link = baseUrl + "/auth/reset-password.xhtml?token=" + token;
        return new MailMessage(user.getEmail(), "Reset your password",
                "Hello " + user.getFirstName() + ",\n\nReset your password by visiting:\n" + link);
    }

    private void dispatch(MailMessage message) {
        executor.submit(() -> send(message));
    }

    private void send(MailMessage message) {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", mailHost);
        properties.put("mail.smtp.port", mailPort);
        properties.put("mail.smtp.auth", String.valueOf(smtpAuth));
        properties.put("mail.smtp.starttls.enable", String.valueOf(smtpStartTls));

        Session session = Session.getInstance(properties);
        try {
            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.setFrom(new InternetAddress(mailFrom));
            mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(message.to));
            mimeMessage.setSubject(message.subject);
            mimeMessage.setText(message.body);
            Transport.send(mimeMessage);
            LOGGER.log(Level.INFO, "Sent email to {0} with subject ''{1}''", new Object[]{message.to, message.subject});
        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "Failed to send email to " + message.to, e);
        }
    }

    @PreDestroy
    void shutdown() {
        executor.shutdown();
    }

    public static final class MailMessage {
        public final String to;
        public final String subject;
        public final String body;

        MailMessage(String to, String subject, String body) {
            this.to = to;
            this.subject = subject;
            this.body = body;
        }
    }
}
