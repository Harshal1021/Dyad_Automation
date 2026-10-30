package com.dyad.hrms.base;

import com.dyad.hrms.driver.DriverFactory;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;

public abstract class BaseTest {

    protected WebDriver driver;

    @BeforeClass(alwaysRun = true)
    public void startBrowser() {
        driver = DriverFactory.create();
    }

    @AfterClass(alwaysRun = true)
    public void quitBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }

    public WebDriver getDriver() {
        return driver;
    }
}
