# Dyad_Automation

UI automation of the full employee lifecycle (create, search, update, delete) on the
[OrangeHRM demo site](https://opensource-demo.orangehrmlive.com/), built for the Dyad Technologies
QA Automation assessment, Scenario 2.

## Tech stack

| Purpose | Tool |
|---|---|
| Language | Java 21 |
| Browser automation | Selenium 4 (drivers resolved by Selenium Manager) |
| Test runner | TestNG |
| Build | Maven, through the included Maven Wrapper |
| Reporting | Allure |
| Logging | SLF4J with Log4j2 |
| CI | GitHub Actions |

## Prerequisites

- JDK 21 or later, with `JAVA_HOME` set
- Chrome (default), Edge or Firefox
- Internet access to the demo site

Maven and Allure do not need to be installed.

## Running the tests

On Windows, use `mvnw.cmd` in place of `./mvnw`.

```bash
./mvnw clean test
```

Run headless:

```bash
./mvnw clean test -Dheadless=true
```

Run on another browser:

```bash
./mvnw clean test -Dbrowser=edge
```

## Viewing the report

Run this after the tests. It generates the Allure report and opens it in a browser:

```bash
./mvnw allure:serve
```

Failed tests include a screenshot, the current URL and the page source. Logs are written to the
console and to `target/logs/test-run.log`.

## Configuration

Defaults are in `src/test/resources/config.properties`. A value is resolved in this order:

1. `-D` system property, such as `-Dtimeout.seconds=60`
2. Environment variable, such as `TIMEOUT_SECONDS=60`
3. `config.properties`

| Key | Default | Meaning |
|---|---|---|
| `base.url` | demo site URL | Application under test |
| `browser` | `chrome` | `chrome`, `edge` or `firefox` |
| `headless` | `false` | Run without a visible browser |
| `timeout.seconds` | `30` | Explicit wait timeout |
| `admin.username`, `admin.password` | `Admin`, `admin123` | The public credentials shown on the demo login page |

## Project structure

```
src/main/java/com/dyad/hrms/
├── config/       Configuration lookup
├── driver/       Browser creation
├── models/       Test data model
├── utils/        Unique test data
├── components/   Widgets shared across pages (dropdown, delete dialog)
└── pages/        Page objects: locators and actions, no assertions
src/test/java/com/dyad/hrms/
├── base/         Browser setup and teardown
├── listeners/    Failure diagnostics for Allure
└── tests/        Test flow and all assertions
```

## Test coverage

`EmployeeLifecycleTest` has one test per phase, chained with `dependsOnMethods`, so a failure marks
the later phases as skipped instead of reporting misleading failures.

| Test | What it verifies |
|---|---|
| `loginAsAdmin` | Admin reaches the dashboard |
| `createEmployee` | The record is saved with the generated ID (validation 1) |
| `searchEmployee` | Searching by that ID returns exactly that employee |
| `updateEmployee` | Saving changed details shows a success message |
| `verifyUpdate` | After a reload, the details and the list show the new values |
| `deleteEmployee` | Deleting shows a success message |
| `searchAfterDeletion` | Searching by the ID returns no records (validation 2) |

**Validation 1, dynamic ID:** the test never supplies an ID. It reads the value the form pre-fills
and asserts that it is numeric, that the saved record has the same ID, and that the form then
generates a different one.

**Validation 2, after deletion:** the test searches by the captured ID and asserts both the
"No Records Found" label and zero table rows.

## Assumptions and design decisions

- The brief does not name the fields to update, so the tests change last name, Other Id and marital
  status, covering text inputs and a custom dropdown.
- The demo site is public and shared, so:
  - Test data has random suffixes and all lookups use the captured ID.
  - The site can pre-fill an ID that is already taken. It rejects that on save, so the test reads a
    fresh ID and retries, up to 3 attempts.
  - A failed run deletes its employee in `@AfterClass`.
  - The site occasionally serves an HTTP 500 page. Waits reload only when the application failed to
    load at all, so failures inside the application still fail the test.
- Waits are explicit only. The implicit wait stays at 0 and there are no sleeps.
- There is no automatic test retry, because it would hide real flakiness. Failures caused by the
  demo site being slow or unavailable can be addressed by raising `timeout.seconds`.
- Tests run sequentially because they are steps of one lifecycle sharing one browser session.
