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
