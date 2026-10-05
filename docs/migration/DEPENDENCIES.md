# Dependencies and environment

What the 14 passing P1 tests on `migration/passed-p1` need to build and run today. Use it to choose the equivalents in the target framework. Every version below is taken from `pom.xml` on this branch.

## Toolchain

| Item | Version / value | Notes |
|------|-----------------|-------|
| JDK | 17 (`maven.compiler.release` 17) | CI uses Temurin 17 (`.github/workflows/ci.yml`) |
| Maven | 3.9+ | CI runs `mvn -B -ntp clean test-compile` only; tests need the VPN, so they do not run in CI |
| Encoding | UTF-8 | source and reporting |
| Browser | Google Chrome (local runs used Chrome 154) | Edge and Firefox are supported by `DriverFactory` but were not used in the P1 runs |
| Driver binaries | Selenium Manager (bundled with Selenium) | no WebDriverManager and no checked-in drivers |

## Maven dependencies

| GroupId:ArtifactId | Version | Scope | Used for |
|--------------------|---------|-------|----------|
| `org.seleniumhq.selenium:selenium-java` | 4.50.0 | compile | WebDriver, waits (`WebDriverWait`, `ExpectedConditions`), `Select`, Chromium CDP |
| `org.junit:junit-bom` | 5.14.4 | import (dependencyManagement) | aligns all JUnit Platform/Jupiter artifacts |
| `org.junit.jupiter:junit-jupiter-api` | 5.14.4 (BOM) | test | `@Test`, `@Tag`, `@DisplayName`, assertions, `@ExtendWith` |
| `org.junit.jupiter:junit-jupiter-engine` | 5.14.4 (BOM) | test | test engine |
| `org.junit.jupiter:junit-jupiter-params` | 5.14.4 (BOM) | test | on the classpath; the 14 tests do not use parameterized tests |
| `com.fasterxml.jackson.core:jackson-databind` | 2.22.3 | compile | JSON test data to POJOs (`JsonUtils`); plain `ObjectMapper`, so unknown JSON fields fail the read |
| `io.qameta.allure:allure-jupiter` | 2.35.5 | compile | Allure results, `@Description`, `@Story`, attachments |
| `org.aspectj:aspectjweaver` | 1.9.25 | compile | Java agent for Allure (set in the Surefire `argLine`) |
| `org.slf4j:slf4j-simple` | 2.0.20 | compile | logging (`TestLifecycleExtension` START / FAILED lines) |

## Maven plugins

| Plugin | Version | Configuration that matters |
|--------|---------|----------------------------|
| `maven-compiler-plugin` | 3.16.0 | `release` 17, UTF-8 |
| `maven-surefire-plugin` | 3.6.0 | `groups` / `excludedGroups` from `-Dgroups` / `-DexcludedGroups` (JUnit 5 tag expressions); `-javaagent` AspectJ weaver; system property `allure.results.directory` (default `target/allure-results`) |
| `io.qameta:allure-maven` | 3.1.0 | `mvn allure:report` / `allure:serve`; report CLI 2.46.1; reads `allure.results.directory` |

`src/test/resources/junit-platform.properties`: `junit.jupiter.execution.parallel.enabled=false`. Tests run one at a time. `DriverManager` keeps one driver per thread, so parallel runs could be enabled later.

## Runtime configuration

`src/test/resources/config/config.properties`. Every key can be overridden with `-D<key>=<value>`:

| Key | Default | Meaning |
|-----|---------|---------|
| `browser` | `chrome` | `chrome` (alias `chromium`), `firefox`, `edge` |
| `headless` | `true` | `-Dheadless=false` for a visible browser |
| `env` | `test` | selects `<env>.baseUrl` |
| `baseUrl` | empty | overrides `<env>.baseUrl` when set |
| `test.baseUrl` | `https://10.20.3.66/innovation-portal` | the only environment |
| `acceptInsecureCerts` | `true` | the test environment's TLS certificate is not publicly trusted |
| `explicitWaitSeconds` | `10` | default explicit wait |
| `pageLoadTimeoutSeconds` | `30` | local runs used `-DpageLoadTimeoutSeconds=90` |
| `windowSize` | `1920,1080` | also used for the headless virtual screen (see below) |

Fixed timeouts in `Constants`: `SHORT_TIMEOUT` 3 s (settle checks), `DEFAULT_TIMEOUT` 10 s, `LONG_TIMEOUT` 30 s (Approval Dashboard filter AJAX), polling 250 ms. There are no implicit waits and no `Thread.sleep`.

Browser options (`DriverFactory`): `--headless=new`, `--window-size=<windowSize>`, `--no-sandbox`, `--disable-dev-shm-usage`, `--disable-gpu`, `acceptInsecureCerts`. In headless Chrome/Edge it also passes `--screen-info={<w>x<h>}`. Without it the virtual screen is 800x600, and the portal's CSS hides all content behind a "not supporting Landscape mode" overlay (DEM-62).

## Environment prerequisites

| Prerequisite | Details |
|--------------|---------|
| Network | FortiClient VPN connected; `https://10.20.3.66/innovation-portal` is not reachable otherwise |
| TLS | self-signed / internal CA certificate; the browser must accept insecure certificates |
| Sign-in | ADFS (`adtestadfs.adtest.com`): `#userNameInput`, `#passwordInput`, `#submitButton`; an optional Xornet Global Screen follows (`#stop` = Stay Here) |
| Accounts | all `ADTest\...` test accounts share one password (never committed) |
| Test data | each run creates new ideas (title = JSON `titlePrefix` + `_` + 8 random chars); nothing is cleaned up |

## Environment variables

Copy `.env.example` to `.env` and set `EMPLOYEE_PASSWORD`. `./run-tests.sh` loads `.env` and maps `ENV`, `BASE_URL`, `BROWSER`, `HEADED` to `-Denv`, `-DbaseUrl`, `-Dbrowser`, `-Dheadless`. JSON test data reads credentials as `"${VAR}"` placeholders (system property first, then environment variable; a missing one fails the read).

| Variable | Account at run time | Used by (on this branch) |
|----------|---------------------|--------------------------|
| `EMPLOYEE_USERNAME` / `_PASSWORD` | `ADTest\Adtest04` | submit, validation (`submit.json`, `validation.json`) |
| `EVALUATOR_USERNAME` / `_PASSWORD` | `ADTest\Adtest04` (evaluator + System Admin) | approval, idealist, tracking |
| `EMPLOYEE_ONLY_USERNAME` / `_PASSWORD` | `ADTest\Adtest15` (no evaluator rights) | TC-NEW-035 (`security.json`), TC-NEW-022 submitter (`tracking.json`) |
| `PROJECT_MANAGER_USERNAME` / `_PASSWORD` | `ADTest\Adtest08` (Project Manager by the DEM-82 decision) | TC-NEW-022 (`tracking.json`) |
| `ADMIN_*`, `VERTICAL_HEAD_*`, `PRACTICE_HEAD_*`, `DEPARTMENT_HEAD_*` | `ADTest\Adtest04` placeholders | no test on this branch (they were used by the removed accelerator tests) |
| `ADTEST08/09/10/15_*` | idea routing accounts | reference only (`idea-routing.json`) |
| `ENV`, `BASE_URL`, `BROWSER`, `HEADED` | `test`, portal URL, `chromium`, `false` | `run-tests.sh` |
| `RETRY_COUNT`, `PARALLEL`, `LOG_LEVEL` | `1`, `1`, `info` | documented project settings, not read by the framework |

## Commands

| Goal | Command |
|------|---------|
| Compile | `mvn clean test-compile` |
| All 14 P1 tests | `./run-tests.sh -Dgroups=P1` (about 16 min headless) |
| One class / method | `./run-tests.sh -Dgroups=P1 -Dtest=IdeaEvaluationTest#evaluatorCannotRejectWithoutComment` |
| Headed | `HEADED="true"` in `.env`, or `-Dheadless=false` |
| Allure report | `mvn allure:report` (HTML in `target/site/allure-maven-plugin`) or `mvn allure:serve` |

Outputs: `target/allure-results`, `target/surefire-reports`, and `target/screenshots`. A screenshot and the current URL are attached to every failed test by `TestLifecycleExtension`.
