package com.edgareldy.jsftutorial.e2e;

import com.edgareldy.jsftutorial.security.PasswordHasher;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
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
 * Full-page Selenium flow on {@code /admin/products.xhtml}: log in as an
 * ADMIN, create a category and a product in it, edit the product, filter by
 * category, then confirm the category itself can't be deleted while it still
 * holds that product. Deliberately named without a {@code Test}/{@code IT}
 * suffix so neither Surefire nor Failsafe pick it up automatically, see
 * README.md § Tech stack.
 * <p>
 * Created by Edgar Muhamyangabo on 8/18/26
 * Author : Edgar Muhamyangabo
 * Date : 8/18/26
 * Project : jsf_tutorial
 */
class ProductCrudE2E {

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
     * No admin UI exists to grant the ADMIN role yet (out of scope for the
     * auth/categories branches too), so this test seeds an enabled admin
     * account directly via JDBC before logging in through the real page.
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
    void createEditFilterThenRejectDeletingTheNonEmptyCategory() {
        logInAsAdmin();

        String categoryName = "E2E-" + UUID.randomUUID();
        createCategory(categoryName);

        String originalProductName = "Widget-" + UUID.randomUUID();
        String editedProductName = originalProductName + "-edited";

        driver.get(BASE_URL + "/admin/products.xhtml");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.findElement(By.xpath("//button[contains(., 'New Product')]")).click();
        waitUntilVisible(wait, By.id("form:productName")).sendKeys(originalProductName);
        driver.findElement(By.id("form:unitPrice_input")).sendKeys("9.99");
        selectByVisibleText(By.id("form:productCategory"), categoryName);
        driver.findElement(By.xpath("//button[contains(., 'Save')]")).click();
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("form:products-table"), originalProductName));

        driver.findElement(By.xpath("//tr[contains(., '" + originalProductName + "')]//button[contains(@class, 'pi-pencil')]/..")).click();
        WebElement nameField = waitUntilVisible(wait, By.id("form:productName"));
        nameField.clear();
        nameField.sendKeys(editedProductName);
        driver.findElement(By.xpath("//button[contains(., 'Save')]")).click();
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("form:products-table"), editedProductName));

        selectByVisibleText(By.id("form:categoryFilter"), categoryName);
        wait.until(driver1 -> driver1.getPageSource().contains(editedProductName));
        assertTrue(driver.getPageSource().contains(editedProductName));

        attemptToDeleteCategoryExpectingRejection(categoryName);
    }

    private void attemptToDeleteCategoryExpectingRejection(String categoryName) {
        driver.get(BASE_URL + "/admin/categories.xhtml");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("form:categories-table"), categoryName));

        driver.findElement(By.xpath("//tr[contains(., '" + categoryName + "')]//button[contains(@class, 'pi-trash')]/..")).click();

        wait.until(ExpectedConditions.textToBePresentInElementLocated(
                By.tagName("body"), "Cannot delete a category that still has products"));
        assertFalse(driver.getPageSource().contains("Category deleted"));
    }

    private void createCategory(String categoryName) {
        driver.get(BASE_URL + "/admin/categories.xhtml");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        driver.findElement(By.xpath("//button[contains(., 'New Category')]")).click();
        waitUntilVisible(wait, By.id("form:categoryName")).sendKeys(categoryName);
        driver.findElement(By.xpath("//button[contains(., 'Save')]")).click();
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("form:categories-table"), categoryName));
    }

    private void selectByVisibleText(By locator, String visibleText) {
        new Select(driver.findElement(locator)).selectByVisibleText(visibleText);
    }

    private WebElement waitUntilVisible(WebDriverWait wait, By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    private void logInAsAdmin() {
        driver.get(BASE_URL + "/auth/login.xhtml");
        driver.findElement(By.id("email")).sendKeys(ADMIN_EMAIL);
        driver.findElement(By.id("password")).sendKeys(ADMIN_PASSWORD);
        driver.findElement(By.xpath("//button[contains(., 'Log in')]")).click();
    }
}
