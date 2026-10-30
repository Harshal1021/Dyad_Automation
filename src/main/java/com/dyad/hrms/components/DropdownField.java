package com.dyad.hrms.components;

import com.dyad.hrms.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/** OrangeHRM dropdowns are custom Vue widgets, so Selenium's Select class cannot drive them. */
public class DropdownField extends BasePage {

    private final By selectedText;

    public DropdownField(WebDriver driver, String label) {
        super(driver);
        this.selectedText = By.xpath("//label[normalize-space()='" + label + "']"
                + "/ancestor::div[contains(@class,'oxd-input-group')]//div[contains(@class,'oxd-select-text-input')]");
    }

    public void select(String option) {
        click(selectedText);
        click(By.xpath("//div[@role='listbox']//div[@role='option'][normalize-space()='" + option + "']"));
    }

    public String getSelected() {
        return waitForVisible(selectedText).getText();
    }
}
