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
    private final By evaluateLink = By.cssSelector("a[href$='/evaluation']");

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

    /** Opens the Evaluation page ({@code /idea/<id>/evaluation}) for this idea. */
    public EvaluationPage clickEvaluate() {
        click(evaluateLink);
        return new EvaluationPage(driver);
    }
}
