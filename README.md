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

## How to run

_Placeholder — will be documented once the framework is in place._

## Running in Barko

_Placeholder — will be documented once the framework is in place._

## Contribution rules

- Branch: `feature/<JIRA-KEY>-<short-name>`
- Commit message: `<JIRA-KEY>: <change>`
- One PR per ticket, using the PR template (`.github/pull_request_template.md`)
- CI (`.github/workflows/ci.yml`) runs `mvn clean test-compile` on every PR; merge only when it is green
- Squash-merge; head branches are deleted automatically after merge
