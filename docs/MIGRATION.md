# Migration: documentation for moving the P1 suite

These documents describe the 14 P1 tests that passed in the latest full local run (2026-10-05, `./run-tests.sh -Dgroups=P1`: 14 passed, 2 failed, 2 skipped on `main` @ fa4aca2). They are the source reference for converting the suite to another repository and framework ([InnovationPortalPlaywright](https://github.com/AleksandarJovanchev/InnovationPortalPlaywright), copied there under `reference/selenium-docs/`).

`main` keeps all 18 P1 tests. Branch `migration/passed-p1` keeps only the 14 passing ones (it is not meant to be merged). Where the documents say "this branch", they mean `migration/passed-p1`; for those 14 tests the code on `main` is the same.

## Migration documents

| Document | Contents |
|----------|----------|
| [migration/DEPENDENCIES.md](migration/DEPENDENCIES.md) | Toolchain, Maven dependencies and plugins with versions, configuration keys, browser options, environment prerequisites, environment variables and accounts, commands |
| [migration/TEST_INVENTORY.md](migration/TEST_INVENTORY.md) | Each of the 14 tests step by step: data, actions, selectors, assertions, side effects, open questions; shared flows; selector and test data appendices |
| [migration/APP_BEHAVIOUR_NOTES.md](migration/APP_BEHAVIOUR_NOTES.md) | Portal behaviour a new framework must handle (sign-in, no logout, Access Denied, layout, AJAX, grids) and the known app/spec gaps |

## Tests documented (14, all on `main` and on `migration/passed-p1`)

| Test case | Test method |
|-----------|-------------|
| TC-LEGACY-001 | `SubmitClientIdeaTest#employeeSubmitsClientInitiativeIdea` |
| TC-LEGACY-004 | `SubmitClientIdeaTest#employeeSubmitsOrganisationInitiativeIdea` |
| TC-LEGACY-007 | `SubmitFormValidationTest#clientFormBlocksSubmitUntilMandatoryFieldsAreFilled` |
| TC-LEGACY-008 | `SubmitFormValidationTest#orgFormBlocksSubmitUntilMandatoryFieldsAreFilled` |
| TC-LEGACY-029 | `IdeaEvaluationTest#evaluatorSavesDraftWithoutImpactOfIdea` |
| TC-LEGACY-116 | `IdeaEvaluationTest#evaluatorMustRateIdeaToApprove` |
| TC-NEW-011 | `IdeaEvaluationTest#newIdeasAreAssignedToDefaultEvaluators` |
| TC-NEW-013 | `IdeaEvaluationTest#evaluatorCannotRejectWithoutComment` |
| TC-LEGACY-081 | `IdeaListVisibilityTest#approvedNonConfidentialIdeaIsShownInIdeaList` |
| TC-LEGACY-082 | `IdeaListVisibilityTest#approvedConfidentialIdeaIsNotShownInIdeaList` |
| TC-LEGACY-101 | `CustomerInitiativeProgressTest#implementedAndRejectedAreEndStates` |
| TC-LEGACY-107 | `CustomerInitiativeProgressTest#customerStatusOffersAllOptions` |
| TC-NEW-022 | `CustomerInitiativeProgressTest#projectManagerUpdatesCustomerProgressOnly` |
| TC-NEW-035 | `ApprovalDashboardAccessTest#employeeCannotOpenApprovalDashboardByDirectUrl` |

## Not documented: removed from `migration/passed-p1` (still on `main`)

| Test case | Why | Ticket |
|-----------|-----|--------|
| TC-NEW-025, TC-NEW-026 | Fail: the IP / Accelerator radio is shown at Pending Approval; the spec says hidden (Q26) | DEM-78 |
| TC-NEW-027 | Skipped: Idea Reports and its Excel export have no Accelerator/IP flag (Q27) | - |
| TC-LEGACY-089 | Skipped: the approver cannot set Confidential (Evaluation page has no field; admin edit radios are disabled) (Q19) | - |

Code used only by those tests was removed too: `AcceleratorVisibilityTest`, `AcceleratorDashboardPage`, `IdeaReportsPage`, `OrganisationInitiativeProgressPage`, `AcceleratorGateData`, `accelerator.json`, `EvaluationPage.isMarkAcceleratorDisplayed`, `Constants.TAG_FEATURE_ACCELERATOR`, and the `approverConfidentialChoice` data in `idealist.json`.

The analysis documents in `docs/testcases/` still describe all 18 P1 test cases.

## Running

Same as `main`: `cp .env.example .env`, set `EMPLOYEE_PASSWORD`, then `./run-tests.sh -Dgroups=P1`. Accounts: Adtest04 (employee, evaluator, admin), Adtest15 (employee without evaluator rights), Adtest08 (Project Manager role for TC-NEW-022). All accounts share one password.
