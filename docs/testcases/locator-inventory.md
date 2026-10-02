# Locator and Test Data Inventory - P1 Test Cases

- **Ticket:** DEM-61 (IPS 3.2). Input: `docs/testcases/p1-analysis.md` (pages from Summary 1, questions Q1-Q30).
- **Source of every locator:** the live `test` environment (`https://10.20.3.66/innovation-portal`), crawled on 2026-10-02 with the repo's own `DriverFactory` (Chrome, `acceptInsecureCerts=true`). The crawl was read-only: no form was submitted and no idea was changed. Each locator below was seen in the rendered DOM. Nothing was copied from the sheet without being checked against the DOM.
- **Account used:** `EMPLOYEE_USERNAME` = `ADTest\Adtest04` (per the ticket owner; the sheet calls it "Admin user"). Pages that only differ for other roles (Employee Adtest15, Committee member Adtest08, Project Manager, heads) were **not** crawled with those roles. See [Blockers](#blockers).
- **Scope:** no tests and no page-object code. This is the design input for the page objects and the JSON files in `src/test/resources/testdata/`.

Legend: **Best** = locator to use in the page object. **Fallback** = second choice if Best breaks. **Dyn** = dynamic behaviour to wait for. A **GAP** row means there is no stable locator. Each GAP comes with a suggested developer request.

## Cross-cutting findings

| # | Finding | Impact | Recommendation |
|---|---|---|---|
| X1 | **Headless Chrome was blocked (fixed in DEM-62).** In `--headless=new` the portal showed only "Currently we are not supporting Landscape mode...". Cause: the portal CSS rule `@media screen and (max-device-width: 898px) and (orientation: landscape)`; headless Chromium reports an 800x600 screen whatever `--window-size` is. | Every UI test failed headless / in CI. | `DriverFactory` now passes `--screen-info={<windowSize>}` in headless Chrome/Edge, so the screen matches the window (1920x1080). |
| X2 | The app is Drupal. Form controls have stable `id`, `name` and `data-drupal-selector`. The `id` gets a random suffix after an AJAX rebuild (for example `#edit-project` becomes `#edit-project--XU3HMh4cKhs` once a Customer is chosen). | An `id` locator breaks on AJAX-refreshed fields. | Use `[name='...']` or `[data-drupal-selector='...']` for AJAX-dependent fields (marked **Dyn**). |
| X3 | Radios and checkboxes (Innovation Category, Impact of Idea, rating 1-10, Confidential, My evaluations, committee members) are visually hidden inputs styled through their `<label>`. | Clicking the `<input>` throws `ElementNotInteractableException`. | Click `label[for='<input id>']`; read state from the `<input>` (`isSelected()`). |
| X4 | Grids (My Ideas, Approval Dashboard, Idea List, Idea Reports) are `div` rows with no row or column ids. Columns are positional, and the status cell class contains spaces (`class="Pending Approval"`). | Positional column reads are fragile. | Locate a row by its title link (`a[href*='/innovation-portal/idea/<id>']`), then read sibling cells. Ask devs for `data-testid` per column and `data-idea-id` per row. |
| X5 | Route-based action links (Evaluate, Change the Evaluator, Delete, progress pages) have no ids but stable `href` patterns. | Stable as long as the routes don't change. | Use `a[href$='/evaluation']` style CSS; prefer navigating to the route directly where the test allows it. |
| X6 | `JsonUtils` resolves every `${VAR}` in a file when the file is loaded and throws if any is unset. | A JSON file that mixes several roles' credentials fails for a test that only needs one role. | Keep only the roles a feature needs in that feature's file (done below). Don't add more roles to `users.json`. |
| X7 | The shared `test` env already holds dozens of automation ideas ("BB6 Client Auto ...", "TC-EXT-024 ...", "Draft Idea ...") from earlier runs. | Lookups by title must use unique titles. Grids are long, so filter first. | Unique title prefix + `RandomDataUtils` suffix (`titlePrefix` in the JSON); always use the grid's `Idea Title` filter before reading rows (Q12). |

---

## 1. LoginPage (ADFS) - new

Host: `https://adtestadfs.adtest.com/adfs/ls/...` (title `Sign In`). Reached by redirect from any portal URL (via `https://10.20.3.66/saml_login?destination=...`).

| Element | Best | Fallback | Notes |
|---|---|---|---|
| Username | `#userNameInput` | `input[type='email'][name='UserName']` | Format must be `domain\user` or `user@domain` (client-side check). Plain `input[name='UserName']` is ambiguous: a hidden `#userNameInputOptionsHolder` has the same name |
| Password | `#passwordInput` | `input[name='Password']` | Value never logged |
| Sign in button | `#submitButton` | `span.submit[role='button']` | Resolves Q13 (button not given in sheet) |
| Error message | `#errorText` | `#error` | Text seen: `Enter your user ID in the format "domain\user" or "user@domain".` |
| Keep me signed in | `#kmsiInput` | `input[name='Kmsi']` | Inside `#kmsiArea` (hidden; not needed) |
| Form | `#loginForm` | - | Wait condition for "page loaded" |

Dyn: after Sign in, wait until the URL no longer contains `/adfs/` **or** `#errorText` has text. With Adtest04, Sign in lands directly on `/innovation-portal?check_logged_in=1` (no MFA prompt, no Global Screen).

## 2. XornetGlobalScreenPage - new

Route `https://10.20.3.66/global-screen` (title `Global Screen`). It did **not** appear after login for Adtest04. Keep it as an optional "if shown" step.

| Element | Best | Fallback | Notes |
|---|---|---|---|
| Stay Here | `#stop` | `.xor-span #stop` | Clickable `div`, text " Stay Here" |
| Countdown seconds | `#countr` | `.flex-f #countr` | Showed `4` at capture; redirects to Xornet home when it reaches 0 |

Dyn: wait for either `#stop` (click it, then navigate to the base URL) or a URL containing `/innovation-portal` (Q13).

## 3. HomePage (portal landing) - existing, extend

Route `/innovation-portal` (title `Innovation Portal | XORNET`, `h1.title` = `Innovation Portal`). Today's `HomePage` only checks `#userNameInput`; that check belongs in `LoginPage`.

| Element | Best | Fallback | Notes |
|---|---|---|---|
| Page heading | `#content h1.title` | `main#content h1` | Text `Innovation Portal` |
| + Submit Idea | `a[href='/innovation-portal/idea-submit']` | `#content a[href$='/idea-submit']` | GAP (no id) - request `data-testid="nav-submit-idea"` |
| Approval Dashboard | `a[href='/innovation-portal/approval-dashboard']` | - | GAP (no id) |
| My Ideas | `a[href='/innovation-portal/my-idea-list']` | - | GAP (no id) |
| Idea Reports | `a[href='/innovation-portal/idea-report']` | - | GAP (no id) |
| Intellectual Property Dashboard | `a[href='/innovation-portal/accelerator-dashboard']` | - | GAP (no id) |
| Committee List | `a[href='/innovation-portal/committee']` | - | Committee page lists DU / Department / Core committees (Q23) |
| Submit Problem Statement / Problem Statements / Quarterly Awards / All Updates | `a[href$='/add-problem-statement']`, `a[href$='/problem-statement-dashboard']`, `a[href$='/innovation-rnr/qawards']`, `a[href$='innovation-updates-view']` | - | Not used by P1. "All Updates" has a relative href without a leading `/` |
| Logged-in user name | `#block-siteheadercustomblock .user.profile a` | `header .user.profile a.p1` | Text = display name (Adtest04 shows "Shamli Joshi"); href `/user/<uid>`. GAP: class-based only, request `data-testid="current-user"` (Q13 step 6) |
| Logout | GAP | GAP | No logout link in the header markup (Q30). `/innovation-portal/user/logout` is "Page not Found" |

## 4. IdeaSubmitPage - new

Route `/innovation-portal/idea-submit` (title `Idea Submit | XORNET`). Form `#innovation-portal-idea-submit-form`. Drafts reopen at `/innovation-portal/idea-submit/<id>`.

| Element | Best | Fallback | Notes |
|---|---|---|---|
| Idea title | `#edit-title` | `input[name='title']` | maxlength 128 |
| Innovation Type | `#innovation_type` | `select[name='innovation_type']` | Options (value = text): `Client Initiative` (default), `Organisation Initiative` - British spelling (Q8) |
| Customer Name (Client only) | `#edit-customer-name` | `select[name='customer_name']` | 59 customers + `--Select--`. **Dyn:** hidden for Org; choosing a customer reloads Project via AJAX |
| Project Name (Client only) | `select[name='project']` | `#project_field_container_div select` | **Dyn:** `id` becomes `edit-project--<random>` after the Customer AJAX. Never use `#edit-project`. Options value = project code (for example `AMDOCS` gives only `AMDOC112`) |
| Department (Org only) | `#edit-department` | `select[name='department']` | Hidden for Client. Options: Business System Group, Facility Management, Finance, HR, IT Support, Legal, Marketing, RESOURCE MANAGEMENT GROUP, Recruitment, SEPG, Sales-Presales, Sysnet, Training, US Staffing (Q6) |
| Innovation Category - Client | `label[for='edit-innovation-category-cli-<slug>']` | `input[name='innovation_category_cli'][value='<text>']` | Wrapper `#edit-innovation-category-cli--wrapper`. Values: `Client Specific Solution beyond project area`, `Project area specific Improvement`. Hidden radio (X3) |
| Innovation Category - Org | `label[for='edit-innovation-category-org-<slug>']` | `input[name='innovation_category_org'][value='<text>']` | Wrapper `#edit-innovation-category-org--wrapper`. Values: `High Impact Tech/Domain Framework/PoC`, `Solution to a Critical organization issue`, `Good to have Generic solution/Tool/Framework`, `Client Specific Solution beyond project area`, `Project area specific Improvement` |
| Domain (typing box) | `#token-input-edit-domain` | - | jQuery token-input autocomplete (not a select). Visible box maxlength 20; hidden value `#edit-domain` (maxlength 110) was pre-filled with `Solution Architecture & Technology Solutions`. GAP: the suggestion dropdown only renders while typing and was not captured. Request `data-testid` on suggestions. Max count of Domains unknown (Q8, Q9) |
| Domain (stored value) | `#edit-domain` | `input[name='domain']` | Hidden; read-only check of what was selected |
| Innovators | `#token-input-edit-innovators` | - | Token-input; hidden `#edit-innovators` pre-filled with the logged-in user's id. Not in sheet |
| Problem Statement | `#token-input-edit-problem-statement` | - | Token-input; optional; not in sheet |
| Briefly describe the idea | `#edit-summary` | `textarea[name='summary']` | maxlength 500 (help text "Max length: 500 characters"). The sheet's "Brief description" |
| Briefly describe your potential solution | `#edit-innovation-details` | `textarea[name='innovation_details']` | maxlength 500. The sheet's "Innovation Details" |
| Attachments | `#edit-innovation-file-upload` | `input[name='files[innovation_file][]']` | `sendKeys(path)`, auto-uploads. Max 3 files, 15 MB each; types pdf, png, jpg, jpeg, msg, xls, xlsx, txt, ppt, pptx, csv, doc, docx, oft (Q9) |
| Confidential Yes / No | `#edit-confidential-yes` / `#edit-confidential-no` | `input[name='confidential'][value='Yes'/'No']` | Visible radios (clickable directly) |
| Impact of Idea checkbox (x4) | `label[for='edit-impect-of-idea-<key>']` | `input[name='impect_of_idea_<key_>']` | App spells it **impect**. Keys: `cost-avoidance-for-customers`, `cost-savings-for-customers`, `increasing-mindshare-of-customers`, `delivery-led-growth` |
| Impact Amount($) | `#edit-impect-of-idea-amount-<key>` | `input[name='impect_of_idea_amount_<key_>']` | **Dyn:** visible only when its checkbox is ticked. maxlength 10; numeric format not enforced in markup (Q8) |
| Impact Description | `#edit-impect-of-idea-comment-<key>` | `textarea[name='impect_of_idea_comment_<key_>']` | **Dyn:** as above. maxlength 150 |
| Business Use Case | `#edit-business-use-case` | `textarea[name='business_use_case']` | Visible for both types; maxlength 500 |
| Draft | `#edit-save-draft-idea` | `button[name='op'][value='Draft']` | Disabled until valid |
| Submit Idea | `#edit-submit-idea` | `button[name='op'][value='Submit Idea']` | **Dyn:** disabled on load; wait for `isEnabled()` |
| Cancel | `#innovation-portal-idea-submit-form a[href='/innovation-portal']` | - | |
| Required-field marker | GAP | GAP | No `required`, `aria-required` or `form-required` class on any Submit-form field (the Evaluation and IP forms do have them). Request `aria-required="true"` (Q7) |
| Field validation messages | GAP | GAP | Not seen without submitting. Capture with an HTML snippet or a validation run (Q9) |
| Post-submit success / redirect | GAP | GAP | Not observed (read-only crawl). Expected `/innovation-portal/idea/<id>` (Q11) |

Visibility verified by switching `#innovation_type`: Client shows Customer, Project and the Client categories; Org shows Department and the Org categories and removes Project.

## 5. IdeaDetailPage - new

Route `/innovation-portal/idea/<id>` (title `Idea | XORNET`, `h1.title` = `Idea`). Form `#idea-info-page`.

| Element | Best | Fallback | Notes |
|---|---|---|---|
| Value fields | `#idea-info-page .<field>_val` | - | `innovation_type_val` (1st = Title, 2nd = Type), `project_val`, `customer_name_val` (Customer, or Department for Org), `category_val`, `domain_val`, `gdate_val`, `innovators_val`, `summary_val`, `details_val`, `innovator_business_use_case_val`. GAP: Title and Type share a class, so read by index or by the preceding `.label` text. Request distinct classes or `data-testid` |
| Innovation Status | `#idea-info-page .innovation_status_val .status_label` | `.innovation_status_val` | Text `Pending Approval` (class `status1`), `Approved` (`status2`) |
| Confidential / IP (Yes/No) | `#idea-info-page .Confidential_val` | - | Two elements share the class: 1st = Confidential, 2nd = Intellectual Property (IP). Same GAP as above |
| Evaluate | `a[href$='/evaluation']` | `a[href='/innovation-portal/idea/<id>/evaluation']` | |
| Change the Evaluator | `a.edit-evalutor` | `a[href$='/change-evaluator']` | Opens a Drupal AJAX modal (`data-dialog-type="modal"`) |
| Delete | `a[href*='/innovation-portal/unblishidea/']` | - | Only on Pending ideas (admin). Route spelled `unblishidea` (unpublish) |
| Admin edit | `a[href*='/innovation-portal/admin-idea-edit/']` | - | Icon link, no text |
| Customer Initiative Progress (Client) | `a[href*='/actual_benefit_realization/']` | - | Only on approved Client ideas |
| Organisation Initiative Progress (Org) | `a[href*='/actual_benefit_accelerators/']` | - | Only on approved Org ideas |
| Add Intellectual Property | `a[href*='/innovation-portal/accelerator-form/']` | - | On an approved and Implemented Client idea. Related link `a[href*='/all-accelerators/']` "View all the received intellectual property (IP)" (Q26) |
| Evaluation summary | `.innovation_idea_evaluation__header` | - | Text `Idea Evaluation Details - <evaluator>`; ratings shown as stars `.que_container__rating_star.selected`. No overall weighted score visible (Q22) |
| Idea id | parse from URL `/idea/(\d+)` | - | No id element on page |

## 6. MyIdeasPage - new

Route `/innovation-portal/my-idea-list` (title `My Ideas | XORNET`). Form `#innovation-portal-my-ideas-dashboard`. No filter controls on this page.

| Element | Best | Fallback | Notes |
|---|---|---|---|
| Rows | `.My_ideas_dashboard .result_row` | `.results-body .all_idea_dashboard__row` | No row id (X4) |
| Row by title | `.result_row a[href*='/innovation-portal/idea/']` with text = title | - | Drafts link to `/idea-submit/<id>` instead |
| Columns (in order) | `.result_row__col` nth 1-7 | - | Idea/Problem Statement, Submitted On (`.i_submitted_on`), Innovation type, Project Name, Domain, Category, Action/Status (`.action_col`) |
| Status | `.result_row .action_col > div` | - | Text, for example `Pending Approval`, `Approved`. Q11: a freshly submitted idea shows `Pending Approval` |

## 7. IdeaListPage - new (Q17 resolved)

Route `/innovation-portal/dashboard/all_idea_list` (title `Idea List | XORNET`). The landing page's customer links use `?customer=<name>`. `/innovation-portal/idea-list` is "Page not Found".

| Element | Best | Fallback | Notes |
|---|---|---|---|
| Idea Title filter | `#edit-innovation-title` | `input[name='innovation_title']` | |
| Innovation Type filter | `#edit-innovation-type` | `select[name='innovation_type']` | `--Select Value--`, Client Initiative, Organisation Initiative |
| Domain filter | `#edit-domain` | `input[name='domain']` | Same id as the Submit form's hidden domain (different page) |
| Customer Name filter | `#edit-customer` | `input[name='customer']` | |
| Submission Date From / To | `#datepicker` / `#datepicker1` | `input[name='form_date']` / `input[name='to_date']` | jQuery UI datepicker; date format unknown |
| Filter / Clear All | `#edit-filter` / `#edit-clear-btn` | `button[name='Filter']` | **Dyn:** AJAX reload of the grid; wait for staleness of the first row |
| Rows / title link | `.all_idea_dashboard__row.result_row` / `.result_row a[href*='/idea/']` | - | Columns: Title, Innovation type, Innovation Category, Customer Name, Submitted on, Domain, Description, Overall Rating. There is no Confidential column, so the TC-LEGACY-081/082/089 oracle is "title present / absent" after filtering by title |

## 8. ApprovalDashboardPage - new

Route `/innovation-portal/approval-dashboard` (title `Approval Dashboard | XORNET`). Form `#innovation-portal-approval-dashboard`.

| Element | Best | Fallback | Notes |
|---|---|---|---|
| Idea Title filter | `#edit-innovation-title` | `input[name='innovation_title']` | maxlength 128 |
| Innovation Type | `#edit-innovation-type` | `select[name='innovation_type']` | |
| Innovation Status | `#edit-innovation-status` | `select[name='innovation_status']` | value=text: `1`=Pending Approval (default), `2`=Approved, `3`=Rejected, `4`=Hold, `6`=All |
| Customer Status | `#edit-customer-status` | `select[name='customer_status']` | All, In Progress, Proposed to Customer, Accepted, Rejected, On Hold, Implemented, POC Completed, No Action (Q21) |
| Submission Date From / To | `#datepicker` / `#datepicker1` | name `form_date` / `to_date` | |
| Filter | `#edit-filter` | `button[name='Filter']` | **Dyn:** AJAX |
| Clear All | `#edit-clear-btn` | `button[name='Clear All']` | **Dyn:** AJAX |
| Export | `#edit-export-btn` | `button[name='Export']` | File download |
| My evaluations | `label[for='edit-my-evaluations']` | `#edit-my-evaluations` (state) | Toggle styled as a slider; **Dyn:** AJAX on change |
| Grid container | `#approval_dashboard_container` | `[data-drupal-selector='approval-dashboard-container']` | Replaced on AJAX; re-find after filtering |
| Rows / title link | `.approval_dashboard__row.result_row` / `.result_row a[href*='/idea/']` | - | Columns: Idea, Submitted on, Innovation type, Project Name, Domain, Evaluator, Status, Customer Status. **Evaluate / Delete / Change the Evaluator are not in the grid.** They are on the Idea detail page (section 5) |
| Access denied (TC-NEW-035) | GAP | GAP | Not observable with Adtest04 (has access). Needs an Employee-only account (Q4, Q28) |

## 9. EvaluationPage - new

Route `/innovation-portal/idea/<id>/evaluation` (title `Idea Evaluation | XORNET`). Form `#idea-evalute-page` (app spelling). Page note: "Approval: It is necessary to respond to each rating in order to approve the suggestion and fill perceived benefit. Rejection: It is necessary to respond to below comment in order to reject the suggestion."

| Element | Best | Fallback | Notes |
|---|---|---|---|
| Status Approve / Reject / Hold | `label[for='edit-idea-status-2']` / `-3` / `-4` | `input[name='idea_status'][value='2'/'3'/'4']` | Wrapper `#edit-idea-status--wrapper` (`aria-required`). **Dyn:** AJAX on change (shows Business Use Case and reason fields) |
| Mark as Intellectual Property (Yes / No) | `label[for='edit-mark-accelerator-yes']` / `-no` | `input[name='mark_accelerator']` | "Do you want to mark this idea as a Xoriant's Intellectual property?" This is the Accelerator/IP radio for TC-NEW-025/026 (Q26). Visibility per role and status not verified |
| Rating question N (0-4), value 1-10 | `label[for='edit-ques-<N>-<value>']` | `input[name='ques_<N>'][value='<value>']` | **5 questions**, not 4: 0 Innovation Index, 1 Potential Business Impact, 2 Depth of Idea, 3 Feasibility, 4 End customer benefit / Domain / AI focus (Q22) |
| Impact of idea (perceived benefit) | same ids as Submit form: `edit-impect-of-idea-<key>`, `-amount-<key>`, `-comment-<key>` | name variants | Note: "You are required to select at least one of the four alternatives". This is the sheet's "Perceived Benefit" (Q16) |
| Applicable Industry | `#edit-applicable-industry` | `textarea[name='applicable_industry']` | **Dyn:** depends on status |
| Business Use Case | `#edit-business-use-case` | `textarea[name='business_use_case']` | Required (`aria-required`); **Dyn:** depends on status |
| Additional Comments | `#edit-remarks` | `textarea[name='remarks']` | |
| Reason to change status | `#edit-admin-comment` | `textarea[name='admin_comment']` | Required; this is the Reject comment for TC-NEW-013 (Q24). Error text not captured |
| Save as Draft | `#edit-draft-evalution` | `button[name='op'][value='Save as Draft']` | App spelling `evalution` |
| Submit Evaluation | `#edit-submit-evalution` | `button[name='op'][value='Submit Evaluation']` | Disabled until valid. **Dyn:** wait for enabled |
| Cancel | `#idea-evalute-page a[href^='/innovation-portal/idea/']` | - | |
| Approver "Confidential Yes/No" | GAP | GAP | No such control on the Evaluation page (only IP Yes/No). Q19 stays open |
| Duplicate id | - | - | `#innovation_rating_evaluation` appears twice on this page; don't use it |

## 10. ChangeEvaluatorDialog - new (component)

Opened from `a.edit-evalutor` as a modal. The same form renders standalone at `/innovation-portal/idea/<id>/change-evaluator` (title `Change the Evaluator`). Form `#change-evalutor-form`.

| Element | Best | Fallback | Notes |
|---|---|---|---|
| Search member | `#emp-search-input` | `input[name='emp_search']` | |
| Committee member checkbox | `label[for='edit-select-evalutors-<code>']` | `input[name='select_evalutors[<CODE>]']` | Codes seen: `test0008`/`TEST0008` (adtest08), `test0009`, `test0010`, plus numeric ids. Wrapper `#edit-select-evalutors--wrapper` |
| Core committee checkbox | `label[for='edit-select-core-evalutors-<code>']` | `input[name='select_core_evalutors[<CODE>]']` | Wrapper `#edit-select-core-evalutors--wrapper` |
| Submit | `#edit-submit-data` | `#change-evalutor-form button[name='op']` | |

Dyn: inside the modal, scope locators to `.ui-dialog #change-evalutor-form` (assumed jQuery UI dialog, not verified - GAP: capture the modal container).

## 11. CustomerInitiativeProgressPage (Client) - new, and OrganisationInitiativeProgressPage (Org) - new

Client route `/innovation-portal/actual_benefit_realization/<id>` (title `Customer Initiative Progress Tracking`, form `#actual-benefit-realization-form`). Org route `/innovation-portal/actual_benefit_accelerators/<id>` (title `Organisation Initiative Progress Tracking`, form `#actual-benefit-realization-accelerator-form`). The Org page was not in p1-analysis Summary 1; it is proposed here as a separate page object.

| Element | Best | Fallback | Notes |
|---|---|---|---|
| Customer Status (Client) / Idea Status (Org) | `#edit-customer-status` | `select[name='customer_status']` | Required. Client idea already `Implemented`: the only option is `Implemented` (the lock, Q20). Org options: In Progress, On Hold, Rejected, POC Completed |
| Customer POC / Designation / LOB (Client) | `#edit-customer-poc` / `#edit-customer-designation` / `#edit-customer-lob` | name `customer_POC` / `customer_designation` / `customer_LOB` | Disabled on the Implemented idea |
| Actual Benefit Realization - Outcome (Client) | `#edit-abr-outcome` | `select[name='abr_outcome']` | Required; options Business improvement, Framework enhancement, Process improvement. **Dyn:** visible per Customer Status |
| MBRs and QBRs with customer (Client) | `#edit-mbrs-and-qbrs-with-customer` | name `mbrs_and_qbrs_with_customer` | Visible checkbox |
| Impact of idea (both) | `edit-impect-of-idea-<key>` / `-amount-` / `-comment-` | name variants | Amount and Description become required when checked |
| Sales person details (Client) | `#edit-sales-person-response` then `#edit-sales-person-name`, `#edit-sales-person-code`, `#edit-sales-person-attachment-upload` | name variants | **Dyn:** revealed by the checkbox |
| Comment | `#edit-abr-comment` | `textarea[name='abr_comment']` | |
| Save | `#edit-submit` | `button[name='op'][value='Save']` | Generic id; scope to the form id |
| Lock / validation message | GAP | GAP | Not observed without saving (Q20) |

## 12. AcceleratorDashboardPage (Intellectual Property Dashboard) - new, and IntellectualPropertyFormPage - new

Dashboard route `/innovation-portal/accelerator-dashboard` (title `Intellectual Property Dashboard`, form `#innovation-accelerator-dashboard`).

| Element | Best | Fallback | Notes |
|---|---|---|---|
| IP Title / Idea title / Submitted By filters | `#edit-accelerator-search` / `#edit-idea-search` / `#edit-submitted-by-search` | name `accelerator_search` / `idea_search` / `submitted_by_search` | |
| Practice | `#edit-practice` | `select[name='practice']` | Digital & Platforms, Data & AI, X-Labs, Cloud & Infrastructure, Gen AI, Security, MSAL, SFDC |
| Status | `#edit-status-filter` | `select[name='status_filter']` | Pending, Approved, Rejected (IP status, not Innovation or Customer status; Q26) |
| Date From / To | `#datepicker` / `#datepicker1` | | |
| Filter / Clear All / Export | `#edit-filter` / `#edit-clear-btn` / `#edit-export-excel` | | |
| Grid | `#innovation-accelerator-dashboard table` | - | Real `<table>` with headers: Intellectual Property Title, Intellectual Property for Idea, Practice, Domain, Submitted By, Submitted On, Approver, Status. GAP: no row ids |

IP form route `/innovation-portal/accelerator-form/<id>` (title `Intellectual Property Form`, form `#accelerator-form`), opened via "Add Intellectual Property" on an approved, Implemented Client idea.

| Element | Best | Fallback | Notes |
|---|---|---|---|
| IP Title | `#edit-accelerator-name` | `input[name='accelerator_name']` | Required |
| IP Type | `#edit-accelerator-type` | `select[name='accelerator_type']` | Framework, Tool, Libraries, Best Practice |
| Customer Name / Project Name | `#edit-customer-name` / `select[name='project']` | | Project disabled until a Customer is chosen. **Dyn:** AJAX, see X2 |
| Practice | `#edit-select-practice` | `select[name='select_practice']` | Required |
| Applicable Domains | `#token-input-edit-applicable-domains` | hidden `#edit-applicable-domains` | Token-input |
| Applicable Technologies | `#edit-applicable-tenologies` | `input[name='applicable_tenologies']` | App spelling |
| Business Impact | `#edit-business-impact` | `select[name='business_impact']` | Improved Productivity, Customer Satisfaction, Cost Reduction |
| Description / Overview | `#edit-description` | `textarea[name='description']` | Required |
| Attachments | `#edit-attachments-upload` | `input[name='files[attachments][]']` | |
| Customization Required Yes / No | `#edit-customization-required-yes` / `-no` | name `customization_required` | `#edit-customization-required-detail` revealed on Yes |
| Submit | `#accelerator-form #edit-submit` | `button[name='op']` | Disabled until valid |

## 13. IdeaReportsPage - new

Route `/innovation-portal/idea-report` (title `Idea Reports`, form `#innovation-portal-idea-report-dashboard`).

| Element | Best | Fallback | Notes |
|---|---|---|---|
| Idea Title | `#edit-innovation-title` | `input[name='innovation_title']` | |
| Overall Rating | `#edit-overall-rating-filter` | `input[name='overall_rating_filter']` | Free text |
| Innovation Type | `#edit-innovation-type` | `select[name='innovation_type']` | All, Client Initiative, Organisation Initiative |
| Innovation Status | `#edit-innovation-status` | `select[name='innovation_status']` | Same values as the Approval Dashboard (1/2/3/4/6) |
| Delivery Unit (DU) | `#edit-vertical-filter` | `select[name='vertical_filter']` | Asia Pacific, BFS, Cloud and Infrastructure, Delivery Enablement, ... |
| Department | `#edit-department-filter` | `select[name='department_filter']` | Option values have leading/trailing spaces (`' HR '`), so select by visible text |
| Customer Status | `#edit-customer-status-filter` | `select[name='customer_status_filter']` | Same list as the Approval Dashboard |
| Date From / To | `#datepicker` / `#datepicker1` | | |
| Filter / Clear All / Export | `#edit-filter` / `#edit-clear-btn` / `#edit-export-btn` | | Export = Excel download (TC-NEW-027) |
| Pagination | `#edit-previous-page` / `#edit-next-page` | name `previous_page` / `next_page` | AJAX |
| Rows | `.innovation_reports_dashboard__row.result_row` | - | Columns: Title, Innovation type, Project Name, Innovators, Domain, Overall Rating, DU/Department, Submitted on, Status, Customer Status. GAP: no IP/Accelerator column seen (Q27) |

---

## Developer requests (stable-locator gaps)

| # | Page | Element | Request |
|---|---|---|---|
| D1 | All | Headless "Landscape mode" overlay | Disable for automation, or document the trigger (X1) |
| D2 | Home | Landing tiles | `data-testid="nav-<name>"` on each tile link |
| D3 | Home | Logged-in user, Logout | `data-testid="current-user"`; a visible logout link (or the logout route) |
| D4 | Idea Submit | Required markers, validation messages | `aria-required="true"` on mandatory fields; error elements with `data-testid="error-<field>"` |
| D5 | Idea Submit / IP form | Token-input suggestion list (Domain, Innovators, Problem Statement, Applicable Domains) | `data-testid` on the suggestion dropdown and its items |
| D6 | Idea Submit / IP form | Project select | Keep a stable `id` across AJAX rebuilds (no random suffix) |
| D7 | Idea detail | Title/Type and Confidential/IP share a class | Distinct classes or `data-testid` per field |
| D8 | Grids (My Ideas, Approval, Idea List, Reports, IP) | Rows and columns | `data-idea-id` per row, `data-testid="col-<name>"` per cell |
| D9 | Evaluation | Duplicate `#innovation_rating_evaluation` | Make ids unique |
| D10 | Change Evaluator modal | Modal container | `data-testid="change-evaluator-dialog"` |

## Test data design

One JSON file per feature tag (proposed tags from Q29) in `src/test/resources/testdata/`. Rules:

- Credentials only as `${ENV_VAR}`, and only the roles that feature needs (X6). The env var names follow the existing `EMPLOYEE_*` pattern.
- Values come from the live DOM (option texts, routes, limits) or the sheet. Unknown values use the string `TODO(<TestCaseID or Q#>)` so they fail loudly instead of passing silently.
- Unique per-run data (titles) = `titlePrefix` + `RandomDataUtils` suffix at runtime, never stored.

| File | Feature tag | Test cases | Credentials (env vars) |
|---|---|---|---|
| `submit.json` | `submit` | TC-LEGACY-001, TC-LEGACY-004, TC-NEW-011 (submit part) | `EMPLOYEE_USERNAME` / `EMPLOYEE_PASSWORD` |
| `validation.json` | `validation` | TC-LEGACY-007, TC-LEGACY-008 | `EMPLOYEE_*` |
| `approval.json` | `approval` | TC-LEGACY-029, TC-LEGACY-116, TC-NEW-011, TC-NEW-013 | `EVALUATOR_USERNAME` / `EVALUATOR_PASSWORD` (committee member, sheet Adtest08) |
| `idealist.json` | `idealist` | TC-LEGACY-081, TC-LEGACY-082, TC-LEGACY-089 | `EMPLOYEE_*`, `EVALUATOR_*` |
| `tracking.json` | `tracking` | TC-LEGACY-101, TC-LEGACY-107, TC-NEW-022 | `EVALUATOR_*`, `PROJECT_MANAGER_USERNAME` / `PROJECT_MANAGER_PASSWORD` |
| `accelerator.json` | `accelerator` | TC-NEW-025, TC-NEW-026, TC-NEW-027 | `ADMIN_USERNAME` / `ADMIN_PASSWORD`, `VERTICAL_HEAD_*`, `PRACTICE_HEAD_*`, `DEPARTMENT_HEAD_*` |
| `security.json` | `security` | TC-NEW-035 | `EMPLOYEE_ONLY_USERNAME` / `EMPLOYEE_ONLY_PASSWORD` (a non-evaluator employee; Adtest04 can open the dashboard) |

The role-to-account mapping (which ADTest user goes into which env var) is in the ticket owner's credentials attachment on DEM-61, chosen per test case. It is never copied into the repo.

## Blockers

These stop specific tests and need the ticket owner or the developers:

1. **Role coverage (Q1, Q3, Q4, Q25):** only Adtest04 was crawled. Employee-only (denial, TC-NEW-035), Committee member, Project Manager and head-role views are unverified. Most locators are the same pages, but visibility of Evaluate/Delete/IP controls per role is not.
2. **Headless overlay (X1):** fixed in DEM-62 (`--screen-info` in `DriverFactory`).
3. **Post-submit and validation behaviour (Q9, Q10, Q11, Q24):** success URL/text and error messages need a real submit, which this read-only ticket didn't do.
4. **Data lifecycle and fixtures (Q12, Q15, Q17 fixtures, Q26 ideas):** policy for creating and cleaning ideas, and the known approved/Implemented/POC Completed ideas to use.
5. **Oracles still undefined in the sheet (Q16, Q18, Q19, Q20, Q22, Q23, Q27, Q28):** see the status table in `p1-analysis.md` Summary 4.
