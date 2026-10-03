package com.innovationportal.pages;

import com.innovationportal.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Idea Reports ({@code /innovation-portal/idea-report}, title {@code Idea Reports}).
 */
public class IdeaReportsPage extends BasePage {

    private static final String ROUTE = "/idea-report";

    private final By reportForm = By.id("innovation-portal-idea-report-dashboard");

    public IdeaReportsPage(WebDriver driver) {
        super(driver);
    }

    /** Opens Idea Reports by direct URL (the route is relative to the configured base URL). */
    public IdeaReportsPage open() {
        driver.get(ConfigReader.baseUrl() + ROUTE);
        return this;
    }

    /** True once the report filter form is visible and the browser is on the report route. */
    public boolean isLoaded() {
        return isDisplayed(reportForm) && driver.getCurrentUrl().contains(ROUTE);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
