package com.innovationportal.pages;

import com.innovationportal.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Approval Dashboard ({@code /innovation-portal/approval-dashboard}) for evaluators and committee members.
 * Only the load check is mapped so far; filters and the grid come with the approval tests.
 */
public class ApprovalDashboardPage extends BasePage {

    private static final String ROUTE = "/approval-dashboard";

    private final By dashboardForm = By.id("innovation-portal-approval-dashboard");

    public ApprovalDashboardPage(WebDriver driver) {
        super(driver);
    }

    /** Opens the dashboard by direct URL (the route is relative to the configured base URL). */
    public ApprovalDashboardPage open() {
        driver.get(ConfigReader.baseUrl() + ROUTE);
        return this;
    }

    /** True once the dashboard filter form is visible (waits up to the configured timeout). */
    public boolean isLoaded() {
        return isDisplayed(dashboardForm);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
