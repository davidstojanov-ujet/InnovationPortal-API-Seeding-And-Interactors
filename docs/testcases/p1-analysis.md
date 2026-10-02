# P1 Test Case Analysis - Innovation Portal

- **Source:** `docs/testcases/p1-testcases.csv` (export of `ManualSteps-P1.xlsx`, sheet `TestCases`)
- **Count check:** 18 P1 rows found = 18 expected (all rows have Priority = P1)
- **Scope:** the 18 Priority 1 manual test cases only (per `docs/PROJECT_CONTEXT.md`). This document is analysis only; it contains no code. All data, locators, routes and texts come from the CSV or the repo. Gaps are recorded as open questions (Q1-Q30) instead of being filled in.
- **Credentials:** the sheet gives ADFS usernames only; passwords always come from env vars (`EMPLOYEE_PASSWORD`, etc.) and are never written into the repo.
- **Repo context:** `BasePage` (click/type/getText/isDisplayed/select helpers with explicit waits); `HomePage` (detects the ADFS redirect via `#userNameInput`); `users.json` has only the `employee` role (`${EMPLOYEE_USERNAME}` + password from env); `Constants` has tags `P1`, `smoke`, `login`; `acceptInsecureCerts=true` is already set in `config.properties`.
- **Step flags:** each step's inline flag in "Detailed Test Steps" was cross-checked against the "Step Automation Status" column and they match for all 18 cases. Steps 1-5 (browser, navigate, ADFS, Xornet Global Screen, portal loaded) are the same in every case.

## Contents

- [TC-LEGACY-001](#tc-legacy-001) - Verify if an employee who is allocated to a project is able to submit the Client initiativ...
- [TC-LEGACY-004](#tc-legacy-004) - Verify if an employee who is allocated to a project is able to submit the Organization ini...
- [TC-LEGACY-007](#tc-legacy-007) - To verify the validation for Client Innovation Form.
- [TC-LEGACY-008](#tc-legacy-008) - To verify the validation for Organization Innovation Form.
- [TC-LEGACY-029](#tc-legacy-029) - Verify Approve is blocked when Impact of Idea (option + $ + description) is missing; Save ...
- [TC-LEGACY-081](#tc-legacy-081) - Verify if this is a dashboard which displays all ideas that have been approved and are not...
- [TC-LEGACY-082](#tc-legacy-082) - Verify dashboard displays all ideas that have been approved and confidential.
- [TC-LEGACY-089](#tc-legacy-089) - Verify if Approver selects ‘Yes’ idea will be kept confidential, In this case it will not ...
- [TC-LEGACY-101](#tc-legacy-101) - Verify Implemented locks further Customer Progress updates; POC Completed and Proposed als...
- [TC-LEGACY-107](#tc-legacy-107) - Verify Customer Status options include In Progress, Proposed to Customer, Accepted, Reject...
- [TC-LEGACY-116](#tc-legacy-116) - Verify Approve requires ratings 1-10 on Innovation Index (25%), Potential Business Impact ...
- [TC-NEW-011](#tc-new-011) - Verify Client idea auto-assigns vertical default evaluator; Org assigns department default...
- [TC-NEW-013](#tc-new-013) - Verify Reject without comment is blocked.
- [TC-NEW-022](#tc-new-022) - Verify Project Manager (Rel 5.1) can update Customer Progress only (not full evaluator app...
- [TC-NEW-025](#tc-new-025) - Verify Client Accelerator radio visible only when Status=Implemented to Vertical/Practice ...
- [TC-NEW-026](#tc-new-026) - Verify Org Accelerator radio visible only when Status=POC Completed for Dept/Practice Head...
- [TC-NEW-027](#tc-new-027) - Verify marking Accelerator notifies Department/Practice/Vertical heads and reflects in rep...
- [TC-NEW-035](#tc-new-035) - Verify Employee cannot open Approval Dashboard via direct URL.
- [Summary 1 - Proposed page objects](#summary-1---proposed-page-objects)
- [Summary 2 - Open questions](#summary-2---open-questions)
- [Summary 3 - Implementation plan](#summary-3---implementation-plan)
- [Summary 4 - Open question status (DEM-61)](#summary-4---open-question-status-dem-61)

---

## TC-LEGACY-001

**Title:** Verify if an employee who is allocated to a project is able to submit the Client initiative innovation.

| Field | Value |
|---|---|
| ID | TC-LEGACY-001 |
| Feature / module | Submit / My Ideas (sheet Name: Innovation Portal) |
| Change status | EXISTING |
| Requirement IDs | REQ-SUB-01, REQ-SUB-02 |
| Preconditions | Sheet: "Login as an employee user." Steps: Employee user (project-allocated). |
| Actions (sheet) | Click On Submit Idea. |
| Expected Result (sheet) | The user should be able to submit the Client initiative innovation. |
| Automated yes/no / Test Steps Automated | Yes / Partial |
| Tags | `P1`, `submit` |

### Steps

| # | Step (as in sheet) | Expected result (as in sheet) | Automated |
|---|---|---|---|
| 1 | Launch a supported browser (Chrome/Edge recommended). | Browser opens without errors. | [Yes] |
| 2 | Navigate to https://10.20.3.66/innovation-portal. | Portal or ADFS login page is displayed (ignore self-signed cert warning in test env). | [Yes] |
| 3 | If redirected to ADFS SSO (adtestadfs.adtest.com), enter Username "ADTest\Adtest15" in #userNameInput and Password in #passwordInput; click Sign in. | ADFS authenticates successfully. | [Yes] |
| 4 | If the Xornet Global Screen appears after login, click Stay Here (or wait for redirect countdown). | Global Screen is dismissed; session remains active. | [Yes] |
| 5 | Confirm Innovation Portal loads (URL contains /innovation-portal); re-navigate to https://10.20.3.66/innovation-portal if still on Xornet home. | Innovation Portal landing page is visible with role-appropriate navigation. | [Yes] |
| 6 | Verify logged-in user context matches precondition: Employee user (project-allocated). | User context matches Employee user (project-allocated) (ADTest\Adtest15). | [Partial] |
| 7 | Click "+ Submit Idea" on the landing page (or open https://10.20.3.66/innovation-portal/idea-submit). | Submit Idea form loads at /idea-submit. | [Yes] |
| 8 | In Innovation Type (#innovation_type), select "Client Initiative". | Client Initiative is selected; Customer Name and Project Name fields appear (Department hidden). | [Yes] |
| 9 | Fill all mandatory Client Initiative fields with random/test data: Customer Name* (#edit-customer-name — pick any assigned customer), Project Name* (#edit-project — pick any available project), Idea Title (#edit-title — unique value), Innovation Category* (click a category card), Domain* (#edit-domain — select 1–5 values), Brief description (#edit-summary), Innovation Details (#edit-innovation-details), Confidential* (#edit-confidential-yes or #edit-confidential-no), and at least one Impact of Idea checkbox with Amount + Description. | All mandatory fields accept test/random values; dropdowns show valid selections. | [Yes] |
| 10 | Optionally fill Business Use Case (#edit-business-use-case) if visible. | Optional Business Use Case accepts text if shown. | [Partial] |
| 11 | Review all fields marked mandatory (required indicator / red asterisk); complete any missing mandatory values with appropriate test data or random dropdown selections. | No mandatory field remains empty; required markers satisfied. | [Yes] |
| 12 | Verify the Submit Idea button (#edit-submit-idea) is enabled. If disabled, cross-check mandatory fields, update missing values, tab out of fields to trigger validation, and re-check until enabled. | Submit Idea button transitions from disabled to enabled once mandatory data is complete. | [Yes] |
| 13 | Click Submit Idea when the button is enabled. | Form submits without validation errors. | [Yes] |
| 14 | After submission, note the redirected URL (/innovation-portal/idea/{id}) and idea ID. | Browser navigates to idea detail page with a new idea ID. | [Partial] |
| 15 | Open My Ideas (https://10.20.3.66/innovation-portal/my-idea-list) and locate the submitted idea by title. | Submitted idea appears in My Ideas with Innovation Type "Client Initiative" and initial status (e.g., Pending Approval). | [No] |

### Test data needed

| Field | Value / source |
|---|---|
| ADFS username | `ADTest\Adtest15` (sheet, Employee) - supply via `EMPLOYEE_USERNAME` / `EMPLOYEE_PASSWORD` env vars (users.json key `employee`); never hardcode |
| Innovation Type | "Client Initiative" |
| Test Data column | "ADTEST-08     SafetyChain Mobile" - meaning unclear (user? customer? project?) - see Q5 |
| Customer Name* | "pick any assigned customer" - concrete value not specified - see Q5 |
| Project Name* | "pick any available project" - concrete value not specified - see Q5 |
| Idea Title | unique value (sheet) - generate with RandomDataUtils |
| Innovation Category* | not specified - see Q8 |
| Domain* | 1-5 values; values not specified - see Q8 |
| Brief description / Innovation Details | random/test text (sheet) |
| Confidential* | Yes or No (sheet allows either); which to use not specified - see Q12 |
| Impact of Idea | at least one checkbox + Amount + Description; options/amount not specified - see Q8 |
| Business Use Case | optional, "if visible" |

### Page objects

LoginPage, XornetGlobalScreenPage, HomePage, IdeaSubmitPage, IdeaDetailPage, MyIdeasPage

### Dependencies / setup

Employee account that is allocated to a project with at least one assigned customer (Q2, Q5). No dependency on other test cases.

### Automation risks

- Creates a real idea in a shared environment on every run (data pollution, triggers evaluator workload / possible notifications) - Q12.
- Dynamic dependent fields: Customer Name drives Project Name; Department hidden; Business Use Case conditionally visible.
- Submit button enable state depends on client-side validation ("tab out of fields") - wait on enabled state, not on time.
- Category is a clickable card and Impact of Idea is a checkbox+amount+description group - no locators (Q7).
- Steps 14-15: idea ID must be parsed from redirect URL /idea/{id}; My Ideas lookup by title is [No] and grid locators are missing (Q11).
- ADFS SSO redirect to a second host (adtestadfs.adtest.com) plus the optional Xornet Global Screen with a countdown redirect: conditional page, needs an explicit "either/or" wait (Q13).
- Self-signed certificate in test env: covered by `acceptInsecureCerts=true` in config.properties.
- Step 6 (verify user context) is [Partial]: no locator for the logged-in user indicator (Q13).

---

## TC-LEGACY-004

**Title:** Verify if an employee who is allocated to a project is able to submit the Organization initiative innovation.

| Field | Value |
|---|---|
| ID | TC-LEGACY-004 |
| Feature / module | Submit / My Ideas (sheet Name: Innovation Portal) |
| Change status | EXISTING |
| Requirement IDs | REQ-SUB-01, REQ-SUB-03 |
| Preconditions | Sheet: "Login as an employee user." Steps: Employee user. |
| Actions (sheet) | Click On Submit Idea. |
| Expected Result (sheet) | The user should be able to submit the organization initiative innovation. |
| Automated yes/no / Test Steps Automated | Yes / Partial |
| Tags | `P1`, `submit` |

### Steps

| # | Step (as in sheet) | Expected result (as in sheet) | Automated |
|---|---|---|---|
| 1 | Launch a supported browser (Chrome/Edge recommended). | Browser opens without errors. | [Yes] |
| 2 | Navigate to https://10.20.3.66/innovation-portal. | Portal or ADFS login page is displayed (ignore self-signed cert warning in test env). | [Yes] |
| 3 | If redirected to ADFS SSO (adtestadfs.adtest.com), enter Username "ADTest\Adtest15" in #userNameInput and Password in #passwordInput; click Sign in. | ADFS authenticates successfully. | [Yes] |
| 4 | If the Xornet Global Screen appears after login, click Stay Here (or wait for redirect countdown). | Global Screen is dismissed; session remains active. | [Yes] |
| 5 | Confirm Innovation Portal loads (URL contains /innovation-portal); re-navigate to https://10.20.3.66/innovation-portal if still on Xornet home. | Innovation Portal landing page is visible with role-appropriate navigation. | [Yes] |
| 6 | Verify logged-in user context matches precondition: Employee user. | User context matches Employee user (ADTest\Adtest15). | [Partial] |
| 7 | Click "+ Submit Idea" on the landing page (or open https://10.20.3.66/innovation-portal/idea-submit). | Submit Idea form loads at /idea-submit. | [Yes] |
| 8 | In Innovation Type (#innovation_type), select "Organisation Initiative". | Department* appears; Customer/Project fields hidden. | [Yes] |
| 9 | Fill all mandatory fields with random/test data from Test Data column; select random valid dropdown values (Title, Category, Domain, Summary, Details, Confidential, Impact of Idea, etc.). | All mandatory fields accept values; required markers satisfied. | [Yes] |
| 10 | Verify Submit Idea button (#edit-submit-idea) is enabled; if disabled, cross-check mandatory fields and complete missing values. | Submit button enabled when form is complete. | [Yes] |
| 11 | Click Submit Idea. | Form submits; browser navigates to /idea/{id} or shows success. | [Yes] |
| 12 | Open My Ideas (https://10.20.3.66/innovation-portal/my-idea-list) and verify submitted idea status/details. | The user should be able to submit the organization initiative innovation. | [Partial] |
| 13 | Capture screenshot if actual result differs from expected; log out / close browser. | Test evidence recorded; session closed. | [No] |

### Test data needed

| Field | Value / source |
|---|---|
| ADFS username | `ADTest\Adtest15` (sheet, Employee) - supply via `EMPLOYEE_USERNAME` / `EMPLOYEE_PASSWORD` env vars (users.json key `employee`); never hardcode |
| Innovation Type | "Organisation Initiative" (sheet spelling; Summary says "Organization") - see Q8 |
| Test Data column | "ADTEST-14           Tibco iMany" - meaning unclear; looks like a project, but Project is hidden for Org - see Q5, Q6 |
| Department* | not specified - see Q6 |
| Title, Category, Domain, Summary, Details, Confidential, Impact of Idea | "random/test data ... random valid dropdown values" (sheet); Category/Domain/Impact values not specified - see Q8 |

### Page objects

LoginPage, XornetGlobalScreenPage, HomePage, IdeaSubmitPage, IdeaDetailPage, MyIdeasPage

### Dependencies / setup

Employee account (Q2). Shares the IdeaSubmitPage with TC-LEGACY-001; no data dependency on other cases.

### Automation risks

- Creates a real idea per run (Q12).
- Post-submit result is "navigates to /idea/{id} or shows success" - two possible outcomes, oracle ambiguous (Q11).
- Department field appears only for Org type - no locator (Q7).
- Step 12 expected result just restates the Expected Result; which status/details to verify in My Ideas is unspecified (Q11).
- ADFS SSO redirect to a second host (adtestadfs.adtest.com) plus the optional Xornet Global Screen with a countdown redirect: conditional page, needs an explicit "either/or" wait (Q13).
- Self-signed certificate in test env: covered by `acceptInsecureCerts=true` in config.properties.
- Step 6 (verify user context) is [Partial]: no locator for the logged-in user indicator (Q13).

---

## TC-LEGACY-007

**Title:** To verify the validation for Client Innovation Form.

| Field | Value |
|---|---|
| ID | TC-LEGACY-007 |
| Feature / module | General (sheet Name: Innovation Portal) |
| Change status | EXISTING |
| Requirement IDs | REQ-SUB-01, REQ-SUB-02 |
| Preconditions | Sheet: "Login as an employee user." Steps: Employee user. |
| Actions (sheet) | Click On Submit Idea. |
| Expected Result (sheet) | The validations should work properly i.e. user should not be able to submit the form without mandatory fields and also mandatory fields should be shown with red asterisk. |
| Automated yes/no / Test Steps Automated | Yes / Partial |
| Tags | `P1`, `validation` |

### Steps

| # | Step (as in sheet) | Expected result (as in sheet) | Automated |
|---|---|---|---|
| 1 | Launch a supported browser (Chrome/Edge recommended). | Browser opens without errors. | [Yes] |
| 2 | Navigate to https://10.20.3.66/innovation-portal. | Portal or ADFS login page is displayed (ignore self-signed cert warning in test env). | [Yes] |
| 3 | If redirected to ADFS SSO (adtestadfs.adtest.com), enter Username "ADTest\Adtest15" in #userNameInput and Password in #passwordInput; click Sign in. | ADFS authenticates successfully. | [Yes] |
| 4 | If the Xornet Global Screen appears after login, click Stay Here (or wait for redirect countdown). | Global Screen is dismissed; session remains active. | [Yes] |
| 5 | Confirm Innovation Portal loads (URL contains /innovation-portal); re-navigate to https://10.20.3.66/innovation-portal if still on Xornet home. | Innovation Portal landing page is visible with role-appropriate navigation. | [Yes] |
| 6 | Verify logged-in user context matches precondition: Employee user. | User context matches Employee user (ADTest\Adtest15). | [Partial] |
| 7 | Click "+ Submit Idea" on the landing page (or open https://10.20.3.66/innovation-portal/idea-submit). | Submit Idea form loads at /idea-submit. | [Yes] |
| 8 | In Innovation Type (#innovation_type), select "Client Initiative". | Customer Name* and Project Name* fields appear; Department hidden. | [Yes] |
| 9 | Apply valid, invalid, and boundary data from Test Data / Summary (missing mandatory fields, 501-char text, extra domains, invalid attachments, etc.). | Validation messages display for invalid/boundary inputs. | [Yes] |
| 10 | Verify Submit Idea button (#edit-submit-idea) stays disabled or blocks submit when validation fails. | Invalid data is rejected per Expected Result. | [Yes] |
| 11 | Re-test with valid data; confirm Submit enables and succeeds when scenario requires positive path. | The validations should work properly i.e. user should not be able to submit the form without mandatory fields and also mandatory fields should be shown with red asterisk. | [Partial] |
| 12 | Capture screenshot if actual result differs from expected; log out / close browser. | Test evidence recorded; session closed. | [No] |

### Test data needed

| Field | Value / source |
|---|---|
| ADFS username | `ADTest\Adtest15` (sheet, Employee) - supply via `EMPLOYEE_USERNAME` / `EMPLOYEE_PASSWORD` env vars (users.json key `employee`); never hardcode |
| Innovation Type | "Client Initiative" |
| Test Data column | "ADTEST-08     SafetyChain Mobile" - see Q5 |
| Boundary data | "missing mandatory fields, 501-char text, extra domains, invalid attachments" (sheet) - target fields, limits, file types not specified - see Q9 |
| Expected validation messages | not specified - see Q9 |

### Page objects

LoginPage, XornetGlobalScreenPage, HomePage, IdeaSubmitPage

### Dependencies / setup

Employee account (Q2). Independent of other cases.

### Automation risks

- Oracle is vague ("validation messages display", "red asterisk") - no message texts or asterisk locator (Q7, Q9).
- Submit may "stay disabled or block submit" - two behaviours, test must know which (Q9).
- Attachment upload needs a test file under src/test/resources and an upload input locator (Q7, Q9).
- Step 11 positive path would submit an idea - data pollution (Q10, Q12).
- Step 9 bundles several scenarios; may need splitting into separate test methods for independence.
- ADFS SSO redirect to a second host (adtestadfs.adtest.com) plus the optional Xornet Global Screen with a countdown redirect: conditional page, needs an explicit "either/or" wait (Q13).
- Self-signed certificate in test env: covered by `acceptInsecureCerts=true` in config.properties.
- Step 6 (verify user context) is [Partial]: no locator for the logged-in user indicator (Q13).

---

## TC-LEGACY-008

**Title:** To verify the validation for Organization Innovation Form.

| Field | Value |
|---|---|
| ID | TC-LEGACY-008 |
| Feature / module | General (sheet Name: Innovation Portal) |
| Change status | EXISTING |
| Requirement IDs | REQ-SUB-01, REQ-SUB-03 |
| Preconditions | Sheet: "Login as an employee user." Steps: Employee user. |
| Actions (sheet) | Click On Submit Idea. |
| Expected Result (sheet) | The validations should work properly i.e. user should not be able to submit the form without mandatory fields and also mandatory fields should be shown with red asterisk. |
| Automated yes/no / Test Steps Automated | Yes / Partial |
| Tags | `P1`, `validation` |

### Steps

| # | Step (as in sheet) | Expected result (as in sheet) | Automated |
|---|---|---|---|
| 1 | Launch a supported browser (Chrome/Edge recommended). | Browser opens without errors. | [Yes] |
| 2 | Navigate to https://10.20.3.66/innovation-portal. | Portal or ADFS login page is displayed (ignore self-signed cert warning in test env). | [Yes] |
| 3 | If redirected to ADFS SSO (adtestadfs.adtest.com), enter Username "ADTest\Adtest15" in #userNameInput and Password in #passwordInput; click Sign in. | ADFS authenticates successfully. | [Yes] |
| 4 | If the Xornet Global Screen appears after login, click Stay Here (or wait for redirect countdown). | Global Screen is dismissed; session remains active. | [Yes] |
| 5 | Confirm Innovation Portal loads (URL contains /innovation-portal); re-navigate to https://10.20.3.66/innovation-portal if still on Xornet home. | Innovation Portal landing page is visible with role-appropriate navigation. | [Yes] |
| 6 | Verify logged-in user context matches precondition: Employee user. | User context matches Employee user (ADTest\Adtest15). | [Partial] |
| 7 | Click "+ Submit Idea" on the landing page (or open https://10.20.3.66/innovation-portal/idea-submit). | Submit Idea form loads at /idea-submit. | [Yes] |
| 8 | In Innovation Type (#innovation_type), select "Organisation Initiative". | Department* appears; Customer/Project fields hidden. | [Yes] |
| 9 | Apply valid, invalid, and boundary data from Test Data / Summary (missing mandatory fields, 501-char text, extra domains, invalid attachments, etc.). | Validation messages display for invalid/boundary inputs. | [Yes] |
| 10 | Verify Submit Idea button (#edit-submit-idea) stays disabled or blocks submit when validation fails. | Invalid data is rejected per Expected Result. | [Yes] |
| 11 | Re-test with valid data; confirm Submit enables and succeeds when scenario requires positive path. | The validations should work properly i.e. user should not be able to submit the form without mandatory fields and also mandatory fields should be shown with red asterisk. | [Partial] |
| 12 | Capture screenshot if actual result differs from expected; log out / close browser. | Test evidence recorded; session closed. | [No] |

### Test data needed

| Field | Value / source |
|---|---|
| ADFS username | `ADTest\Adtest15` (sheet, Employee) - supply via `EMPLOYEE_USERNAME` / `EMPLOYEE_PASSWORD` env vars (users.json key `employee`); never hardcode |
| Innovation Type | "Organisation Initiative" - see Q8 |
| Test Data column | "ADTEST-08     SafetyChain Mobile" (same as the Client case; Org form hides Customer/Project) - see Q5, Q6 |
| Department* | not specified - see Q6 |
| Boundary data | "missing mandatory fields, 501-char text, extra domains, invalid attachments" - limits not specified - see Q9 |
| Expected validation messages | not specified - see Q9 |

### Page objects

LoginPage, XornetGlobalScreenPage, HomePage, IdeaSubmitPage

### Dependencies / setup

Employee account (Q2). Independent; mirrors TC-LEGACY-007 for the Org form.

### Automation risks

- Same as TC-LEGACY-007 (vague oracle, uploads, optional positive submit - Q9, Q10).
- Department field has no locator (Q7).
- ADFS SSO redirect to a second host (adtestadfs.adtest.com) plus the optional Xornet Global Screen with a countdown redirect: conditional page, needs an explicit "either/or" wait (Q13).
- Self-signed certificate in test env: covered by `acceptInsecureCerts=true` in config.properties.
- Step 6 (verify user context) is [Partial]: no locator for the logged-in user indicator (Q13).

---

## TC-LEGACY-029

**Title:** Verify Approve is blocked when Impact of Idea (option + $ + description) is missing; Save as Draft may retain partial data.

| Field | Value |
|---|---|
| ID | TC-LEGACY-029 |
| Feature / module | Approval (sheet Name: Innovation Portal) |
| Change status | UPDATED |
| Requirement IDs | REQ-APR-02 |
| Preconditions | Sheet: "Login as a committee approver." Steps: Committee member / Evaluator. Test Data: "Committee Members - ADTEST10 (PEIP Vertical)". |
| Actions (sheet) | Click On Perceived Benefit Screen. |
| Expected Result (sheet) | The evaluator should be able to skip Perceived Benefit Realization. |
| Automated yes/no / Test Steps Automated | Yes / Partial |
| Tags | `P1`, `approval` |

### Steps

| # | Step (as in sheet) | Expected result (as in sheet) | Automated |
|---|---|---|---|
| 1 | Launch a supported browser (Chrome/Edge recommended). | Browser opens without errors. | [Yes] |
| 2 | Navigate to https://10.20.3.66/innovation-portal. | Portal or ADFS login page is displayed (ignore self-signed cert warning in test env). | [Yes] |
| 3 | If redirected to ADFS SSO (adtestadfs.adtest.com), enter Username "ADTest\Adtest08" in #userNameInput and Password in #passwordInput; click Sign in. | ADFS authenticates successfully. | [Yes] |
| 4 | If the Xornet Global Screen appears after login, click Stay Here (or wait for redirect countdown). | Global Screen is dismissed; session remains active. | [Yes] |
| 5 | Confirm Innovation Portal loads (URL contains /innovation-portal); re-navigate to https://10.20.3.66/innovation-portal if still on Xornet home. | Innovation Portal landing page is visible with role-appropriate navigation. | [Yes] |
| 6 | Verify logged-in user context matches precondition: Committee member / Evaluator. | User context matches Committee member / Evaluator (ADTest\Adtest08). | [Partial] |
| 7 | Click "Approval Dashboard" (or open https://10.20.3.66/innovation-portal/approval-dashboard). | Approval Dashboard loads. | [Yes] |
| 8 | Set Innovation Status filter as required by scenario (default Pending Approval unless testing Approved/Rejected/On Hold). | Status filter matches test intent. | [Partial] |
| 9 | Enable "My Evaluations" checkbox; click Filter. | Grid shows evaluator-assigned ideas for selected status. | [Partial] |
| 10 | Select a Pending Approval idea from the grid (click Innovation Title or row). | Idea detail / evaluation context opens. | [Partial] |
| 11 | Click Evaluate; enter partial evaluation data; Save as Draft; reopen idea. | Evaluation draft saved; status remains Pending Approval. | [Partial] |
| 12 | Verify: The evaluator should be able to skip Perceived Benefit Realization. | The evaluator should be able to skip Perceived Benefit Realization. | [Partial] |
| 13 | Capture screenshot if actual result differs from expected; log out / close browser. | Test evidence recorded; session closed. | [No] |

### Test data needed

| Field | Value / source |
|---|---|
| ADFS username | `ADTest\Adtest08` (sheet, Committee member / Evaluator) - no env var / users.json key exists yet - see Q1; Test Data names ADTEST10 instead - see Q16 |
| Innovation Status filter | "Pending Approval" (default per steps) |
| Partial evaluation data | not specified - see Q16 |
| Idea to evaluate | a Pending Approval idea in "My Evaluations" - not specified - see Q15 |

### Page objects

LoginPage, XornetGlobalScreenPage, HomePage, ApprovalDashboardPage, EvaluationPage (incl. Perceived Benefit section - location unknown, Q16)

### Dependencies / setup

Evaluator account (Q1); at least one Pending Approval idea assigned to that evaluator (Q15) - could be created by a TC-LEGACY-001-style setup.

### Automation risks

- Three conflicting oracles (Summary vs Expected Result vs Actions) - cannot write an assertion until resolved (Q16).
- Save as Draft mutates a shared idea; re-runs see a pre-filled draft.
- Grid is data-driven (rows change between runs) - must select by a known title/ID, not by row index.
- ADFS SSO redirect to a second host (adtestadfs.adtest.com) plus the optional Xornet Global Screen with a countdown redirect: conditional page, needs an explicit "either/or" wait (Q13).
- Self-signed certificate in test env: covered by `acceptInsecureCerts=true` in config.properties.
- Step 6 (verify user context) is [Partial]: no locator for the logged-in user indicator (Q13).

---

## TC-LEGACY-081

**Title:** Verify if this is a dashboard which displays all ideas that have been approved and are not confidential.

| Field | Value |
|---|---|
| ID | TC-LEGACY-081 |
| Feature / module | Approval (sheet Name: Homepage - Idea List) |
| Change status | UPDATED |
| Requirement IDs | REQ-LIST-01, REQ-APR-03 |
| Preconditions | Sheet: "Login as an User." Test Data: "All Users. (Adtest 04,05,06 etc.)". Steps: Employee user. |
| Actions (sheet) | Click On Idea List. |
| Expected Result (sheet) | Such records should be shown here. |
| Automated yes/no / Test Steps Automated | No / Partial |
| Tags | `P1`, `idealist` |

### Steps

| # | Step (as in sheet) | Expected result (as in sheet) | Automated |
|---|---|---|---|
| 1 | Launch a supported browser (Chrome/Edge recommended). | Browser opens without errors. | [Yes] |
| 2 | Navigate to https://10.20.3.66/innovation-portal. | Portal or ADFS login page is displayed (ignore self-signed cert warning in test env). | [Yes] |
| 3 | If redirected to ADFS SSO (adtestadfs.adtest.com), enter Username "ADTest\Adtest15" in #userNameInput and Password in #passwordInput; click Sign in. | ADFS authenticates successfully. | [Yes] |
| 4 | If the Xornet Global Screen appears after login, click Stay Here (or wait for redirect countdown). | Global Screen is dismissed; session remains active. | [Yes] |
| 5 | Confirm Innovation Portal loads (URL contains /innovation-portal); re-navigate to https://10.20.3.66/innovation-portal if still on Xornet home. | Innovation Portal landing page is visible with role-appropriate navigation. | [Yes] |
| 6 | Verify logged-in user context matches precondition: Employee user. | User context matches Employee user (ADTest\Adtest15). | [Partial] |
| 7 | Click "Approval Dashboard" (or open https://10.20.3.66/innovation-portal/approval-dashboard). | Approval Dashboard loads. | [Yes] |
| 8 | Set Innovation Status filter as required by scenario (default Pending Approval unless testing Approved/Rejected/On Hold). | Status filter matches test intent. | [Partial] |
| 9 | Enable "My Evaluations" checkbox; click Filter. | Grid shows evaluator-assigned ideas for selected status. | [Partial] |
| 10 | Select a Pending Approval idea from the grid (click Innovation Title or row). | Idea detail / evaluation context opens. | [Partial] |
| 11 | Based on test intention, click Delete, Evaluate, or Change the Evaluator. | Selected action control is available for evaluator role. | [Partial] |
| 12 | (Evaluate path) Click Approve, Reject, or On Hold; fill mandatory ratings/comments; click Submit Evaluation. | Evaluation action completes; status updates per scenario. | [Partial] |
| 13 | (Change Evaluator path) Select different committee member and Submit. | Such records should be shown here. | [Partial] |
| 14 | Capture screenshot if actual result differs from expected; log out / close browser. | Test evidence recorded; session closed. | [No] |

### Test data needed

| Field | Value / source |
|---|---|
| ADFS username | `ADTest\Adtest15` (sheet, Employee) - supply via `EMPLOYEE_USERNAME` / `EMPLOYEE_PASSWORD` env vars (users.json key `employee`); never hardcode; Test Data says "Adtest 04,05,06 etc." - see Q1, Q17 |
| Approved, non-confidential idea(s) to look for | not specified - see Q17 |
| Idea List URL / entry point | not specified - see Q17 |

### Page objects

LoginPage, XornetGlobalScreenPage, HomePage, IdeaListPage; (per detailed steps) ApprovalDashboardPage, EvaluationPage, ChangeEvaluatorDialog

### Dependencies / setup

Existing approved + non-confidential idea(s) with known titles (Q17). Steps assume the employee can open the Approval Dashboard, which contradicts TC-NEW-035 (Q4).

### Automation risks

- Detailed steps (Approval Dashboard, Delete/Evaluate/Change Evaluator) do not match Summary/Actions (Idea List) - Q17.
- "Automated yes/no" = No while step automation = Partial - Q18.
- Verifying "all" approved non-confidential ideas requires a data oracle (DB/API or known fixture).
- Steps 11-13 could Delete or evaluate an idea - destructive on shared data.
- ADFS SSO redirect to a second host (adtestadfs.adtest.com) plus the optional Xornet Global Screen with a countdown redirect: conditional page, needs an explicit "either/or" wait (Q13).
- Self-signed certificate in test env: covered by `acceptInsecureCerts=true` in config.properties.
- Step 6 (verify user context) is [Partial]: no locator for the logged-in user indicator (Q13).

---

## TC-LEGACY-082

**Title:** Verify dashboard displays all ideas that have been approved and confidential.

| Field | Value |
|---|---|
| ID | TC-LEGACY-082 |
| Feature / module | Approval (sheet Name: Homepage - Idea List) |
| Change status | EXISTING |
| Requirement IDs | REQ-APR-02, REQ-APR-03, REQ-LIST-01 |
| Preconditions | Sheet: "Login as an User." Test Data: "All Users. (Adtest 04,05,06 etc.)". Steps: Employee user. |
| Actions (sheet) | Click On Idea List. |
| Expected Result (sheet) | Such records should not be shown here. |
| Automated yes/no / Test Steps Automated | Yes / Partial |
| Tags | `P1`, `idealist` |

### Steps

| # | Step (as in sheet) | Expected result (as in sheet) | Automated |
|---|---|---|---|
| 1 | Launch a supported browser (Chrome/Edge recommended). | Browser opens without errors. | [Yes] |
| 2 | Navigate to https://10.20.3.66/innovation-portal. | Portal or ADFS login page is displayed (ignore self-signed cert warning in test env). | [Yes] |
| 3 | If redirected to ADFS SSO (adtestadfs.adtest.com), enter Username "ADTest\Adtest15" in #userNameInput and Password in #passwordInput; click Sign in. | ADFS authenticates successfully. | [Yes] |
| 4 | If the Xornet Global Screen appears after login, click Stay Here (or wait for redirect countdown). | Global Screen is dismissed; session remains active. | [Yes] |
| 5 | Confirm Innovation Portal loads (URL contains /innovation-portal); re-navigate to https://10.20.3.66/innovation-portal if still on Xornet home. | Innovation Portal landing page is visible with role-appropriate navigation. | [Yes] |
| 6 | Verify logged-in user context matches precondition: Employee user. | User context matches Employee user (ADTest\Adtest15). | [Partial] |
| 7 | Click "Approval Dashboard" (or open https://10.20.3.66/innovation-portal/approval-dashboard). | Approval Dashboard loads. | [Yes] |
| 8 | Set Innovation Status filter as required by scenario (default Pending Approval unless testing Approved/Rejected/On Hold). | Status filter matches test intent. | [Partial] |
| 9 | Enable "My Evaluations" checkbox; click Filter. | Grid shows evaluator-assigned ideas for selected status. | [Partial] |
| 10 | Select a Pending Approval idea from the grid (click Innovation Title or row). | Idea detail / evaluation context opens. | [Partial] |
| 11 | Based on test intention, click Delete, Evaluate, or Change the Evaluator. | Selected action control is available for evaluator role. | [Partial] |
| 12 | (Evaluate path) Click Approve, Reject, or On Hold; fill mandatory ratings/comments; click Submit Evaluation. | Evaluation action completes; status updates per scenario. | [Partial] |
| 13 | (Change Evaluator path) Select different committee member and Submit. | Such records should not be shown here. | [Partial] |
| 14 | Capture screenshot if actual result differs from expected; log out / close browser. | Test evidence recorded; session closed. | [No] |

### Test data needed

| Field | Value / source |
|---|---|
| ADFS username | `ADTest\Adtest15` (sheet, Employee) - supply via `EMPLOYEE_USERNAME` / `EMPLOYEE_PASSWORD` env vars (users.json key `employee`); never hardcode; Test Data says "Adtest 04,05,06 etc." - see Q1, Q17 |
| Approved + confidential idea(s) that must NOT appear | not specified - see Q17 |
| Idea List URL / entry point | not specified - see Q17 |

### Page objects

LoginPage, XornetGlobalScreenPage, HomePage, IdeaListPage; (per detailed steps) ApprovalDashboardPage, EvaluationPage, ChangeEvaluatorDialog

### Dependencies / setup

Existing approved + confidential idea with a known title (Q17). Same Approval Dashboard contradiction as TC-LEGACY-081 (Q4).

### Automation risks

- Negative assertion ("not shown") is only meaningful if the confidential idea is known to exist - needs a fixture (Q17).
- Summary vs Expected Result wording conflict (Q18).
- Same step/summary mismatch and destructive actions as TC-LEGACY-081.
- ADFS SSO redirect to a second host (adtestadfs.adtest.com) plus the optional Xornet Global Screen with a countdown redirect: conditional page, needs an explicit "either/or" wait (Q13).
- Self-signed certificate in test env: covered by `acceptInsecureCerts=true` in config.properties.
- Step 6 (verify user context) is [Partial]: no locator for the logged-in user indicator (Q13).

---

## TC-LEGACY-089

**Title:** Verify if Approver selects ‘Yes’ idea will be kept confidential, In this case it will not be displayed in Idea List. If approver selects ‘No’ and approve the idea, it will be displayed in Idealist for all employees.

| Field | Value |
|---|---|
| ID | TC-LEGACY-089 |
| Feature / module | Idea List / Solutions (sheet Name: Homepage - Idea List) |
| Change status | EXISTING |
| Requirement IDs | REQ-APR-02, REQ-APR-03, REQ-LIST-01 |
| Preconditions | Sheet: "Login as an employee user." Steps: Employee user. Scenario needs an approver choosing Confidential Yes/No. |
| Actions (sheet) | Click On Idea List. |
| Expected Result (sheet) | If approver selects ‘Yes’ idea will be kept confidential, In this case it will not be displayed in Idea List. If approver selects ‘No’ and approve the idea, it will be displayed in Idealist for all employees. |
| Automated yes/no / Test Steps Automated | Yes / Partial |
| Tags | `P1`, `idealist` |

### Steps

| # | Step (as in sheet) | Expected result (as in sheet) | Automated |
|---|---|---|---|
| 1 | Launch a supported browser (Chrome/Edge recommended). | Browser opens without errors. | [Yes] |
| 2 | Navigate to https://10.20.3.66/innovation-portal. | Portal or ADFS login page is displayed (ignore self-signed cert warning in test env). | [Yes] |
| 3 | If redirected to ADFS SSO (adtestadfs.adtest.com), enter Username "ADTest\Adtest15" in #userNameInput and Password in #passwordInput; click Sign in. | ADFS authenticates successfully. | [Yes] |
| 4 | If the Xornet Global Screen appears after login, click Stay Here (or wait for redirect countdown). | Global Screen is dismissed; session remains active. | [Yes] |
| 5 | Confirm Innovation Portal loads (URL contains /innovation-portal); re-navigate to https://10.20.3.66/innovation-portal if still on Xornet home. | Innovation Portal landing page is visible with role-appropriate navigation. | [Yes] |
| 6 | Verify logged-in user context matches precondition: Employee user. | User context matches Employee user (ADTest\Adtest15). | [Partial] |
| 7 | Click "Approval Dashboard" (or open https://10.20.3.66/innovation-portal/approval-dashboard). | Approval Dashboard loads. | [Yes] |
| 8 | Set Innovation Status filter as required by scenario (default Pending Approval unless testing Approved/Rejected/On Hold). | Status filter matches test intent. | [Partial] |
| 9 | Enable "My Evaluations" checkbox; click Filter. | Grid shows evaluator-assigned ideas for selected status. | [Partial] |
| 10 | Select a Pending Approval idea from the grid (click Innovation Title or row). | Idea detail / evaluation context opens. | [Partial] |
| 11 | Based on test intention, click Delete, Evaluate, or Change the Evaluator. | Selected action control is available for evaluator role. | [Partial] |
| 12 | (Evaluate path) Click Approve, Reject, or On Hold; fill mandatory ratings/comments; click Submit Evaluation. | Evaluation action completes; status updates per scenario. | [Partial] |
| 13 | (Change Evaluator path) Select different committee member and Submit. | If approver selects ‘Yes’ idea will be kept confidential, In this case it will not be displayed in Idea List. If approver selects ‘No’ and approve the idea, it will be displayed in Idealist for all employees. | [Partial] |
| 14 | Capture screenshot if actual result differs from expected; log out / close browser. | Test evidence recorded; session closed. | [No] |

### Test data needed

| Field | Value / source |
|---|---|
| ADFS username | `ADTest\Adtest15` (sheet, Employee) - supply via `EMPLOYEE_USERNAME` / `EMPLOYEE_PASSWORD` env vars (users.json key `employee`); never hardcode; approver account not specified - see Q1, Q19 |
| Test Data column | "ADTEST-08     SafetyChain Mobile" - see Q5 |
| Approver confidentiality choice | "Yes" (kept confidential) and "No" (shown in Idea List) - sheet |
| Idea(s) under test | two Pending Approval ideas (one per branch) - not specified - see Q15 |

### Page objects

LoginPage, XornetGlobalScreenPage, HomePage, ApprovalDashboardPage, EvaluationPage, IdeaListPage

### Dependencies / setup

Approver/evaluator account + employee account in the same test (two sessions or re-login); Pending Approval ideas assigned to the approver (Q15, Q19).

### Automation risks

- Multi-role flow in one test (approve as approver, verify as employee) - session switching via ADFS.
- Approves ideas each run (irreversible state change, consumes Pending ideas).
- Where the approver sets confidentiality is unknown (Q19).
- Steps log in only as Employee and use the Approval Dashboard (Q4).
- ADFS SSO redirect to a second host (adtestadfs.adtest.com) plus the optional Xornet Global Screen with a countdown redirect: conditional page, needs an explicit "either/or" wait (Q13).
- Self-signed certificate in test env: covered by `acceptInsecureCerts=true` in config.properties.
- Step 6 (verify user context) is [Partial]: no locator for the logged-in user indicator (Q13).

---

## TC-LEGACY-101

**Title:** Verify Implemented locks further Customer Progress updates; POC Completed and Proposed also support Actual Benefits (Phase 5).

| Field | Value |
|---|---|
| ID | TC-LEGACY-101 |
| Feature / module | Tracking (sheet Name: Customer Initiative Progress Tracking) |
| Change status | UPDATED |
| Requirement IDs | REQ-TRK-01 |
| Preconditions | Sheet: "Login as an User." Test Data: "Committee Members - ADTEST09, ADTEST10 (PEIP Vertical)". Steps: Committee member / Evaluator. |
| Actions (sheet) | Click On Approval Dashboard. |
| Expected Result (sheet) | “Rejected” and “Implemented” will be end state for customer initiatives in system. |
| Automated yes/no / Test Steps Automated | Yes / Partial |
| Tags | `P1`, `tracking` |

### Steps

| # | Step (as in sheet) | Expected result (as in sheet) | Automated |
|---|---|---|---|
| 1 | Launch a supported browser (Chrome/Edge recommended). | Browser opens without errors. | [Yes] |
| 2 | Navigate to https://10.20.3.66/innovation-portal. | Portal or ADFS login page is displayed (ignore self-signed cert warning in test env). | [Yes] |
| 3 | If redirected to ADFS SSO (adtestadfs.adtest.com), enter Username "ADTest\Adtest08" in #userNameInput and Password in #passwordInput; click Sign in. | ADFS authenticates successfully. | [Yes] |
| 4 | If the Xornet Global Screen appears after login, click Stay Here (or wait for redirect countdown). | Global Screen is dismissed; session remains active. | [Yes] |
| 5 | Confirm Innovation Portal loads (URL contains /innovation-portal); re-navigate to https://10.20.3.66/innovation-portal if still on Xornet home. | Innovation Portal landing page is visible with role-appropriate navigation. | [Yes] |
| 6 | Verify logged-in user context matches precondition: Committee member / Evaluator. | User context matches Committee member / Evaluator (ADTest\Adtest08). | [Partial] |
| 7 | Open an approved Client idea from My Ideas / Idea List / Approval Dashboard. | Idea detail page loads. | [Yes] |
| 8 | Navigate to Customer Initiative Progress (CIP) /actual_benefit_realization/{id}. | CIP tracking form loads. | [Yes] |
| 9 | Set Customer Status and required tracking fields from Test Data; Save and re-open to verify locks/rules. | “Rejected” and “Implemented” will be end state for customer initiatives in system. | [Partial] |
| 10 | Capture screenshot if actual result differs from expected; log out / close browser. | Test evidence recorded; session closed. | [No] |

### Test data needed

| Field | Value / source |
|---|---|
| ADFS username | `ADTest\Adtest08` (sheet, Committee member / Evaluator) - no env var / users.json key exists yet - see Q1; Test Data names ADTEST09/ADTEST10 - see Q20 |
| Approved Client idea ID | not specified - see Q20 |
| Customer Status values to set | "Rejected", "Implemented" (Expected Result); Summary also mentions "POC Completed", "Proposed" - see Q20 |
| Other tracking fields | "from Test Data" but Test Data has none - see Q20 |

### Page objects

LoginPage, XornetGlobalScreenPage, HomePage, MyIdeasPage / IdeaListPage / ApprovalDashboardPage (any, to open the idea), IdeaDetailPage, CustomerInitiativeProgressPage

### Dependencies / setup

Committee/evaluator account (Q1); an approved Client idea (Q15, Q20).

### Automation risks

- Setting an end state (Implemented/Rejected) is irreversible - needs a fresh approved idea per run.
- How a "lock" is shown (disabled fields? hidden Save? message?) is unspecified (Q20).
- Summary and Expected Result describe different checks (Q20).
- ADFS SSO redirect to a second host (adtestadfs.adtest.com) plus the optional Xornet Global Screen with a countdown redirect: conditional page, needs an explicit "either/or" wait (Q13).
- Self-signed certificate in test env: covered by `acceptInsecureCerts=true` in config.properties.
- Step 6 (verify user context) is [Partial]: no locator for the logged-in user indicator (Q13).

---

## TC-LEGACY-107

**Title:** Verify Customer Status options include In Progress, Proposed to Customer, Accepted, Rejected, On Hold, POC Completed, Implemented.

| Field | Value |
|---|---|
| ID | TC-LEGACY-107 |
| Feature / module | Tracking (sheet Name: Customer Initiative Progress Tracking) |
| Change status | UPDATED |
| Requirement IDs | REQ-TRK-01 |
| Preconditions | Sheet: "Login as an User." Test Data: "Committee Members - ADTEST09, ADTEST10 (PEIP Vertical)". Steps: Committee member / Evaluator. |
| Actions (sheet) | Click On Approval Dashboard. |
| Expected Result (sheet) | “Customer Status” field will have following list of options<br>In Progress<br>Proposed to Customer<br>Accepted<br>Rejected<br>On Hold<br>Implemented |
| Automated yes/no / Test Steps Automated | Yes / Partial |
| Tags | `P1`, `tracking` |

### Steps

| # | Step (as in sheet) | Expected result (as in sheet) | Automated |
|---|---|---|---|
| 1 | Launch a supported browser (Chrome/Edge recommended). | Browser opens without errors. | [Yes] |
| 2 | Navigate to https://10.20.3.66/innovation-portal. | Portal or ADFS login page is displayed (ignore self-signed cert warning in test env). | [Yes] |
| 3 | If redirected to ADFS SSO (adtestadfs.adtest.com), enter Username "ADTest\Adtest08" in #userNameInput and Password in #passwordInput; click Sign in. | ADFS authenticates successfully. | [Yes] |
| 4 | If the Xornet Global Screen appears after login, click Stay Here (or wait for redirect countdown). | Global Screen is dismissed; session remains active. | [Yes] |
| 5 | Confirm Innovation Portal loads (URL contains /innovation-portal); re-navigate to https://10.20.3.66/innovation-portal if still on Xornet home. | Innovation Portal landing page is visible with role-appropriate navigation. | [Yes] |
| 6 | Verify logged-in user context matches precondition: Committee member / Evaluator. | User context matches Committee member / Evaluator (ADTest\Adtest08). | [Partial] |
| 7 | Click "Approval Dashboard" (or open https://10.20.3.66/innovation-portal/approval-dashboard). | Approval Dashboard loads. | [Yes] |
| 8 | Set Innovation Status filter as required by scenario (default Pending Approval unless testing Approved/Rejected/On Hold). | Status filter matches test intent. | [Partial] |
| 9 | Enable "My Evaluations" checkbox; click Filter. | Grid shows evaluator-assigned ideas for selected status. | [Partial] |
| 10 | Select a Pending Approval idea from the grid (click Innovation Title or row). | Idea detail / evaluation context opens. | [Partial] |
| 11 | Click Evaluate; click On Hold; submit with comment if prompted. | Status changes to On Hold. | [Partial] |
| 12 | Re-open same idea; click Approve or Reject with mandatory fields; Submit Evaluation. | “Customer Status” field will have following list of options<br>In Progress<br>Proposed to Customer<br>Accepted<br>Rejected<br>On Hold<br>Implemented | [Partial] |
| 13 | Capture screenshot if actual result differs from expected; log out / close browser. | Test evidence recorded; session closed. | [No] |

### Test data needed

| Field | Value / source |
|---|---|
| ADFS username | `ADTest\Adtest08` (sheet, Committee member / Evaluator) - no env var / users.json key exists yet - see Q1; Test Data names ADTEST09/ADTEST10 - see Q20 |
| Expected Customer Status options | In Progress, Proposed to Customer, Accepted, Rejected, On Hold, Implemented (Expected Result) - Summary adds POC Completed - see Q21 |
| Idea to evaluate | Pending Approval idea in My Evaluations - see Q15 |
| On Hold / Approve / Reject comments and ratings | not specified - see Q21, Q22 |

### Page objects

LoginPage, XornetGlobalScreenPage, HomePage, ApprovalDashboardPage, EvaluationPage, CustomerInitiativeProgressPage (where the Customer Status field lives - Q21)

### Dependencies / setup

Evaluator account (Q1); a Pending Approval Client idea (Q15).

### Automation risks

- Option list differs between Summary (7) and Expected Result (6) (Q21).
- Steps 11-12 put the idea On Hold then Approve/Reject - state-changing on shared data.
- Steps never open the Customer Status field (CIP page); location unclear (Q21).
- ADFS SSO redirect to a second host (adtestadfs.adtest.com) plus the optional Xornet Global Screen with a countdown redirect: conditional page, needs an explicit "either/or" wait (Q13).
- Self-signed certificate in test env: covered by `acceptInsecureCerts=true` in config.properties.
- Step 6 (verify user context) is [Partial]: no locator for the logged-in user indicator (Q13).

---

## TC-LEGACY-116

**Title:** Verify Approve requires ratings 1-10 on Innovation Index (25%), Potential Business Impact (35%), Depth (25%), Feasibility (15%) with overall score on 1-10 points scale.

| Field | Value |
|---|---|
| ID | TC-LEGACY-116 |
| Feature / module | Approval (sheet Name: Idea Approval) |
| Change status | UPDATED |
| Requirement IDs | REQ-APR-02 |
| Preconditions | Sheet: "Login as an User." Steps: Employee user. |
| Actions (sheet) | Approve the idea. |
| Expected Result (sheet) | When evaluators are Approving on Idea then it will mandatory for the Approver to rate the Idea. |
| Automated yes/no / Test Steps Automated | Yes / Partial |
| Tags | `P1`, `approval` |

### Steps

| # | Step (as in sheet) | Expected result (as in sheet) | Automated |
|---|---|---|---|
| 1 | Launch a supported browser (Chrome/Edge recommended). | Browser opens without errors. | [Yes] |
| 2 | Navigate to https://10.20.3.66/innovation-portal. | Portal or ADFS login page is displayed (ignore self-signed cert warning in test env). | [Yes] |
| 3 | If redirected to ADFS SSO (adtestadfs.adtest.com), enter Username "ADTest\Adtest15" in #userNameInput and Password in #passwordInput; click Sign in. | ADFS authenticates successfully. | [Yes] |
| 4 | If the Xornet Global Screen appears after login, click Stay Here (or wait for redirect countdown). | Global Screen is dismissed; session remains active. | [Yes] |
| 5 | Confirm Innovation Portal loads (URL contains /innovation-portal); re-navigate to https://10.20.3.66/innovation-portal if still on Xornet home. | Innovation Portal landing page is visible with role-appropriate navigation. | [Yes] |
| 6 | Verify logged-in user context matches precondition: Employee user. | User context matches Employee user (ADTest\Adtest15). | [Partial] |
| 7 | Click "Approval Dashboard" (or open https://10.20.3.66/innovation-portal/approval-dashboard). | Approval Dashboard loads. | [Yes] |
| 8 | Set Innovation Status filter as required by scenario (default Pending Approval unless testing Approved/Rejected/On Hold). | Status filter matches test intent. | [Partial] |
| 9 | Enable "My Evaluations" checkbox; click Filter. | Grid shows evaluator-assigned ideas for selected status. | [Partial] |
| 10 | Select a Pending Approval idea from the grid (click Innovation Title or row). | Idea detail / evaluation context opens. | [Partial] |
| 11 | Click Evaluate; enter ratings 1–10 (Innovation Index, Potential Business Impact, Depth, Feasibility); complete Impact of Idea / Business Use Case if required. | Weighted score calculated; mandatory fields enforced. | [Partial] |
| 12 | Click Approve; click Submit Evaluation. | When evaluators are Approving on Idea then it will mandatory for the Approver to rate the Idea. | [Partial] |
| 13 | Capture screenshot if actual result differs from expected; log out / close browser. | Test evidence recorded; session closed. | [No] |

### Test data needed

| Field | Value / source |
|---|---|
| ADFS username | `ADTest\Adtest15` (sheet, Employee) - supply via `EMPLOYEE_USERNAME` / `EMPLOYEE_PASSWORD` env vars (users.json key `employee`); never hardcode - but approving requires an evaluator - see Q4 |
| Test Data column | "ADTEST-14           Tibco iMany" - see Q5 |
| Ratings | 1-10 for Innovation Index (25%), Potential Business Impact (35%), Depth (25%), Feasibility (15%) - concrete values not specified - see Q22 |
| Expected overall score | weighted on 1-10 scale; formula/rounding not specified - see Q22 |
| Impact of Idea / Business Use Case | "if required" - not specified - see Q22 |

### Page objects

LoginPage, XornetGlobalScreenPage, HomePage, ApprovalDashboardPage, EvaluationPage

### Dependencies / setup

Evaluator account (Q4); a Pending Approval idea assigned to it (Q15).

### Automation risks

- Approves an idea each run (irreversible).
- Score calculation may be client-side and update dynamically - wait for recalculated value.
- No expected message for the "rating mandatory" block (Q22).
- ADFS SSO redirect to a second host (adtestadfs.adtest.com) plus the optional Xornet Global Screen with a countdown redirect: conditional page, needs an explicit "either/or" wait (Q13).
- Self-signed certificate in test env: covered by `acceptInsecureCerts=true` in config.properties.
- Step 6 (verify user context) is [Partial]: no locator for the logged-in user indicator (Q13).

---

## TC-NEW-011

**Title:** Verify Client idea auto-assigns vertical default evaluator; Org assigns department default evaluator.

| Field | Value |
|---|---|
| ID | TC-NEW-011 |
| Feature / module | Approval (sheet Name: Innovation Portal) |
| Change status | NEW |
| Requirement IDs | REQ-APR-01 |
| Preconditions | Sheet: Preconditions empty. Steps: Committee member / Evaluator. |
| Actions (sheet) | _empty in sheet_ |
| Expected Result (sheet) | _empty in sheet_ |
| Automated yes/no / Test Steps Automated | Yes / Partial |
| Tags | `P1`, `approval` |

### Steps

| # | Step (as in sheet) | Expected result (as in sheet) | Automated |
|---|---|---|---|
| 1 | Launch a supported browser (Chrome/Edge recommended). | Browser opens without errors. | [Yes] |
| 2 | Navigate to https://10.20.3.66/innovation-portal. | Portal or ADFS login page is displayed (ignore self-signed cert warning in test env). | [Yes] |
| 3 | If redirected to ADFS SSO (adtestadfs.adtest.com), enter Username "ADTest\Adtest08" in #userNameInput and Password in #passwordInput; click Sign in. | ADFS authenticates successfully. | [Yes] |
| 4 | If the Xornet Global Screen appears after login, click Stay Here (or wait for redirect countdown). | Global Screen is dismissed; session remains active. | [Yes] |
| 5 | Confirm Innovation Portal loads (URL contains /innovation-portal); re-navigate to https://10.20.3.66/innovation-portal if still on Xornet home. | Innovation Portal landing page is visible with role-appropriate navigation. | [Yes] |
| 6 | Verify logged-in user context matches precondition: Committee member / Evaluator. | User context matches Committee member / Evaluator (ADTest\Adtest08). | [Partial] |
| 7 | Click "Approval Dashboard" (or open https://10.20.3.66/innovation-portal/approval-dashboard). | Approval Dashboard loads. | [Yes] |
| 8 | Set Innovation Status filter as required by scenario (default Pending Approval unless testing Approved/Rejected/On Hold). | Status filter matches test intent. | [Partial] |
| 9 | Enable "My Evaluations" checkbox; click Filter. | Grid shows evaluator-assigned ideas for selected status. | [Partial] |
| 10 | Select a Pending Approval idea from the grid (click Innovation Title or row). | Idea detail / evaluation context opens. | [Partial] |
| 11 | Review grid rows without changing filters; confirm Status column values. | Only Pending Approval ideas shown by default filter + My Evaluations. | [Partial] |
| 12 | Verify: Outcome matches Expected Result column. | Outcome matches Expected Result column. | [Partial] |
| 13 | Capture screenshot if actual result differs from expected; log out / close browser. | Test evidence recorded; session closed. | [No] |

### Test data needed

| Field | Value / source |
|---|---|
| ADFS username | `ADTest\Adtest08` (sheet, Committee member / Evaluator) - no env var / users.json key exists yet - see Q1 |
| Test Data / Preconditions / Actions / Expected Result | all empty in sheet - see Q23 |
| Vertical default evaluator / Department default evaluator | not specified - see Q23 |

### Page objects

LoginPage, XornetGlobalScreenPage, HomePage, ApprovalDashboardPage; likely IdeaSubmitPage + IdeaDetailPage to create and inspect the Client/Org ideas (Q23)

### Dependencies / setup

Needs a freshly submitted Client idea and Org idea (TC-LEGACY-001 / TC-LEGACY-004 flows as setup) and knowledge of the configured default evaluators (Q23).

### Automation risks

- Detailed steps verify the Pending filter, not evaluator auto-assignment - steps do not exercise the Summary (Q23).
- Requires an employee and an evaluator session in one test.
- Creates two ideas per run (Q12).
- ADFS SSO redirect to a second host (adtestadfs.adtest.com) plus the optional Xornet Global Screen with a countdown redirect: conditional page, needs an explicit "either/or" wait (Q13).
- Self-signed certificate in test env: covered by `acceptInsecureCerts=true` in config.properties.
- Step 6 (verify user context) is [Partial]: no locator for the logged-in user indicator (Q13).

---

## TC-NEW-013

**Title:** Verify Reject without comment is blocked.

| Field | Value |
|---|---|
| ID | TC-NEW-013 |
| Feature / module | Approval (sheet Name: Innovation Portal) |
| Change status | NEW |
| Requirement IDs | REQ-APR-02 |
| Preconditions | Sheet: Preconditions empty. Steps: Employee user. |
| Actions (sheet) | _empty in sheet_ |
| Expected Result (sheet) | _empty in sheet_ |
| Automated yes/no / Test Steps Automated | Yes / Partial |
| Tags | `P1`, `approval` |

### Steps

| # | Step (as in sheet) | Expected result (as in sheet) | Automated |
|---|---|---|---|
| 1 | Launch a supported browser (Chrome/Edge recommended). | Browser opens without errors. | [Yes] |
| 2 | Navigate to https://10.20.3.66/innovation-portal. | Portal or ADFS login page is displayed (ignore self-signed cert warning in test env). | [Yes] |
| 3 | If redirected to ADFS SSO (adtestadfs.adtest.com), enter Username "ADTest\Adtest15" in #userNameInput and Password in #passwordInput; click Sign in. | ADFS authenticates successfully. | [Yes] |
| 4 | If the Xornet Global Screen appears after login, click Stay Here (or wait for redirect countdown). | Global Screen is dismissed; session remains active. | [Yes] |
| 5 | Confirm Innovation Portal loads (URL contains /innovation-portal); re-navigate to https://10.20.3.66/innovation-portal if still on Xornet home. | Innovation Portal landing page is visible with role-appropriate navigation. | [Yes] |
| 6 | Verify logged-in user context matches precondition: Employee user. | User context matches Employee user (ADTest\Adtest15). | [Partial] |
| 7 | Click "Approval Dashboard" (or open https://10.20.3.66/innovation-portal/approval-dashboard). | Approval Dashboard loads. | [Yes] |
| 8 | Set Innovation Status filter as required by scenario (default Pending Approval unless testing Approved/Rejected/On Hold). | Status filter matches test intent. | [Partial] |
| 9 | Enable "My Evaluations" checkbox; click Filter. | Grid shows evaluator-assigned ideas for selected status. | [Partial] |
| 10 | Select a Pending Approval idea from the grid (click Innovation Title or row). | Idea detail / evaluation context opens. | [Partial] |
| 11 | Click Evaluate; attempt Reject without comment; observe block. | Reject blocked without mandatory comment. | [Partial] |
| 12 | Enter rejection comment; click Submit Evaluation / confirm Reject. | Outcome matches Expected Result column. | [Partial] |
| 13 | Capture screenshot if actual result differs from expected; log out / close browser. | Test evidence recorded; session closed. | [No] |

### Test data needed

| Field | Value / source |
|---|---|
| ADFS username | `ADTest\Adtest15` (sheet, Employee) - supply via `EMPLOYEE_USERNAME` / `EMPLOYEE_PASSWORD` env vars (users.json key `employee`); never hardcode - but rejecting requires an evaluator - see Q4 |
| Rejection comment | not specified - see Q24 |
| Expected block message | not specified - see Q24 |

### Page objects

LoginPage, XornetGlobalScreenPage, HomePage, ApprovalDashboardPage, EvaluationPage

### Dependencies / setup

Evaluator account (Q4); a Pending Approval idea (Q15).

### Automation risks

- Step 12 actually rejects the idea - irreversible end state, consumes data (Q24).
- Block may be a disabled button, inline error or alert - unknown (Q24).
- ADFS SSO redirect to a second host (adtestadfs.adtest.com) plus the optional Xornet Global Screen with a countdown redirect: conditional page, needs an explicit "either/or" wait (Q13).
- Self-signed certificate in test env: covered by `acceptInsecureCerts=true` in config.properties.
- Step 6 (verify user context) is [Partial]: no locator for the logged-in user indicator (Q13).

---

## TC-NEW-022

**Title:** Verify Project Manager (Rel 5.1) can update Customer Progress only (not full evaluator approve privileges).

| Field | Value |
|---|---|
| ID | TC-NEW-022 |
| Feature / module | Tracking (sheet Name: Innovation Portal) |
| Change status | NEW |
| Requirement IDs | REQ-TRK-01, REQ-AUTH-01 |
| Preconditions | Sheet: Preconditions empty. Summary requires a Project Manager (Rel 5.1); steps log in as Committee member / Evaluator. |
| Actions (sheet) | _empty in sheet_ |
| Expected Result (sheet) | _empty in sheet_ |
| Automated yes/no / Test Steps Automated | Yes / Partial |
| Tags | `P1`, `tracking` |

### Steps

| # | Step (as in sheet) | Expected result (as in sheet) | Automated |
|---|---|---|---|
| 1 | Launch a supported browser (Chrome/Edge recommended). | Browser opens without errors. | [Yes] |
| 2 | Navigate to https://10.20.3.66/innovation-portal. | Portal or ADFS login page is displayed (ignore self-signed cert warning in test env). | [Yes] |
| 3 | If redirected to ADFS SSO (adtestadfs.adtest.com), enter Username "ADTest\Adtest08" in #userNameInput and Password in #passwordInput; click Sign in. | ADFS authenticates successfully. | [Yes] |
| 4 | If the Xornet Global Screen appears after login, click Stay Here (or wait for redirect countdown). | Global Screen is dismissed; session remains active. | [Yes] |
| 5 | Confirm Innovation Portal loads (URL contains /innovation-portal); re-navigate to https://10.20.3.66/innovation-portal if still on Xornet home. | Innovation Portal landing page is visible with role-appropriate navigation. | [Yes] |
| 6 | Verify logged-in user context matches precondition: Committee member / Evaluator. | User context matches Committee member / Evaluator (ADTest\Adtest08). | [Partial] |
| 7 | Click "Approval Dashboard" (or open https://10.20.3.66/innovation-portal/approval-dashboard). | Approval Dashboard loads. | [Yes] |
| 8 | Set Innovation Status filter as required by scenario (default Pending Approval unless testing Approved/Rejected/On Hold). | Status filter matches test intent. | [Partial] |
| 9 | Enable "My Evaluations" checkbox; click Filter. | Grid shows evaluator-assigned ideas for selected status. | [Partial] |
| 10 | Select a Pending Approval idea from the grid (click Innovation Title or row). | Idea detail / evaluation context opens. | [Partial] |
| 11 | Based on test intention, click Delete, Evaluate, or Change the Evaluator. | Selected action control is available for evaluator role. | [Partial] |
| 12 | (Evaluate path) Click Approve, Reject, or On Hold; fill mandatory ratings/comments; click Submit Evaluation. | Evaluation action completes; status updates per scenario. | [Partial] |
| 13 | (Change Evaluator path) Select different committee member and Submit. | Outcome matches Expected Result column. | [Partial] |
| 14 | Capture screenshot if actual result differs from expected; log out / close browser. | Test evidence recorded; session closed. | [No] |

### Test data needed

| Field | Value / source |
|---|---|
| ADFS username | `ADTest\Adtest08` (sheet, Committee member / Evaluator) - no env var / users.json key exists yet - see Q1; Project Manager account required by Summary - not specified - see Q3, Q25 |
| Test Data / Expected Result | empty in sheet - see Q25 |
| Idea to update | an idea on the PM's project - not specified - see Q25 |

### Page objects

LoginPage, XornetGlobalScreenPage, HomePage, ApprovalDashboardPage, EvaluationPage, CustomerInitiativeProgressPage, ChangeEvaluatorDialog (per steps)

### Dependencies / setup

Project Manager account (Q3); an approved Client idea on that PM's project (Q25).

### Automation risks

- Steps exercise evaluator actions with an evaluator, i.e. the opposite of the Summary (Q25).
- Privilege check needs both positive (can update Customer Progress) and negative (cannot approve) assertions - expected UI for "cannot" unknown.
- Customer Progress updates mutate shared data.
- ADFS SSO redirect to a second host (adtestadfs.adtest.com) plus the optional Xornet Global Screen with a countdown redirect: conditional page, needs an explicit "either/or" wait (Q13).
- Self-signed certificate in test env: covered by `acceptInsecureCerts=true` in config.properties.
- Step 6 (verify user context) is [Partial]: no locator for the logged-in user indicator (Q13).

---

## TC-NEW-025

**Title:** Verify Client Accelerator radio visible only when Status=Implemented to Vertical/Practice head and System Admin.

| Field | Value |
|---|---|
| ID | TC-NEW-025 |
| Feature / module | Accelerator (sheet Name: Innovation Portal) |
| Change status | NEW |
| Requirement IDs | REQ-ACC-01 |
| Preconditions | Sheet: Preconditions empty. Steps: Admin user. Summary: visible only to Vertical/Practice head and System Admin, only when Status=Implemented. |
| Actions (sheet) | _empty in sheet_ |
| Expected Result (sheet) | _empty in sheet_ |
| Automated yes/no / Test Steps Automated | Yes / Partial |
| Tags | `P1`, `accelerator` |

### Steps

| # | Step (as in sheet) | Expected result (as in sheet) | Automated |
|---|---|---|---|
| 1 | Launch a supported browser (Chrome/Edge recommended). | Browser opens without errors. | [Yes] |
| 2 | Navigate to https://10.20.3.66/innovation-portal. | Portal or ADFS login page is displayed (ignore self-signed cert warning in test env). | [Yes] |
| 3 | If redirected to ADFS SSO (adtestadfs.adtest.com), enter Username "ADTest\Adtest04" in #userNameInput and Password in #passwordInput; click Sign in. | ADFS authenticates successfully. | [Yes] |
| 4 | If the Xornet Global Screen appears after login, click Stay Here (or wait for redirect countdown). | Global Screen is dismissed; session remains active. | [Yes] |
| 5 | Confirm Innovation Portal loads (URL contains /innovation-portal); re-navigate to https://10.20.3.66/innovation-portal if still on Xornet home. | Innovation Portal landing page is visible with role-appropriate navigation. | [Yes] |
| 6 | Verify logged-in user context matches precondition: Admin user. | User context matches Admin user (ADTest\Adtest04). | [Partial] |
| 7 | Open Intellectual Property Dashboard via landing tile or https://10.20.3.66/innovation-portal/accelerator-dashboard. | Accelerator dashboard loads (title: Intellectual Property Dashboard). | [Yes] |
| 8 | Apply Practice/Status filters; validate Client/Org accelerator gates per Test Data. | Outcome matches Expected Result column. | [Yes] |
| 9 | Capture screenshot if actual result differs from expected; log out / close browser. | Test evidence recorded; session closed. | [No] |
| 10 | Re-check UI labels, navigation, and role permissions for TC-NEW-025 against Summary. | Observed state still matches Expected Result at this checkpoint. | [Partial] |

### Test data needed

| Field | Value / source |
|---|---|
| ADFS username | `ADTest\Adtest04` (sheet, "Admin user") - no env var / users.json key exists yet - see Q1, Q3 |
| Vertical head / Practice head / non-authorised accounts | not specified - see Q3 |
| Practice / Status filter values | Status "Implemented" (Summary); Practice not specified - see Q26 |
| Test Data / Expected Result | empty, although step 8 says "per Test Data" - see Q26 |

### Page objects

LoginPage, XornetGlobalScreenPage, HomePage, AcceleratorDashboardPage ("Intellectual Property Dashboard"); possibly IdeaDetailPage / CustomerInitiativeProgressPage if the radio lives there (Q26)

### Dependencies / setup

A Client idea in Status=Implemented and one in another status (Q26); head/admin and non-authorised accounts (Q3).

### Automation risks

- "Visible only when..." needs negative checks across roles and statuses - many accounts and fixtures.
- Accelerator radio location and locator unknown (Q26).
- Implemented ideas come only from the full approval + tracking flow (long setup chain).
- ADFS SSO redirect to a second host (adtestadfs.adtest.com) plus the optional Xornet Global Screen with a countdown redirect: conditional page, needs an explicit "either/or" wait (Q13).
- Self-signed certificate in test env: covered by `acceptInsecureCerts=true` in config.properties.
- Step 6 (verify user context) is [Partial]: no locator for the logged-in user indicator (Q13).

---

## TC-NEW-026

**Title:** Verify Org Accelerator radio visible only when Status=POC Completed for Dept/Practice Head/System Admin.

| Field | Value |
|---|---|
| ID | TC-NEW-026 |
| Feature / module | Accelerator (sheet Name: Innovation Portal) |
| Change status | NEW |
| Requirement IDs | REQ-ACC-02 |
| Preconditions | Sheet: Preconditions empty. Steps: Admin user. Summary: visible only to Dept/Practice Head and System Admin, only when Status=POC Completed. |
| Actions (sheet) | _empty in sheet_ |
| Expected Result (sheet) | _empty in sheet_ |
| Automated yes/no / Test Steps Automated | Yes / Partial |
| Tags | `P1`, `accelerator` |

### Steps

| # | Step (as in sheet) | Expected result (as in sheet) | Automated |
|---|---|---|---|
| 1 | Launch a supported browser (Chrome/Edge recommended). | Browser opens without errors. | [Yes] |
| 2 | Navigate to https://10.20.3.66/innovation-portal. | Portal or ADFS login page is displayed (ignore self-signed cert warning in test env). | [Yes] |
| 3 | If redirected to ADFS SSO (adtestadfs.adtest.com), enter Username "ADTest\Adtest04" in #userNameInput and Password in #passwordInput; click Sign in. | ADFS authenticates successfully. | [Yes] |
| 4 | If the Xornet Global Screen appears after login, click Stay Here (or wait for redirect countdown). | Global Screen is dismissed; session remains active. | [Yes] |
| 5 | Confirm Innovation Portal loads (URL contains /innovation-portal); re-navigate to https://10.20.3.66/innovation-portal if still on Xornet home. | Innovation Portal landing page is visible with role-appropriate navigation. | [Yes] |
| 6 | Verify logged-in user context matches precondition: Admin user. | User context matches Admin user (ADTest\Adtest04). | [Partial] |
| 7 | Open Intellectual Property Dashboard via landing tile or https://10.20.3.66/innovation-portal/accelerator-dashboard. | Accelerator dashboard loads (title: Intellectual Property Dashboard). | [Yes] |
| 8 | Apply Practice/Status filters; validate Client/Org accelerator gates per Test Data. | Outcome matches Expected Result column. | [Yes] |
| 9 | Capture screenshot if actual result differs from expected; log out / close browser. | Test evidence recorded; session closed. | [No] |
| 10 | Re-check UI labels, navigation, and role permissions for TC-NEW-026 against Summary. | Observed state still matches Expected Result at this checkpoint. | [Partial] |

### Test data needed

| Field | Value / source |
|---|---|
| ADFS username | `ADTest\Adtest04` (sheet, "Admin user") - no env var / users.json key exists yet - see Q1, Q3 |
| Department head / Practice head / non-authorised accounts | not specified - see Q3 |
| Practice / Status filter values | Status "POC Completed" (Summary); Practice not specified - see Q26 |
| Test Data / Expected Result | empty - see Q26 |

### Page objects

LoginPage, XornetGlobalScreenPage, HomePage, AcceleratorDashboardPage; possibly IdeaDetailPage / CustomerInitiativeProgressPage (Q26)

### Dependencies / setup

An Org idea in Status=POC Completed and one in another status (Q26); head/admin and non-authorised accounts (Q3).

### Automation risks

- Same as TC-NEW-025.
- Summary uses "Status" for an Org idea - which status field (Innovation Status vs Customer Status) is ambiguous (Q26).
- ADFS SSO redirect to a second host (adtestadfs.adtest.com) plus the optional Xornet Global Screen with a countdown redirect: conditional page, needs an explicit "either/or" wait (Q13).
- Self-signed certificate in test env: covered by `acceptInsecureCerts=true` in config.properties.
- Step 6 (verify user context) is [Partial]: no locator for the logged-in user indicator (Q13).

---

## TC-NEW-027

**Title:** Verify marking Accelerator notifies Department/Practice/Vertical heads and reflects in reports; committee still fills Actual Benefits.

| Field | Value |
|---|---|
| ID | TC-NEW-027 |
| Feature / module | Accelerator (sheet Name: Innovation Portal) |
| Change status | NEW |
| Requirement IDs | REQ-ACC-01, REQ-ACC-02 |
| Preconditions | Sheet: Preconditions empty. Steps: Committee member / Evaluator. |
| Actions (sheet) | _empty in sheet_ |
| Expected Result (sheet) | _empty in sheet_ |
| Automated yes/no / Test Steps Automated | Yes / Partial |
| Tags | `P1`, `accelerator` |

### Steps

| # | Step (as in sheet) | Expected result (as in sheet) | Automated |
|---|---|---|---|
| 1 | Launch a supported browser (Chrome/Edge recommended). | Browser opens without errors. | [Yes] |
| 2 | Navigate to https://10.20.3.66/innovation-portal. | Portal or ADFS login page is displayed (ignore self-signed cert warning in test env). | [Yes] |
| 3 | If redirected to ADFS SSO (adtestadfs.adtest.com), enter Username "ADTest\Adtest08" in #userNameInput and Password in #passwordInput; click Sign in. | ADFS authenticates successfully. | [Yes] |
| 4 | If the Xornet Global Screen appears after login, click Stay Here (or wait for redirect countdown). | Global Screen is dismissed; session remains active. | [Yes] |
| 5 | Confirm Innovation Portal loads (URL contains /innovation-portal); re-navigate to https://10.20.3.66/innovation-portal if still on Xornet home. | Innovation Portal landing page is visible with role-appropriate navigation. | [Yes] |
| 6 | Verify logged-in user context matches precondition: Committee member / Evaluator. | User context matches Committee member / Evaluator (ADTest\Adtest08). | [Partial] |
| 7 | Open Idea Reports via landing tile or https://10.20.3.66/innovation-portal/idea-report. | Idea Reports page loads for authorized role. | [Partial] |
| 8 | Apply filters (Title, Rating, Type, Status, DU, Department, Customer Status, dates); click Filter. | Grid reflects filter criteria. | [Partial] |
| 9 | Click Export (if scenario requires); verify Excel contents. | Outcome matches Expected Result column. | [Partial] |
| 10 | Capture screenshot if actual result differs from expected; log out / close browser. | Test evidence recorded; session closed. | [No] |

### Test data needed

| Field | Value / source |
|---|---|
| ADFS username | `ADTest\Adtest08` (sheet, Committee member / Evaluator) - no env var / users.json key exists yet - see Q1 |
| Notification recipients (Department/Practice/Vertical heads) | accounts/mailboxes not specified - see Q3, Q27 |
| Report filters | Title, Rating, Type, Status, DU, Department, Customer Status, dates (sheet) - values not specified - see Q27 |
| Test Data / Expected Result | empty - see Q27 |

### Page objects

LoginPage, XornetGlobalScreenPage, HomePage, IdeaReportsPage; AcceleratorDashboardPage / CustomerInitiativeProgressPage to mark Accelerator and fill Actual Benefits (Q27)

### Dependencies / setup

An idea already marked as Accelerator, or the TC-NEW-025/026 flow as setup (needs head/admin account) (Q27).

### Automation risks

- Notification verification (email / in-app) is outside the browser - needs mailbox or API access (Q27).
- Excel export: download directory handling in headless Chrome and parsing the file (no Excel library in the stack yet).
- Steps never mark an idea as Accelerator; the Summary chain is not covered by the steps (Q27).
- Report grid contents depend on all data in the env - filter to a known idea.
- ADFS SSO redirect to a second host (adtestadfs.adtest.com) plus the optional Xornet Global Screen with a countdown redirect: conditional page, needs an explicit "either/or" wait (Q13).
- Self-signed certificate in test env: covered by `acceptInsecureCerts=true` in config.properties.
- Step 6 (verify user context) is [Partial]: no locator for the logged-in user indicator (Q13).

---

## TC-NEW-035

**Title:** Verify Employee cannot open Approval Dashboard via direct URL.

| Field | Value |
|---|---|
| ID | TC-NEW-035 |
| Feature / module | Security (sheet Name: Innovation Portal) |
| Change status | NEW |
| Requirement IDs | REQ-AUTH-01 |
| Preconditions | Sheet: Preconditions empty. Steps: Employee user. |
| Actions (sheet) | _empty in sheet_ |
| Expected Result (sheet) | _empty in sheet_ |
| Automated yes/no / Test Steps Automated | Yes / Partial |
| Tags | `P1`, `security` |

### Steps

| # | Step (as in sheet) | Expected result (as in sheet) | Automated |
|---|---|---|---|
| 1 | Launch a supported browser (Chrome/Edge recommended). | Browser opens without errors. | [Yes] |
| 2 | Navigate to https://10.20.3.66/innovation-portal. | Portal or ADFS login page is displayed (ignore self-signed cert warning in test env). | [Yes] |
| 3 | If redirected to ADFS SSO (adtestadfs.adtest.com), enter Username "ADTest\Adtest15" in #userNameInput and Password in #passwordInput; click Sign in. | ADFS authenticates successfully. | [Yes] |
| 4 | If the Xornet Global Screen appears after login, click Stay Here (or wait for redirect countdown). | Global Screen is dismissed; session remains active. | [Yes] |
| 5 | Confirm Innovation Portal loads (URL contains /innovation-portal); re-navigate to https://10.20.3.66/innovation-portal if still on Xornet home. | Innovation Portal landing page is visible with role-appropriate navigation. | [Yes] |
| 6 | Verify logged-in user context matches precondition: Employee user. | User context matches Employee user (ADTest\Adtest15). | [Partial] |
| 7 | Navigate to the feature under test using landing tiles or verified routes for module "Security". | Target screen loads without error. | [Partial] |
| 8 | Execute Actions: perform scenario steps from Summary. | Intermediate UI state matches test intent. | [Partial] |
| 9 | Verify Expected Result: Outcome matches Expected Result column. | Outcome matches Expected Result column. | [Partial] |
| 10 | Capture screenshot if actual result differs from expected; log out / close browser. | Test evidence recorded; session closed. | [No] |

### Test data needed

| Field | Value / source |
|---|---|
| ADFS username | `ADTest\Adtest15` (sheet, Employee) - supply via `EMPLOYEE_USERNAME` / `EMPLOYEE_PASSWORD` env vars (users.json key `employee`); never hardcode |
| Target URL | https://10.20.3.66/innovation-portal/approval-dashboard (route taken from the other cases; step 7 here is generic) - see Q28 |
| Expected Result | empty - expected denial behaviour not specified - see Q28 |

### Page objects

LoginPage, XornetGlobalScreenPage, HomePage, ApprovalDashboardPage (only to detect that it did NOT load)

### Dependencies / setup

Employee account without evaluator rights (Q2, Q4). No data dependency.

### Automation risks

- No oracle: redirect vs access-denied page vs HTTP 403 vs empty grid is unknown (Q28).
- Contradiction with cases that open the Approval Dashboard as the same employee (Q4).
- Steps 7-9 are generic placeholders.
- ADFS SSO redirect to a second host (adtestadfs.adtest.com) plus the optional Xornet Global Screen with a countdown redirect: conditional page, needs an explicit "either/or" wait (Q13).
- Self-signed certificate in test env: covered by `acceptInsecureCerts=true` in config.properties.
- Step 6 (verify user context) is [Partial]: no locator for the logged-in user indicator (Q13).

---

## Summary 1 - Proposed page objects

| Page object | Purpose | Key locators / routes known from the sheet | Used by |
|---|---|---|---|
| LoginPage (ADFS) - new | ADFS sign-in on adtestadfs.adtest.com; credentials from env vars | #userNameInput, #passwordInput (Sign in button: not given - Q13). #userNameInput currently lives in HomePage | All 18 |
| XornetGlobalScreenPage - new | Dismiss the optional post-login Xornet Global Screen | "Stay Here" (text only, no locator - Q13); countdown redirect | All 18 |
| HomePage - existing | Portal landing page after login; role-appropriate navigation, user context; today only detects the ADFS redirect | URL contains /innovation-portal; "+ Submit Idea", "Approval Dashboard", landing tiles (text only - Q7, Q14) | All 18 |
| IdeaSubmitPage - new | Submit Idea form (Client / Organisation Initiative), validation | /idea-submit; #innovation_type, #edit-customer-name, #edit-project, #edit-title, #edit-domain, #edit-summary, #edit-innovation-details, #edit-confidential-yes, #edit-confidential-no, #edit-business-use-case, #edit-submit-idea; Category cards, Impact of Idea, Department, attachments: no locators (Q7) | TC-LEGACY-001, -004, -007, -008, TC-NEW-011 |
| IdeaDetailPage - new | Read idea ID / details after submit or when opening an idea | /idea/{id} | TC-LEGACY-001, -004, -101, TC-NEW-011, (TC-NEW-025, -026) |
| MyIdeasPage - new | List of the user's own ideas; find by title, read type/status | /my-idea-list; grid locators not given (Q11) | TC-LEGACY-001, -004, -101 |
| IdeaListPage - new | Public Idea List (approved, non-confidential ideas) | entry point not given (Q17) | TC-LEGACY-081, -082, -089 |
| ApprovalDashboardPage - new | Evaluator grid: filter, select idea, start actions | /approval-dashboard; Innovation Status filter, "My Evaluations" checkbox, Filter, Innovation Title link, Status column, Delete, Evaluate, Change the Evaluator (text only - Q14) | TC-LEGACY-029, -081, -082, -089, -101, -107, -116, TC-NEW-011, -013, -022, -035 |
| EvaluationPage - new | Evaluate an idea: Approve/Reject/On Hold, ratings, comments, draft | Approve, Reject, On Hold, ratings (Innovation Index, Potential Business Impact, Depth, Feasibility), comment, Save as Draft, Submit Evaluation; Perceived Benefit section (Q16); approver Confidential choice (Q19); no locators (Q14) | TC-LEGACY-029, -081, -082, -089, -107, -116, TC-NEW-013, -022 |
| ChangeEvaluatorDialog - new (component) | Reassign an idea to another committee member | "Select different committee member and Submit" (no locators - Q14) | TC-LEGACY-081, -082, -089, TC-NEW-022 |
| CustomerInitiativeProgressPage - new | CIP tracking: Customer Status, tracking fields, Save, lock rules | /actual_benefit_realization/{id}; Customer Status field, Save (no locators - Q20, Q21) | TC-LEGACY-101, -107, TC-NEW-022, (TC-NEW-027) |
| AcceleratorDashboardPage - new | Intellectual Property Dashboard; filters; Accelerator radio | /accelerator-dashboard; title "Intellectual Property Dashboard"; Practice / Status filters; Accelerator radio (location unknown - Q26) | TC-NEW-025, -026, (TC-NEW-027) |
| IdeaReportsPage - new | Idea Reports grid, filters, Excel export | /idea-report; filters Title, Rating, Type, Status, DU, Department, Customer Status, dates; Filter; Export (no locators - Q27) | TC-NEW-027 |

All new page objects extend `BasePage`. Locators that are known only by visible text are listed so they can be confirmed (Q7, Q13, Q14); per PROJECT_CONTEXT, XPath/text locators are a last resort.

## Summary 2 - Open questions

1. **Q1.** **[Partial - see Summary 4]** Role accounts: users.json only has the `employee` key (`EMPLOYEE_USERNAME` / `EMPLOYEE_PASSWORD`). The sheet logs in as `ADTest\Adtest08` (Committee member / Evaluator) and `ADTest\Adtest04` (Admin user), and the Test Data column names ADTEST09 / ADTEST10 (PEIP Vertical committee) and "Adtest 04,05,06 etc." (all users). Which accounts should automation use per role, and what env var names / users.json keys should hold them? *Blocks: TC-LEGACY-029, TC-LEGACY-081, TC-LEGACY-082, TC-LEGACY-089, TC-LEGACY-101, TC-LEGACY-107, TC-NEW-011, TC-NEW-022, TC-NEW-025, TC-NEW-026, TC-NEW-027.*
2. **Q2.** **[Resolved - see Summary 4]** Is the `EMPLOYEE_USERNAME` account the sheet's `ADTest\Adtest15`? Is it allocated to a project (TC-LEGACY-001 needs a project-allocated employee), and to which customer/project? *Blocks: TC-LEGACY-001, TC-LEGACY-004, TC-LEGACY-007, TC-LEGACY-008, TC-NEW-035.*
3. **Q3.** **[Blocker - see Summary 4]** Roles with no account anywhere: Project Manager (Rel 5.1); Vertical head, Practice head, Department head; System Admin. Is `ADTest\Adtest04` ("Admin user") the System Admin? Note Adtest04 is also listed under "All Users" in TC-LEGACY-081/082. Which roles must NOT see the Accelerator radio (for negative checks), and which accounts represent them? *Blocks: TC-NEW-022, TC-NEW-025, TC-NEW-026, TC-NEW-027.*
4. **Q4.** **[Blocker - see Summary 4]** Contradiction: TC-NEW-035 says an Employee cannot open the Approval Dashboard by direct URL, but TC-LEGACY-081, -082, -089, -116 and TC-NEW-013 log in as Employee `ADTest\Adtest15` and expect the Approval Dashboard to load with evaluator-assigned ideas and evaluator actions (Evaluate / Approve / Reject). Which account should run those cases, and does Adtest15 have evaluator rights? *Blocks: TC-LEGACY-081, TC-LEGACY-082, TC-LEGACY-089, TC-LEGACY-116, TC-NEW-013, TC-NEW-035.*
5. **Q5.** **[Partial - see Summary 4]** What do the Test Data values "ADTEST-08     SafetyChain Mobile" and "ADTEST-14           Tibco iMany" mean (user account, customer, project, or idea)? TC-LEGACY-001 step 9 says "pick any assigned customer / any available project": which Customer Name and Project Name should automation select? *Blocks: TC-LEGACY-001, TC-LEGACY-004, TC-LEGACY-007, TC-LEGACY-008, TC-LEGACY-089, TC-LEGACY-116.*
6. **Q6.** **[Partial - see Summary 4]** Organisation Initiative: the Department* value is not given anywhere. TC-LEGACY-004 Test Data ("Tibco iMany") looks like a project, but the Project field is hidden for Org ideas, and TC-LEGACY-008 reuses the Client test data. Which Department (and other Org-specific data) should be used? *Blocks: TC-LEGACY-004, TC-LEGACY-008, TC-NEW-011.*
7. **Q7.** **[Resolved - see Summary 4]** Missing Submit Idea locators: "+ Submit Idea" button on the landing page, Innovation Category cards, Impact of Idea checkboxes with their Amount and Description inputs, Department field, attachment/upload input, required-field indicator (red asterisk), field-level validation messages. Only #innovation_type, #edit-customer-name, #edit-project, #edit-title, #edit-domain, #edit-summary, #edit-innovation-details, #edit-confidential-yes/-no, #edit-business-use-case and #edit-submit-idea are given. *Blocks: TC-LEGACY-001, TC-LEGACY-004, TC-LEGACY-007, TC-LEGACY-008.*
8. **Q8.** **[Partial - see Summary 4]** What are the exact option texts and values for Innovation Type (the sheet writes "Organisation Initiative" in steps, "Organization" in Summary/Expected), Innovation Category, Domain (and is #edit-domain a multi-select, a checkbox list or a widget?), and the Impact of Idea options, plus a valid Amount format? *Blocks: TC-LEGACY-001, TC-LEGACY-004, TC-LEGACY-007, TC-LEGACY-008.*
9. **Q9.** **[Partial - see Summary 4]** Validation rules for the Client and Org forms: which fields have the limit behind the "501-char text" boundary; the max number of Domains ("extra domains"; TC-LEGACY-001 says 1-5); allowed attachment types and size ("invalid attachments"); the exact validation message texts. Does an invalid form keep Submit disabled, or allow a click and then block (step 10 says "stays disabled or blocks submit")? *Blocks: TC-LEGACY-007, TC-LEGACY-008.*
10. **Q10.** **[Blocker - see Summary 4]** Validation cases step 11 ("re-test with valid data; confirm Submit enables and succeeds when scenario requires positive path"): should automation actually submit an idea, or stop once Submit is enabled? *Blocks: TC-LEGACY-007, TC-LEGACY-008.*
11. **Q11.** **[Partial - see Summary 4]** Post-submit oracle: what is the exact initial status (sheet says "e.g., Pending Approval")? TC-LEGACY-004 says the browser goes to /idea/{id} "or shows success". Which one happens, and what is the success text? What are the My Ideas grid locators and columns (title, Innovation Type, status), and which "status/details" must be checked? *Blocks: TC-LEGACY-001, TC-LEGACY-004.*
12. **Q12.** **[Blocker - see Summary 4]** Test data lifecycle: submitted, approved and rejected ideas persist in the shared `test` environment. Is there a cleanup mechanism (Delete on the Approval Dashboard, API, DB reset), a required title prefix for automation ideas, or a rule for the Confidential Yes/No choice on submitted ideas? Are notifications sent on submit? *Blocks: TC-LEGACY-001, TC-LEGACY-004, TC-LEGACY-007, TC-LEGACY-008, TC-NEW-011.*
13. **Q13.** **[Resolved - see Summary 4]** Login flow details: the ADFS "Sign in" button locator (only #userNameInput / #passwordInput are given); the Xornet Global Screen "Stay Here" locator, when it appears, and how long the countdown is; whether MFA or other prompts can appear; and the locator of the logged-in user indicator used to "verify user context" (step 6, [Partial] in every case). *Blocks: All 18.*
14. **Q14.** **[Resolved - see Summary 4]** Approval Dashboard and evaluation locators are missing: Approval Dashboard link on the landing page, Innovation Status filter, "My Evaluations" checkbox, Filter button, grid rows / Innovation Title link / Status column, Delete, Evaluate, Change the Evaluator, Approve / Reject / On Hold, rating inputs, comment field, Save as Draft, Submit Evaluation, and the committee-member selector in Change Evaluator. *Blocks: TC-LEGACY-029, TC-LEGACY-081, TC-LEGACY-082, TC-LEGACY-089, TC-LEGACY-107, TC-LEGACY-116, TC-NEW-011, TC-NEW-013, TC-NEW-022.*
15. **Q15.** **[Blocker - see Summary 4]** Pre-existing data states: who provides Pending Approval ideas assigned to the evaluator ("My Evaluations"), approved Client ideas, and ideas in Implemented / POC Completed status? Approve, Reject and On Hold consume these ideas on every run. Should tests create them in setup through the UI (submit, then approve), use a fixed seeded set, or use an API? *Blocks: TC-LEGACY-029, TC-LEGACY-089, TC-LEGACY-101, TC-LEGACY-107, TC-LEGACY-116, TC-NEW-011, TC-NEW-013, TC-NEW-022, TC-NEW-025, TC-NEW-026, TC-NEW-027.*
16. **Q16.** **[Partial - see Summary 4]** TC-LEGACY-029 has three conflicting descriptions. Summary: "Approve is blocked when Impact of Idea is missing; Save as Draft may retain partial data". Expected Result: "evaluator should be able to skip Perceived Benefit Realization". Action: "Click On Perceived Benefit Screen". Which is the oracle? Where is the Perceived Benefit screen (route or locator)? Which partial data should be entered? Test Data names ADTEST10, but the steps log in as Adtest08: which one is correct? *Blocks: TC-LEGACY-029.*
17. **Q17.** **[Partial - see Summary 4]** TC-LEGACY-081/082: Summary and Actions are about the Idea List (approved non-confidential ideas shown; approved confidential ideas not shown), but the detailed steps go to the Approval Dashboard and Delete/Evaluate/Change Evaluator. What is the Idea List entry point (URL, landing tile or locator)? Which known approved ideas (titles, one non-confidential and one confidential) can serve as fixtures? *Blocks: TC-LEGACY-081, TC-LEGACY-082.*
18. **Q18.** **[Blocker - see Summary 4]** TC-LEGACY-082 Summary reads "dashboard displays all ideas that have been approved and confidential", while its Expected Result says "Such records should not be shown here". Please confirm that the expected result is "not shown". TC-LEGACY-081 has "Automated yes/no" = No but "Test Steps Automated" = Partial: is it in scope for automation? *Blocks: TC-LEGACY-081, TC-LEGACY-082.*
19. **Q19.** **[Blocker - see Summary 4]** TC-LEGACY-089: where does the approver choose Confidential Yes/No during approval (control and locator; is it different from #edit-confidential-yes/-no on the submit form)? Which approver account? Does "displayed in Idealist for all employees" mean checking with one employee account or several? *Blocks: TC-LEGACY-089.*
20. **Q20.** **[Partial - see Summary 4]** TC-LEGACY-101: the Summary ("Implemented locks further Customer Progress updates; POC Completed and Proposed also support Actual Benefits (Phase 5)") and the Expected Result ("Rejected and Implemented will be end state") describe different checks. Which should be asserted? Step 9 says "required tracking fields from Test Data", but Test Data only lists committee members. Which fields and values? How is a lock shown in the UI? Is /actual_benefit_realization/{id} really the Customer Initiative Progress page? Which approved Client idea should be used? Login is Adtest08 while Test Data names ADTEST09/ADTEST10. *Blocks: TC-LEGACY-101.*
21. **Q21.** **[Partial - see Summary 4]** TC-LEGACY-107: the Summary lists 7 Customer Status options (including "POC Completed"), but the Expected Result lists 6 (without it). Which list is correct? The steps put an idea On Hold and then Approve/Reject on the Approval Dashboard, but never open the Customer Status field. Where is that field (CIP page?), what is its locator, and are the On Hold / Approve / Reject steps needed at all? *Blocks: TC-LEGACY-107.*
22. **Q22.** **[Partial - see Summary 4]** TC-LEGACY-116: what is the exact weighted-score formula and rounding (25/35/25/15 on a 1-10 scale), and where is the overall score displayed? What type of control is each rating input? What message or behaviour appears when Approve is clicked without ratings? Are Impact of Idea / Business Use Case required at approval? *Blocks: TC-LEGACY-116.*
23. **Q23.** **[Partial - see Summary 4]** TC-NEW-011: Test Data, Preconditions, Actions and Expected Result are empty, and the detailed steps check the Pending Approval filter rather than evaluator auto-assignment. Which vertical default evaluator and department default evaluator are configured (for which vertical/department)? Where is the assigned evaluator visible (idea detail, grid column)? Should the test submit a Client and an Org idea first? *Blocks: TC-NEW-011.*
24. **Q24.** **[Partial - see Summary 4]** TC-NEW-013: the Expected Result is empty. How exactly is "Reject without comment" blocked (disabled button, inline message, alert), and what is the message text? Step 12 then really rejects the idea (an end state). Should automation do that step? *Blocks: TC-NEW-013.*
25. **Q25.** **[Blocker - see Summary 4]** TC-NEW-022: the Expected Result is empty, and the steps log in as committee member Adtest08 and perform evaluator actions, the opposite of the Summary (Project Manager can update Customer Progress only). Which PM account and which idea (on the PM's project) should be used? What should the PM see: CIP editable, Approve/Reject/Evaluate hidden or disabled? *Blocks: TC-NEW-022.*
26. **Q26.** **[Partial - see Summary 4]** TC-NEW-025/026: Test Data and Expected Result are empty, although step 8 says "validate ... per Test Data". Where is the Client/Org Accelerator radio (Intellectual Property Dashboard, idea detail, CIP page), and what is its locator? Which Practice/Status filter values apply? Is "Status" the Innovation Status or the Customer Status? Which ideas in Implemented (Client) and POC Completed (Org) status exist? *Blocks: TC-NEW-025, TC-NEW-026.*
27. **Q27.** **[Partial - see Summary 4]** TC-NEW-027: Test Data and Expected Result are empty. How should notifications to Department/Practice/Vertical heads be verified (email inbox access, in-app notification, out of scope)? Which report and column reflects the Accelerator flag? What should the exported Excel contain? How is "committee still fills Actual Benefits" verified? The steps never mark an idea as Accelerator: should the test do it (and with which role), or use an existing idea? Idea Reports locators (filters, Filter, Export) are missing. *Blocks: TC-NEW-027.*
28. **Q28.** **[Blocker - see Summary 4]** TC-NEW-035: the Expected Result is empty and step 7 is generic. Is the target https://10.20.3.66/innovation-portal/approval-dashboard? What is the correct denial behaviour: redirect to the landing page, an access-denied page with a message (which text?), HTTP 403, or an empty grid? *Blocks: TC-NEW-035.*
29. **Q29.** **[Blocker - see Summary 4]** Feature tags: Constants currently defines only `login` (TAG_FEATURE_LOGIN). Please confirm the proposed feature tags `submit`, `validation`, `approval`, `idealist`, `tracking`, `accelerator`, `security`. Should `login` stay as a separate tag? *Blocks: All 18 (tagging only).*
30. **Q30.** **[Partial - see Summary 4]** Closing step "Capture screenshot ...; log out / close browser" is [No] in every case. The framework already takes a screenshot on failure and quits the driver. Is an explicit portal/ADFS logout required, and if so, what is the logout locator? *Blocks: All 18 (teardown only).*

## Summary 3 - Implementation plan

### Golden path: TC-LEGACY-001 - Employee submits a Client Initiative idea

Why this test comes first:
- **Uses only the role the repo already supports.** It runs as the `employee` account (`EMPLOYEE_USERNAME` / `EMPLOYEE_PASSWORD`, users.json `employee`). No new accounts are needed.
- **Has the most concrete data in the sheet.** It names 10 element IDs (`#innovation_type`, `#edit-customer-name`, `#edit-project`, `#edit-title`, `#edit-domain`, `#edit-summary`, `#edit-innovation-details`, `#edit-confidential-yes/-no`, `#edit-business-use-case`, `#edit-submit-idea`), the routes `/idea-submit`, `/idea/{id}` and `/my-idea-list`, and a filled-in Expected Result.
- **Builds the shared foundation.** It creates LoginPage, XornetGlobalScreenPage, the landing-page HomePage and IdeaSubmitPage, which are reused by every other test (login) and by batch 1 (the form).
- **Supplies data for later tests.** Its idea-creation flow can serve as setup for the approval cases (Q15).
- **Why not TC-NEW-035?** It is smaller, but it has no Expected Result (Q28), and its premise conflicts with five other cases (Q4). It therefore has no reliable oracle.

What blocks it, and how to work around it: Q2, Q5, Q7 (Category cards and Impact of Idea), Q8, Q11, Q12 and Q13. The login part and steps 1-13 can be built with the IDs the sheet gives. The gaps get `// TODO(TC-LEGACY-001)` markers until they are answered.

### Remaining 17 tests in 4 batches

| Batch | Tests | Feature group | Rationale | Blocking questions |
|---|---|---|---|---|
| 1 | TC-LEGACY-004, TC-LEGACY-007, TC-LEGACY-008, TC-NEW-035 | Submit / validation / employee security | Reuses LoginPage + IdeaSubmitPage from the golden path; employee account only, so no new role accounts are needed. | Q2, Q4, Q5, Q6, Q7, Q8, Q9, Q10, Q11, Q12, Q28 |
| 2 | TC-LEGACY-029, TC-LEGACY-116, TC-NEW-011, TC-NEW-013 | Approval (evaluator) | Introduces ApprovalDashboardPage + EvaluationPage and the evaluator account; uses golden-path submission as setup for Pending Approval ideas. | Q1, Q4, Q14, Q15, Q16, Q22, Q23, Q24 |
| 3 | TC-LEGACY-081, TC-LEGACY-082, TC-LEGACY-089, TC-LEGACY-101, TC-LEGACY-107 | Post-approval visibility and tracking (idealist + tracking) | All need ideas that are already approved, in a known status and confidentiality. Adds IdeaListPage and CustomerInitiativeProgressPage on top of batch 2's approval flow. | Q1, Q4, Q5, Q14, Q15, Q17, Q18, Q19, Q20, Q21 |
| 4 | TC-NEW-022, TC-NEW-025, TC-NEW-026, TC-NEW-027 | Role-gated features (Accelerator + PM privileges) | Need special roles (Project Manager, Vertical/Practice/Department head, System Admin) and late-lifecycle ideas (Implemented / POC Completed). Adds AcceleratorDashboardPage and IdeaReportsPage. The most blocked group, so it goes last. | Q1, Q3, Q15, Q25, Q26, Q27 |

Cross-cutting before any batch: Q13 (login flow locators), Q29 (feature tags) and Q30 (logout).

## Summary 4 - Open question status (DEM-61)

Added by DEM-61. No written answers to Q1-Q30 were supplied on the ticket (the "[PASTE ANSWERS]" placeholder was left as is). Each question was therefore checked against the live `test` environment (read-only crawl on 2026-10-02 as `ADTest\Adtest04`) and against the ticket owner's chat answer about accounts. Locators and evidence are in `docs/testcases/locator-inventory.md` (section numbers in brackets).

- **Resolved:** answered with evidence.
- **Partial:** locators or option lists answered; a business rule or fixture is still needed.
- **Blocker:** needs the ticket owner or developers.

| Q | Status | Answer / evidence | Still needed |
|---|---|---|---|
| Q1 | Partial | Ticket owner: `EMPLOYEE_USERNAME` = `ADTest\Adtest04`. The other role accounts are in the DEM-61 attachment "Credentials_Username 1.txt", chosen per test case. Proposed env vars: `EVALUATOR_*`, `ADMIN_*`, `PROJECT_MANAGER_*`, `VERTICAL_HEAD_*`, `PRACTICE_HEAD_*`, `DEPARTMENT_HEAD_*`, `EMPLOYEE_ONLY_*` (inventory, Test data design) | Which account goes into each env var |
| Q2 | Resolved | `EMPLOYEE_USERNAME` is Adtest04, not the sheet's Adtest15. With Adtest04 the Submit Idea form loads with the Customer/Project lists (nothing was submitted). It also has admin and evaluator rights | - (see Q4 for the side effect) |
| Q3 | Blocker | No Project Manager or head-role accounts were available to crawl | Accounts, and which roles must NOT see the IP radio |
| Q4 | Blocker | Adtest04 opens the Approval Dashboard and sees Evaluate / Change the Evaluator, so it cannot prove TC-NEW-035's denial | A non-evaluator employee account (`EMPLOYEE_ONLY_*`) |
| Q5 | Partial | Customer Name is a select of 59 customers. Choosing one reloads Project Name via AJAX (for example AMDOCS gives AMDOC112), and the Project `id` changes, so use `select[name='project']` (inventory 4) | Which customer/project to use; meaning of "ADTEST-08 SafetyChain Mobile" |
| Q6 | Partial | Department appears only for Org ideas: Business System Group, Facility Management, Finance, HR, IT Support, Legal, Marketing, RESOURCE MANAGEMENT GROUP, Recruitment, SEPG, Sales-Presales, Sysnet, Training, US Staffing (inventory 4) | Which department to use |
| Q7 | Resolved | "+ Submit Idea" = `a[href='/innovation-portal/idea-submit']`. Category = hidden radios clicked via `label[for]`. Impact of Idea = `edit-impect-of-idea-*` (+ `-amount-`, `-comment-`). Department `#edit-department`. Attachments `#edit-innovation-file-upload`. There is no required-field marker and validation messages were not seen (flagged D4) | Dev request D4 |
| Q8 | Partial | Innovation Type values: `Client Initiative`, `Organisation Initiative`. Client categories (2) and Org categories (5) listed. Domain is a token-input autocomplete (`#token-input-edit-domain`), not a select. 4 Impact options. Amount maxlength 10 (inventory 4) | Domain suggestion list; Amount format |
| Q9 | Partial | Limits from the DOM: title 128, summary/details/business use case 500, impact description 150, amount 10, max 3 attachments at 15 MB, allowed types listed. Submit Idea starts disabled | Max domains; validation message texts; the 501-char behaviour |
| Q10 | Blocker | Not observable read-only | Decision: submit or stop when Submit enables |
| Q11 | Partial | My Ideas shows freshly submitted ideas as `Pending Approval`; the grid columns are mapped (inventory 6) | Post-submit redirect / success text (needs a real submit) |
| Q12 | Blocker | The env already holds dozens of automation ideas. Pending ideas have a Delete link (`/unblishidea/<id>`, unpublish) for admin. Proposed: a `titlePrefix` per feature | Cleanup policy; whether notifications fire |
| Q13 | Resolved | Sign in = `#submitButton`; error = `#errorText`. Global Screen "Stay Here" = `#stop`, countdown `#countr` (showed 4 at capture; full duration not measured). For Adtest04 no Global Screen and no MFA appeared. User indicator = `.user.profile a` (class-based; D3) | - |
| Q14 | Resolved | All Approval Dashboard, Evaluation and Change the Evaluator locators are mapped. Evaluate, Delete and Change the Evaluator are on the Idea detail page, not in the grid (inventory 5, 8, 9, 10) | - |
| Q15 | Blocker | - | Fixture strategy for Pending / Approved / Implemented / POC Completed ideas |
| Q16 | Partial | "Perceived Benefit" is the "Impact of idea" section on the Evaluation page. The page note says approval requires every rating and the perceived benefit (inventory 9) | Which oracle; which account (Adtest08 vs ADTEST10) |
| Q17 | Partial | Idea List = `/innovation-portal/dashboard/all_idea_list` (`/idea-list` is 404). It has no Confidential column, so the oracle is "title present / absent" (inventory 7) | Fixture ideas (one non-confidential, one confidential) |
| Q18 | Blocker | - | Confirm "not shown"; is TC-LEGACY-081 in scope |
| Q19 | Blocker | The Evaluation page has no approver Confidential control, only "mark as Intellectual Property" Yes/No | Where the approver sets confidentiality |
| Q20 | Partial | `/actual_benefit_realization/<id>` is "Customer Initiative Progress Tracking". For an Implemented idea, Customer Status offers only `Implemented` and the POC/Designation/LOB fields are disabled (the lock). Org ideas use `/actual_benefit_accelerators/<id>` (inventory 11) | Which assertion; which idea |
| Q21 | Partial | The app's Customer Status list: In Progress, Proposed to Customer, Accepted, Rejected, On Hold, Implemented, POC Completed (+ No Action as the dashboard default). The field is on the progress page | Confirm the expected list; whether the On Hold/Approve steps are needed |
| Q22 | Partial | The Evaluation page has **5** rating questions (adds "End customer benefit / Domain / AI focus"), each a 1-10 radio group `ques_0`..`ques_4`. No overall score is shown on the Idea detail page | Formula/weights for 5 questions; where the score shows |
| Q23 | Partial | The Approval Dashboard has an Evaluator column (for example Org ideas in HR show adtest10). `/innovation-portal/committee` lists DU / Department / Core committees | Which defaults to assert |
| Q24 | Partial | Reject needs "Please provide the reason to change the status of the idea." (`#edit-admin-comment`, required) | Exact error behaviour/text; whether to really reject |
| Q25 | Blocker | - | PM account, idea, expected UI |
| Q26 | Partial | The IP/Accelerator radio is on the Evaluation page (`mark_accelerator` Yes/No). An approved, Implemented Client idea shows "Add Intellectual Property", which opens `/accelerator-form/<id>`. IP Dashboard filters: Practice, Status (Pending/Approved/Rejected) (inventory 9, 12) | Role/status visibility rules; fixture ideas |
| Q27 | Partial | Idea Reports filters, Export and pagination are mapped. No IP column seen in the report grid (inventory 13) | How to verify notifications; which report shows the flag |
| Q28 | Blocker | The route `/innovation-portal/approval-dashboard` exists | Denial behaviour (needs `EMPLOYEE_ONLY_*`) |
| Q29 | Blocker | The JSON files use the proposed tags: `submit`, `validation`, `approval`, `idealist`, `tracking`, `accelerator`, `security` | Confirmation before Constants are changed |
| Q30 | Partial | No logout link exists in the portal header; `/innovation-portal/user/logout` is 404. The framework already quits the driver | Whether logout is needed at all |

New finding (not in Q1-Q30): in headless Chrome the portal shows only "Currently we are not supporting Landscape mode..." and hides all content. Headed Chrome works. This blocks headless/CI runs until fixed (inventory X1).
