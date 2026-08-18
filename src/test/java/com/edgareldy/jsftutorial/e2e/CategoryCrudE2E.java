package com.edgareldy.jsftutorial.e2e;

import com.edgareldy.jsftutorial.security.PasswordHasher;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Full-page Selenium flow on {@code /admin/categories.xhtml}: log in as an
 * ADMIN, create a category, edit it, then delete it. Deliberately named
 * without a {@code Test}/{@code IT} suffix so neither Surefire nor Failsafe
 * pick it up automatically, see README.md § Tech stack.
 * <p>
 * Created by Edgar Muhamyangabo on 8/18/26
 * Author : Edgar Muhamyangabo
 * Date : 8/18/26
 * Project : jsf_tutorial
 */
class CategoryCrudE2E {

    private static final String BASE_URL = System.getProperty("app.baseUrl", "http://localhost:8080/jsf_tutorial");
    private static final String DB_URL = System.getProperty("db.url",
            "jdbc:mysql://localhost:3306/jsf_tutorial_db?useSSL=false&allowPublicKeyRetrieval=true");
    private static final String DB_USER = System.getProperty("db.user", "jsf_tutorial");
    private static final String DB_PASSWORD = System.getProperty("db.password", "jsf_tutorial");
    private static final String ADMIN_EMAIL = System.getProperty("admin.email", "admin@example.com");
    private static final String ADMIN_PASSWORD = System.getProperty("admin.password", "admin");

    private static WebDriver driver;

    @BeforeAll
    static void startBrowser() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
    }

    /**
     * No admin UI exists to grant the ADMIN role yet (it's out of scope for
     * this branch), so this test seeds an enabled admin account directly via
     * JDBC before logging in through the actual page.
     */
    @BeforeAll
    static void seedAdminAccount() throws Exception {
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            try (PreparedStatement insertRole = connection.prepareStatement(
                    "INSERT IGNORE INTO roles (role_name) VALUES ('ADMIN')")) {
                insertRole.executeUpdate();
            }

            String hashedPassword = PasswordHasher.hash(ADMIN_PASSWORD);
            try (PreparedStatement upsertUser = connection.prepareStatement(
                    "INSERT INTO users (first_name, last_name, email, password, enabled, account_locked) "
                            + "VALUES ('Admin', 'Admin', ?, ?, 1, 0) "
                            + "ON DUPLICATE KEY UPDATE password = ?, enabled = 1")) {
                upsertUser.setString(1, ADMIN_EMAIL);
                upsertUser.setString(2, hashedPassword);
                upsertUser.setString(3, hashedPassword);
                upsertUser.executeUpdate();
            }

            long userId;
            try (PreparedStatement selectUser = connection.prepareStatement(
                    "SELECT id FROM users WHERE email = ?")) {
                selectUser.setString(1, ADMIN_EMAIL);
                try (ResultSet row = selectUser.executeQuery()) {
                    row.next();
                    userId = row.getLong("id");
                }
            }

            long roleId;
            try (PreparedStatement selectRole = connection.prepareStatement(
                    "SELECT id FROM roles WHERE role_name = 'ADMIN'")) {
                try (ResultSet row = selectRole.executeQuery()) {
                    row.next();
                    roleId = row.getLong("id");
                }
            }

            try (PreparedStatement insertRoleUser = connection.prepareStatement(
                    "INSERT IGNORE INTO role_user (user_id, role_id) VALUES (?, ?)")) {
                insertRoleUser.setLong(1, userId);
                insertRoleUser.setLong(2, roleId);
                insertRoleUser.executeUpdate();
            }
        }
    }

    @AfterAll
    static void stopBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void createEditThenDeleteACategory() {
        logInAsAdmin();

        String originalName = "E2E-" + UUID.randomUUID();
        String editedName = originalName + "-edited";

        driver.get(BASE_URL + "/admin/categories.xhtml");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.findElement(By.xpath("//button[contains(., 'New Category')]")).click();
        waitUntilVisible(wait, By.id("form:categoryName")).sendKeys(originalName);
        driver.findElement(By.xpath("//button[contains(., 'Save')]")).click();
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("form:categories-table"), originalName));

        driver.findElement(By.xpath("//tr[contains(., '" + originalName + "')]//button[contains(@class, 'pi-pencil')]/..")).click();
        waitUntilVisible(wait, By.id("form:categoryName")).clear();
        driver.findElement(By.id("form:categoryName")).sendKeys(editedName);
        driver.findElement(By.xpath("//button[contains(., 'Save')]")).click();
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("form:categories-table"), editedName));
        assertTrue(driver.getPageSource().contains(editedName));

        driver.findElement(By.xpath("//tr[contains(., '" + editedName + "')]//button[contains(@class, 'pi-trash')]/..")).click();
        wait.until(driver1 -> !driver1.getPageSource().contains(editedName));
        assertFalse(driver.getPageSource().contains(editedName));
    }

    private org.openqa.selenium.WebElement waitUntilVisible(WebDriverWait wait, By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    private void logInAsAdmin() {
        driver.get(BASE_URL + "/auth/login.xhtml");
        driver.findElement(By.id("email")).sendKeys(ADMIN_EMAIL);
        driver.findElement(By.id("password")).sendKeys(ADMIN_PASSWORD);
        driver.findElement(By.xpath("//button[contains(., 'Log in')]")).click();
    }
}
