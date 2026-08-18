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

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Full-page Selenium flow: browse the catalog, place an order, see it in
 * "my orders", then see it in the admin order list. Deliberately named
 * without a {@code Test}/{@code IT} suffix so neither Surefire nor Failsafe
 * pick it up automatically, see README.md § Tech stack.
 * <p>
 * The seeded account is both the admin (to view {@code admin/orders.xhtml})
 * and the one placing the order (has a complete customer profile seeded
 * alongside it, since {@code OrderBean.placeOrder} requires one): nothing in
 * this project stops an admin from also being a customer, and using one
 * account keeps this flow's setup simpler than juggling two logins.
 * <p>
 * Created by Edgar Muhamyangabo on 8/18/26
 * Author : Edgar Muhamyangabo
 * Date : 8/18/26
 * Project : jsf_tutorial
 */
class OrderPlacementE2E {

    private static final String BASE_URL = System.getProperty("app.baseUrl", "http://localhost:8080/jsf_tutorial");
    private static final String DB_URL = System.getProperty("db.url",
            "jdbc:mysql://localhost:3306/jsf_tutorial_db?useSSL=false&allowPublicKeyRetrieval=true");
    private static final String DB_USER = System.getProperty("db.user", "jsf_tutorial");
    private static final String DB_PASSWORD = System.getProperty("db.password", "jsf_tutorial");
    private static final String ADMIN_EMAIL = System.getProperty("admin.email", "admin@example.com");
    private static final String ADMIN_PASSWORD = System.getProperty("admin.password", "admin");

    private static WebDriver driver;
    private static String productName;

    @BeforeAll
    static void startBrowser() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
    }

    /**
     * Seeds an admin account with a customer profile already filled in
     * (placeOrder requires one), plus a category and product to order,
     * directly via JDBC: no admin UI to grant roles yet, and seeding a
     * product this way is simpler than driving the categories/products
     * pages just to set up this test's fixture data.
     */
    @BeforeAll
    static void seedAccountAndProduct() throws Exception {
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

            long userId = queryLong(connection, "SELECT id FROM users WHERE email = ?", ADMIN_EMAIL);
            long roleId = queryLong(connection, "SELECT id FROM roles WHERE role_name = 'ADMIN'", null);

            try (PreparedStatement insertRoleUser = connection.prepareStatement(
                    "INSERT IGNORE INTO role_user (user_id, role_id) VALUES (?, ?)")) {
                insertRoleUser.setLong(1, userId);
                insertRoleUser.setLong(2, roleId);
                insertRoleUser.executeUpdate();
            }

            try (PreparedStatement insertCustomer = connection.prepareStatement(
                    "INSERT IGNORE INTO customers (user_id, first_name, last_name, telephone, email, address) "
                            + "VALUES (?, 'Admin', 'Admin', '555-0100', ?, '1 Test Street')")) {
                insertCustomer.setLong(1, userId);
                insertCustomer.setString(2, ADMIN_EMAIL);
                insertCustomer.executeUpdate();
            }

            String categoryName = "E2E-" + UUID.randomUUID();
            try (PreparedStatement insertCategory = connection.prepareStatement(
                    "INSERT INTO categories (category_name) VALUES (?)")) {
                insertCategory.setString(1, categoryName);
                insertCategory.executeUpdate();
            }
            long categoryId = queryLong(connection, "SELECT id FROM categories WHERE category_name = ?", categoryName);

            productName = "Widget-" + UUID.randomUUID();
            try (PreparedStatement insertProduct = connection.prepareStatement(
                    "INSERT INTO products (category_id, product_name, unit_price) VALUES (?, ?, 9.99)")) {
                insertProduct.setLong(1, categoryId);
                insertProduct.setString(2, productName);
                insertProduct.executeUpdate();
            }
        }
    }

    private static long queryLong(Connection connection, String sql, String param) throws Exception {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            if (param != null) {
                statement.setString(1, param);
            }
            try (ResultSet row = statement.executeQuery()) {
                row.next();
                return row.getLong(1);
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
    void browseCatalogPlaceOrderThenSeeItInMyOrdersAndAdminOrders() {
        logIn();

        driver.get(BASE_URL + "/shop/catalog.xhtml");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("form:catalog-table"), productName));

        driver.findElement(By.xpath("//tr[contains(., '" + productName + "')]//a[text()='Buy']")).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("form:quantity_input")));

        driver.findElement(By.xpath("//button[contains(., 'Place order')]")).click();
        wait.until(ExpectedConditions.urlContains("/customer/my-orders.xhtml"));
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), productName));
        assertTrue(driver.getPageSource().contains(productName));

        driver.get(BASE_URL + "/admin/orders.xhtml");
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), productName));
        assertTrue(driver.getPageSource().contains(productName));
    }

    private void logIn() {
        driver.get(BASE_URL + "/auth/login.xhtml");
        driver.findElement(By.id("email")).sendKeys(ADMIN_EMAIL);
        driver.findElement(By.id("password")).sendKeys(ADMIN_PASSWORD);
        driver.findElement(By.xpath("//button[contains(., 'Log in')]")).click();
    }
}
