package com.innovationportal.pages;

import com.innovationportal.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Intellectual Property Dashboard ({@code /innovation-portal/accelerator-dashboard}, title
 * {@code Intellectual Property Dashboard}), the sheet's Accelerator dashboard.
 */
public class AcceleratorDashboardPage extends BasePage {

    private static final String ROUTE = "/accelerator-dashboard";

    private final By dashboardForm = By.id("innovation-accelerator-dashboard");

    public AcceleratorDashboardPage(WebDriver driver) {
        super(driver);
    }

    /** Opens the dashboard by direct URL (the route is relative to the configured base URL). */
    public AcceleratorDashboardPage open() {
        driver.get(ConfigReader.baseUrl() + ROUTE);
        return this;
    }

    /** True once the dashboard filter form is visible and the browser is on the dashboard route. */
    public boolean isLoaded() {
        return isDisplayed(dashboardForm) && driver.getCurrentUrl().contains(ROUTE);
    }

    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }
}
