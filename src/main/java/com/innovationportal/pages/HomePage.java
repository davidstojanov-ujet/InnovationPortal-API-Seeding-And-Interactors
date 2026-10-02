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
