# Innovation Portal: behaviour a new framework must handle

Lessons from automating and running the P1 suite (DEM-61 to DEM-82, verified on the live test environment up to 2026-10-05). Each item says what the app does, what breaks if it is ignored, and how this repo handles it. Port the handling, not just the selectors.

## Access and sign-in

| Behaviour | Impact | Handling here |
|-----------|--------|---------------|
| The portal is only reachable over FortiClient VPN, with an untrusted TLS certificate | without them nothing loads, or the browser stops at a certificate error | `acceptInsecureCerts=true` in the browser capabilities |
| Every portal URL redirects to ADFS sign-in (`adtestadfs.adtest.com`, page title "Sign In") when there is no session | tests must sign in first | `LoginPage`: `#userNameInput`, `#passwordInput`, `#submitButton`. It waits until the URL leaves `/adfs/` or `#errorText` shows text |
| After sign-in some accounts get the **Xornet Global Screen**, which redirects to the Xornet home page when its countdown ends | the test lands outside the portal | `XornetGlobalScreenPage.dismissIfShown()`: click `#stop` (Stay Here), then reopen the base URL if the URL is not `/innovation-portal` |
| **No working logout.** `/innovation-portal/user/logout` is "Page not Found"; `/user/logout` only goes to the Global Screen and ADFS single sign-on logs the same user back in | a second user cannot sign in within the same browser | start a **new browser session** per user (`BaseTest.restartBrowser()`), used by TC-NEW-022 |
| Users without rights get an **"Access Denied \| XORNET"** page at the same URL (no redirect, no HTTP error surfaced to the browser) | a URL check alone cannot detect denial | check that the page's main form is absent (e.g. `#innovation-portal-approval-dashboard`, `#idea-evalute-page`) |
| Role differences seen live: Adtest04 = evaluator + System Admin (sees everything, can evaluate any idea); Adtest15 = employee (no Approval Dashboard, gets Access Denied there); Adtest08 = committee member / progress updater (sees the Approval Dashboard, has no Evaluate on ideas it does not evaluate, can edit Customer Initiative Progress) | tests that check rights need the right account | accounts are mapped by environment variables (see `DEPENDENCIES.md`) |

## Layout and scrolling

| Behaviour | Impact | Handling here |
|-----------|--------|---------------|
| CSS shows a **"not supporting Landscape mode"** overlay on landscape screens up to 898 px wide. Headless Chromium reports an 800x600 screen whatever the window size | in headless runs every element is covered and nothing is clickable | headless Chrome/Edge get `--screen-info={1920x1080}` (the window size) |
| The site uses Bootstrap 5 `scroll-behavior: smooth` | WebDriver's own scroll is animated, so the click lands before the element is in view ("element click intercepted") | `BasePage` scrolls with `scrollIntoView({block: 'center', behavior: 'instant'})` before every click and type |
| Radios and checkboxes are hidden inputs styled through their `<label>` | clicking the input fails or does nothing | click `label[for='<input id>']`; read state from the input (`isSelected`) |

## AJAX (Drupal)

| Behaviour | Impact | Handling here |
|-----------|--------|---------------|
| The Submit Idea form loads the **Innovators** list by AJAX on load and after a Customer change. Until then the field is disabled ("Please wait fetching innovators list") and the page does not scroll | clicks below the fold are intercepted; this sometimes takes more than 10 s (flaky TC-LEGACY-082 / TC-LEGACY-116, DEM-77) | `IdeaSubmitPage.waitForInnovatorsLoaded()`: `#edit-innovators` enabled and `#token-input-edit-innovators` present |
| Selecting a Customer rebuilds **Project Name** by AJAX with a new id | an id locator goes stale | locate by name (`select[name='project']`), then wait until the expected project value appears |
| The **Approval Dashboard Filter** replaces the whole grid container by AJAX. On the test environment it takes more than 10 s (the dashboard lists every Pending Approval idea, and each run adds more) | waits time out before the grid refreshes (DEM-75) | wait up to 30 s for `[data-drupal-selector='approval-dashboard-container']` to go stale, then for rows |
| Dashboard and Idea List filter controls get a **random id suffix** after AJAX (e.g. `edit-export-btn--N_ub8jsGQ-4`) | id locators fail after the first filter | locate filter controls by `name` (`innovation_title`, `innovation_status`, `Filter`, `Export`) |
| On the Evaluation page, choosing **Reject** rebuilds "Additional Comments" as **"Please provide the reason for reject the idea"** (`name="remarks"`, random id). Submit Evaluation stays disabled until it is filled | typing into the status-change comment (`#edit-admin-comment`) alone leaves Submit disabled (DEM-76) | `EvaluationPage.rejectReason = By.name("remarks")` |
| Status radios on the Evaluation page trigger AJAX | reading or typing during the rebuild goes stale | after a status click, wait until it is selected and `.ajax-progress` is gone |
| Text fields validate on blur | Submit/Save stays disabled until focus leaves the field | type, then send TAB |

## Data and grids

| Behaviour | Impact | Handling here |
|-----------|--------|---------------|
| Grid rows (Approval Dashboard, Idea List, My Ideas) have no ids | rows can't be addressed directly | find the row by its title link `a[href*='/innovation-portal/idea/']`, then read cells by position (`.result_row__col`) |
| Idea ids only exist in URLs (`/innovation-portal/idea/<id>`) | the id is needed for direct routes | parse it from the URL after submitting |
| The idea title link on the Approval Dashboard opens a **new window** (`target="_blank"`) | the driver stays on the old window | switch to the new window handle |
| Each run creates ideas (unique titles from `titlePrefix` + random suffix) and nothing cleans them up | dashboards grow and get slower over time | an open data-lifecycle question (Q12/Q15). Consider a cleanup or an isolated test tenant in the new setup |
| Direct routes used: `/idea-submit`, `/my-idea-list`, `/approval-dashboard`, `/dashboard/all_idea_list`, `/idea/<id>`, `/idea/<id>/evaluation`, `/actual_benefit_realization/<id>` (Customer Initiative Progress) | | build them from the base URL; never hard-code the host |

## Known app/spec gaps (tests not on this branch)

| Area | Observed | Status |
|------|----------|--------|
| IP / Accelerator radio ("mark this idea as a Xoriant's Intellectual property?") | shown on the Evaluation page while the idea is Pending Approval; the spec says only at Implemented / POC Completed | TC-NEW-025/026 fail, Defect DEM-78 (Q26) |
| Approver's Confidential choice | the Evaluation page has no Confidential field; on the admin edit page (`/admin-idea-edit/<id>`) the Confidential radios are disabled; it can only be set at submit | TC-LEGACY-089 not automatable (Q19) |
| Accelerator flag in reports | Idea Reports grid and its Excel export (79 columns) have no Accelerator/IP column; the IP Dashboard lists IP submissions, not marked ideas | TC-NEW-027 not automatable (Q27) |
