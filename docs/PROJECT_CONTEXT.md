# Project context: InnovationPortalSelenium

Every task in this repository must follow these rules. Read this file before doing any work.

## Project facts

- Application under test: Innovation Portal, base URL `https://10.20.3.66/innovation-portal`; environments: `test`

  ```
  ENV="test"
  BASE_URL="https://10.20.3.66/innovation-portal"
  EMPLOYEE_USERNAME="<set locally>"
  EMPLOYEE_PASSWORD="<set locally>"
  HEADED="false"
  RETRY_COUNT="1"
  PARALLEL="1"
  LOG_LEVEL="info"
  BROWSER="chromium"
  ```

- Scope: only the 18 Priority 1 (P1) manual test cases from docs/testcases/
- Test accounts come from environment variables `EMPLOYEE_USERNAME`, `EMPLOYEE_PASSWORD`; never hardcode credentials

## Mandatory stack

- Java 17, Maven
- Selenium WebDriver 4 with Selenium Manager
- JUnit 5 (Jupiter) only. No Cucumber, no Gherkin, no BDD tooling.
- Page Object Model
- Test data in JSON files (Jackson)
- Constants class for timeouts, paths and fixed strings
- Hooks via JUnit 5 extensions and @BeforeEach/@AfterEach; tags via @Tag (P1, smoke, one tag per feature)
- Utils package (waits, JSON reader, screenshots, random data)
- Reporting: Allure + Maven Surefire reports

## Package layout

- src/main/java/com/innovationportal/{config,constants,driver,pages,utils,models}
- src/test/java/com/innovationportal/{base,hooks,tests/<feature>}
- src/test/resources/{testdata,config}

## Coding rules

1. Locators only in page objects, never in tests.
2. Assertions only in tests, never in page objects.
3. Explicit waits only. No Thread.sleep, no implicit waits.
4. Test data only from JSON files.
5. Every test: @Test, @Tag("P1"), a feature tag, @DisplayName("<TestCaseID> - <title>").
6. Tests are independent and can run in any order.
7. Browser, headless, env and baseUrl are configurable via system properties with defaults.
8. Locator preference: id, data-* attribute, name, CSS. XPath only as a last resort.

## Way of working

- Do only what the current Jira ticket asks.
- List the files you will create or change before writing code.
- If information is missing (locators, data, expected results), stop and comment on the ticket, or add // TODO(<TestCaseID>): <what is missing>. Never invent it.
- Work on the branch named in the ticket, and open one PR titled "<JIRA-KEY>: <ticket title>".
- In the PR description: what changed, how to run it, results, open questions.
