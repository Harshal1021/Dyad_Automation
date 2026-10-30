package com.dyad.hrms.pages;

import com.dyad.hrms.config.Config;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage extends BasePage {

    private static final String PATH = "/web/index.php/auth/login";
    private static final By USERNAME = By.name("username");
    private static final By PASSWORD = By.name("password");
    private static final By SUBMIT = By.cssSelector("button[type='submit']");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open the login page")
    public LoginPage open() {
        driver.get(Config.baseUrl() + PATH);
        return this;
    }

    @Step("Log in as {username}")
    public DashboardPage loginAs(String username, String password) {
        log.info("Logging in as {}", username);
        type(USERNAME, username);
        type(PASSWORD, password);
        click(SUBMIT);
        return new DashboardPage(driver);
    }
}
