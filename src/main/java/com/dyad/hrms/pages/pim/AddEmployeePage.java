package com.dyad.hrms.pages.pim;

import com.dyad.hrms.config.Config;
import com.dyad.hrms.models.Employee;
import com.dyad.hrms.pages.BasePage;
import io.qameta.allure.Step;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class AddEmployeePage extends BasePage {

    private static final String PATH = "/web/index.php/pim/addEmployee";
    private static final By FIRST_NAME = By.name("firstName");
    private static final By LAST_NAME = By.name("lastName");
    private static final By EMPLOYEE_ID = inputByLabel("Employee Id");
    private static final By SAVE = By.cssSelector("button[type='submit']");
    private static final By DUPLICATE_ID_ERROR = By.xpath(
            "//span[contains(@class,'oxd-input-field-error-message')][normalize-space()='Employee Id already exists']");
    private static final By PERSONAL_DETAILS_HEADER = By.xpath("//h6[normalize-space()='Personal Details']");

    public AddEmployeePage(WebDriver driver) {
        super(driver);
    }

    @Step("Open the Add Employee form")
    public AddEmployeePage open() {
        driver.get(Config.baseUrl() + PATH);
        return this;
    }

    public String getGeneratedEmployeeId() {
        return waitForValue(EMPLOYEE_ID);
    }

    @Step("Fill in the new employee's name")
    public AddEmployeePage fillName(Employee employee) {
        type(FIRST_NAME, employee.firstName());
        type(LAST_NAME, employee.lastName());
        return this;
    }

    @Step("Save the new employee")
    public void save() {
        click(SAVE);
        waitFor(ExpectedConditions.or(
                ExpectedConditions.visibilityOfElementLocated(PERSONAL_DETAILS_HEADER),
                ExpectedConditions.visibilityOfElementLocated(DUPLICATE_ID_ERROR)));
    }

    /** Another user of the shared demo site can take the generated ID between reading and saving it. */
    public boolean isDuplicateIdErrorShown() {
        List<?> errors = driver.findElements(DUPLICATE_ID_ERROR);
        return !errors.isEmpty();
    }

    public PersonalDetailsPage personalDetails() {
        return new PersonalDetailsPage(driver);
    }
}
