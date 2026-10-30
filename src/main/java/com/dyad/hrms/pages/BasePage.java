package com.dyad.hrms.pages;

import com.dyad.hrms.config.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BasePage {

    private static final By LOADER = By.cssSelector(".oxd-form-loader, .oxd-loading-spinner");
    private static final By TOAST_MESSAGE = By.cssSelector(".oxd-toast .oxd-text--toast-message");
    private static final By APP_ROOT = By.id("app");

    protected final Logger log = LoggerFactory.getLogger(getClass());
    protected final WebDriver driver;
    protected final WebDriverWait wait;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Config.timeout());
    }

    /** OrangeHRM inputs have no ids, so a form field is found through its visible label. */
    protected static By inputByLabel(String label) {
        return By.xpath("//label[normalize-space()='" + label + "']"
                + "/ancestor::div[contains(@class,'oxd-input-group')]//input");
    }

    protected WebElement waitForVisible(By locator) {
        waitForLoadersToFinish();
        return waitFor(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void click(By locator) {
        waitForLoadersToFinish();
        waitFor(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    /** WebElement.clear() does not reliably update the Vue model, so the text is selected and deleted. */
    protected void type(By locator, String text) {
        WebElement input = waitForVisible(locator);
        Keys selectAllModifier = Platform.getCurrent().is(Platform.MAC) ? Keys.COMMAND : Keys.CONTROL;
        input.sendKeys(Keys.chord(selectAllModifier, "a"), Keys.DELETE);
        input.sendKeys(text);
    }

    /** Forms render empty and are filled in after an API call, so this waits for a value to arrive. */
    protected String waitForValue(By locator) {
        waitForVisible(locator);
        waitFor(d -> !d.findElement(locator).getDomProperty("value").isBlank());
        return driver.findElement(locator).getDomProperty("value");
    }

    /**
     * Waits for a condition, reloading if the shared demo site served a server error page. Such a page
     * has no application root, so failures inside the application are never reloaded away.
     */
    protected <T> T waitFor(ExpectedCondition<T> condition) {
        return wait.until(d -> {
            if (d.findElements(APP_ROOT).isEmpty()) {
                log.warn("Application did not load at {}, reloading", d.getCurrentUrl());
                d.navigate().refresh();
                return null;
            }
            return condition.apply(d);
        });
    }

    protected void waitForLoadersToFinish() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(LOADER));
    }

    /** Returns the toast text and waits for it to go away, because a toast can cover other controls. */
    public String consumeToastMessage() {
        WebElement toast = wait.until(ExpectedConditions.visibilityOfElementLocated(TOAST_MESSAGE));
        String message = toast.getText();
        log.info("Toast: {}", message);
        wait.until(ExpectedConditions.invisibilityOfElementLocated(TOAST_MESSAGE));
        return message;
    }
}
