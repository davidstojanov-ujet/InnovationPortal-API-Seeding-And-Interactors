# P1 coverage and code-rule report

Jira: DEM-68 (parent DEM-59). Snapshot of `main` at `0e9f147` (after DEM-67). Read-only audit: no test logic was changed.

Source of truth for the IDs: [p1-analysis.md](p1-analysis.md) (18 P1 test cases). Rules: [PROJECT_CONTEXT.md](../PROJECT_CONTEXT.md#coding-rules).

## Summary

| Check | Result |
|---|---|
| P1 IDs in p1-analysis.md | 18 |
| Implemented (enabled) | 14 |
| `@Disabled` (blocked by open questions) | 4: TC-LEGACY-089, TC-NEW-022, TC-NEW-027, TC-NEW-035 |
| Missing | 0 |
| Duplicated | 0 |
| Tests without `@Tag("P1")`, a feature tag or `@DisplayName` | 0 |
| Locators in tests | 0 |
| Assertions in page objects | 0 |
| `Thread.sleep` / implicit waits | 0 |
| Hardcoded test data in tests | 0 |
| XPath locators | 0 |
| Unused page-object methods | 3 (follow-up F1) |
| Minor findings | 4 (follow-ups F2 to F4, plus one informational) |

## Coverage table

Classes are in `src/test/java/com/innovationportal/tests/`. Every P1 method has `@Test`, `@Tag(Constants.TAG_P1)`, a feature tag and `@DisplayName("<ID> - <title>")`.

| Test case ID | Class | Method | Feature tag | Status |
|---|---|---|---|---|
| TC-LEGACY-001 | submit/SubmitClientIdeaTest | employeeSubmitsClientInitiativeIdea | submit | implemented |
| TC-LEGACY-004 | submit/SubmitClientIdeaTest | employeeSubmitsOrganisationInitiativeIdea | submit | implemented |
| TC-LEGACY-007 | validation/SubmitFormValidationTest | clientFormBlocksSubmitUntilMandatoryFieldsAreFilled | validation | implemented |
| TC-LEGACY-008 | validation/SubmitFormValidationTest | orgFormBlocksSubmitUntilMandatoryFieldsAreFilled | validation | implemented |
| TC-LEGACY-029 | approval/IdeaEvaluationTest | evaluatorSavesDraftWithoutImpactOfIdea | approval | implemented |
| TC-LEGACY-081 | idealist/IdeaListVisibilityTest | approvedNonConfidentialIdeaIsShownInIdeaList | idealist | implemented |
| TC-LEGACY-082 | idealist/IdeaListVisibilityTest | approvedConfidentialIdeaIsNotShownInIdeaList | idealist | implemented |
| TC-LEGACY-089 | idealist/IdeaListVisibilityTest | approverConfidentialChoiceDecidesIdeaListVisibility | idealist | @Disabled |
| TC-LEGACY-101 | tracking/CustomerInitiativeProgressTest | implementedAndRejectedAreEndStates | tracking | implemented |
| TC-LEGACY-107 | tracking/CustomerInitiativeProgressTest | customerStatusOffersAllOptions | tracking | implemented |
| TC-LEGACY-116 | approval/IdeaEvaluationTest | evaluatorMustRateIdeaToApprove | approval | implemented |
| TC-NEW-011 | approval/IdeaEvaluationTest | newIdeasAreAssignedToDefaultEvaluators | approval | implemented |
| TC-NEW-013 | approval/IdeaEvaluationTest | evaluatorCannotRejectWithoutComment | approval | implemented |
| TC-NEW-022 | tracking/CustomerInitiativeProgressTest | projectManagerUpdatesCustomerProgressOnly | tracking | @Disabled |
| TC-NEW-025 | accelerator/AcceleratorVisibilityTest | clientAcceleratorRadioShownOnlyAtImplemented | accelerator | implemented (fails by design, see note) |
| TC-NEW-026 | accelerator/AcceleratorVisibilityTest | orgAcceleratorRadioShownOnlyAtPocCompleted | accelerator | implemented (fails by design, see note) |
| TC-NEW-027 | accelerator/AcceleratorVisibilityTest | markedAcceleratorNotifiesHeadsAndShowsInReports | accelerator | @Disabled |
| TC-NEW-035 | security/ApprovalDashboardAccessTest | employeeCannotOpenApprovalDashboardByDirectUrl | security | @Disabled |

Not P1: `smoke/FrameworkSmokeTest.baseUrlOpensSignInPage` (SMOKE-001, tag `smoke`).

Note on TC-NEW-025 and TC-NEW-026: both are implemented and assert the spec. They fail on the live app because the accelerator radio's visibility doesn't match the spec (Q26, app/spec mismatch, reported in DEM-67). They should stay red until the app or the spec changes.

## Missing, duplicated and disabled IDs

- Missing: none.
- Duplicated: none. Each ID appears in exactly one `@DisplayName`.
- Disabled, with the reason taken from the `@Disabled` text:

| ID | Location | Reason |
|---|---|---|
| TC-LEGACY-089 | IdeaListVisibilityTest.java:83 | Q19: the spec doesn't say where the approver sets Confidential Yes/No |
| TC-NEW-022 | CustomerInitiativeProgressTest.java:151 | Q25: no Expected Result and no PM-owned idea. Q3: no Project Manager account (Adtest04 is admin, so "cannot approve" can't be checked) |
| TC-NEW-027 | AcceleratorVisibilityTest.java:181 | Q27: no Expected Result (how to verify notifications, which report shows the Accelerator flag, what the Excel contains). Q3: no head-role accounts |
| TC-NEW-035 | ApprovalDashboardAccessTest.java:29 | Q4: no Employee-only account. Q28: the expected denial behaviour isn't specified |

## Rule violations

| Rule | Result |
|---|---|
| 1. Locators only in page objects | No `By.`, `findElement` or `@FindBy` in `src/test/java`. Tests only use `DriverManager` in `BaseTest` and `TestLifecycleExtension` (driver lifecycle and screenshots). |
| 2. Assertions only in tests | No `assert*`, `Assertions` or `fail(` in `src/main/java`. See F2 for borderline state checks. |
| 3. Explicit waits only | No `Thread.sleep`, `implicitlyWait` or `TimeUnit` anywhere. All waits go through `WaitUtils`. |
| 4. Test data from JSON | All inputs and expected values come from `JsonUtils.read(...)` (`src/test/resources/testdata`). Unique titles come from `RandomDataUtils.uniqueString(<JSON prefix>)`. No hardcoded credentials. |
| 5. Tags and display names | All 18 P1 methods have `@Test`, `@Tag(P1)`, one feature tag and `@DisplayName("<ID> - <title>")`. The titles match p1-analysis.md (see F4). |
| 6. Independent tests | No `@Order`, `@TestMethodOrder`, `@BeforeAll` or shared mutable static state. Each test creates its own idea. |
| 7. Configurable via system properties | `ConfigReader.java:67-73` reads the system property first, then `config.properties` (`browser`, `headless`, `env`, `baseUrl`). |
| 8. Locator preference | No XPath. Locators use id, name, `data-drupal-selector` and CSS. |
| Unused page-object methods | 3 found (F1) |

### Findings for follow-up

| # | File:line | Finding | Suggested fix |
|---|---|---|---|
| F1 | `pages/HomePage.java:32` | `HomePage.getHeading()` is never called. Its `pageHeading` locator is only used by this method. | Remove it, or use it in a test. |
| F1 | `pages/HomePage.java:36` | `HomePage.getCurrentUrl()` is never called. | Remove it. |
| F1 | `pages/IdeaSubmitPage.java:68` | `IdeaSubmitPage.getCurrentUrl()` is never called. | Remove it. |
| F2 | `pages/IdeaSubmitPage.java:273` | `isSubmitIdeaEnabled()` lets the `TimeoutException` from `WaitUtils.waitForEnabled` escape, so a failure shows up as a timeout and not as the test's assertion message. `isLoaded()` (`:62-66`) does the same through `waitForInnovatorsLoaded()`. | Catch `TimeoutException` and return `false`, as `EvaluationPage.java:197-203` already does. |
| F2 | `pages/ApprovalDashboardPage.java:95` | `openIdea` throws `IllegalStateException` when the idea isn't in the grid. This is acceptable as a precondition guard (the test checks `isIdeaListed` first, `IdeaEvaluationTest.java:324`). | Optional: keep it, or return an `Optional`. |
| F3 | `pages/MyIdeasPage.java:40` | Column index `get(2)` is a magic number. `ApprovalDashboardPage.java:22-23` uses named constants for the same kind of lookup. | Add a named constant. |
| F4 | 9 `@DisplayName`s (`SubmitClientIdeaTest:37,128`, `SubmitFormValidationTest:44,110`, `IdeaEvaluationTest:49,107,160,224`, `ApprovalDashboardAccessTest:33`) | These drop the title's trailing period. The other 9 keep it. | Cosmetic: pick one style. |

Informational, no action: the `/innovation-portal` context path is part of some page-object `open()` paths and `href` locators (for example `HomePage.java:14`). If `baseUrl` ever points to a different context path, those would need changing.

## How this was checked

- `grep` over `src/test/java` for `@Test`, `@Tag`, `@DisplayName` and `@Disabled`, cross-checked against the 18 IDs in p1-analysis.md.
- `grep` over `src` for `By.`, `findElement`, `@FindBy`, `xpath`, `assert`, `Thread.sleep` and `implicitlyWait`.
- Manual review of the test classes for literals, and a check of every page-object method for callers in `src/main` and `src/test`.
- `mvn -q clean test-compile` passes (Java 17).
