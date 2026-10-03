package com.innovationportal.pages;

import com.innovationportal.config.ConfigReader;
import java.util.List;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * Customer Initiative Progress Tracking ({@code /innovation-portal/actual_benefit_realization/<id>}) for an
 * approved Client idea. Customer Status offers only the next allowed states of the current one, and changing it
 * shows or hides fields by AJAX. Save is disabled until the form is valid and redirects to the idea detail page.
 */
public class CustomerInitiativeProgressPage extends BasePage {

    private static final String ROUTE = "/actual_benefit_realization/";

    private final By form = By.id("actual-benefit-realization-form");
    private final By customerStatus = By.name("customer_status");
    private final By actualBenefitOutcome = By.name("abr_outcome");
    private final By comment = By.name("abr_comment");
    private final By saveButton = By.cssSelector("#actual-benefit-realization-form #edit-submit");
    private final By ajaxProgress = By.cssSelector(".ajax-progress");

    public CustomerInitiativeProgressPage(WebDriver driver) {
        super(driver);
    }

    /** Opens the progress page of idea {@code ideaId} (the route is relative to the configured base URL). */
    public CustomerInitiativeProgressPage open(String ideaId) {
        driver.get(ConfigReader.baseUrl() + ROUTE + ideaId);
        return this;
    }

    /** True once the progress form is visible and the browser is on the progress route. */
    public boolean isLoaded() {
        return isDisplayed(form) && driver.getCurrentUrl().contains(ROUTE);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /** Customer Status options offered now, without the empty {@code --Select--} placeholder. */
    public List<String> getCustomerStatusOptions() {
        return new Select(waits.waitForVisible(customerStatus)).getOptions().stream()
                .filter(option -> !option.getAttribute("value").isEmpty())
                .map(option -> option.getText().trim())
                .collect(Collectors.toList());
    }

    public String getSelectedCustomerStatus() {
        return new Select(waits.waitForVisible(customerStatus)).getFirstSelectedOption().getText().trim();
    }

    /** Selects a Customer Status and waits for the AJAX that shows or hides the dependent fields. */
    public CustomerInitiativeProgressPage selectCustomerStatus(String status) {
        selectByVisibleText(customerStatus, status);
        waits.waitFor(d -> d.findElements(ajaxProgress).isEmpty());
        return this;
    }

    /** True if "Actual Benefit Realization - Outcome" is shown for the selected Customer Status (no wait). */
    public boolean isActualBenefitOutcomeDisplayed() {
        return isShownNow(actualBenefitOutcome);
    }

    /** Selects the Actual Benefit Realization outcome when the selected Customer Status asks for it. */
    public CustomerInitiativeProgressPage selectActualBenefitOutcomeIfShown(String outcome) {
        if (isActualBenefitOutcomeDisplayed()) {
            selectByVisibleText(actualBenefitOutcome, outcome);
        }
        return this;
    }

    /**
     * Ticks an Impact of idea option (key such as {@code cost-savings-for-customers}) and fills its fields, when
     * the option is shown. Saving {@code Implemented} needs at least one.
     */
    public CustomerInitiativeProgressPage addImpactIfShown(String key, String amount, String description) {
        By label = By.cssSelector("label[for='edit-impect-of-idea-" + key + "']");
        if (isShownNow(label)) {
            if (!driver.findElement(By.id("edit-impect-of-idea-" + key)).isSelected()) {
                click(label);
            }
            typeAndBlur(By.id("edit-impect-of-idea-amount-" + key), amount);
            typeAndBlur(By.id("edit-impect-of-idea-comment-" + key), description);
        }
        return this;
    }

    public CustomerInitiativeProgressPage enterComment(String value) {
        typeAndBlur(comment, value);
        return this;
    }

    /** True once Save becomes enabled within the configured timeout; false (no exception) otherwise. */
    public boolean waitUntilSaveEnabled() {
        try {
            return waits.waitForEnabled(saveButton).isEnabled();
        } catch (TimeoutException e) {
            return false;
        }
    }

    /** Saves and waits for the redirect to the idea detail page. */
    public IdeaDetailPage save() {
        click(saveButton);
        waits.waitFor(d -> !d.getCurrentUrl().contains(ROUTE));
        return new IdeaDetailPage(driver);
    }

    private boolean isShownNow(By locator) {
        return driver.findElements(locator).stream().anyMatch(WebElement::isDisplayed);
    }

    private void typeAndBlur(By locator, String text) {
        type(locator, text);
        driver.findElement(locator).sendKeys(Keys.TAB);
    }
}
