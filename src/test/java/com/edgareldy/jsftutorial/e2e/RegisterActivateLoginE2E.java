package com.edgareldy.jsftutorial.e2e;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Full-page Selenium flow: register a new account, retrieve the activation
 * link from the MailHog inbox, activate it, then log in. Deliberately named
 * without a {@code Test}/{@code IT} suffix so neither Surefire nor Failsafe
 * pick it up automatically — this is the manually triggered job described in
 * README.md § Tech stack, run against a real Tomcat 9 + MySQL + MailHog, not
 * part of {@code mvn verify}.
 * <p>
 * Created by Edgar Muhamyangabo on 8/17/26
 * Author : Edgar Muhamyangabo
 * Date : 8/17/26
 * Project : jsf_tutorial
 */
class RegisterActivateLoginE2E {

    private static final String BASE_URL = System.getProperty("app.baseUrl", "http://localhost:8080/jsf_tutorial");
    private static final String MAILHOG_API = System.getProperty("mailhog.apiUrl", "http://localhost:8025");
    private static final Pattern ACTIVATION_LINK_PATTERN =
            Pattern.compile("(http\\S*/auth/activate\\.xhtml\\?token=\\S+)");

    private static WebDriver driver;

    @BeforeAll
    static void startBrowser() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new", "--no-sandbox", "--disable-dev-shm-usage");
        driver = new ChromeDriver(options);
    }

    @AfterAll
    static void stopBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    @Test
    void registerActivateThenLogIn() throws Exception {
        String email = "e2e-" + UUID.randomUUID() + "@example.com";
        String password = "correct-horse-battery-staple";

        register(email, password);
        String activationLink = fetchActivationLink(email);
        driver.get(activationLink);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.tagName("body"), "activated"));

        logIn(email, password);
        wait.until(ExpectedConditions.urlContains("/index.xhtml"));

        assertTrue(driver.getCurrentUrl().contains("/index.xhtml"));
    }

    private void register(String email, String password) {
        driver.get(BASE_URL + "/auth/register.xhtml");
        driver.findElement(By.id("firstName")).sendKeys("Ada");
        driver.findElement(By.id("lastName")).sendKeys("Lovelace");
        driver.findElement(By.id("email")).sendKeys(email);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.xpath("//button[contains(., 'Register')]")).click();
    }

    private void logIn(String email, String password) {
        driver.get(BASE_URL + "/auth/login.xhtml");
        driver.findElement(By.id("email")).sendKeys(email);
        driver.findElement(By.id("password")).sendKeys(password);
        driver.findElement(By.xpath("//button[contains(., 'Log in')]")).click();
    }

    /**
     * Polls MailHog's REST API for the activation email sent to {@code email}
     * and extracts the activation link from its body.
     */
    private String fetchActivationLink(String email) throws Exception {
        for (int attempt = 0; attempt < 20; attempt++) {
            String body = httpGet(MAILHOG_API + "/api/v2/search?kind=to&query=" + email);
            Matcher matcher = ACTIVATION_LINK_PATTERN.matcher(body);
            if (matcher.find()) {
                return matcher.group(1).replace("\\u003d", "=").replace("\\u0026", "&");
            }
            Thread.sleep(500);
        }
        throw new IllegalStateException("No activation email received for " + email + " within 10s");
    }

    private String httpGet(String url) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");
        StringBuilder response = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
        }
        return response.toString();
    }
}
