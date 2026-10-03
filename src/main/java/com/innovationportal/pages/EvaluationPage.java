package com.innovationportal.pages;

import com.innovationportal.constants.Constants;
import com.innovationportal.utils.WaitUtils;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Idea Evaluation page ({@code /innovation-portal/idea/<id>/evaluation}); the form id keeps the app's spelling
 * ({@code idea-evalute-page}). The status radios are clicked on the input itself (their label click is
 * intercepted by the input). The rating radios and Impact of idea checkboxes are hidden inputs, so their
 * label is clicked and the input is read. Submit Evaluation stays disabled until a client-side check passes;
 * the check runs on change/keyup, so typed fields are blurred with TAB.
 */
public class EvaluationPage extends BasePage {

    private static final String EVALUATION_URL = ".*/innovation-portal/idea/\\d+/evaluation(?:[?#].*)?$";
    private static final String EVALUATION_PATH = "/evaluation";

    private final By form = By.id("idea-evalute-page");
    private final By approveStatus = By.id("edit-idea-status-2");
    private final By rejectStatus = By.id("edit-idea-status-3");
    private final By holdStatus = By.id("edit-idea-status-4");
    private final By reason = By.id("edit-admin-comment");
    private final By saveAsDraftButton = By.id("edit-draft-evalution");
    private final By submitEvaluationButton = By.id("edit-submit-evalution");
    private final By impactCheckboxes = By.cssSelector("#idea-evalute-page input[type='checkbox'][id^='edit-impect-of-idea-']");
    private final By ajaxProgress = By.cssSelector(".ajax-progress");
    private final By markAcceleratorYes = By.cssSelector("label[for='edit-mark-accelerator-yes']");
    private final By markAcceleratorNo = By.cssSelector("label[for='edit-mark-accelerator-no']");
    private final By markAcceleratorInputs = By.name("mark_accelerator");

    /** Short wait used to let the Submit Evaluation state settle before reading it. */
    private final WaitUtils shortWaits;

    public EvaluationPage(WebDriver driver) {
        super(driver);
        this.shortWaits = new WaitUtils(driver, Constants.SHORT_TIMEOUT);
    }

    /** True once the browser is on {@code /idea/<id>/evaluation} and the evaluation form is visible. */
    public boolean isLoaded() {
        try {
            waits.waitForUrlMatches(EVALUATION_URL);
        } catch (TimeoutException e) {
            return false;
        }
        return isDisplayed(form);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * True if the Intellectual Property (Accelerator) radio "Do you want to mark this idea as a Xoriant's
     * Intellectual property?" is shown now (its Yes/No label or input). Waits only for the loaded form, the
     * finished page load and any running AJAX, then checks without a visibility wait. Call after
     * {@link #isLoaded()}.
     */
    public boolean isMarkAcceleratorDisplayed() {
        waits.waitForVisible(form);
        waits.waitFor(d -> "complete".equals(((JavascriptExecutor) d).executeScript("return document.readyState"))
                && d.findElements(ajaxProgress).isEmpty());
        return isShownNow(markAcceleratorYes) || isShownNow(markAcceleratorNo) || isShownNow(markAcceleratorInputs);
    }

    /** Approve is selected by default; clicking it again is harmless. */
    public EvaluationPage selectApprove() {
        selectStatus(approveStatus);
        return this;
    }

    public boolean isApproveSelected() {
        return driver.findElement(approveStatus).isSelected();
    }

    public EvaluationPage selectReject() {
        selectStatus(rejectStatus);
        return this;
    }

    public boolean isRejectSelected() {
        return driver.findElement(rejectStatus).isSelected();
    }

    /** "Hold" in the app; the sheet calls it On Hold. Only the reason is required. */
    public EvaluationPage selectHold() {
        selectStatus(holdStatus);
        return this;
    }

    public boolean isHoldSelected() {
        return driver.findElement(holdStatus).isSelected();
    }

    /** Rates question {@code question} (0-4) with {@code value} (1-10). */
    public EvaluationPage rate(int question, int value) {
        click(By.cssSelector("label[for='" + ratingId(question, value) + "']"));
        return this;
    }

    /** Rates questions 0..n-1 with the given values, in order. */
    public EvaluationPage rateAll(List<Integer> ratings) {
        for (int question = 0; question < ratings.size(); question++) {
            rate(question, ratings.get(question));
        }
        return this;
    }

    public boolean isRatingSelected(int question, int value) {
        return driver.findElement(By.id(ratingId(question, value))).isSelected();
    }

    /** Ticks an Impact of idea option (perceived benefit) and fills its Amount and Description. */
    public EvaluationPage addImpact(String key, String amount, String description) {
        click(By.cssSelector("label[for='edit-impect-of-idea-" + key + "']"));
        typeAndBlur(By.id("edit-impect-of-idea-amount-" + key), amount);
        typeAndBlur(By.id("edit-impect-of-idea-comment-" + key), description);
        return this;
    }

    public boolean isImpactSelected(String key) {
        return driver.findElement(By.id("edit-impect-of-idea-" + key)).isSelected();
    }

    /** True if any Impact of idea option is ticked. */
    public boolean isAnyImpactSelected() {
        return driver.findElements(impactCheckboxes).stream().anyMatch(WebElement::isSelected);
    }

    /** Fills "Please provide the reason to change the status of the idea." and blurs it. */
    public EvaluationPage enterReason(String value) {
        typeAndBlur(reason, value);
        return this;
    }

    public boolean isReasonFieldDisplayed() {
        return isDisplayed(reason);
    }

    public String getReasonValue() {
        return waits.waitForVisible(reason).getAttribute("value");
    }

    public boolean isSaveAsDraftEnabled() {
        return waits.waitForVisible(saveAsDraftButton).isEnabled();
    }

    /**
     * State of Submit Evaluation after a short settle time ({@link Constants#SHORT_TIMEOUT}): true if it is
     * or becomes enabled within that time, false if it stays disabled.
     */
    public boolean isSubmitEvaluationEnabled() {
        return isEnabledWithin(shortWaits);
    }

    /** True once Submit Evaluation becomes enabled within the configured timeout; false (no exception) otherwise. */
    public boolean waitUntilSubmitEvaluationEnabled() {
        return isEnabledWithin(waits);
    }

    /** Saves the draft and waits for the redirect back to the idea detail page. */
    public IdeaDetailPage saveAsDraft() {
        click(saveAsDraftButton);
        waitForRedirectToDetail();
        return new IdeaDetailPage(driver);
    }

    /** Submits the evaluation and waits for the redirect back to the idea detail page. */
    public IdeaDetailPage submitEvaluation() {
        click(submitEvaluationButton);
        waitForRedirectToDetail();
        return new IdeaDetailPage(driver);
    }

    /** Status radios trigger Drupal AJAX/#states on change; wait until it has settled. */
    private void selectStatus(By status) {
        click(status);
        waits.waitFor(d -> d.findElement(status).isSelected() && d.findElements(ajaxProgress).isEmpty());
    }

    private boolean isShownNow(By locator) {
        return driver.findElements(locator).stream().anyMatch(WebElement::isDisplayed);
    }

    private void typeAndBlur(By locator, String text) {
        type(locator, text);
        driver.findElement(locator).sendKeys(Keys.TAB);
    }

    private boolean isEnabledWithin(WaitUtils wait) {
        try {
            return wait.waitForEnabled(submitEvaluationButton).isEnabled();
        } catch (TimeoutException e) {
            return false;
        }
    }

    private void waitForRedirectToDetail() {
        waits.waitFor(d -> !d.getCurrentUrl().contains(EVALUATION_PATH));
    }

    private String ratingId(int question, int value) {
        return "edit-ques-" + question + "-" + value;
    }
}
