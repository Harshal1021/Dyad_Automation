package com.dyad.hrms.components;

import com.dyad.hrms.pages.BasePage;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class ConfirmDeleteModal extends BasePage {

    private static final By CONFIRM = By.xpath("//div[contains(@class,'orangehrm-modal-footer')]"
            + "//button[normalize-space()='Yes, Delete']");

    public ConfirmDeleteModal(WebDriver driver) {
        super(driver);
    }

    @Step("Confirm the deletion")
    public void confirm() {
        click(CONFIRM);
    }
}
