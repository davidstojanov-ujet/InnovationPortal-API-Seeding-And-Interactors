# AI conventions and few-shot examples

Read [PROJECT_CONTEXT.md](PROJECT_CONTEXT.md) first. This file shows how those rules look in real code. Use it as the reference when you turn a manual P1 test case into an automated test.

All examples come from the golden-path test case **TC-LEGACY-001** (employee submits a Client Initiative idea). It was automated in DEM-62 (PR #14, merged to `main`) and passed headless on 2026-10-02 (`Tests run: 1, Failures: 0`).

Each code block below is an exact copy of a file, or a contiguous part of a file, on `main`. The `<!-- source: ... -->` comment above a block names the file it was copied from. If that code changes on `main`, copy the block again so the two stay the same. Do not edit a copied block by hand.

## Example 1: test case and its page objects

### The test case as written

From `docs/testcases/p1-testcases.csv` (columns copied verbatim):

<!-- source: docs/testcases/p1-testcases.csv#TC-LEGACY-001 -->
```text
Test Case ID: TC-LEGACY-001
Summary / Test Cases: Verify if an employee who is allocated to a project is able to submit the Client initiative innovation.
Module: Submit / My Ideas
Priority: P1
Requirement IDs: REQ-SUB-01, REQ-SUB-02
Preconditions: Login as an employee user.
Test Data: ADTEST-08     SafetyChain Mobile
Detailed Test Steps:
Step 1: Launch a supported browser (Chrome/Edge recommended). [Yes]
Step 2: Navigate to https://10.20.3.66/innovation-portal. [Yes]
Step 3: If redirected to ADFS SSO (adtestadfs.adtest.com), enter Username "ADTest\Adtest15" in #userNameInput and Password in #passwordInput; click Sign in. [Yes]
Step 4: If the Xornet Global Screen appears after login, click Stay Here (or wait for redirect countdown). [Yes]
Step 5: Confirm Innovation Portal loads (URL contains /innovation-portal); re-navigate to https://10.20.3.66/innovation-portal if still on Xornet home. [Yes]
Step 6: Verify logged-in user context matches precondition: Employee user (project-allocated). [Partial]
Step 7: Click "+ Submit Idea" on the landing page (or open https://10.20.3.66/innovation-portal/idea-submit). [Yes]
Step 8: In Innovation Type (#innovation_type), select "Client Initiative". [Yes]
Step 9: Fill all mandatory Client Initiative fields with random/test data: Customer Name* (#edit-customer-name — pick any assigned customer), Project Name* (#edit-project — pick any available project), Idea Title (#edit-title — unique value), Innovation Category* (click a category card), Domain* (#edit-domain — select 1–5 values), Brief description (#edit-summary), Innovation Details (#edit-innovation-details), Confidential* (#edit-confidential-yes or #edit-confidential-no), and at least one Impact of Idea checkbox with Amount + Description. [Yes]
Step 10: Optionally fill Business Use Case (#edit-business-use-case) if visible. [Partial]
Step 11: Review all fields marked mandatory (required indicator / red asterisk); complete any missing mandatory values with appropriate test data or random dropdown selections. [Yes]
Step 12: Verify the Submit Idea button (#edit-submit-idea) is enabled. If disabled, cross-check mandatory fields, update missing values, tab out of fields to trigger validation, and re-check until enabled. [Yes]
Step 13: Click Submit Idea when the button is enabled. [Yes]
Step 14: After submission, note the redirected URL (/innovation-portal/idea/{id}) and idea ID. [Partial]
Step 15: Open My Ideas (https://10.20.3.66/innovation-portal/my-idea-list) and locate the submitted idea by title. [No]

Step-wise Expected Results:
Step 1: Browser opens without errors.
Step 2: Portal or ADFS login page is displayed (ignore self-signed cert warning in test env).
Step 3: ADFS authenticates successfully.
Step 4: Global Screen is dismissed; session remains active.
Step 5: Innovation Portal landing page is visible with role-appropriate navigation.
Step 6: User context matches Employee user (project-allocated) (ADTest\Adtest15).
Step 7: Submit Idea form loads at /idea-submit.
Step 8: Client Initiative is selected; Customer Name and Project Name fields appear (Department hidden).
Step 9: All mandatory fields accept test/random values; dropdowns show valid selections.
Step 10: Optional Business Use Case accepts text if shown.
Step 11: No mandatory field remains empty; required markers satisfied.
Step 12: Submit Idea button transitions from disabled to enabled once mandatory data is complete.
Step 13: Form submits without validation errors.
Step 14: Browser navigates to idea detail page with a new idea ID.
Step 15: Submitted idea appears in My Ideas with Innovation Type "Client Initiative" and initial status (e.g., Pending Approval).

```

The sheet's *Test Data* column (`ADTEST-08 SafetyChain Mobile`) and its step 3 user (`ADTest\Adtest15`) are as written. The automation uses customer `AMDOCS` / project `AMDOC112` (the ticket owner's answer to Q5 in DEM-62) and the account from `EMPLOYEE_USERNAME`.

### The finished page objects

The flow uses one page object per screen. Each action that moves to another screen returns that screen's page object. All of them extend `BasePage`, which wraps every interaction in an explicit wait:

<!-- source: src/main/java/com/innovationportal/pages/BasePage.java -->
```java
package com.innovationportal.pages;

import com.innovationportal.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * Parent of all page objects: wraps common interactions with explicit waits.
 * Page objects hold locators and actions only; assertions stay in tests.
 *
 * <pre>{@code
 * public class LoginPage extends BasePage {
 *     private final By username = By.id("username");
 *
 *     public LoginPage(WebDriver driver) { super(driver); }
 *
 *     public void enterUsername(String value) { type(username, value); }
 * }
 * }</pre>
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WaitUtils waits;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.waits = new WaitUtils(driver);
    }

    protected void click(By locator) {
        scrollIntoView(waits.waitForVisible(locator));
        waits.waitForClickable(locator).click();
    }

    protected void type(By locator, String text) {
        WebElement element = waits.waitForVisible(locator);
        scrollIntoView(element);
        element.clear();
        element.sendKeys(text);
    }

    protected String getText(By locator) {
        return waits.waitForVisible(locator).getText();
    }

    /** True if the element becomes visible within the configured timeout, false otherwise. */
    protected boolean isDisplayed(By locator) {
        try {
            return waits.waitForVisible(locator).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Scrolls the element to the middle of the viewport without animation. Pages with CSS
     * {@code scroll-behavior: smooth} (Bootstrap 5) otherwise animate WebDriver's own scroll, and the
     * click lands before the element is in view ("element click intercepted").
     */
    protected void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center', inline: 'nearest', behavior: 'instant'});", element);
    }

    protected void selectByVisibleText(By locator, String text) {
        new Select(waits.waitForVisible(locator)).selectByVisibleText(text);
    }

    protected void selectByValue(By locator, String value) {
        new Select(waits.waitForVisible(locator)).selectByValue(value);
    }
}
```

Steps 8-13 (the Submit Idea form):

<!-- source: src/main/java/com/innovationportal/pages/IdeaSubmitPage.java -->
```java
package com.innovationportal.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * Submit Idea form ({@code /innovation-portal/idea-submit}) for Client and Organisation initiatives.
 * Radios and checkboxes are hidden inputs styled through their label, so the label is clicked and the
 * input is read. Project Name is rebuilt by AJAX (its id changes), so it is located by name.
 */
public class IdeaSubmitPage extends BasePage {

    private static final String ROUTE = "/innovation-portal/idea-submit";

    private final By form = By.id("innovation-portal-idea-submit-form");
    private final By innovationType = By.id("innovation_type");
    private final By customerName = By.cssSelector("select[name='customer_name']");
    private final By projectName = By.cssSelector("select[name='project']");
    private final By department = By.id("edit-department");
    private final By title = By.id("edit-title");
    private final By innovators = By.id("edit-innovators");
    private final By innovatorsTokenInput = By.id("token-input-edit-innovators");
    private final By domainInput = By.id("token-input-edit-domain");
    private final By domainValue = By.id("edit-domain");
    private final By domainSuggestions = By.cssSelector(".token-input-dropdown li, [class^='token-input-dropdown'] li");
    private final By summary = By.id("edit-summary");
    private final By innovationDetails = By.id("edit-innovation-details");
    private final By confidentialYes = By.id("edit-confidential-yes");
    private final By confidentialNo = By.id("edit-confidential-no");
    private final By businessUseCase = By.id("edit-business-use-case");
    private final By submitIdeaButton = By.id("edit-submit-idea");

    public IdeaSubmitPage(WebDriver driver) {
        super(driver);
    }

    public boolean isLoaded() {
        boolean loaded = isDisplayed(form) && driver.getCurrentUrl().contains(ROUTE);
        waitForInnovatorsLoaded();
        return loaded;
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public IdeaSubmitPage selectInnovationType(String type) {
        selectByVisibleText(innovationType, type);
        return this;
    }

    public String getSelectedInnovationType() {
        return selectedText(innovationType);
    }

    public boolean isCustomerNameDisplayed() {
        return isDisplayed(customerName);
    }

    public boolean isProjectNameDisplayed() {
        return isDisplayed(projectName);
    }

    /** True once the Department field is hidden (it only applies to Organisation ideas). */
    public boolean isDepartmentHidden() {
        try {
            return waits.waitForInvisible(department);
        } catch (TimeoutException e) {
            return false;
        }
    }

    /** Selects the customer and waits for the AJAX-rebuilt Project Name list to offer {@code projectCode}. */
    public IdeaSubmitPage selectCustomer(String customer, String projectCode) {
        selectByVisibleText(customerName, customer);
        waits.waitFor(d -> {
            try {
                return new Select(d.findElement(projectName)).getOptions().stream()
                        .anyMatch(option -> projectCode.equals(option.getAttribute("value")));
            } catch (StaleElementReferenceException e) {
                return false;
            }
        });
        waitForInnovatorsLoaded();
        return this;
    }

    public String getSelectedCustomer() {
        return selectedText(customerName);
    }

    public IdeaSubmitPage selectProject(String projectCode) {
        selectByValue(projectName, projectCode);
        return this;
    }

    public String getSelectedProjectValue() {
        return new Select(waits.waitForVisible(projectName)).getFirstSelectedOption().getAttribute("value");
    }

    public IdeaSubmitPage enterTitle(String value) {
        type(title, value);
        return this;
    }

    public String getTitleValue() {
        return waits.waitForVisible(title).getAttribute("value");
    }

    public IdeaSubmitPage selectClientCategory(String category) {
        click(labelFor(clientCategoryInput(category)));
        return this;
    }

    public boolean isClientCategorySelected(String category) {
        return driver.findElement(clientCategoryInput(category)).isSelected();
    }

    /** Adds a Domain token unless the form already holds it (the form pre-fills a default domain). */
    public IdeaSubmitPage addDomain(String domain) {
        if (getDomainValue().contains(domain)) {
            return this;
        }
        type(domainInput, domain);
        waits.waitFor(d -> d.findElements(domainSuggestions).stream()
                        .filter(item -> item.isDisplayed() && item.getText().trim().equals(domain))
                        .findFirst()
                        .orElse(null))
                .click();
        return this;
    }

    /** Stored (hidden) value of the Domain token input. */
    public String getDomainValue() {
        return driver.findElement(domainValue).getAttribute("value");
    }

    public IdeaSubmitPage enterSummary(String value) {
        type(summary, value);
        return this;
    }

    public String getSummaryValue() {
        return waits.waitForVisible(summary).getAttribute("value");
    }

    public IdeaSubmitPage enterInnovationDetails(String value) {
        type(innovationDetails, value);
        return this;
    }

    public String getInnovationDetailsValue() {
        return waits.waitForVisible(innovationDetails).getAttribute("value");
    }

    /** {@code "Yes"} or {@code "No"}. */
    public IdeaSubmitPage selectConfidential(String yesOrNo) {
        click(confidentialRadio(yesOrNo));
        return this;
    }

    public boolean isConfidentialSelected(String yesOrNo) {
        return driver.findElement(confidentialRadio(yesOrNo)).isSelected();
    }

    /** Ticks an Impact of Idea option (key such as {@code cost-savings-for-customers}) and fills its fields. */
    public IdeaSubmitPage addImpact(String key, String amount, String description) {
        click(By.cssSelector("label[for='edit-impect-of-idea-" + key + "']"));
        type(By.id("edit-impect-of-idea-amount-" + key), amount);
        type(By.id("edit-impect-of-idea-comment-" + key), description);
        return this;
    }

    public boolean isImpactSelected(String key) {
        return driver.findElement(By.id("edit-impect-of-idea-" + key)).isSelected();
    }

    public String getImpactAmount(String key) {
        return driver.findElement(By.id("edit-impect-of-idea-amount-" + key)).getAttribute("value");
    }

    public IdeaSubmitPage enterBusinessUseCaseIfShown(String value) {
        if (isDisplayed(businessUseCase)) {
            type(businessUseCase, value);
        }
        return this;
    }

    public String getBusinessUseCaseValue() {
        return driver.findElement(businessUseCase).getAttribute("value");
    }

    /** True once the Submit Idea button becomes enabled (it starts disabled until the form is valid). */
    public boolean isSubmitIdeaEnabled() {
        return waits.waitForEnabled(submitIdeaButton).isEnabled();
    }

    /** Submits and waits for the redirect to the new idea's detail page. */
    public IdeaDetailPage submitIdea() {
        click(submitIdeaButton);
        return new IdeaDetailPage(driver);
    }

    /**
     * The form fetches the Innovators list by AJAX on load and after a Customer change. Until then the
     * field is disabled ("Please wait fetching innovators list") and the page does not scroll, so clicks
     * below the fold are intercepted.
     */
    private void waitForInnovatorsLoaded() {
        waits.waitFor(d -> {
            try {
                return d.findElement(innovators).isEnabled() && !d.findElements(innovatorsTokenInput).isEmpty();
            } catch (StaleElementReferenceException e) {
                return false;
            }
        });
    }

    private By clientCategoryInput(String category) {
        return By.cssSelector("input[name='innovation_category_cli'][value='" + category + "']");
    }

    private By confidentialRadio(String yesOrNo) {
        return "Yes".equalsIgnoreCase(yesOrNo) ? confidentialYes : confidentialNo;
    }

    private By labelFor(By input) {
        WebElement element = driver.findElement(input);
        return By.cssSelector("label[for='" + element.getAttribute("id") + "']");
    }

    private String selectedText(By select) {
        return new Select(waits.waitForVisible(select)).getFirstSelectedOption().getText().trim();
    }
}
```

Step 3 (ADFS sign-in):

<!-- source: src/main/java/com/innovationportal/pages/LoginPage.java -->
```java
package com.innovationportal.pages;

import com.innovationportal.models.UserCredentials;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * ADFS sign-in page (adtestadfs.adtest.com). Every portal URL redirects here without a session.
 */
public class LoginPage extends BasePage {

    private static final String ADFS_PATH = "/adfs/";

    private final By userNameInput = By.id("userNameInput");
    private final By passwordInput = By.id("passwordInput");
    private final By signInButton = By.id("submitButton");
    private final By errorText = By.id("errorText");

    public LoginPage(WebDriver driver) {
        super(driver);
    }

    /** True once the sign-in username field is visible (waits up to the configured timeout). */
    public boolean isLoaded() {
        return isDisplayed(userNameInput);
    }

    public String getTitle() {
        return driver.getTitle();
    }

    /**
     * Signs in and waits until ADFS either redirects away or shows an error.
     * Returns the optional Xornet Global Screen step that follows a successful sign-in.
     */
    public XornetGlobalScreenPage loginAs(UserCredentials user) {
        type(userNameInput, user.getUsername());
        type(passwordInput, user.getPassword());
        click(signInButton);
        waits.waitFor(d -> !d.getCurrentUrl().contains(ADFS_PATH)
                || !d.findElements(errorText).isEmpty() && !d.findElement(errorText).getText().isBlank());
        return new XornetGlobalScreenPage(driver);
    }

    /** True while the browser is still on ADFS (sign-in rejected). */
    public boolean isOnSignInPage() {
        return driver.getCurrentUrl().contains(ADFS_PATH);
    }

    public String getErrorText() {
        return driver.findElements(errorText).isEmpty() ? "" : driver.findElement(errorText).getText();
    }
}
```

<details>
<summary>Steps 4-7 and 14-15: XornetGlobalScreenPage, HomePage, IdeaDetailPage, MyIdeasPage</summary>

<!-- source: src/main/java/com/innovationportal/pages/XornetGlobalScreenPage.java -->
```java
package com.innovationportal.pages;

import com.innovationportal.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Optional Xornet Global Screen shown after sign-in for some accounts. It redirects to the Xornet
 * home page when its countdown ends, so "Stay Here" is clicked and the portal is re-opened.
 */
public class XornetGlobalScreenPage extends BasePage {

    private static final String PORTAL_PATH = "/innovation-portal";

    private final By stayHere = By.id("stop");

    public XornetGlobalScreenPage(WebDriver driver) {
        super(driver);
    }

    /** Dismisses the Global Screen if it is shown, then makes sure the portal landing page is open. */
    public HomePage dismissIfShown() {
        waits.waitFor(d -> d.getCurrentUrl().contains(PORTAL_PATH) || !d.findElements(stayHere).isEmpty());
        if (!driver.findElements(stayHere).isEmpty()) {
            click(stayHere);
        }
        if (!driver.getCurrentUrl().contains(PORTAL_PATH)) {
            driver.get(ConfigReader.baseUrl());
        }
        return new HomePage(driver);
    }
}
```

<!-- source: src/main/java/com/innovationportal/pages/HomePage.java -->
```java
package com.innovationportal.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Innovation Portal landing page ({@code /innovation-portal}) shown after sign-in.
 * Before sign-in the portal redirects to ADFS; use {@link LoginPage} for that page.
 */
public class HomePage extends BasePage {

    private final By pageHeading = By.cssSelector("#content h1.title");
    private final By submitIdeaLink = By.cssSelector("a[href='/innovation-portal/idea-submit']");
    private final By loggedInUserName = By.cssSelector("#block-siteheadercustomblock .user.profile a");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    /** True once the landing page heading is visible (waits up to the configured timeout). */
    public boolean isLoaded() {
        return isDisplayed(pageHeading);
    }

    public String getHeading() {
        return getText(pageHeading);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    public boolean isSubmitIdeaLinkDisplayed() {
        return isDisplayed(submitIdeaLink);
    }

    /** Display name of the signed-in user shown in the header. */
    public String getLoggedInUserName() {
        return getText(loggedInUserName);
    }

    public IdeaSubmitPage clickSubmitIdea() {
        click(submitIdeaLink);
        return new IdeaSubmitPage(driver);
    }
}
```

<!-- source: src/main/java/com/innovationportal/pages/IdeaDetailPage.java -->
```java
package com.innovationportal.pages;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

/**
 * Idea detail page ({@code /innovation-portal/idea/<id>}). The idea id exists only in the URL.
 */
public class IdeaDetailPage extends BasePage {

    private static final Pattern IDEA_URL = Pattern.compile(".*/innovation-portal/idea/(\\d+)(?:[/?#].*)?$");

    private final By detailForm = By.id("idea-info-page");
    private final By innovationStatus = By.cssSelector("#idea-info-page .innovation_status_val .status_label");

    public IdeaDetailPage(WebDriver driver) {
        super(driver);
    }

    /** True once the browser is on {@code /idea/<id>} and the detail form is visible. */
    public boolean isLoaded() {
        try {
            waits.waitForUrlMatches(IDEA_URL.pattern());
        } catch (TimeoutException e) {
            return false;
        }
        return isDisplayed(detailForm);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /** The numeric idea id parsed from the URL, or an empty string when not on a detail page. */
    public String getIdeaId() {
        Matcher matcher = IDEA_URL.matcher(driver.getCurrentUrl());
        return matcher.matches() ? matcher.group(1) : "";
    }

    public String getInnovationStatus() {
        return getText(innovationStatus).trim();
    }
}
```

<!-- source: src/main/java/com/innovationportal/pages/MyIdeasPage.java -->
```java
package com.innovationportal.pages;

import com.innovationportal.config.ConfigReader;
import java.util.Optional;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * My Ideas grid ({@code /innovation-portal/my-idea-list}). Rows have no ids, so a row is found by its
 * title link and its cells are read by position: 3 = Innovation type, last = Action/Status.
 */
public class MyIdeasPage extends BasePage {

    private static final String ROUTE = "/my-idea-list";

    private final By rows = By.cssSelector(".My_ideas_dashboard .result_row");
    private final By titleLink = By.cssSelector("a[href*='/innovation-portal/idea/']");
    private final By columns = By.cssSelector(".result_row__col");
    private final By status = By.cssSelector(".action_col > div");

    public MyIdeasPage(WebDriver driver) {
        super(driver);
    }

    /** Opens My Ideas directly (the route is relative to the configured base URL). */
    public MyIdeasPage open() {
        driver.get(ConfigReader.baseUrl() + ROUTE);
        waits.waitForVisible(rows);
        return this;
    }

    public boolean isIdeaListed(String title) {
        return findRow(title).isPresent();
    }

    public String getInnovationType(String title) {
        return findRow(title).map(row -> row.findElements(columns).get(2).getText().trim()).orElse("");
    }

    public String getStatus(String title) {
        return findRow(title).map(row -> row.findElement(status).getText().trim()).orElse("");
    }

    private Optional<WebElement> findRow(String title) {
        return driver.findElements(rows).stream()
                .filter(row -> row.findElements(titleLink).stream()
                        .anyMatch(link -> link.getText().trim().equals(title)))
                .findFirst();
    }
}
```

</details>

## Example 2: test case and its test class

The same test case as a test class. Steps 1-2 come from `BaseTest`. Steps 3-15 are page-object calls, each followed by one assertion for each expected result:

<!-- source: src/test/java/com/innovationportal/tests/submit/SubmitClientIdeaTest.java -->
```java
package com.innovationportal.tests.submit;

import com.innovationportal.base.BaseTest;
import com.innovationportal.constants.Constants;
import com.innovationportal.models.IdeaData;
import com.innovationportal.models.IdeaImpact;
import com.innovationportal.models.UserCredentials;
import com.innovationportal.pages.HomePage;
import com.innovationportal.pages.IdeaDetailPage;
import com.innovationportal.pages.IdeaSubmitPage;
import com.innovationportal.pages.LoginPage;
import com.innovationportal.pages.MyIdeasPage;
import com.innovationportal.pages.XornetGlobalScreenPage;
import com.innovationportal.utils.JsonUtils;
import com.innovationportal.utils.RandomDataUtils;
import io.qameta.allure.Description;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Golden path: an employee submits a Client Initiative idea and finds it in My Ideas. */
class SubmitClientIdeaTest extends BaseTest {

    private static final String DATA_FILE = "submit.json";

    @Test
    @Tag(Constants.TAG_P1)
    @Tag(Constants.TAG_FEATURE_SUBMIT)
    @DisplayName("TC-LEGACY-001 - Verify if an employee who is allocated to a project is able to submit the Client initiative innovation")
    @Description("Employee signs in through ADFS, submits a Client Initiative idea with all mandatory fields, "
            + "lands on the new idea's detail page and finds the idea in My Ideas with its initial status.")
    @Story("Submit a Client Initiative idea")
    void employeeSubmitsClientInitiativeIdea() {
        // Arrange
        UserCredentials employee = JsonUtils.read(DATA_FILE, "user", UserCredentials.class);
        IdeaData idea = JsonUtils.read(DATA_FILE, "clientIdea", IdeaData.class);
        String ideaTitle = RandomDataUtils.uniqueString(JsonUtils.read(DATA_FILE, "titlePrefix", String.class));
        String domain = idea.getDomains().get(0);
        IdeaImpact impact = idea.getImpacts().get(0);
        LoginPage loginPage = new LoginPage(driver());

        // Steps 2-6: sign in and reach the portal
        assertTrue(loginPage.isLoaded(), "Step 2: Portal or ADFS login page is displayed");

        XornetGlobalScreenPage globalScreen = loginPage.loginAs(employee);
        assertFalse(loginPage.isOnSignInPage(),
                "Step 3: ADFS authenticates successfully (ADFS error: '" + loginPage.getErrorText() + "')");

        HomePage homePage = globalScreen.dismissIfShown();
        assertTrue(homePage.isLoaded(),
                "Step 4-5: Global Screen is dismissed and the Innovation Portal landing page is visible");
        assertTrue(homePage.isSubmitIdeaLinkDisplayed(),
                "Step 5: Landing page shows role-appropriate navigation (+ Submit Idea)");
        assertFalse(homePage.getLoggedInUserName().isBlank(),
                "Step 6: Logged-in user context is shown in the header");

        // Steps 7-8: open the form and choose Client Initiative
        IdeaSubmitPage submitPage = homePage.clickSubmitIdea();
        assertTrue(submitPage.isLoaded(), "Step 7: Submit Idea form loads at /idea-submit");

        submitPage.selectInnovationType(idea.getInnovationType());
        assertEquals(idea.getInnovationType(), submitPage.getSelectedInnovationType(),
                "Step 8: Client Initiative is selected");
        assertTrue(submitPage.isCustomerNameDisplayed(), "Step 8: Customer Name field appears");
        assertTrue(submitPage.isProjectNameDisplayed(), "Step 8: Project Name field appears");
        assertTrue(submitPage.isDepartmentHidden(), "Step 8: Department is hidden");

        // Steps 9-11: fill every mandatory field and the optional Business Use Case
        submitPage.selectCustomer(idea.getCustomerName(), idea.getProjectName())
                .selectProject(idea.getProjectName())
                .enterTitle(ideaTitle)
                .selectClientCategory(idea.getInnovationCategory())
                .addDomain(domain)
                .enterSummary(idea.getSummary())
                .enterInnovationDetails(idea.getInnovationDetails())
                .selectConfidential(idea.getConfidential())
                .addImpact(impact.getKey(), impact.getAmount(), impact.getDescription())
                .enterBusinessUseCaseIfShown(idea.getBusinessUseCase());

        assertEquals(idea.getCustomerName(), submitPage.getSelectedCustomer(),
                "Step 9: Customer Name shows a valid selection");
        assertEquals(idea.getProjectName(), submitPage.getSelectedProjectValue(),
                "Step 9: Project Name shows a valid selection");
        assertEquals(ideaTitle, submitPage.getTitleValue(), "Step 9: Idea Title accepts the unique value");
        assertTrue(submitPage.isClientCategorySelected(idea.getInnovationCategory()),
                "Step 9: Innovation Category is selected");
        assertTrue(submitPage.getDomainValue().contains(domain), "Step 9: Domain holds a valid selection");
        assertEquals(idea.getSummary(), submitPage.getSummaryValue(), "Step 9: Brief description accepts text");
        assertEquals(idea.getInnovationDetails(), submitPage.getInnovationDetailsValue(),
                "Step 9: Innovation Details accepts text");
        assertTrue(submitPage.isConfidentialSelected(idea.getConfidential()),
                "Step 9: Confidential choice is selected");
        assertTrue(submitPage.isImpactSelected(impact.getKey()), "Step 9: Impact of Idea option is ticked");
        assertEquals(impact.getAmount(), submitPage.getImpactAmount(impact.getKey()),
                "Step 9: Impact of Idea Amount accepts the value");
        assertEquals(idea.getBusinessUseCase(), submitPage.getBusinessUseCaseValue(),
                "Step 10: Optional Business Use Case accepts text");

        // Steps 12-14: submit
        assertTrue(submitPage.isSubmitIdeaEnabled(),
                "Step 11-12: No mandatory field remains empty and Submit Idea becomes enabled");
        IdeaDetailPage detailPage = submitPage.submitIdea();
        assertTrue(detailPage.isLoaded(),
                "Step 13-14: Form submits and the browser navigates to the idea detail page (actual URL: "
                        + detailPage.getCurrentUrl() + ")");
        assertFalse(detailPage.getIdeaId().isEmpty(), "Step 14: Detail page URL carries a new idea ID");

        // Step 15: the idea is listed in My Ideas
        MyIdeasPage myIdeasPage = new MyIdeasPage(driver()).open();
        assertTrue(myIdeasPage.isIdeaListed(ideaTitle), "Step 15: Submitted idea appears in My Ideas");
        assertEquals(idea.getInnovationType(), myIdeasPage.getInnovationType(ideaTitle),
                "Step 15: My Ideas shows Innovation Type 'Client Initiative'");
        assertEquals(idea.getExpectedInitialStatus(), myIdeasPage.getStatus(ideaTitle),
                "Step 15: My Ideas shows the initial status 'Pending Approval'");
    }
}
```

The setup it inherits (step 1 *Launch a browser* and step 2 *Navigate to the portal*):

<!-- source: src/test/java/com/innovationportal/base/BaseTest.java -->
```java
package com.innovationportal.base;

import com.innovationportal.config.ConfigReader;
import com.innovationportal.driver.DriverFactory;
import com.innovationportal.driver.DriverManager;
import com.innovationportal.hooks.TestLifecycleExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;

/**
 * Base test: opens a browser on the configured base URL before each test and quits it afterwards.
 * This is the only place that creates and quits the driver. Logging and screenshot-on-failure
 * live in {@link TestLifecycleExtension}.
 */
@ExtendWith(TestLifecycleExtension.class)
public abstract class BaseTest {

    @BeforeEach
    void setUpDriver() {
        DriverManager.setDriver(DriverFactory.createDriver());
        driver().get(ConfigReader.baseUrl());
    }

    @AfterEach
    void tearDownDriver() {
        DriverManager.quitDriver();
    }

    protected WebDriver driver() {
        return DriverManager.getDriver();
    }
}
```

How each manual step maps to code:

| Step | Manual step | Code (action) | Assertion (expected result) |
|------|-------------|---------------|-----------------------------|
| 1 | Launch a browser | `BaseTest.setUpDriver()` → `DriverFactory.createDriver()` | none; a failure to start fails the test |
| 2 | Navigate to the portal | `BaseTest.setUpDriver()` → `driver().get(ConfigReader.baseUrl())` | `assertTrue(loginPage.isLoaded(), "Step 2: ...")` |
| 3 | Sign in on ADFS | `loginPage.loginAs(employee)` | `assertFalse(loginPage.isOnSignInPage(), "Step 3: ...")` |
| 4-5 | Stay Here on the Global Screen, portal loads | `globalScreen.dismissIfShown()` | `homePage.isLoaded()`, `homePage.isSubmitIdeaLinkDisplayed()` |
| 6 | User context *(Partial)* | n/a | `assertFalse(homePage.getLoggedInUserName().isBlank(), ...)` |
| 7 | Click + Submit Idea | `homePage.clickSubmitIdea()` | `submitPage.isLoaded()` |
| 8 | Select Client Initiative | `selectInnovationType(...)` | selected type, Customer/Project shown, Department hidden |
| 9 | Fill mandatory fields | `selectCustomer` … `addImpact` (fluent chain) | one `assertEquals`/`assertTrue` for each field |
| 10 | Business Use Case if shown *(Partial)* | `enterBusinessUseCaseIfShown(...)` | `getBusinessUseCaseValue()` |
| 11-12 | Submit Idea becomes enabled | n/a (the form validates itself) | `assertTrue(submitPage.isSubmitIdeaEnabled(), ...)` |
| 13-14 | Submit, go to `/idea/{id}` | `submitPage.submitIdea()` | `detailPage.isLoaded()`, `getIdeaId()` not empty |
| 15 | Find the idea in My Ideas *(No → automated)* | `new MyIdeasPage(driver()).open()` | listed, Innovation Type, initial status |

## Example 3: JSON test data and how the test reads it

Each feature has one JSON file in `src/test/resources/testdata/`. For TC-LEGACY-001 that file is `submit.json`. Credentials are `${ENV_VAR}` placeholders and never real values. `orgIdea` belongs to a later test case (TC-LEGACY-004) and still has an open `TODO(Q6)`.

<!-- source: src/test/resources/testdata/submit.json -->
```json
{
  "user": {
    "username": "${EMPLOYEE_USERNAME}",
    "password": "${EMPLOYEE_PASSWORD}"
  },
  "titlePrefix": "AUTO-P1-SUBMIT",
  "clientIdea": {
    "testCaseId": "TC-LEGACY-001",
    "innovationType": "Client Initiative",
    "customerName": "AMDOCS",
    "projectName": "AMDOC112",
    "innovationCategory": "Project area specific Improvement",
    "domains": ["Solution Architecture & Technology Solutions"],
    "summary": "Automation P1 idea - brief description",
    "innovationDetails": "Automation P1 idea - potential solution",
    "confidential": "No",
    "impacts": [
      {
        "key": "cost-savings-for-customers",
        "label": "Cost savings for customers",
        "amount": "1000",
        "description": "Automation P1 impact description"
      }
    ],
    "businessUseCase": "Automation P1 business use case",
    "expectedInitialStatus": "Pending Approval"
  },
  "orgIdea": {
    "testCaseId": "TC-LEGACY-004",
    "innovationType": "Organisation Initiative",
    "department": "TODO(Q6): department, one of HR, Finance, IT Support, ...",
    "innovationCategory": "Good to have Generic solution/Tool/Framework",
    "domains": ["Solution Architecture & Technology Solutions"],
    "summary": "Automation P1 org idea - brief description",
    "innovationDetails": "Automation P1 org idea - potential solution",
    "confidential": "No",
    "impacts": [
      {
        "key": "delivery-led-growth",
        "label": "Delivery led Growth",
        "amount": "1000",
        "description": "Automation P1 impact description"
      }
    ],
    "businessUseCase": "Automation P1 business use case",
    "expectedInitialStatus": "Pending Approval"
  },
  "routes": {
    "submit": "/idea-submit",
    "myIdeas": "/my-idea-list",
    "ideaDetailPattern": "/idea/(\\d+)"
  }
}
```

The test reads each top-level key into a model, a POJO with a no-args constructor and getters/setters:

<!-- source: src/test/java/com/innovationportal/tests/submit/SubmitClientIdeaTest.java (excerpt) -->
```java
        // Arrange
        UserCredentials employee = JsonUtils.read(DATA_FILE, "user", UserCredentials.class);
        IdeaData idea = JsonUtils.read(DATA_FILE, "clientIdea", IdeaData.class);
        String ideaTitle = RandomDataUtils.uniqueString(JsonUtils.read(DATA_FILE, "titlePrefix", String.class));
        String domain = idea.getDomains().get(0);
        IdeaImpact impact = idea.getImpacts().get(0);
        LoginPage loginPage = new LoginPage(driver());
```

<!-- source: src/main/java/com/innovationportal/models/IdeaData.java -->
```java
package com.innovationportal.models;

import java.util.List;

/**
 * Submit Idea form data bound from {@code submit.json} ({@code clientIdea} / {@code orgIdea}).
 *
 * <pre>{@code
 * IdeaData idea = JsonUtils.read("submit.json", "clientIdea", IdeaData.class);
 * }</pre>
 */
public class IdeaData {

    private String testCaseId;
    private String innovationType;
    private String customerName;
    private String projectName;
    private String department;
    private String innovationCategory;
    private List<String> domains;
    private String summary;
    private String innovationDetails;
    private String confidential;
    private List<IdeaImpact> impacts;
    private String businessUseCase;
    private String expectedInitialStatus;

    public IdeaData() {
    }

    public String getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(String testCaseId) {
        this.testCaseId = testCaseId;
    }

    public String getInnovationType() {
        return innovationType;
    }

    public void setInnovationType(String innovationType) {
        this.innovationType = innovationType;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getInnovationCategory() {
        return innovationCategory;
    }

    public void setInnovationCategory(String innovationCategory) {
        this.innovationCategory = innovationCategory;
    }

    public List<String> getDomains() {
        return domains;
    }

    public void setDomains(List<String> domains) {
        this.domains = domains;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getInnovationDetails() {
        return innovationDetails;
    }

    public void setInnovationDetails(String innovationDetails) {
        this.innovationDetails = innovationDetails;
    }

    public String getConfidential() {
        return confidential;
    }

    public void setConfidential(String confidential) {
        this.confidential = confidential;
    }

    public List<IdeaImpact> getImpacts() {
        return impacts;
    }

    public void setImpacts(List<IdeaImpact> impacts) {
        this.impacts = impacts;
    }

    public String getBusinessUseCase() {
        return businessUseCase;
    }

    public void setBusinessUseCase(String businessUseCase) {
        this.businessUseCase = businessUseCase;
    }

    public String getExpectedInitialStatus() {
        return expectedInitialStatus;
    }

    public void setExpectedInitialStatus(String expectedInitialStatus) {
        this.expectedInitialStatus = expectedInitialStatus;
    }
}
```

<details>
<summary>IdeaImpact and UserCredentials models</summary>

<!-- source: src/main/java/com/innovationportal/models/IdeaImpact.java -->
```java
package com.innovationportal.models;

/** One "Impact of Idea" entry of the Submit Idea form, bound from JSON test data. */
public class IdeaImpact {

    private String key;
    private String label;
    private String amount;
    private String description;

    public IdeaImpact() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
```

<!-- source: src/main/java/com/innovationportal/models/UserCredentials.java -->
```java
package com.innovationportal.models;

/**
 * Test user loaded from JSON test data. Pattern example for test-data POJOs:
 * a no-args constructor plus getters/setters so Jackson can bind it.
 *
 * <pre>{@code
 * UserCredentials employee = JsonUtils.read("users.json", "employee", UserCredentials.class);
 * loginPage.login(employee.getUsername(), employee.getPassword());
 * }</pre>
 */
public class UserCredentials {

    private String username;
    private String password;

    public UserCredentials() {
    }

    public UserCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /** Never prints the password. */
    @Override
    public String toString() {
        return "UserCredentials{username='" + username + "'}";
    }
}
```

</details>

`JsonUtils` replaces a `"${NAME}"` string with the system property `NAME`, or with the environment variable `NAME` if no such property is set. It fails fast if neither is set:

<!-- source: src/main/java/com/innovationportal/utils/JsonUtils.java (excerpt) -->
```java
    private static String envValue(String name, String fileName) {
        String value = System.getProperty(name);
        if (value == null) {
            value = System.getenv(name);
        }
        if (value == null || value.isEmpty()) {
            throw new IllegalStateException(
                    "Environment variable '" + name + "' referenced in " + fileName + " is not set");
        }
        return value;
    }
```

## Mapping rules

**Test case → test method**

- One manual test case becomes one `@Test` method. The class is in `tests/<feature>/` and extends `BaseTest`.
- `@DisplayName("<Test Case ID> - <Summary / Test Cases>")`. Use the summary verbatim, without its trailing period.
- `@Tag(Constants.TAG_P1)` plus one feature tag constant (`Constants.TAG_FEATURE_SUBMIT`). Add Allure `@Description` (what the flow proves) and `@Story`.
- The method body follows *Arrange* (load JSON data, create the first page object), then the steps in order. A comment such as `// Steps 7-8: ...` groups the steps.

**Manual step → page-object method**

- One user action becomes one public method on the page object for the screen where the action happens. The name starts with a verb and then names the field: `selectInnovationType`, `enterTitle`, `selectConfidential`, `addDomain`, `addImpact`.
- A method that stays on the same screen returns `this`, so the steps chain (`submitPage.selectCustomer(...).selectProject(...).enterTitle(...)`).
- A method that moves to another screen returns that screen's page object: `loginAs` → `XornetGlobalScreenPage`, `dismissIfShown` → `HomePage`, `clickSubmitIdea` → `IdeaSubmitPage`, `submitIdea` → `IdeaDetailPage`.
- For an optional step ("if visible", "if it appears") use an `…IfShown` method that does nothing when the element is absent: `enterBusinessUseCaseIfShown`, `dismissIfShown`.
- The waits a step needs go inside its method. For example, `selectCustomer` waits for the AJAX-rebuilt Project list. The test never waits.
- A page that can be opened directly gets an `open()` method that builds its URL from `ConfigReader.baseUrl()` and a relative route (`MyIdeasPage.open()`). Never hardcode the host.

**Expected result → assertion**

- Write one assertion for each expected result, in the test method only. Its message starts with the step number and repeats the expected result, e.g. `"Step 7: Submit Idea form loads at /idea-submit"`.
- The page object gives the state to check through queries such as `isLoaded()`, `is…Displayed()`, `is…Selected(...)`, `is…Enabled()`, `get…()`, `get…Value()`. These return `boolean`/`String` and never assert.
- Expected values come from the JSON data, not from literals: `assertEquals(idea.getInnovationType(), submitPage.getSelectedInnovationType(), ...)`.
- Add useful diagnostics to the message: `"(actual URL: " + detailPage.getCurrentUrl() + ")"`, the ADFS error text.
- Assert a *Partial* step only as far as the app allows, and keep the assertion honest. Step 6 asserts that a user name is shown, not which role it has.

**Preconditions → setup**

- Opening the browser on the base URL is shared setup, done once in `BaseTest` (`@BeforeEach`/`@AfterEach`). Screenshots and logging on failure are handled by `TestLifecycleExtension`. Tests never create or quit the driver.
- Preconditions that need data (the *employee user*) become JSON keys (`user`) read in *Arrange*. In TC-LEGACY-001 the login precondition is also written out as steps 2-6, so the test performs it and asserts it through `LoginPage`.
- Data that must be unique on every run (the idea title) gets a JSON prefix (`titlePrefix`) and `RandomDataUtils.uniqueString(prefix)`.
- If a precondition, locator or expected value is missing, do not invent it. Add `// TODO(<TestCaseID>): <what is missing>` or a `TODO(Qn)` placeholder in the JSON, like `orgIdea.department` above.

## DO / DON'T

**Explicit wait, not `Thread.sleep`**

DO: wait for a condition through `BasePage`/`WaitUtils` (real code):

<!-- source: src/main/java/com/innovationportal/pages/BasePage.java (excerpt) -->
```java
    protected void click(By locator) {
        scrollIntoView(waits.waitForVisible(locator));
        waits.waitForClickable(locator).click();
    }
```

<!-- source: src/main/java/com/innovationportal/pages/IdeaSubmitPage.java (excerpt) -->
```java
    /** Selects the customer and waits for the AJAX-rebuilt Project Name list to offer {@code projectCode}. */
    public IdeaSubmitPage selectCustomer(String customer, String projectCode) {
        selectByVisibleText(customerName, customer);
        waits.waitFor(d -> {
            try {
                return new Select(d.findElement(projectName)).getOptions().stream()
                        .anyMatch(option -> projectCode.equals(option.getAttribute("value")));
            } catch (StaleElementReferenceException e) {
                return false;
            }
        });
        waitForInnovatorsLoaded();
        return this;
    }
```

DON'T (anti-pattern, never written in this repo):

```java
selectByVisibleText(customerName, customer);
Thread.sleep(3000);                                    // fixed delay: flaky and slow
driver.manage().timeouts().implicitlyWait(...);        // implicit waits are banned too
```

**Locator in the page object, not in the test**

DO: the page object owns the `By` and exposes an action; the test calls it (real code):

<!-- source: src/main/java/com/innovationportal/pages/HomePage.java (excerpt) -->
```java
    private final By pageHeading = By.cssSelector("#content h1.title");
    private final By submitIdeaLink = By.cssSelector("a[href='/innovation-portal/idea-submit']");
    private final By loggedInUserName = By.cssSelector("#block-siteheadercustomblock .user.profile a");
```

<!-- source: src/main/java/com/innovationportal/pages/HomePage.java (excerpt) -->
```java
    public IdeaSubmitPage clickSubmitIdea() {
        click(submitIdeaLink);
        return new IdeaSubmitPage(driver);
    }
```

<!-- source: src/test/java/com/innovationportal/tests/submit/SubmitClientIdeaTest.java (excerpt) -->
```java
        IdeaSubmitPage submitPage = homePage.clickSubmitIdea();
        assertTrue(submitPage.isLoaded(), "Step 7: Submit Idea form loads at /idea-submit");
```

DON'T (anti-pattern):

```java
// in a test class
driver().findElement(By.cssSelector("a[href='/innovation-portal/idea-submit']")).click();
assertTrue(driver().findElement(By.id("innovation-portal-idea-submit-form")).isDisplayed());
```

**Data from JSON, not hardcoded**

DO: read every value from the feature's JSON file (real code):

<!-- source: src/test/java/com/innovationportal/tests/submit/SubmitClientIdeaTest.java (excerpt) -->
```java
        UserCredentials employee = JsonUtils.read(DATA_FILE, "user", UserCredentials.class);
        IdeaData idea = JsonUtils.read(DATA_FILE, "clientIdea", IdeaData.class);
```

<!-- source: src/test/java/com/innovationportal/tests/submit/SubmitClientIdeaTest.java (excerpt) -->
```java
        submitPage.selectInnovationType(idea.getInnovationType());
        assertEquals(idea.getInnovationType(), submitPage.getSelectedInnovationType(),
                "Step 8: Client Initiative is selected");
```

DON'T (anti-pattern):

```java
loginPage.loginAs(new UserCredentials("ADTest\\Adtest04", "some-password"));   // credentials in code
submitPage.selectInnovationType("Client Initiative").selectCustomer("AMDOCS", "AMDOC112");
```

## Naming conventions actually used

| Item | Convention | Examples on `main` |
|------|------------|--------------------|
| Page object class | `<Screen>Page` in `com.innovationportal.pages`, extends `BasePage` | `LoginPage`, `XornetGlobalScreenPage`, `HomePage`, `IdeaSubmitPage`, `IdeaDetailPage`, `MyIdeasPage` |
| Test class | `<Behaviour>Test`, package-private, in `tests.<feature>`, extends `BaseTest` | `tests.submit.SubmitClientIdeaTest`, `tests.smoke.FrameworkSmokeTest` |
| Test method | camelCase sentence: who does what | `employeeSubmitsClientInitiativeIdea`, `baseUrlOpensSignInPage` |
| Locator fields | `private final By <element>`, named after the element, no `By`/`Locator` suffix | `submitIdeaButton`, `innovationType`, `userNameInput`, `stayHere` |
| Page actions | verb + field: `click…`, `select…`, `enter…`, `add…`, `loginAs`, `open`, `…IfShown` | `clickSubmitIdea`, `enterTitle`, `addImpact`, `dismissIfShown` |
| Page queries | `isLoaded`, `is…Displayed/Selected/Enabled/Hidden`, `get…`, `get…Value` | `isSubmitIdeaEnabled`, `isDepartmentHidden`, `getTitleValue`, `getIdeaId` |
| Routes | `private static final String ROUTE` (relative) | `"/innovation-portal/idea-submit"`, `"/my-idea-list"` |
| Models | noun + `Data`, or a domain noun, in `com.innovationportal.models` | `IdeaData`, `IdeaImpact`, `UserCredentials` |
| JSON files | `<feature tag>.json` in `src/test/resources/testdata/` | `submit.json`, `smoke.json`, `approval.json`, `security.json` |
| JSON keys | camelCase; one key for each scenario/role; `testCaseId` inside scenario data | `user`, `titlePrefix`, `clientIdea`, `orgIdea`, `routes` |
| Data file constant | `private static final String DATA_FILE` in the test class | `"submit.json"` |
| Tags | `Constants.TAG_*` constants, never string literals: `P1`, `smoke`, one feature tag | `TAG_P1`, `TAG_SMOKE`, `TAG_FEATURE_SUBMIT` (`"submit"`), `TAG_FEATURE_LOGIN` |
| Display name | `"<Test Case ID> - <summary>"` | `"TC-LEGACY-001 - Verify if an employee who is allocated to a project is able to submit the Client initiative innovation"`, `"SMOKE-001 - Base URL opens the sign-in page"` |
| Assertion message | `"Step <n>: <expected result>"` | `"Step 15: Submitted idea appears in My Ideas"` |
| Unique test data | `<PREFIX>_<8 chars>` via `RandomDataUtils.uniqueString` | `AUTO-P1-SUBMIT_1a2b3c4d` |

## Running the golden path

With `EMPLOYEE_USERNAME` / `EMPLOYEE_PASSWORD` set locally (never committed) and the VPN connected:

```
mvn clean test -Dtest=SubmitClientIdeaTest -Dheadless=true
```
