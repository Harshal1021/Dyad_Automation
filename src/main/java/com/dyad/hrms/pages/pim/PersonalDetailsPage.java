package com.dyad.hrms.pages.pim;

import com.dyad.hrms.components.DropdownField;
import com.dyad.hrms.pages.BasePage;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class PersonalDetailsPage extends BasePage {

    private static final By FIRST_NAME = By.name("firstName");
    private static final By LAST_NAME = By.name("lastName");
    private static final By EMPLOYEE_ID = inputByLabel("Employee Id");
    private static final By OTHER_ID = inputByLabel("Other Id");
    private static final By SAVE = By.xpath("//h6[normalize-space()='Personal Details']"
            + "/following::button[@type='submit'][1]");

    private final DropdownField maritalStatus;

    public PersonalDetailsPage(WebDriver driver) {
        super(driver);
        this.maritalStatus = new DropdownField(driver, "Marital Status");
    }

    public String getEmployeeId() {
        return waitForValue(EMPLOYEE_ID);
    }

    public String getFirstName() {
        return waitForValue(FIRST_NAME);
    }

    public String getLastName() {
        return waitForValue(LAST_NAME);
    }

    public String getOtherId() {
        return waitForValue(OTHER_ID);
    }

    public String getMaritalStatus() {
        waitForValue(EMPLOYEE_ID);
        return maritalStatus.getSelected();
    }

    @Step("Update last name to {lastName}, Other Id to {otherId} and marital status to {status}")
    public PersonalDetailsPage updateDetails(String lastName, String otherId, String status) {
        waitForValue(EMPLOYEE_ID);
        type(LAST_NAME, lastName);
        type(OTHER_ID, otherId);
        maritalStatus.select(status);
        click(SAVE);
        return this;
    }

    @Step("Reload the employee record")
    public PersonalDetailsPage reload() {
        driver.navigate().refresh();
        return this;
    }
}
