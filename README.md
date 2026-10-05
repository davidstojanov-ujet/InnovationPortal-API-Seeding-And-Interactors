# InnovationPortalSelenium

Innovation Portal Selenium test automation.

## Purpose

Automated UI regression tests for the Innovation Portal, covering the 18 Priority 1 (P1) manual test cases from `docs/testcases/`. All rules for working in this repository are in [docs/PROJECT_CONTEXT.md](docs/PROJECT_CONTEXT.md). Read it before you start any task.

## Tech stack

- Java 17, Maven
- Selenium WebDriver 4 (with Selenium Manager for driver binaries)
- JUnit 5 (Jupiter)
- Page Object Model
- Test data in JSON files (Jackson)
- Reporting: Allure + Maven Surefire reports

## Prerequisites

- JDK 17
- Maven 3.9+
- Google Chrome
- VS Code with the Extension Pack for Java

## Project structure

```
.
├── docs/
│   ├── PROJECT_CONTEXT.md        # project rules — read first
│   └── testcases/                # P1 manual test cases (source of scope)
├── src/
│   ├── main/java/com/innovationportal/
│   │   ├── config/               # configuration loading
│   │   ├── constants/            # timeouts, paths, fixed strings
│   │   ├── driver/               # WebDriver creation and lifecycle
│   │   ├── pages/                # page objects (locators live here only)
│   │   ├── utils/                # waits, JSON reader, screenshots, random data
│   │   └── models/               # test data models
│   └── test/
│       ├── java/com/innovationportal/
│       │   ├── base/             # base test class
│       │   ├── hooks/            # JUnit 5 extensions
│       │   └── tests/<feature>/  # test classes, one package per feature
│       └── resources/
│           ├── testdata/         # JSON test data
│           └── config/           # environment configuration
└── pom.xml
```

## Run it as a new QA

1. Connect the FortiClient VPN (the test environment `https://10.20.3.66/innovation-portal` is only reachable through it).
2. `cp .env.example .env`. Then set `EMPLOYEE_PASSWORD` in `.env`. Ask the QA manager for the password. `.env` is gitignored: never commit it.
3. `./run-tests.sh -Dgroups=P1` (any extra arguments go to Maven, e.g. `-Dtest=SubmitClientIdeaTest`).

`run-tests.sh` loads `.env` and passes `ENV`, `BASE_URL`, `BROWSER` and `HEADED` to Maven as `-Denv`, `-DbaseUrl`, `-Dbrowser` and `-Dheadless`. The role accounts (`EVALUATOR_*`, `ADMIN_*`, ...) default to the employee account until separate accounts exist.
Idea routing data (employee, customer/project or department, default evaluator, committee members, progress updaters) is in `src/test/resources/testdata/idea-routing.json`. Its accounts (Adtest15, Adtest08, Adtest09, Adtest10, Rajesh, Satyam Sharma) have their own `<USER>_USERNAME` / `<USER>_PASSWORD` entries in `.env.example`. All test accounts share one password, so they reuse `EMPLOYEE_PASSWORD`: you only type it once. `EMPLOYEE_ONLY_*` (an employee without evaluator rights, used by TC-NEW-035 and TC-NEW-022) is Adtest15, and `PROJECT_MANAGER_*` (TC-NEW-022) is Adtest08.

## How to run

The test account comes from the `EMPLOYEE_*` environment variables listed in [docs/PROJECT_CONTEXT.md](docs/PROJECT_CONTEXT.md). Set them in your shell (or in `.env` for `run-tests.sh`); never commit them.

| Goal | Command |
|------|---------|
| Compile only | `mvn clean test-compile` |
| Run all tests | `mvn clean test` |
| Only P1 tests | `mvn test -Dgroups=P1` |
| P1 **and** smoke | `mvn test -Dgroups="P1 & smoke"` |
| Skip work-in-progress tests | `mvn test -DexcludedGroups=wip` |
| Headless / headed browser | `mvn test -Dheadless=true` (default) / `-Dheadless=false` |
| Choose the environment | `mvn test -Denv=<env>` (reads `<env>.baseUrl` from `src/test/resources/config/config.properties`) |

Options can be combined, e.g. `mvn test -Dgroups="P1 & smoke" -DexcludedGroups=wip -Dheadless=true -Denv=test`.
`-Dgroups` / `-DexcludedGroups` take JUnit 5 tag expressions (`&`, `|`, `!`). Tags are set with `@Tag` (`P1`, `smoke`, one per feature).
Other overrides: `-Dbrowser=chrome|firefox|edge`, `-DbaseUrl=<url>`, `-DacceptInsecureCerts=false` (default `true`, because the test environment's TLS certificate is not publicly trusted).

Tests run one at a time. Parallel execution is off in `src/test/resources/junit-platform.properties`, which documents how to turn it on later.

### Reports

- **Allure**: raw results are written to `target/allure-results`.
  - `mvn allure:report` builds the HTML report in `target/site/allure-maven-plugin`.
  - `mvn allure:serve` builds the report and opens it in a browser.
- **Surefire**: plain-text and XML reports in `target/surefire-reports`.
- **Failures**: `TestLifecycleExtension` (registered on `BaseTest`) attaches a screenshot and the current URL to the Allure result of a failed test. The PNG is also saved in `target/screenshots`.

## Running in Barko

_Placeholder — will be documented once the framework is in place._

## Contribution rules

- Branch: `feature/<JIRA-KEY>-<short-name>`
- Commit message: `<JIRA-KEY>: <change>`
- One PR per ticket, using the PR template (`.github/pull_request_template.md`)
- CI (`.github/workflows/ci.yml`) runs `mvn clean test-compile` on every PR; merge only when it is green
- Squash-merge; head branches are deleted automatically after merge
