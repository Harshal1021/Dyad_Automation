package com.dyad.hrms.pages.pim;

import com.dyad.hrms.components.ConfirmDeleteModal;
import com.dyad.hrms.config.Config;
import com.dyad.hrms.pages.BasePage;
import io.qameta.allure.Step;
import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class EmployeeListPage extends BasePage {

    private static final String PATH = "/web/index.php/pim/viewEmployeeList";
    private static final Duration SEARCH_START_TIMEOUT = Duration.ofSeconds(3);
    private static final By EMPLOYEE_ID_FILTER = inputByLabel("Employee Id");
    private static final By SEARCH = By.cssSelector("button[type='submit']");
    private static final By RECORD_COUNT_LABEL = By.cssSelector(".orangehrm-horizontal-padding .oxd-text--span");
    private static final By ROWS = By.cssSelector(".oxd-table-body .oxd-table-card");
    private static final By CELLS = By.cssSelector(".oxd-table-cell");
    private static final By DELETE_ICON = By.cssSelector(".bi-trash");

    private static final int ID_COLUMN = 1;
    private static final int FIRST_NAME_COLUMN = 2;
    private static final int LAST_NAME_COLUMN = 3;

    public EmployeeListPage(WebDriver driver) {
        super(driver);
    }

    @Step("Open the employee list")
    public EmployeeListPage open() {
        driver.get(Config.baseUrl() + PATH);
        waitForVisible(RECORD_COUNT_LABEL);
        return this;
    }

    @Step("Search for employee ID {employeeId}")
    public EmployeeListPage searchByEmployeeId(String employeeId) {
        log.info("Searching for employee ID {}", employeeId);
        String countBeforeSearch = getRecordCountLabel();
        type(EMPLOYEE_ID_FILTER, employeeId);
        click(SEARCH);
        waitForSearchToStart(countBeforeSearch);
        waitForLoadersToFinish();
        return this;
    }

    public String getRecordCountLabel() {
        return waitForVisible(RECORD_COUNT_LABEL).getText();
    }

    public int getRowCount() {
        return driver.findElements(ROWS).size();
    }

    public String getRowEmployeeId(int rowIndex) {
        return cellText(rowIndex, ID_COLUMN);
    }

    public String getRowFirstName(int rowIndex) {
        return cellText(rowIndex, FIRST_NAME_COLUMN);
    }

    public String getRowLastName(int rowIndex) {
        return cellText(rowIndex, LAST_NAME_COLUMN);
    }

    @Step("Open the employee in row {rowIndex}")
    public void openRow(int rowIndex) {
        driver.findElements(ROWS).get(rowIndex).click();
    }

    @Step("Delete the employee in row {rowIndex}")
    public void deleteRow(int rowIndex) {
        driver.findElements(ROWS).get(rowIndex).findElement(DELETE_ICON).click();
        new ConfirmDeleteModal(driver).confirm();
    }

    private String cellText(int rowIndex, int columnIndex) {
        WebElement row = driver.findElements(ROWS).get(rowIndex);
        List<WebElement> cells = row.findElements(CELLS);
        return cells.get(columnIndex).getText();
    }

    /**
     * The count label is the only signal that a search has been applied. If the new result has the
     * same count as the old one the label never changes, so this gives up quietly after a short wait.
     */
    private void waitForSearchToStart(String countBeforeSearch) {
        try {
            new WebDriverWait(driver, SEARCH_START_TIMEOUT).until(ExpectedConditions.not(
                    ExpectedConditions.textToBe(RECORD_COUNT_LABEL, countBeforeSearch)));
        } catch (TimeoutException e) {
            log.debug("Record count stayed at '{}' after searching", countBeforeSearch);
        }
    }
}
