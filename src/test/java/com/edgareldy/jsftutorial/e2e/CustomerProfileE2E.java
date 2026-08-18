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
import java.time.Duration;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Full-page Selenium flow on {@code /customer/profile.xhtml}: log in as an
 * ordinary (non-admin) authenticated user, fill in and save the profile
 * form, then reload the page and confirm the saved values are still there.
 * Deliberately named without a {@code Test}/{@code IT} suffix so neither
 * Surefire nor Failsafe pick it up automatically, see README.md § Tech stack.
 * <p>
 * Created by Edgar Muhamyangabo on 8/18/26
 * Author : Edgar Muhamyangabo
 * Date : 8/18/26
 * Project : jsf_tutorial
 */
class CustomerProfileE2E {

    private static final String BASE_URL = System.getProperty("app.baseUrl", "http://localhost:8080/jsf_tutorial");
    private static final String DB_URL = System.getProperty("db.url",
            "jdbc:mysql://localhost:3306/jsf_tutorial_db?useSSL=false&allowPublicKeyRetrieval=true");
    private static final String DB_USER = System.getProperty("db.user", "jsf_tutorial");
    private static final String DB_PASSWORD = System.getProperty("db.password", "jsf_tutorial");

    private static WebDriver driver;
    private static String userEmail;
    private static final String USER_PASSWORD = "correct-horse-battery-staple";

    @BeforeAll
    static void startBrowser() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
    }

    /**
     * No email inbox round trip needed here (unlike RegisterActivateLoginE2E):
     * this test only needs a plain enabled, non-admin account, so it's seeded
     * directly via JDBC rather than going through register/activate.
     */
    @BeforeAll
    static void seedEnabledUser() throws Exception {
        userEmail = "e2e-" + UUID.randomUUID() + "@example.com";
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             PreparedStatement insertUser = connection.prepareStatement(
                     "INSERT INTO users (first_name, last_name, email, password, enabled, account_locked) "
                             + "VALUES ('Ada', 'Lovelace', ?, ?, 1, 0)")) {
            insertUser.setString(1, userEmail);
            insertUser.setString(2, PasswordHasher.hash(USER_PASSWORD));
            insertUser.executeUpdate();
        }
    }

    @AfterAll
    static void stopBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void savedProfileSurvivesAReload() {
        logIn();

        driver.get(BASE_URL + "/customer/profile.xhtml");
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("form:firstName")));

        fill("form:firstName", "Ada");
        fill("form:lastName", "Lovelace");
        fill("form:telephone", "555-0100");
        fill("form:email", userEmail);
        fill("form:address", "1 Analytical Engine Way");
        driver.findElement(By.xpath("//button[contains(., 'Save')]")).click();
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), "Profile saved"));

        driver.navigate().refresh();
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("form:firstName")));
        assertEquals("Ada", driver.findElement(By.id("form:firstName")).getAttribute("value"));
        assertEquals("555-0100", driver.findElement(By.id("form:telephone")).getAttribute("value"));
    }

    private void fill(String elementId, String value) {
        driver.findElement(By.id(elementId)).clear();
        driver.findElement(By.id(elementId)).sendKeys(value);
    }

    private void logIn() {
        driver.get(BASE_URL + "/auth/login.xhtml");
        driver.findElement(By.id("email")).sendKeys(userEmail);
        driver.findElement(By.id("password")).sendKeys(USER_PASSWORD);
        driver.findElement(By.xpath("//button[contains(., 'Log in')]")).click();
    }
}
