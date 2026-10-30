package com.dyad.hrms.pages;

import com.dyad.hrms.config.Config;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BasePage {

    private static final By LOADER = By.cssSelector(".oxd-form-loader, .oxd-loading-spinner");
    private static final By TOAST_MESSAGE = By.cssSelector(".oxd-toast .oxd-text--toast-message");

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
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    protected void click(By locator) {
        waitForLoadersToFinish();
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    /** WebElement.clear() does not reliably update the Vue model, so the text is selected and deleted. */
    protected void type(By locator, String text) {
        WebElement input = waitForVisible(locator);
        input.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
        input.sendKeys(text);
    }

    /** Forms render empty and are filled in after an API call, so this waits for a value to arrive. */
    protected String waitForValue(By locator) {
        waitForVisible(locator);
        wait.until(d -> !d.findElement(locator).getDomProperty("value").isBlank());
        return driver.findElement(locator).getDomProperty("value");
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
