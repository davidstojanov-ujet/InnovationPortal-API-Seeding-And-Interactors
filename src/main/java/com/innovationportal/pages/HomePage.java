package com.innovationportal.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * The page shown when the base URL is opened. Without a session the portal redirects to the
 * ADFS sign-in page, so "loaded" means the sign-in username field is visible.
 */
public class HomePage extends BasePage {

    private final By userNameInput = By.id("userNameInput");

    public HomePage(WebDriver driver) {
        super(driver);
    }

    /** True once the sign-in username field is visible (waits up to the configured timeout). */
    public boolean isLoaded() {
        return isDisplayed(userNameInput);
    }

    public String getTitle() {
        return driver.getTitle();
    }
}
