# Test inventory: the 14 passing P1 tests

This inventory describes the 14 P1 tests on branch `migration/passed-p1` (see `docs/MIGRATION.md`) so that they can be rebuilt in another repository and framework without reading the Java code. The code is the source of truth. Every selector below is copied from the page objects in `src/main/java/com/innovationportal/pages/`. Every value comes from the JSON files in `src/test/resources/testdata/`.

General facts that apply to every test:

- **Application**: Innovation Portal (Drupal). The base URL is `test.baseUrl=https://10.20.3.66/innovation-portal` (`config.properties`). The test environment's TLS certificate is not publicly trusted, so `acceptInsecureCerts=true`. Every route below is relative to the base URL.
- **Browser**: Chrome headless by default, window `1920,1080`. In headless mode the code adds `--screen-info={1920x1080}`, because headless Chromium reports an 800x600 screen and the portal's CSS then hides everything behind a "not supporting Landscape mode" overlay (`DriverFactory`).
- **Waits**: explicit waits only, with no implicit waits and no sleeps. The default is `explicitWaitSeconds=10`, polling every 250 ms. Page loads time out after `pageLoadTimeoutSeconds=30`. `Constants.SHORT_TIMEOUT` = 3 s and `Constants.LONG_TIMEOUT` = 30 s.
- **Lifecycle** (`BaseTest`, `TestLifecycleExtension`): before each test, a new browser opens on the base URL. Without a session, the portal redirects to ADFS. After each test, the browser quits. When a test fails, the current URL and a screenshot are attached to the Allure report before the browser closes. Tests run one at a time (parallel execution is off).
- **Clicks and typing** (`BasePage`): each target is scrolled to the middle of the viewport with `scrollIntoView({block:'center', inline:'nearest', behavior:'instant'})` before the action. Without this, Bootstrap 5's `scroll-behavior: smooth` causes "element click intercepted". A click waits for the element to be visible and then clickable. Typing waits for visibility, then calls `clear()` and `sendKeys`.
- **No cleanup**: no test deletes the ideas it creates. Cleanup policy is open question Q12. Each run creates fresh ideas whose titles are `titlePrefix + "_" + 8 random hex characters` (`RandomDataUtils.uniqueString`, for example `AUTO-P1-SUBMIT_1a2b3c4d`).
- **Tags**: every test has `@Tag("P1")` and one feature tag. The feature tag names are still pending confirmation (`TODO(Q29)` in `Constants`).

## 1. Summary

Accounts are shown as the env-var prefix used in JSON, followed by who signs in at run time. `EMPLOYEE`, `EVALUATOR` and `ADMIN` are all `ADTest\Adtest04`. `EMPLOYEE_ONLY` is `ADTest\Adtest15`. `PROJECT_MANAGER` is `ADTest\Adtest08`. For passwords, see Appendix B.

| TC id | Test class#method | Feature / tags | Accounts | Approx. runtime | Ideas it creates (per run) |
|---|---|---|---|---|---|
| TC-LEGACY-001 | `SubmitClientIdeaTest#employeeSubmitsClientInitiativeIdea` | Submit / `P1`, `submit` | `EMPLOYEE` (Adtest04) | ~60 s | 1 Client idea `AUTO-P1-SUBMIT_<id>`, left Pending Approval |
| TC-LEGACY-004 | `SubmitClientIdeaTest#employeeSubmitsOrganisationInitiativeIdea` | Submit / `P1`, `submit` | `EMPLOYEE` (Adtest04) | ~60 s | 1 Organisation idea `AUTO-P1-SUBMIT_<id>`, left Pending Approval |
| TC-LEGACY-007 | `SubmitFormValidationTest#clientFormBlocksSubmitUntilMandatoryFieldsAreFilled` | Validation / `P1`, `validation` | `EMPLOYEE` (Adtest04) | ~40 s | None. The form is never submitted; the title `AUTO-P1-VALIDATION_<id>` is only typed |
| TC-LEGACY-008 | `SubmitFormValidationTest#orgFormBlocksSubmitUntilMandatoryFieldsAreFilled` | Validation / `P1`, `validation` | `EMPLOYEE` (Adtest04) | ~40 s | None (same as 007) |
| TC-LEGACY-029 | `IdeaEvaluationTest#evaluatorSavesDraftWithoutImpactOfIdea` | Approval / `P1`, `approval` | `EVALUATOR` (Adtest04) | 70-100 s | 1 Client idea `AUTO-P1-APPROVAL_<id>`, left Pending Approval with a saved evaluation draft |
| TC-LEGACY-116 | `IdeaEvaluationTest#evaluatorMustRateIdeaToApprove` | Approval / `P1`, `approval` | `EVALUATOR` (Adtest04) | 70-100 s | 1 Client idea `AUTO-P1-APPROVAL_<id>`, Approved |
| TC-NEW-011 | `IdeaEvaluationTest#newIdeasAreAssignedToDefaultEvaluators` | Approval / `P1`, `approval` | `EVALUATOR` (Adtest04) | 70-100 s | 2 ideas `AUTO-P1-APPROVAL_<id>` (1 Client + 1 Organisation), left Pending Approval |
| TC-NEW-013 | `IdeaEvaluationTest#evaluatorCannotRejectWithoutComment` | Approval / `P1`, `approval` | `EVALUATOR` (Adtest04) | 70-100 s | 1 Organisation idea `AUTO-P1-APPROVAL_<id>`, left Pending Approval (Reject is never submitted) |
| TC-LEGACY-081 | `IdeaListVisibilityTest#approvedNonConfidentialIdeaIsShownInIdeaList` | Idea List / `P1`, `idealist` | `EVALUATOR` (Adtest04) | ~60 s | 1 Client idea `AUTO-P1-IDEALIST_<id>`, Confidential = No, Approved |
| TC-LEGACY-082 | `IdeaListVisibilityTest#approvedConfidentialIdeaIsNotShownInIdeaList` | Idea List / `P1`, `idealist` | `EVALUATOR` (Adtest04) | ~60 s | 1 Client idea `AUTO-P1-IDEALIST_<id>`, Confidential = Yes, Approved |
| TC-LEGACY-101 | `CustomerInitiativeProgressTest#implementedAndRejectedAreEndStates` | Tracking / `P1`, `tracking` | `EVALUATOR` (Adtest04) | 50-130 s | 2 Client ideas `AUTO-P1-TRACKING_<id>`, both Approved: one with Customer Status Implemented, one with Customer Status Rejected |
| TC-LEGACY-107 | `CustomerInitiativeProgressTest#customerStatusOffersAllOptions` | Tracking / `P1`, `tracking` | `EVALUATOR` (Adtest04) | 50-130 s | 1 Client idea `AUTO-P1-TRACKING_<id>`: Hold, then Approved, then Customer Status Proposed to Customer |
| TC-NEW-022 | `CustomerInitiativeProgressTest#projectManagerUpdatesCustomerProgressOnly` | Tracking / `P1`, `tracking` | `EMPLOYEE_ONLY` (Adtest15) submits, `EVALUATOR` (Adtest04) approves, `PROJECT_MANAGER` (Adtest08) tracks. Three browser sessions | 50-130 s | 1 Cisco Client idea `AUTO-P1-TRACKING_<id>`: Approved, then Customer Status Proposed to Customer (saved by Adtest08) |
| TC-NEW-035 | `ApprovalDashboardAccessTest#employeeCannotOpenApprovalDashboardByDirectUrl` | Security / `P1`, `security` | `EMPLOYEE_ONLY` (Adtest15) | ~45 s | None |

Total: 14 ideas per full run. TC-LEGACY-007, TC-LEGACY-008 and TC-NEW-035 create none.

## 2. Shared flows

The test sections refer to these flows by id (F1-F8). Selectors are given inline. The full list is in Appendix A.

### F1. Sign in (ADFS, then the optional Xornet Global Screen, then the portal landing page)

Pages used: `LoginPage`, `XornetGlobalScreenPage`, `HomePage`.

1. The browser is already on the base URL, which redirects to ADFS (`adtestadfs.adtest.com`, URL contains `/adfs/`).
   **Assert** "Step 2: Portal or ADFS login page is displayed": `#userNameInput` becomes visible within 10 s (`LoginPage.isLoaded`).
2. `LoginPage.loginAs(user)`:
   - Type the username into `By.id("userNameInput")`.
   - Type the password into `By.id("passwordInput")`.
   - Click `By.id("submitButton")`.
   - Wait up to **30 s** (page-load timeout, not the 10 s explicit wait) until the URL no longer contains `/adfs/` **or** `By.id("errorText")` has non-blank text. A stale `errorText` element counts as "no error". The code comment says the ADFS-to-portal redirect chain can outlast the explicit wait.
3. **Assert** "Step 3: ADFS authenticates successfully": the URL no longer contains `/adfs/`. The failure message includes the `#errorText` text.
4. `XornetGlobalScreenPage.dismissIfShown()`. Some accounts are shown a Xornet Global Screen whose countdown redirects to the Xornet home page.
   - Wait up to 10 s until the URL contains `/innovation-portal` **or** a "Stay Here" button `By.id("stop")` exists.
   - If `#stop` exists, click it.
   - If the URL still does not contain `/innovation-portal`, navigate to the base URL.
5. **Assert** "Step 4-5: Global Screen is dismissed and the Innovation Portal landing page is visible": `HomePage.isLoaded`, meaning `#content h1.title` is visible.
6. **Assert** "Step 5: Landing page shows role-appropriate navigation (+ Submit Idea)": `a[href='/innovation-portal/idea-submit']` is visible. Every sign-in helper checks this except the helpers in `SubmitFormValidationTest` and `ApprovalDashboardAccessTest`.
7. **Assert** "Step 6: Logged-in user context is shown in the header": the text of `#block-siteheadercustomblock .user.profile a` is not blank.

### F2. Submit an idea (Client Initiative or Organisation Initiative)

Page used: `IdeaSubmitPage`, form `#innovation-portal-idea-submit-form`, route `/innovation-portal/idea-submit`.

**Opening the form**

- The submit tests (001/004) and the validation helper click `+ Submit Idea` on the landing page they just reached.
- The setup helpers in the approval, idealist and tracking tests first re-open the landing page (`HomePage.open()`, which navigates to the base URL) and then click `+ Submit Idea`.
- In both cases the click is on `a[href='/innovation-portal/idea-submit']`.

**Assert** "Step 7 / Setup: Submit Idea form loads at /idea-submit". `IdeaSubmitPage.isLoaded` checks two things:

- The form `#innovation-portal-idea-submit-form` is visible and the URL contains `/innovation-portal/idea-submit`.
- It then waits for the **Innovators AJAX**, defined below.

**Innovators AJAX wait** (used repeatedly): the form fetches the Innovators list by AJAX when it loads and again after Customer changes. Until the list arrives, the field is disabled ("Please wait fetching innovators list") and the page does not scroll, so clicks below the fold are intercepted. The wait runs until two conditions hold:

- `By.id("edit-innovators")` is enabled.
- `By.id("token-input-edit-innovators")` exists.

A stale element counts as "not yet". This wait has no timeout handling, so a timeout fails the test.

**Fields, in the order the code fills them:**

| # | Field | Action and selector | Value (Client: `submit.json#clientIdea`; Org: `submit.json#orgIdea`) | Wait / notes |
|---|---|---|---|---|
| 1 | Innovation Type | Select by visible text in `By.id("innovation_type")` | `Client Initiative` / `Organisation Initiative` | Then the Innovators AJAX wait. Client shows Customer Name and Project Name and hides Department; Org does the opposite |
| 2a | Customer Name (Client only) | Select by visible text in `select[name='customer_name']` | `AMDOCS` (TC-NEW-022: `Cisco Systems (India) Pvt. Ltd.`) | Wait until the `select[name='project']` options include an option whose `value` is the project code (stale = retry). Then the Innovators AJAX wait |
| 2b | Project Name (Client only) | Select **by value** in `select[name='project']` | `AMDOC112` (TC-NEW-022: `XMLCS154`) | Located by name because AJAX rebuilds the field and its id changes (`edit-project--<random>`) |
| 2c | Department (Org only) | Select by visible text in `By.id("edit-department")` | `IT Support` | |
| 3 | Idea Title | Type into `By.id("edit-title")` | `titlePrefix + "_" + 8 hex characters` | Max length 128 |
| 4a | Innovation Category (Client) | Click the label of the radio `input[name='innovation_category_cli'][value='<category>']`, i.e. `label[for='<that input's id>']` | `Project area specific Improvement` | Radios are hidden inputs styled through their label: click the label, read the input |
| 4b | Innovation Category (Org) | Click the label of `input[name='innovation_category_org'][value='<category>']` | `Good to have Generic solution/Tool/Framework` | Same as 4a |
| 5 | Domain | If the hidden value `By.id("edit-domain")` already contains the domain, skip this field (the form pre-fills a default domain). Otherwise type into `By.id("token-input-edit-domain")`, wait for a **displayed** suggestion whose trimmed text equals the domain in `.token-input-dropdown li, [class^='token-input-dropdown'] li`, and click it | `Solution Architecture & Technology Solutions` | Token-input widget |
| 6 | Brief description (Summary) | Type into `By.id("edit-summary")` | Client: `Automation P1 idea - brief description`; Org: `Automation P1 org idea - brief description` | Max length 500 |
| 7 | Innovation Details | Type into `By.id("edit-innovation-details")` | Client: `Automation P1 idea - potential solution`; Org: `Automation P1 org idea - potential solution` | Max length 500 |
| 8 | Confidential | Click `By.id("edit-confidential-yes")` or `By.id("edit-confidential-no")` (the input itself) | `No` (TC-LEGACY-082 overrides it to `Yes`) | Pre-selected as "No" when the form loads |
| 9 | Impact of Idea | Click `label[for='edit-impect-of-idea-<key>']`, then type the amount into `#edit-impect-of-idea-amount-<key>` and the description into `#edit-impect-of-idea-comment-<key>` | Client: key `cost-savings-for-customers`. Org: key `delivery-led-growth`. Both use amount `1000` and description `Automation P1 impact description` | `impect` is the app's spelling. No asterisk; does not gate Submit |
| 10 | Business Use Case (optional) | Only if `By.id("edit-business-use-case")` becomes visible (waits up to 10 s), type into it | `Automation P1 business use case` | When the field is hidden, this step costs the full 10 s wait |
| 11 | Submit Idea | Wait until `By.id("edit-submit-idea")` is enabled (up to 10 s), then click it | | The button starts disabled until the form is valid |

**Asserts after the fields:**

- "Setup / Step 11-12: Submit Idea is enabled" means `#edit-submit-idea` became enabled.
- After the click, `IdeaDetailPage.isLoaded` checks two things:
  - The URL matches `.*/innovation-portal/idea/(\d+)(?:[/?#].*)?$`.
  - `By.id("idea-info-page")` is visible.

  This is "Setup: '<title>' is submitted".
- **Idea id**: the only place the idea id exists is the detail URL. It is the digits group of the URL pattern above.

### F3. Approve an idea on the Evaluation page

Pages used: `IdeaDetailPage`, then `EvaluationPage`, then `IdeaDetailPage`. Data: `approval.json#approve`.

1. On the idea detail page, click the Evaluate link `a[href$='/evaluation']`.
2. **Assert** that the Evaluation page loads. The URL must match `.*/innovation-portal/idea/\d+/evaluation(?:[?#].*)?$` and the form `By.id("idea-evalute-page")` must be visible. `evalute` is the app's spelling.
3. Select **Approve**: click the radio input itself, `By.id("edit-idea-status-2")`. Clicking the label is intercepted by the input. Then wait until the radio is selected and no `.ajax-progress` element exists. Approve is selected by default, so clicking it again is harmless.
4. Fill Impact of Idea for key `cost-savings-for-customers`:
   - Click `label[for='edit-impect-of-idea-cost-savings-for-customers']`.
   - Type amount `1000` into `#edit-impect-of-idea-amount-cost-savings-for-customers`, then press TAB.
   - Type description `Automation P1 perceived benefit` into `#edit-impect-of-idea-comment-cost-savings-for-customers`, then press TAB.
5. Type the reason `Automation P1 approval` into `By.id("edit-admin-comment")` and press TAB. This field is "Please provide the reason to change the status of the idea."
6. Rate the 5 questions (0-4) with `[7, 7, 7, 7, 7]`: for each question, click `label[for='edit-ques-<question>-<value>']`. The rating radios are hidden inputs.
   - The question names come from `approval.json#ratingQuestions`: 0 Innovation Index, 1 Potential Business Impact, 2 Depth of Idea, 3 Feasibility of the Idea to Full solution, 4 End customer benefit / Domain / AI focus.
   - The weights are `TODO(Q22)`.
7. **Assert** "Submit Evaluation is enabled to approve": `By.id("edit-submit-evalution")` becomes enabled within 10 s.
   - The button stays disabled until a client-side check passes.
   - That check runs on change/keyup, which is why typed fields are blurred with TAB.
8. Click Submit Evaluation, then wait until the URL no longer contains `/evaluation`.
9. **Assert** that the idea detail page loads, and that the status text of `#idea-info-page .innovation_status_val .status_label` equals `Approved` (`expectedStatusAfterSubmit`).

The setup helpers fill these fields in a different order from TC-LEGACY-116. The helpers fill impact, then reason, then ratings. TC-LEGACY-116 fills impact and reason, asserts, and only then rates (see that test).

### F4. Open an idea from the Approval Dashboard (filter by title)

Page used: `ApprovalDashboardPage`, route `/innovation-portal/approval-dashboard`. Data: `approval.json#defaultStatusFilter` = `Pending Approval`.

1. Navigate directly to `<base>/approval-dashboard`.
2. **Assert** "Step 7: Approval Dashboard loads": the form `By.id("innovation-portal-approval-dashboard")` is visible.
3. **Assert** "Step 8: Status filter defaults to Pending Approval": the selected option text of `By.name("innovation_status")` equals `Pending Approval`.
4. Filter by title, as the test cases' step 9 describes. The tests do not use "My Evaluations" because Adtest04 is not the assigned evaluator (`TODO`, Q1/Q4).
   - Type the title into `By.name("innovation_title")`.
   - Remember the grid container `[data-drupal-selector='approval-dashboard-container']`.
   - Click `By.name("Filter")`.
   - Wait up to **30 s** (`LONG_TIMEOUT`) for the old container to go stale. The code comment says the Filter AJAX takes more than the default timeout on a dashboard with many ideas.
   - Wait for the container to become visible again.
   - Wait up to 10 s for at least one row `.approval_dashboard__row.result_row`. A timeout here is swallowed; the row lookups then return empty values.

   The filter controls are located by name because filtering rebuilds the form by AJAX with random id suffixes.
5. **Assert** "Step 9: Grid shows the idea (filtered by Idea Title)": some row contains a link `a[href*='/innovation-portal/idea/']` whose trimmed visible text equals the title.
6. Click that title link. It opens a **new window/tab** (`target="_blank"`), so wait for a new window handle and switch to it.
7. **Assert** "Step 10: Idea detail opens": `IdeaDetailPage.isLoaded`, as in F2.

Grid cells are `.result_row__col` and are read by position, counting from 0:

| Index | Column |
|---|---|
| 0 | Idea |
| 1 | Submitted on |
| 2 | Innovation type |
| 3 | Project Name |
| 4 | Domain |
| 5 | Evaluator |
| 6 | Status |
| 7 | Customer Status |

### F5. Open Customer Initiative Progress (CIP)

Page used: `CustomerInitiativeProgressPage`, route `/innovation-portal/actual_benefit_realization/<id>`. This page is for Client ideas only; Org ideas use `/actual_benefit_accelerators/<id>`, which these tests do not use.

1. Navigate to `<base>/actual_benefit_realization/<ideaId>`.
2. **Assert** "Step 8: CIP tracking form loads": `By.id("actual-benefit-realization-form")` is visible and the URL contains `/actual_benefit_realization/`.

### F6. Save a Customer Status path (one status after another)

Data: `tracking.json#progressUpdate`. Customer Status offers only the next allowed states of the current status, so each status is saved separately and the page is re-opened before the next one. For each status in the path:

1. Run F5 (re-open the progress page).
2. Read the Customer Status options: the option texts of `By.name("customer_status")`, excluding the empty-value `--Select--` placeholder.
   **Assert** "Step 9: Customer Status offers '<status>'". The failure message lists the offered options.
3. Select the status by visible text in `By.name("customer_status")`. Then wait until no `.ajax-progress` element exists; the AJAX shows or hides the dependent fields.
4. If "Actual Benefit Realization - Outcome" `By.name("abr_outcome")` is displayed **now** (no wait), select `Business improvement` by visible text.
5. If `label[for='edit-impect-of-idea-cost-savings-for-customers']` is displayed now (no wait), fill Impact of Idea. Saving `Implemented` needs at least one.
   - Tick the checkbox `#edit-impect-of-idea-cost-savings-for-customers` only if it is not already selected (click the label).
   - Type `1000` into `#edit-impect-of-idea-amount-cost-savings-for-customers`, then press TAB.
   - Type `Automation P1 actual benefit` into `#edit-impect-of-idea-comment-cost-savings-for-customers`, then press TAB.
6. Type the comment `Automation P1 customer progress update` into `By.name("abr_comment")`, then press TAB.
7. **Assert** "Step 9: Save is enabled for Customer Status '<status>'": `#actual-benefit-realization-form #edit-submit` becomes enabled within 10 s.
8. Click Save, then wait until the URL no longer contains `/actual_benefit_realization/`. Save redirects to the idea detail page.
   **Assert** "Step 9: Customer Status '<status>' is saved": `IdeaDetailPage.isLoaded`.

### F7. Switch users via a fresh browser session

ADFS single sign-on has no working logout, so the only way to drop the signed-in user is a new browser session. `BaseTest.restartBrowser()` does this:

1. Quit the driver.
2. Create a new driver with the same config and navigate to the base URL, which lands on ADFS again.
3. Then run F1 with the next user.

Only TC-NEW-022 uses this flow.

### F8. My Ideas lookup

Page used: `MyIdeasPage`, route `/innovation-portal/my-idea-list`.

1. Navigate directly to the route and wait for the first row `.My_ideas_dashboard .result_row` to be visible.
2. A row is found by its title link `a[href*='/innovation-portal/idea/']`, using whitespace-normalised `textContent` equality with the title.
3. Innovation type is the 3rd `.result_row__col` (index 2). Status is `.action_col > div`.
4. All text is read through `textContent`, not visible text. The grid is paged client-side (50 rows per page, the rest rendered but hidden), so an idea on a later page is still found without paging.

## 3. Tests

### TC-LEGACY-001: `SubmitClientIdeaTest#employeeSubmitsClientInitiativeIdea`

**DisplayName**: `TC-LEGACY-001 - Verify if an employee who is allocated to a project is able to submit the Client initiative innovation`

**Sheet wording**:

- Precondition: "Login as an employee user."
- Action: "Click On Submit Idea."
- Expected: "The user should be able to submit the Client initiative innovation."
- The sheet's test data is "ADTEST-08 SafetyChain Mobile". The code uses Adtest04 and customer AMDOCS.

**Data**:

- `submit.json`:
  - `user` = `${EMPLOYEE_USERNAME}` / `${EMPLOYEE_PASSWORD}`.
  - `titlePrefix` = `AUTO-P1-SUBMIT`.
  - `clientIdea`: every value listed in F2. Its `impacts[0]` is `cost-savings-for-customers` / `1000` / `Automation P1 impact description`, and `expectedInitialStatus` = `Pending Approval`.
- Only `domains[0]` and `impacts[0]` are used.

**Steps**:

1. F1, steps 1-7: sign in and assert steps 2, 3, 4-5, 5 and 6.
2. Click `+ Submit Idea`. **Assert** "Step 7: Submit Idea form loads at /idea-submit".
3. Select Innovation Type `Client Initiative`, then assert:
   - "Step 8: Client Initiative is selected": the selected text of `#innovation_type` equals `Client Initiative`.
   - "Step 8: Customer Name field appears": `select[name='customer_name']` is visible.
   - "Step 8: Project Name field appears": `select[name='project']` is visible.
   - "Step 8: Department is hidden": `#edit-department` becomes invisible within 10 s.
4. Fill F2 fields 2a, 2b, 3, 4a, 5, 6, 7, 8, 9 and 10 with the `clientIdea` values and the unique title.
5. **Assert** each value. Each of these is Step 9 unless marked otherwise:
   - The selected Customer text equals `AMDOCS`.
   - The selected Project `value` equals `AMDOC112`.
   - The title value equals the unique title.
   - The radio `input[name='innovation_category_cli'][value='Project area specific Improvement']` is selected.
   - The `#edit-domain` value contains `Solution Architecture & Technology Solutions`.
   - The Summary value equals the JSON summary.
   - The Innovation Details value equals the JSON innovation details.
   - `#edit-confidential-no` is selected.
   - `#edit-impect-of-idea-cost-savings-for-customers` is selected.
   - The `#edit-impect-of-idea-amount-cost-savings-for-customers` value is `1000`.
   - Step 10: the `#edit-business-use-case` value equals `Automation P1 business use case`. Note that this assertion runs even if the field was not shown and therefore not filled.
6. **Assert** "Step 11-12: No mandatory field remains empty and Submit Idea becomes enabled".
7. Click Submit Idea. **Assert** "Step 13-14: Form submits and the browser navigates to the idea detail page" (failure message includes the actual URL). **Assert** "Step 14: Detail page URL carries a new idea ID" (the id parsed from the URL is not empty).
8. Run F8 (open My Ideas), then assert:
   - "Step 15: Submitted idea appears in My Ideas".
   - Innovation type equals `Client Initiative`.
   - Status equals `Pending Approval`.

**Cleanup / side effects**: one new Pending Approval Client idea remains, with no cleanup (Q12).

**TODOs / open questions**: none in this method.

**Flakiness notes**: these are generic, from the page-object comments:

- ADFS redirect (30 s wait).
- Innovators AJAX.
- Project Name rebuilt by AJAX.
- Smooth-scroll click interception.

### TC-LEGACY-004: `SubmitClientIdeaTest#employeeSubmitsOrganisationInitiativeIdea`

**DisplayName**: `TC-LEGACY-004 - Verify if an employee who is allocated to a project is able to submit the Organization initiative innovation`

**Sheet wording**:

- Precondition: "Login as an employee user."
- Action: "Click On Submit Idea."
- Expected: "The user should be able to submit the organization initiative innovation."
- The sheet's test data is "ADTEST-14 Tibco iMany".

**Data**:

- `submit.json`:
  - `user`.
  - `titlePrefix` = `AUTO-P1-SUBMIT`.
  - `orgIdea`:
    - department `IT Support`;
    - category `Good to have Generic solution/Tool/Framework`;
    - domain `Solution Architecture & Technology Solutions`;
    - summary `Automation P1 org idea - brief description`;
    - details `Automation P1 org idea - potential solution`;
    - confidential `No`;
    - impact `delivery-led-growth` / `1000` / `Automation P1 impact description`;
    - business use case `Automation P1 business use case`;
    - expected status `Pending Approval`.

**Steps**:

1. F1 steps 1-7.
2. Click `+ Submit Idea`. **Assert** Step 7: the form loads.
3. Select Innovation Type `Organisation Initiative`, then assert:
   - "Step 8: Organisation Initiative is selected".
   - "Step 8: Department field appears": `#edit-department` is visible.
   - "Step 8: Customer Name is hidden": `select[name='customer_name']` becomes invisible.
   - "Step 8: Project Name is hidden": `select[name='project']` becomes invisible.
4. Fill F2 fields 2c, 3, 4b, 5, 6, 7, 8, 9 and 10.
5. **Assert** each value. Each of these is Step 9:
   - The selected Department text equals `IT Support`.
   - The title value equals the unique title.
   - `input[name='innovation_category_org'][value='Good to have Generic solution/Tool/Framework']` is selected.
   - The domain value contains the JSON domain.
   - The summary value equals the JSON summary.
   - The details value equals the JSON details.
   - Confidential No is selected.
   - `#edit-impect-of-idea-delivery-led-growth` is selected.
   - The amount is `1000`.

   Business Use Case is **not** asserted in this test.
6. **Assert** "Step 10: Submit Idea is enabled when the form is complete".
7. Click Submit Idea. **Assert** "Step 11: Form submits and the browser navigates to the idea detail page" and "Step 11: Detail page URL carries a new idea ID".
8. Run F8. **Assert** for Step 12:
   - The idea is listed.
   - Innovation type equals `Organisation Initiative`.
   - Status equals `Pending Approval`.

**Cleanup / side effects**: one Pending Approval Organisation idea remains, with no cleanup.

**TODOs / open questions**: none in this method.

### TC-LEGACY-007: `SubmitFormValidationTest#clientFormBlocksSubmitUntilMandatoryFieldsAreFilled`

**DisplayName**: `TC-LEGACY-007 - To verify the validation for Client Innovation Form`

**Sheet wording**:

- Precondition: "Login as an employee user."
- Action: "Click On Submit Idea."
- Expected: "The validations should work properly i.e. user should not be able to submit the form without mandatory fields and also mandatory fields should be shown with red asterisk."

**Data**:

- `validation.json`:
  - `user` = EMPLOYEE.
  - `titlePrefix` = `AUTO-P1-VALIDATION`.
  - `clientForm.innovationType` = `Client Initiative`.
  - `clientForm.mandatoryFields` = `title, innovationType, customerName, projectName, innovationCategory, domain, innovators, summary, confidential`.
  - `limits.titleMaxLength` = 128, `limits.summaryMaxLength` = 500, `limits.innovationDetailsMaxLength` = 500.
- The valid values are reused from `submit.json#clientIdea`.

**Steps**:

1. Sign in with the validation helper `openSubmitForm`. It runs F1 but does **not** assert that the Submit Idea link is displayed. It asserts steps 2, 3, 4-5 and 6, then clicks `+ Submit Idea` and asserts "Step 7: Submit Idea form loads".
2. Select `Client Initiative`, then assert:
   - Step 8: Client Initiative is selected.
   - Customer Name is displayed.
   - Project Name is displayed.
   - Department is hidden.
3. **Assert** "Step 10: Submit Idea is disabled while the mandatory fields are empty". This reads `#edit-submit-idea` `isEnabled()` immediately, without waiting.
4. Check the field limits (helper `assertFieldLimitsEnforced`). The code comment says one character over each limit is cut back to the limit.
   - Type 129 random lowercase alphanumeric characters into `#edit-title`.
   - Type 501 into `#edit-summary`.
   - Type 501 into `#edit-innovation-details`.
   - **Assert** the value lengths are exactly 128, 500 and 500. The messages are "Step 9: Idea Title rejects text beyond 128 characters", "Step 9: Brief description rejects text beyond 500 characters" and "Step 9: Innovation Details rejects text beyond 500 characters".
   - **Assert** "Step 10: Submit Idea stays disabled when only free-text fields are filled".
   - Clear the Summary: type "" into `#edit-summary`, so that Summary is the last missing field later.
5. Select Customer `AMDOCS` and Project `AMDOC112` (F2 2a/2b), then type the unique title. This replaces the 128-character title.
   **Assert** "Step 10: Submit Idea stays disabled while Innovation Category, Domain and Brief description are missing" (immediate read).
6. Fill the remaining fields:
   - Category `Project area specific Improvement`.
   - Domain.
   - Innovation Details (this replaces the 500-character text).
   - Confidential `No`.
   - Impact `cost-savings-for-customers` / `1000` / `Automation P1 impact description`.
   - Then record `submitBlockedWithoutSummary` by reading the button state immediately.
   - The code comment explains: Innovators is pre-filled and Confidential is pre-selected ("No") when the form loads, so they are never missing. Impact of Idea has no asterisk and does not gate Submit.
7. Type the Summary `Automation P1 idea - brief description`.
8. **Grouped assert** (`assertAll`, so every failure is reported) titled "Steps 10-11: Submit is blocked without mandatory fields and mandatory fields show a red asterisk":
   - "Step 10: Submit Idea stays disabled while only Brief description is missing": the value recorded in step 6 is true.
   - "Step 11: Submit Idea becomes enabled once every mandatory field holds valid data": `#edit-submit-idea` becomes enabled within 10 s.
   - For each mandatory field: "Step 11: Mandatory field '<field>' is shown with a red asterisk".
     - The asterisk is not markup. It is a `::after` pseudo-element with the background image `required.svg`.
     - The check runs `getComputedStyle(label, '::after').backgroundImage` and expects it to contain `required.svg`.
     - The label selectors per field are listed in Appendix A (IdeaSubmitPage required-marker labels).
9. The form is **not** submitted.

**Cleanup / side effects**: none. No idea is created.

**TODOs / open questions**:

- `TODO(TC-LEGACY-007)`: validation message texts, the "extra domains" limit and an invalid attachment file are not specified (Q9; `maxDomains` and `invalidAttachmentType` in `validation.json`), so they are not asserted.
- From the code comment: the form is not submitted because TC-LEGACY-001 covers the positive submit, and Q10/Q12 (submit vs stop, data cleanup) are still open.

### TC-LEGACY-008: `SubmitFormValidationTest#orgFormBlocksSubmitUntilMandatoryFieldsAreFilled`

**DisplayName**: `TC-LEGACY-008 - To verify the validation for Organization Innovation Form`

**Sheet wording**: same as TC-LEGACY-007, for the Organisation form.

**Data**:

- `validation.json`:
  - `user`.
  - `titlePrefix` = `AUTO-P1-VALIDATION`.
  - `orgForm.innovationType` = `Organisation Initiative`.
  - `orgForm.mandatoryFields` = `title, innovationType, department, innovationCategory, domain, innovators, summary, confidential`.
  - `limits` (as in 007).
- The valid values are reused from `submit.json#orgIdea`.

**Steps**: identical to TC-LEGACY-007, with these differences:

- Step 2 selects `Organisation Initiative` and asserts that Department is displayed and that Customer Name and Project Name are hidden.
- Step 5 selects Department `IT Support` and then types the unique title, and asserts the same "stays disabled" message.
- Step 6 uses category `Good to have Generic solution/Tool/Framework` and impact `delivery-led-growth` / `1000` / `Automation P1 impact description`.
- Step 7 types the summary `Automation P1 org idea - brief description`.
- Step 8 runs the grouped assert over the 8 Org mandatory fields.

**Cleanup / side effects**: none.

**TODOs / open questions**: `TODO(TC-LEGACY-008)`, the same as 007 (Q9). The test does not submit (Q10/Q12; TC-LEGACY-004 covers the submit).

### TC-LEGACY-029: `IdeaEvaluationTest#evaluatorSavesDraftWithoutImpactOfIdea`

**DisplayName**: `TC-LEGACY-029 - Verify Approve is blocked when Impact of Idea (option + $ + description) is missing; Save as Draft may retain partial data`

**Sheet wording**:

- Precondition: "Login as a committee approver."
- Action: "Click On Perceived Benefit Screen."
- Expected: "The evaluator should be able to skip Perceived Benefit Realization."
- The sheet's test data is "Committee Members - ADTEST10 (PEIP Vertical)". The code uses Adtest04.

**Data**:

- `approval.json`:
  - `evaluator` = `${EVALUATOR_USERNAME}` / `${EVALUATOR_PASSWORD}`.
  - `titlePrefix` = `AUTO-P1-APPROVAL`.
  - `defaultStatusFilter` = `Pending Approval`.
  - `draftWithoutImpact`: `ratings` = `[5, 5, 5, 5, 5]`, `reason` = `Automation P1 draft evaluation`, `expectedStatusAfterDraft` = `Pending Approval`.
- The setup idea is `submit.json#clientIdea`.

**Steps**:

1. F1 (all asserts, including the Submit Idea link).
2. Setup: F2 with a Client idea (opened via `HomePage.open()`). **Assert** that Submit Idea is enabled and the idea is submitted.
3. F4 with the title. **Assert** steps 7, 8, 9 and 10. The idea opens in a new window.
4. On the detail page, click Evaluate `a[href$='/evaluation']`. **Assert** "Step 11: Evaluation page opens".
5. Select Approve `#edit-idea-status-2` (F3 step 3).
6. Rate all 5 questions with `5`: `label[for='edit-ques-<q>-5']` for q = 0..4.
7. Type the reason `Automation P1 draft evaluation` into `#edit-admin-comment` and press TAB. Do **not** fill any Impact of Idea.
8. **Assert**:
   - "Step 11: Approve is selected": `#edit-idea-status-2` is selected.
   - "Step 11: Approve is blocked (Submit Evaluation disabled) while Impact of Idea is missing": `#edit-submit-evalution` is still not enabled after a **3 s** settle wait (`SHORT_TIMEOUT`).
   - "Step 11: Save as Draft is available for the partial evaluation": `By.id("edit-draft-evalution")` is visible and enabled.
9. Click Save as Draft (`#edit-draft-evalution`), then wait until the URL no longer contains `/evaluation`. **Assert**:
   - "Step 11: Save as Draft returns to the idea detail page".
   - "Step 11: Evaluation draft saved; status remains Pending Approval": the `#idea-info-page .innovation_status_val .status_label` text equals `Pending Approval`.
10. Click Evaluate again. **Assert** "Step 11: Evaluation page reopens".
11. **Grouped assert** "Step 11: The reopened draft keeps the partial evaluation data": for each q in 0..4, the input `#edit-ques-<q>-5` is selected. The message per question is "Draft keeps rating 5 for question <q>".
12. **Assert**:
    - "Step 11: Draft keeps the reason text": the `#edit-admin-comment` value equals `Automation P1 draft evaluation`.
    - "Step 12: The evaluator could skip Perceived Benefit Realization (draft saved without Impact of Idea)": no checkbox matching `#idea-evalute-page input[type='checkbox'][id^='edit-impect-of-idea-']` is selected.

**Cleanup / side effects**: one Client idea stays Pending Approval with a saved evaluation draft.

**TODOs / open questions**: `TODO(TC-LEGACY-029)`: step 9 'My Evaluations' is not exercised because Adtest04 is not the assigned evaluator (Q1/Q4).

**Flakiness notes**: the Approval Dashboard Filter AJAX can take more than 10 s (the code waits up to 30 s). The negative "Submit Evaluation disabled" check relies on a fixed 3 s settle window.

### TC-LEGACY-116: `IdeaEvaluationTest#evaluatorMustRateIdeaToApprove`

**DisplayName**: `TC-LEGACY-116 - Verify Approve requires ratings 1-10 on Innovation Index (25%), Potential Business Impact (35%), Depth (25%), Feasibility (15%) with overall score on 1-10 points scale`

**Sheet wording**:

- Precondition: "Login as an User."
- Action: "Approve the idea."
- Expected: "When evaluators are Approving on Idea then it will mandatory for the Approver to rate the Idea."

**Data**:

- `approval.json`:
  - `evaluator`.
  - `titlePrefix` = `AUTO-P1-APPROVAL`.
  - `defaultStatusFilter`.
  - `approve`: `ratings` = `[7, 7, 7, 7, 7]`, `impact` = `cost-savings-for-customers` / `1000` / `Automation P1 perceived benefit`, `reason` = `Automation P1 approval`, `expectedStatusAfterSubmit` = `Approved`.
- The setup idea is `submit.json#clientIdea`.

**Steps**:

1. F1.
2. Setup: F2 with a Client idea.
3. F4 (steps 7-10).
4. Click Evaluate. **Assert** "Step 11: Evaluation page opens".
5. Select Approve.
6. Fill Impact of Idea: click `label[for='edit-impect-of-idea-cost-savings-for-customers']`, type `1000` and press TAB, type `Automation P1 perceived benefit` and press TAB.
7. Type the reason `Automation P1 approval` and press TAB. No ratings yet.
8. **Assert**:
   - "Step 11: Approve is selected".
   - "Step 11: Impact of Idea option is ticked": `#edit-impect-of-idea-cost-savings-for-customers` is selected.
   - "Step 11: Rating is mandatory to approve (Submit Evaluation disabled without ratings)": still disabled after the 3 s settle wait.
9. Rate all 5 questions with `7`. **Assert** "Step 11: Submit Evaluation becomes enabled once every question is rated 1-10" (within 10 s).
10. Click Submit Evaluation and wait for the redirect. **Assert**:
    - "Step 12: Submit Evaluation returns to the idea detail page".
    - "Step 12: The rated idea is approved": the status equals `Approved`.

**Cleanup / side effects**: one Approved Client idea remains.

**TODOs / open questions**:

- `TODO(TC-LEGACY-116)`: step 9 'My Evaluations' is not exercised (Q1/Q4).
- `TODO(TC-LEGACY-116)`: the weighted overall score is not displayed and its formula is unknown (Q22). The app has **5** rating questions, not the 4 named in the title. The weights in `approval.json#ratingQuestions` are `TODO(Q22)`.

### TC-NEW-011: `IdeaEvaluationTest#newIdeasAreAssignedToDefaultEvaluators`

**DisplayName**: `TC-NEW-011 - Verify Client idea auto-assigns vertical default evaluator; Org assigns department default evaluator`

**Sheet wording**: the sheet has only the summary, with no precondition, action or expected result.

**Data**:

- `approval.json`: `evaluator`, `defaultStatusFilter` = `Pending Approval`, `titlePrefix` = `AUTO-P1-APPROVAL`. Two titles are generated.
- `submit.json`: `clientIdea` and `orgIdea` (department `IT Support`).
- `approval.json#defaultEvaluators` is informational only and is not read.

**Steps**:

1. F1.
2. Setup: F2 for the Client idea (title A), then F2 for the Org idea (title B). Both run in the same session.
3. Setup: open the Committee List at `<base>/committee` and wait for `By.id("committeeGrid")`. **Assert** "Setup: Committee List loads".
   - **Department default evaluator** for `IT Support`:
     - Among the cards `#committeeGrid .table-card`, take the one whose `.table-card-header h3` text equals `department` (case-insensitive).
     - In that card, take the row `tr.committee-row` whose `td.committee-name` text equals `IT Support` (case-insensitive).
     - The default evaluator is the `.member-name` text of the first `td.member-cell li` that contains a `.pill.lead` ("default Evaluator" pill) and has a non-empty name.
   - **DU default evaluators**: in the card headed `Delivery Unit (DU)`, apply the same rule to every row. The result is a set of names.
   - **Assert** "Setup: Committee List shows a default evaluator for department 'IT Support'" (the name is not empty) and "Setup: Committee List shows the Delivery Unit default evaluators" (the set is not empty).
4. Open the Approval Dashboard (F4 steps 1-3). **Assert** "Step 7: Approval Dashboard loads" and "Step 8: Status filter defaults to Pending Approval".
5. Filter by title B (Org). **Assert**:
   - "Step 9: Grid shows the new Organisation idea (filtered by title)".
   - "Step 11: Organisation idea is shown as Pending Approval": the column at index 6 equals `Pending Approval`.
   - "Step 12: Organisation idea is assigned to the default evaluator of department 'IT Support'": the column at index 5 equals the department default.
   - The idea is not opened (step 10), because the grid shows the evaluator.
6. Filter by title A (Client). **Assert**:
   - "Step 9: Grid shows the new Client idea (filtered by title)".
   - "Step 11: Client idea is shown as Pending Approval".
   - "Step 12: Client idea is assigned to a vertical (Delivery Unit) default evaluator": the Evaluator column value is contained in the DU default set. The failure message prints the actual value and the set.

**Cleanup / side effects**: two Pending Approval ideas remain (one Client, one Org).

**TODOs / open questions**:

- `TODO(TC-NEW-011)`: step 9 'My Evaluations' is not exercised (Q1/Q4).
- `TODO(TC-NEW-011)`: the exact vertical of the customer is unknown (Q23). Assert the specific vertical default once it is known. Today the test only checks "is one of the DU defaults".

### TC-NEW-013: `IdeaEvaluationTest#evaluatorCannotRejectWithoutComment`

**DisplayName**: `TC-NEW-013 - Verify Reject without comment is blocked`

**Sheet wording**: the sheet has only the summary.

**Data**:

- `approval.json`: `evaluator`, `defaultStatusFilter`, `titlePrefix` = `AUTO-P1-APPROVAL`, and `rejectWithoutComment.reason` = `Automation P1 rejection reason`.
- `rejectWithoutComment.expectedMessage` is `TODO(Q24)` and is not used.
- The setup idea is `submit.json#orgIdea`.

**Steps**:

1. F1.
2. Setup: F2 with an **Organisation** idea.
3. F4 (steps 7-10).
4. Click Evaluate. **Assert** "Step 11: Evaluation page opens".
5. Select Reject: click the input `By.id("edit-idea-status-3")`, then wait until it is selected and no `.ajax-progress` exists.
6. **Assert**:
   - "Step 11: Reject is selected".
   - "Step 11: The mandatory reason field is shown for Reject": `By.name("remarks")` is visible within 10 s.
     - Reject rebuilds "Additional Comments" by AJAX as "Please provide the reason for reject the idea", with a random id suffix, so the field is located by name.
   - "Step 11: Reject blocked without mandatory comment (Submit Evaluation disabled)": still disabled after the 3 s settle wait.
7. Type `Automation P1 rejection reason` into `[name='remarks']` and press TAB. **Assert**:
   - "Step 12: The reason field accepts the comment": the value equals the reason.
   - "Step 12: With the mandatory comment entered, Reject can be submitted (Submit Evaluation enabled)": enabled within 10 s.
8. Submit Evaluation is **not** clicked, so the idea is not rejected.

**Cleanup / side effects**: one Pending Approval Organisation idea remains.

**TODOs / open questions**:

- `TODO(TC-NEW-013)`: step 9 'My Evaluations' is not exercised (Q1/Q4).
- `TODO(TC-NEW-013)`: the final reject outcome and message are not specified (Q24).

### TC-LEGACY-081: `IdeaListVisibilityTest#approvedNonConfidentialIdeaIsShownInIdeaList`

**DisplayName**: `TC-LEGACY-081 - Verify if this is a dashboard which displays all ideas that have been approved and are not confidential.`

**Sheet wording**:

- Precondition: "Login as an User."
- Action: "Click On Idea List."
- Expected: "Such records should be shown here."
- The sheet's test data is "All Users. (Adtest 04,05,06 etc.)".

**Data**:

- `idealist.json`:
  - `evaluator`.
  - `titlePrefix` = `AUTO-P1-IDEALIST`.
  - `approvedNonConfidentialIdea`: `confidential` = `No`, `expectedVisible` = `true`.
- `submit.json#clientIdea`, with `confidential` overwritten by the value above.
- `approval.json#approve`.

**Steps** (the helper `signInAndApproveNewIdea`):

1. F1.
2. Setup: F2 with a Client idea and Confidential `No`. Here the helper always fills the Client branch. After filling, **assert** "Setup: Confidential 'No' is selected for '<title>'" (`#edit-confidential-no` is selected), then submit as in F2.
3. Setup: F3 (approve) **directly from the detail page reached after submit**, not via the dashboard. **Assert** "Setup: The idea is approved".
4. Action "Click On Idea List": navigate to `<base>/dashboard/all_idea_list`. **Assert** "Action: Idea List loads": `form.innovation-portal-dashboards` is visible and the URL contains `/dashboard/all_idea_list`.
5. Filter by title:
   - Type into `By.name("innovation_title")`.
   - Remember `form.innovation-portal-dashboards`.
   - Click `By.name("Filter")`.
   - Wait up to 10 s for the old form to go stale, then for the new form to become visible.
   - Filtering rebuilds the whole form by AJAX with new random id suffixes, so the controls are located by name and the form by class.
6. **Assert** "Expected result: the approved, non-confidential idea '<title>' is shown in the Idea List". This means `true` equals "some row `.all_idea_dashboard__row.result_row` has a link `a[href*='/innovation-portal/idea/']` whose trimmed visible text equals the title". The grid has no Confidential column, so visibility is checked by title.

**Cleanup / side effects**: one Approved, non-confidential Client idea remains. It will appear in the shared Idea List.

**TODOs / open questions**: `TODO(TC-LEGACY-081)`: the check runs in the evaluator's session. The sheet's Employee user is the same account (Adtest04) at run time. Use a separate employee account once one is configured (Q1). No shared fixture ideas are used (Q17).

### TC-LEGACY-082: `IdeaListVisibilityTest#approvedConfidentialIdeaIsNotShownInIdeaList`

**DisplayName**: `TC-LEGACY-082 - Verify dashboard displays all ideas that have been approved and confidential.`

**Sheet wording**:

- Precondition: "Login as an User."
- Action: "Click On Idea List."
- Expected: "Such records should not be shown here."

**Data**: `idealist.json#approvedConfidentialIdea`: `confidential` = `Yes`, `expectedVisible` = `false`. Otherwise the same as 081.

**Steps**: identical to TC-LEGACY-081, with these differences:

- Confidential `Yes` is set by clicking `#edit-confidential-yes`, and the test asserts it is selected.
- The final **assert**, "Expected result: the approved, confidential idea '<title>' is not shown in the Idea List", expects `false`: no row has that title after filtering.
- The row check runs right after the form re-renders, with no wait for rows.

**Cleanup / side effects**: one Approved, confidential Client idea remains.

**TODOs / open questions**: `TODO(TC-LEGACY-082)`, the same as 081 (Q1).

### TC-LEGACY-101: `CustomerInitiativeProgressTest#implementedAndRejectedAreEndStates`

**DisplayName**: `TC-LEGACY-101 - Verify Implemented locks further Customer Progress updates; POC Completed and Proposed also support Actual Benefits (Phase 5).`

**Sheet wording**:

- Precondition: "Login as an User."
- Action: "Click On Approval Dashboard."
- Expected: "'Rejected' and 'Implemented' will be end state for customer initiatives in system."
- The sheet's test data is "Committee Members - ADTEST09, ADTEST10 (PEIP Vertical)".

**Data**:

- `tracking.json`:
  - `evaluator`.
  - `titlePrefix` = `AUTO-P1-TRACKING`.
  - `implementedLock`: `actualBenefitStatuses` = `["Proposed to Customer", "POC Completed"]`, `statusPath` = `["Proposed to Customer", "Accepted", "Implemented"]`, `expectedOptions` = `["Implemented"]`.
  - `rejectedLock`: `statusPath` = `["Proposed to Customer", "Rejected"]`, `expectedOptions` = `["Rejected"]`.
  - `progressUpdate`: outcome `Business improvement`, impact `cost-savings-for-customers` / `1000` / `Automation P1 actual benefit`, comment `Automation P1 customer progress update`.
- `submit.json#clientIdea` and `approval.json#approve` are used for setup.

**Steps**:

1. F1.
2. Setup (step 7): F2 with a Client idea (idea X), then F3 from the detail page. **Assert** "The idea is approved". Keep X's id from the URL.
3. F5 for X (step 8). For each status in `Proposed to Customer`, `POC Completed`:
   - Select it in `[name='customer_status']` and wait for `.ajax-progress` to disappear. Nothing is saved.
   - **Assert** "Step 9: '<status>' supports Actual Benefits (Actual Benefit Realization - Outcome is shown)": `[name='abr_outcome']` is displayed now, with no wait.
4. F6 for X with the path `Proposed to Customer` → `Accepted` → `Implemented`. Each status is saved, and F6 re-opens the page first, which discards the unsaved selection from step 3.
5. End-state check for X: F5, then **assert**:
   - "Step 9: Customer Status 'Implemented' is kept after Save": the selected option text equals `Implemented`.
   - "Step 9: 'Implemented' is an end state: no further Customer Status update is offered": the options list, without the placeholder, equals exactly `["Implemented"]`. This is an ordered list comparison.
6. Setup: F2 and F3 again for a second Client idea (idea Y).
7. F6 for Y with the path `Proposed to Customer` → `Rejected`.
8. End-state check for Y: selected equals `Rejected`, and the options list equals exactly `["Rejected"]`.

**Cleanup / side effects**: two Approved Client ideas remain. One is locked at Customer Status Implemented, the other at Rejected.

**TODOs / open questions**: `TODO(TC-LEGACY-101)`: the test runs as Adtest04 because the sheet's committee member (Adtest08/09/10) is not configured (Q1/Q20).

**Flakiness notes**: this is the longest test (two full submit-and-approve setups plus five saves). The outcome and impact fields are checked with no wait (`isShownNow`) after the `.ajax-progress` wait.

### TC-LEGACY-107: `CustomerInitiativeProgressTest#customerStatusOffersAllOptions`

**DisplayName**: `TC-LEGACY-107 - Verify Customer Status options include In Progress, Proposed to Customer, Accepted, Rejected, On Hold, POC Completed, Implemented.`

**Sheet wording**:

- Precondition: "Login as an User."
- Action: "Click On Approval Dashboard."
- Expected: "'Customer Status' field will have following list of options In Progress, Proposed to Customer, Accepted, Rejected, On Hold, Implemented". The sheet's expected list omits POC Completed, which is in the title.

**Data**:

- `tracking.json#customerStatusList`:
  - `holdReason` = `Automation P1 hold reason`.
  - `expectedHoldStatus` = `Hold`.
  - `statusPath` = `["Proposed to Customer"]`.
  - `expectedOptions` = `["In Progress", "Proposed to Customer", "Accepted", "Rejected", "On Hold", "POC Completed", "Implemented"]`.
- `tracking.json#progressUpdate`.
- `approval.json#approve`.
- `submit.json#clientIdea`.

**Steps**:

1. F1.
2. Setup (steps 7-10): F2 with a Client idea, staying on its detail page. Keep the id.
3. Click Evaluate. **Assert** "Step 11: Evaluation page opens".
4. Select Hold: click the input `By.id("edit-idea-status-4")`, then wait until it is selected and AJAX has settled. Type `Automation P1 hold reason` into `#edit-admin-comment` and press TAB. **Assert**:
   - "Step 11: On Hold is selected".
   - "Step 11: Submit Evaluation is enabled for On Hold with a reason": enabled within 10 s.
5. Click Submit Evaluation and wait for the redirect. **Assert**:
   - "Step 11: Submit Evaluation returns to the idea detail page".
   - "Step 11: Status changes to On Hold": the status text equals `Hold`.
6. Step 12: run F3 from the same detail page. Evaluate is clicked again on the Hold idea. **Assert** "The idea is approved".
7. F5. Read the Customer Status options as `approvedOptions`.
8. F6 with the path `Proposed to Customer`.
9. F5 again. Read the options as `proposedOptions`.
10. **Assert** "Step 12: Customer Status offers exactly [expected list] (after approval: …; after Proposed to Customer: …)". The **set** of expected options must equal the **union** of `approvedOptions` and `proposedOptions`. Order and duplicates are ignored.
    - The test's `@Description` says that after approval the dropdown offers In Progress, Proposed to Customer, POC Completed and Implemented.
    - After Proposed to Customer is saved, it also offers Accepted, Rejected and On Hold.

**Cleanup / side effects**: one Client idea that was Hold, then Approved, then given Customer Status Proposed to Customer.

**TODOs / open questions**: `TODO(TC-LEGACY-107)`: the idea is opened right after submit, not from 'My Evaluations' on the Approval Dashboard, because Adtest04 is not the assigned evaluator (Q1/Q4).

### TC-NEW-022: `CustomerInitiativeProgressTest#projectManagerUpdatesCustomerProgressOnly`

**DisplayName**: `TC-NEW-022 - Verify Project Manager (Rel 5.1) can update Customer Progress only (not full evaluator approve privileges).`

**Sheet wording**: the sheet has only the summary, with no expected result (Q25).

**Data**:

- `tracking.json`:
  - `employeeOnly` = `${EMPLOYEE_ONLY_*}` (Adtest15).
  - `evaluator` = `${EVALUATOR_*}` (Adtest04).
  - `projectManager` = `${PROJECT_MANAGER_*}` (Adtest08).
  - `titlePrefix` = `AUTO-P1-TRACKING`.
  - `ciscoClientIdea`: the same values as `clientIdea`, except customer `Cisco Systems (India) Pvt. Ltd.` and project `XMLCS154`.
  - `progressUpdate`.
  - `projectManagerPrivileges`: `customerStatus` = `Proposed to Customer`, `comment` = `Automation P1 Project Manager customer progress update`.
- `approval.json#approve`.

**Steps**:

1. Session 1: F1 as **EMPLOYEE_ONLY (Adtest15)**, with all F1 asserts including the Submit Idea link.
2. Setup: F2 with the Cisco Client idea. Keep the id.
3. Run F7 (`restartBrowser`). Session 2: F1 as **EVALUATOR (Adtest04)**.
4. Navigate directly to `<base>/idea/<id>`. **Assert** "Setup: the evaluator opens idea <id>" (`IdeaDetailPage.isLoaded`).
5. F3 (approve). **Assert** "The idea is approved".
6. Run F7 again. Session 3: F1 as **PROJECT_MANAGER (Adtest08)**.
7. Navigate to `<base>/idea/<id>`. **Assert** "Summary: Project Manager opens the approved idea".
8. **Assert** "Summary: Project Manager has no Evaluate action on the idea detail page". The check:
   - Waits for `#idea-info-page` to be visible.
   - Waits for `document.readyState == 'complete'`.
   - Then, with no further wait, checks that no displayed `a[href$='/evaluation']` exists.
9. F5. Then fill the progress form:
   - Select Customer Status `Proposed to Customer` (with the AJAX wait).
   - If `abr_outcome` is shown, select `Business improvement`.
   - If the impact label is shown, fill the impact (`cost-savings-for-customers` / `1000` / `Automation P1 actual benefit`).
   - Type the comment `Automation P1 Project Manager customer progress update` and press TAB.
10. **Assert** "Summary: Save is enabled for Customer Status 'Proposed to Customer'". Click Save. **Assert** "Summary: Customer Progress is saved" (the detail page loads).
11. F5 again. **Assert** "Summary: Project Manager can update Customer Progress (the saved Customer Status is kept)": the selected option equals `Proposed to Customer`.
12. Navigate directly to `<base>/idea/<id>/evaluation`. **Assert** "Summary: Project Manager cannot open the evaluation (approve) page": `EvaluationPage.isLoaded` is false.
    - Without evaluator rights, the app shows "Access Denied" at the same URL.
    - The URL regex therefore matches, and the check then waits the full 10 s for `#idea-evalute-page`, which never appears.

**Cleanup / side effects**: one Approved Cisco Client idea with Customer Status Proposed to Customer remains. It was submitted by Adtest15.

**TODOs / open questions**: `TODO(TC-NEW-022)`:

- The sheet has no Expected Result (Q25), so the expected UI for "cannot approve" is still unspecified. The test checks that the Evaluate link is absent and that the Evaluation page does not load.
- There is no Project Manager account (Q3). By the ticket owner's interpretation in DEM-82, `projectManager` (Adtest08, a ClientProgressUpdate user on the Cisco route) plays the Project Manager for a fresh Cisco idea. That is why the idea must be on Cisco project `XMLCS154`.

**Flakiness notes**: three ADFS sign-ins, plus a deliberate 10 s wait for the negative Evaluation-page check.

### TC-NEW-035: `ApprovalDashboardAccessTest#employeeCannotOpenApprovalDashboardByDirectUrl`

**DisplayName**: `TC-NEW-035 - Verify Employee cannot open Approval Dashboard via direct URL`

**Sheet wording**: the sheet has only the summary.

**Data**:

- `security.json#employeeOnly` = `${EMPLOYEE_ONLY_USERNAME}` / `${EMPLOYEE_ONLY_PASSWORD}`, which is Adtest15, an account with no evaluator rights (Q4 was resolved in DEM-81).
- `security.json#directUrl` is informational only and is not read.

**Steps**:

1. F1 as EMPLOYEE_ONLY. The test asserts steps 2, 3, 4-5 and 6 but **not** the Submit Idea link.
2. Steps 7-9: navigate directly to `<base>/approval-dashboard`.
3. **Assert** "Step 9: Employee cannot open the Approval Dashboard via direct URL": `#innovation-portal-approval-dashboard` does **not** become visible within 10 s. The failure message includes the actual URL.

**Cleanup / side effects**: none.

**TODOs / open questions**: `TODO(TC-NEW-035)`: assert the specific denial behaviour once Q28 is answered. Observed on 2026-10-05: the "Access Denied | XORNET" page appears at the same URL, without the dashboard form.

## Appendix A. Selectors per page object

### LoginPage (ADFS)

| Element | Selector | Notes |
|---|---|---|
| Username | `#userNameInput` | Also used as the "page loaded" check |
| Password | `#passwordInput` | |
| Sign in | `#submitButton` | |
| Error text | `#errorText` | Non-blank text means sign-in failed. Read defensively because it can go stale during the redirect |
| (URL marker) | URL contains `/adfs/` | Still on ADFS means rejected |

### XornetGlobalScreenPage

| Element | Selector | Notes |
|---|---|---|
| Stay Here | `#stop` | Optional. The screen auto-redirects to Xornet home when its countdown ends. If the URL does not contain `/innovation-portal` after the click, re-open the base URL |

### HomePage (`/innovation-portal`)

| Element | Selector | Notes |
|---|---|---|
| Page heading | `#content h1.title` | "Loaded" check |
| + Submit Idea link | `a[href='/innovation-portal/idea-submit']` | |
| Logged-in user name | `#block-siteheadercustomblock .user.profile a` | Must be non-blank |

### IdeaSubmitPage (`/innovation-portal/idea-submit`)

| Element | Selector | Notes |
|---|---|---|
| Form | `#innovation-portal-idea-submit-form` | Plus the URL contains the route |
| Innovation Type | `#innovation_type` | `<select>`, by visible text |
| Customer Name | `select[name='customer_name']` | Client only |
| Project Name | `select[name='project']` | Client only. Located by name because AJAX rebuilds it and its id changes. Selected by `value` (project code) |
| Department | `#edit-department` | Org only |
| Idea Title | `#edit-title` | Max length 128 |
| Innovators | `#edit-innovators` | Disabled until the Innovators AJAX has loaded |
| Innovators token input | `#token-input-edit-innovators` | Exists once the Innovators AJAX has loaded |
| Domain token input | `#token-input-edit-domain` | Type here |
| Domain stored value | `#edit-domain` | Hidden; read with `value` |
| Domain suggestions | `.token-input-dropdown li, [class^='token-input-dropdown'] li` | Click the displayed item whose text equals the domain |
| Innovation Category (Client) | `input[name='innovation_category_cli'][value='<category>']` | Hidden radio. Click `label[for='<input id>']` |
| Innovation Category (Org) | `input[name='innovation_category_org'][value='<category>']` | Same |
| Brief description | `#edit-summary` | Max length 500 |
| Innovation Details | `#edit-innovation-details` | Max length 500 |
| Confidential Yes / No | `#edit-confidential-yes` / `#edit-confidential-no` | Clicked directly. "No" is the default |
| Impact option | `label[for='edit-impect-of-idea-<key>']` (click), `#edit-impect-of-idea-<key>` (read) | `impect` is the app's spelling |
| Impact amount | `#edit-impect-of-idea-amount-<key>` | Max length 10 (`validation.json`) |
| Impact description | `#edit-impect-of-idea-comment-<key>` | Max length 150 (`validation.json`) |
| Business Use Case | `#edit-business-use-case` | Optional; filled only if shown |
| Submit Idea | `#edit-submit-idea` | Disabled until the form is valid |

Required-marker labels (red asterisk = the `::after` `background-image` contains `required.svg`):

| Field key (`validation.json`) | Label selector | Notes |
|---|---|---|
| `title` | `label[for='edit-title']` | |
| `innovationType` | `label[for='innovation_type']` | |
| `customerName` | `label[for='edit-customer-name']` | |
| `projectName` | `label[for^='edit-project']` | Prefix match because of the AJAX id suffix `edit-project--<random>` |
| `department` | `label[for='edit-department']` | |
| `innovationCategory` | `.innovation_category_field .field_label` | |
| `domain` | `label[for='edit-domain']` | |
| `innovators` | `label[for='edit-innovators']` | |
| `summary` | `label[for='edit-summary']` | |
| `confidential` | `.confidential_field legend .fieldset-legend` | |

### IdeaDetailPage (`/innovation-portal/idea/<id>`)

| Element | Selector | Notes |
|---|---|---|
| URL pattern | `.*/innovation-portal/idea/(\d+)(?:[/?#].*)?$` | The idea id exists only in the URL |
| Detail form | `#idea-info-page` | |
| Innovation status | `#idea-info-page .innovation_status_val .status_label` | Trimmed text, for example `Pending Approval`, `Approved`, `Hold` |
| Evaluate link | `a[href$='/evaluation']` | The absence check waits for `document.readyState == 'complete'` and then checks with no visibility wait |

### MyIdeasPage (`/innovation-portal/my-idea-list`)

| Element | Selector | Notes |
|---|---|---|
| Rows | `.My_ideas_dashboard .result_row` | Rows have no ids |
| Title link (in row) | `a[href*='/innovation-portal/idea/']` | Matched by `textContent`, because of client-side paging (50 per page) |
| Columns | `.result_row__col` | Index 2 = Innovation type |
| Status | `.action_col > div` | |

### ApprovalDashboardPage (`/innovation-portal/approval-dashboard`)

| Element | Selector | Notes |
|---|---|---|
| Dashboard form | `#innovation-portal-approval-dashboard` | "Loaded" check. Absent for EMPLOYEE_ONLY |
| Idea Title filter | `[name='innovation_title']` | By name, because of the AJAX random id suffix |
| Innovation Status filter | `[name='innovation_status']` | Default `Pending Approval`. Option values are in `approval.json#statusFilterValues` (not used) |
| Filter button | `[name='Filter']` | |
| Grid container | `[data-drupal-selector='approval-dashboard-container']` | Replaced by AJAX. Wait up to 30 s for staleness |
| Rows | `.approval_dashboard__row.result_row` | |
| Title link | `a[href*='/innovation-portal/idea/']` | Matched by visible text. Opens a new window |
| Columns | `.result_row__col` | Index 5 = Evaluator, 6 = Status (see the table in F4) |

### CommitteePage (`/innovation-portal/committee`)

| Element | Selector | Notes |
|---|---|---|
| Grid | `#committeeGrid` | |
| Section cards | `#committeeGrid .table-card` | One per committee type |
| Section heading | `.table-card-header h3` | `Delivery Unit (DU)`, `department` (case-insensitive), `Core Committee` |
| Committee rows | `tr.committee-row` | |
| Committee name | `td.committee-name` | Compared case-insensitively with the department |
| Members | `td.member-cell li` | Some rows hold an empty member |
| Member name | `.member-name` | |
| Default evaluator pill | `.pill.lead` | The "default Evaluator" marker |

### EvaluationPage (`/innovation-portal/idea/<id>/evaluation`)

| Element | Selector | Notes |
|---|---|---|
| URL pattern | `.*/innovation-portal/idea/\d+/evaluation(?:[?#].*)?$` | |
| Form | `#idea-evalute-page` | `evalute` is the app's spelling |
| Approve | `#edit-idea-status-2` | Click the input, because the label click is intercepted. Selected by default |
| Reject | `#edit-idea-status-3` | |
| Hold ("On Hold") | `#edit-idea-status-4` | Only the reason is required |
| AJAX spinner | `.ajax-progress` | Wait until none exists after a status change |
| Reason | `#edit-admin-comment` | "Please provide the reason to change the status of the idea." Type, then TAB |
| Reject reason | `[name='remarks']` | Rebuilt by AJAX on Reject with a random id suffix |
| Rating | `label[for='edit-ques-<q>-<v>']` (click), `#edit-ques-<q>-<v>` (read) | q = 0..4, v = 1..10 |
| Impact option / amount / description | `label[for='edit-impect-of-idea-<key>']`, `#edit-impect-of-idea-amount-<key>`, `#edit-impect-of-idea-comment-<key>` | Type, then TAB |
| Any impact checkbox | `#idea-evalute-page input[type='checkbox'][id^='edit-impect-of-idea-']` | |
| Save as Draft | `#edit-draft-evalution` | App spelling `evalution` |
| Submit Evaluation | `#edit-submit-evalution` | Enabled only after the client-side check, which runs on change/keyup |

### CustomerInitiativeProgressPage (`/innovation-portal/actual_benefit_realization/<id>`)

| Element | Selector | Notes |
|---|---|---|
| Form | `#actual-benefit-realization-form` | Plus the URL contains the route |
| Customer Status | `[name='customer_status']` | Offers only the next allowed states. Ignore the empty-value `--Select--` option |
| Actual Benefit Realization - Outcome | `[name='abr_outcome']` | Shown only for some statuses (for example Proposed to Customer, POC Completed) |
| Impact option / amount / description | `label[for='edit-impect-of-idea-<key>']`, `#edit-impect-of-idea-<key>`, `#edit-impect-of-idea-amount-<key>`, `#edit-impect-of-idea-comment-<key>` | Filled only if shown. Implemented needs one |
| Comment | `[name='abr_comment']` | Type, then TAB |
| Save | `#actual-benefit-realization-form #edit-submit` | Disabled until valid. Redirects to the idea detail page |
| AJAX spinner | `.ajax-progress` | |

### IdeaListPage (`/innovation-portal/dashboard/all_idea_list`)

| Element | Selector | Notes |
|---|---|---|
| Form | `form.innovation-portal-dashboards` | By class, because AJAX rebuilds the whole form with new ids. Wait for staleness after Filter |
| Idea Title filter | `[name='innovation_title']` | |
| Filter button | `[name='Filter']` | |
| Rows | `.all_idea_dashboard__row.result_row` | No Confidential column |
| Title link | `a[href*='/innovation-portal/idea/']` | Matched by visible text |

## Appendix B. Test data

### Env-var placeholders

JSON string values of the form `"${NAME}"` are replaced when a file is loaded (`JsonUtils`). A system property with the same name wins over the environment variable. A missing or empty value throws "Environment variable 'NAME' referenced in <file> is not set".

Placeholders are resolved for the **whole file** on every read, so every variable a file references must be set. For example, any tracking test needs the `EVALUATOR_*`, `PROJECT_MANAGER_*` and `EMPLOYEE_ONLY_*` variables. Never commit or log passwords: `UserCredentials.toString()` prints only the username.

| Variable | Used in | Run-time account (`run-tests.sh` defaults) |
|---|---|---|
| `EMPLOYEE_USERNAME` / `EMPLOYEE_PASSWORD` | `submit.json`, `validation.json`, `idealist.json` (`employee`, not read), `users.json` | `ADTest\Adtest04`. The password is set in the local env file (ask the QA manager) |
| `EVALUATOR_USERNAME` / `EVALUATOR_PASSWORD` | `approval.json`, `idealist.json`, `tracking.json` | Defaults to `EMPLOYEE_*` (Adtest04) |
| `EMPLOYEE_ONLY_USERNAME` / `EMPLOYEE_ONLY_PASSWORD` | `security.json`, `tracking.json` | `ADTest\Adtest15`. The password defaults to `EMPLOYEE_PASSWORD` |
| `PROJECT_MANAGER_USERNAME` / `PROJECT_MANAGER_PASSWORD` | `tracking.json` | `ADTest\Adtest08`. The password defaults to `EMPLOYEE_PASSWORD` |

Other settings that `run-tests.sh` passes to Maven: `ENV` (default `test`), `BASE_URL` (default empty, which means `test.baseUrl`), `BROWSER` (default `chrome`), and `HEADED=true` to turn headless off. All accounts share one password (`docs/MIGRATION.md`).

### submit.json

| Key | Purpose |
|---|---|
| `user` | EMPLOYEE credentials for TC-LEGACY-001 and TC-LEGACY-004 |
| `titlePrefix` | `AUTO-P1-SUBMIT` |
| `clientIdea` | Valid Client Initiative values (F2). Also reused as setup data by validation 007, approval 029/116/NEW-011, idealist 081/082 and tracking 101/107. Fields: `innovationType`, `customerName`, `projectName` (project code = option value), `innovationCategory` (radio value), `domains[0]`, `summary`, `innovationDetails`, `confidential`, `impacts[0]` (`key`, `label`, `amount`, `description`), `businessUseCase`, `expectedInitialStatus` |
| `orgIdea` | Valid Organisation Initiative values. Has `department` instead of customer and project, and impact key `delivery-led-growth`. Reused by validation 008 and approval NEW-011/NEW-013 |
| `routes` | Informational (`/idea-submit`, `/my-idea-list`, `/idea/(\d+)`). Not read |

`testCaseId` and `impacts[].label` are informational in every file.

### validation.json

| Key | Purpose |
|---|---|
| `user` | EMPLOYEE credentials |
| `titlePrefix` | `AUTO-P1-VALIDATION` (typed, never submitted) |
| `limits` | `titleMaxLength` 128, `summaryMaxLength` 500 and `innovationDetailsMaxLength` 500 are **asserted**. `businessUseCaseMaxLength` 500, `impactAmountMaxLength` 10, `impactDescriptionMaxLength` 150, `maxAttachments` 3 and `maxAttachmentSizeMb` 15 are loaded but not asserted. `maxDomains` is `TODO(Q9)` |
| `allowedAttachmentTypes` | Not used (attachments are not tested) |
| `invalidAttachmentType` | `TODO(Q9)`. Not used |
| `clientForm` | `innovationType` and `mandatoryFields` (9 keys mapped to the asterisk labels) for TC-LEGACY-007. `expectedMessages` is `TODO(Q9)` |
| `orgForm` | The same for TC-LEGACY-008 (8 keys: `department` instead of `customerName` and `projectName`) |

### approval.json

| Key | Purpose |
|---|---|
| `evaluator` | EVALUATOR credentials |
| `titlePrefix` | `AUTO-P1-APPROVAL` |
| `defaultStatusFilter` | `Pending Approval`, the expected default of the dashboard status filter |
| `statusFilterValues`, `evaluationStatusValues` | Informational option values (for example Approve = `2`, matching `#edit-idea-status-2`). Not read |
| `ratingQuestions` | Informational names of rating questions 0-4. The weights are `TODO(Q22)` |
| `approve` | `ratings` [7×5], `impact`, `reason`, `expectedStatusAfterSubmit` = `Approved`. Used by TC-LEGACY-116 and by every approve setup (F3). `businessUseCase` is not used. `expectedOverallScore` is `TODO(Q22)` |
| `draftWithoutImpact` | `ratings` [5×5], `reason`, `expectedStatusAfterDraft` = `Pending Approval` (TC-LEGACY-029) |
| `rejectWithoutComment` | `reason` (TC-NEW-013). `expectedMessage` is `TODO(Q24)` |
| `defaultEvaluators` | Informational notes for TC-NEW-011 (`clientVertical` is `TODO(Q23)`). Not read |

### idealist.json

| Key | Purpose |
|---|---|
| `employee` | EMPLOYEE credentials. Not read, but resolved on load |
| `evaluator` | Signs in, submits and approves |
| `titlePrefix` | `AUTO-P1-IDEALIST` |
| `routes.ideaList` | Informational (`/dashboard/all_idea_list`) |
| `approvedNonConfidentialIdea` | `confidential` = `No`, `expectedVisible` = `true` (TC-LEGACY-081) |
| `approvedConfidentialIdea` | `confidential` = `Yes`, `expectedVisible` = `false` (TC-LEGACY-082) |

### tracking.json

| Key | Purpose |
|---|---|
| `evaluator`, `projectManager`, `employeeOnly` | Credentials (see the env-var table) |
| `titlePrefix` | `AUTO-P1-TRACKING` |
| `routes` | Informational: `clientProgress` = `/actual_benefit_realization/{id}`, `orgProgress` = `/actual_benefit_accelerators/{id}` |
| `customerStatusOptions`, `orgIdeaStatusOptions`, `actualBenefitOutcomes` | Informational lists. Not read. `customerStatusOptions` also contains `No Action` |
| `progressUpdate` | `actualBenefitOutcome` = `Business improvement`, `impact` (`cost-savings-for-customers` / `1000` / `Automation P1 actual benefit`), `comment`. Used for every Customer Status save (F6, TC-NEW-022) |
| `implementedLock` | TC-LEGACY-101: `actualBenefitStatuses`, `statusPath` ending in Implemented, `expectedOptions` = `["Implemented"]` |
| `rejectedLock` | TC-LEGACY-101: `statusPath` ending in Rejected, `expectedOptions` = `["Rejected"]` |
| `customerStatusList` | TC-LEGACY-107: `holdReason`, `expectedHoldStatus` = `Hold`, `statusPath` = `["Proposed to Customer"]`, `expectedOptions` (7 options) |
| `ciscoClientIdea` | TC-NEW-022: Client idea on customer `Cisco Systems (India) Pvt. Ltd.`, project `XMLCS154` |
| `projectManagerPrivileges` | TC-NEW-022: `customerStatus` = `Proposed to Customer`, `comment`. `note` records the DEM-82 decision (Q25/Q3) |

### security.json

| Key | Purpose |
|---|---|
| `employeeOnly` | EMPLOYEE_ONLY credentials (Adtest15) |
| `directUrl` | Informational: `route` = `/approval-dashboard`. `expectedBehaviour` is `TODO(Q28)` (observed: Access Denied page, title 'Access Denied \| XORNET', same URL). Not read |

### Open questions referenced above

These are summarised from `docs/testcases/p1-analysis.md`.

| Q | Topic |
|---|---|
| Q1 | Role accounts. Today EMPLOYEE, EVALUATOR and ADMIN are all Adtest04 |
| Q3 | No Project Manager or head-role accounts |
| Q4 | A non-evaluator employee (now `EMPLOYEE_ONLY` = Adtest15). Adtest04 is not the assigned evaluator, so "My Evaluations" is not used |
| Q9 | Max domains, validation message texts, invalid attachment |
| Q10 | Submit or stop when Submit becomes enabled (validation tests) |
| Q12 | Data cleanup policy for created ideas |
| Q17 | Idea List fixture ideas. Fresh ideas are used instead |
| Q20 | Committee member account for tracking; the Implemented lock |
| Q22 | Rating weights and overall score formula (5 questions, no score shown) |
| Q23 | Which vertical/DU default evaluator a Client idea gets |
| Q24 | Exact Reject outcome/message |
| Q25 | TC-NEW-022 expected result, PM account and idea |
| Q28 | Expected denial behaviour for the direct Approval Dashboard URL |
| Q29 | Feature tag names |
