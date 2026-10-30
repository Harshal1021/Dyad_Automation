package com.dyad.hrms.tests;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;
import static org.testng.Assert.assertTrue;

import com.dyad.hrms.base.BaseTest;
import com.dyad.hrms.config.Config;
import com.dyad.hrms.models.Employee;
import com.dyad.hrms.pages.DashboardPage;
import com.dyad.hrms.pages.LoginPage;
import com.dyad.hrms.pages.pim.AddEmployeePage;
import com.dyad.hrms.pages.pim.EmployeeListPage;
import com.dyad.hrms.pages.pim.PersonalDetailsPage;
import com.dyad.hrms.utils.TestDataFactory;
import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

@Epic("HRMS")
@Feature("Employee lifecycle")
public class EmployeeLifecycleTest extends BaseTest {

    private static final Logger LOG = LoggerFactory.getLogger(EmployeeLifecycleTest.class);
    private static final String UPDATED_MARITAL_STATUS = "Married";
    private static final String NO_RECORDS_LABEL = "No Records Found";
    private static final int MAX_SAVE_ATTEMPTS = 3;

    private Employee employee;
    private String employeeId;
    private String updatedOtherId;
    private boolean deleted;

    @Test(description = "Log in as Admin")
    @Story("Login")
    @Severity(SeverityLevel.BLOCKER)
    public void loginAsAdmin() {
        DashboardPage dashboard = new LoginPage(driver).open()
                .loginAs(Config.adminUsername(), Config.adminPassword());

        assertEquals(dashboard.getHeaderText(), "Dashboard", "Admin should land on the dashboard after login");
    }

    @Test(description = "Create an employee and verify the generated ID", dependsOnMethods = "loginAsAdmin")
    @Story("Create")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Validation 1: the application pre-fills the ID, saves that ID, and generates a new one afterwards.")
    public void createEmployee() {
        employee = TestDataFactory.uniqueEmployee();
        PersonalDetailsPage details = saveNewEmployee();

        assertEquals(details.getEmployeeId(), employeeId, "Saved record should keep the generated ID");
        assertEquals(details.getFirstName(), employee.firstName(), "Saved first name should match");
        assertEquals(details.getLastName(), employee.lastName(), "Saved last name should match");
        LOG.info("Created employee {} with generated ID {}", employee, employeeId);

        String nextGeneratedId = new AddEmployeePage(driver).open().getGeneratedEmployeeId();
        assertNotEquals(nextGeneratedId, employeeId, "The form should generate a new ID once this one is taken");
    }

    @Test(description = "Search for the employee by generated ID", dependsOnMethods = "createEmployee")
    @Story("Read")
    @Severity(SeverityLevel.CRITICAL)
    public void searchEmployee() {
        EmployeeListPage list = new EmployeeListPage(driver).open().searchByEmployeeId(employeeId);

        assertEquals(list.getRowCount(), 1, "Search by ID should return exactly one employee");
        assertEquals(list.getRowEmployeeId(0), employeeId, "Returned row should have the generated ID");
        assertEquals(list.getRowFirstName(0), employee.firstName(), "Returned row should have the first name");
        assertEquals(list.getRowLastName(0), employee.lastName(), "Returned row should have the last name");
    }

    @Test(description = "Update the employee's details", dependsOnMethods = "createEmployee")
    @Story("Update")
    @Severity(SeverityLevel.CRITICAL)
    public void updateEmployee() {
        employee = employee.withLastName(TestDataFactory.uniqueLastName());
        updatedOtherId = TestDataFactory.uniqueOtherId();

        PersonalDetailsPage details = openEmployeeFromList();
        details.updateDetails(employee.lastName(), updatedOtherId, UPDATED_MARITAL_STATUS);

        assertTrue(details.consumeToastMessage().contains("Successfully Updated"),
                "Saving the details should show a success message");
    }

    @Test(description = "Verify the update was persisted", dependsOnMethods = "updateEmployee")
    @Story("Update")
    @Severity(SeverityLevel.CRITICAL)
    public void verifyUpdate() {
        PersonalDetailsPage details = new PersonalDetailsPage(driver).reload();

        assertEquals(details.getEmployeeId(), employeeId, "Updating should not change the employee ID");
        assertEquals(details.getLastName(), employee.lastName(), "Last name should be persisted");
        assertEquals(details.getOtherId(), updatedOtherId, "Other Id should be persisted");
        assertEquals(details.getMaritalStatus(), UPDATED_MARITAL_STATUS, "Marital status should be persisted");

        EmployeeListPage list = new EmployeeListPage(driver).open().searchByEmployeeId(employeeId);
        assertEquals(list.getRowLastName(0), employee.lastName(), "Employee list should show the new last name");
    }

    @Test(description = "Delete the employee", dependsOnMethods = "verifyUpdate")
    @Story("Delete")
    @Severity(SeverityLevel.CRITICAL)
    public void deleteEmployee() {
        EmployeeListPage list = new EmployeeListPage(driver).open().searchByEmployeeId(employeeId);
        list.deleteRow(0);

        assertTrue(list.consumeToastMessage().contains("Successfully Deleted"),
                "Deleting should show a success message");
        deleted = true;
    }

    @Test(description = "Search after deletion returns no records", dependsOnMethods = "deleteEmployee")
    @Story("Delete")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Validation 2: searching for the deleted employee's ID returns no records.")
    public void searchAfterDeletion() {
        EmployeeListPage list = new EmployeeListPage(driver).open().searchByEmployeeId(employeeId);

        assertEquals(list.getRecordCountLabel(), NO_RECORDS_LABEL, "Search should report no records");
        assertEquals(list.getRowCount(), 0, "Results table should have no rows");
    }

    @AfterClass(alwaysRun = true)
    public void deleteLeftoverEmployee() {
        if (employeeId == null || deleted) {
            return;
        }
        LOG.warn("Removing employee {} left behind by a failed run", employeeId);
        EmployeeListPage list = new EmployeeListPage(driver).open().searchByEmployeeId(employeeId);
        if (list.getRowCount() > 0) {
            list.deleteRow(0);
            list.consumeToastMessage();
        }
    }

    /** Retries because other users of the shared site can take the generated ID before it is saved. */
    private PersonalDetailsPage saveNewEmployee() {
        for (int attempt = 1; attempt <= MAX_SAVE_ATTEMPTS; attempt++) {
            AddEmployeePage form = new AddEmployeePage(driver).open();
            String generatedId = form.getGeneratedEmployeeId();
            assertTrue(generatedId.matches("\\d+"), "Generated ID should be numeric but was: " + generatedId);

            form.fillName(employee).save();
            if (!form.isDuplicateIdErrorShown()) {
                employeeId = generatedId;
                return form.personalDetails();
            }
            LOG.warn("Generated ID {} was already taken when saving (attempt {})", generatedId, attempt);
        }
        throw new AssertionError("Could not save an employee after " + MAX_SAVE_ATTEMPTS + " attempts");
    }

    private PersonalDetailsPage openEmployeeFromList() {
        new EmployeeListPage(driver).open().searchByEmployeeId(employeeId).openRow(0);
        return new PersonalDetailsPage(driver);
    }
}
