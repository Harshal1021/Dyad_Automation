package com.dyad.hrms.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class DashboardPage extends BasePage {

    private static final By HEADER = By.cssSelector(".oxd-topbar-header-breadcrumb h6");

    public DashboardPage(WebDriver driver) {
        super(driver);
    }

    public String getHeaderText() {
        return waitForVisible(HEADER).getText();
    }
}
