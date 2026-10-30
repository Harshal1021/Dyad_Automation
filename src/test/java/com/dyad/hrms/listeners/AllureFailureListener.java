package com.dyad.hrms.listeners;

import com.dyad.hrms.base.BaseTest;
import io.qameta.allure.Allure;
import java.io.ByteArrayInputStream;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class AllureFailureListener implements ITestListener {

    private static final Logger LOG = LoggerFactory.getLogger(AllureFailureListener.class);

    @Override
    public void onTestFailure(ITestResult result) {
        LOG.error("Test failed: {}", result.getName(), result.getThrowable());
        if (result.getInstance() instanceof BaseTest test && test.getDriver() != null) {
            attachDiagnostics(test.getDriver());
        }
    }

    private void attachDiagnostics(WebDriver driver) {
        byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        Allure.addAttachment("Screenshot", "image/png", new ByteArrayInputStream(screenshot), "png");
        Allure.addAttachment("Current URL", driver.getCurrentUrl());
        Allure.addAttachment("Page source", "text/html", driver.getPageSource(), "html");
    }
}
