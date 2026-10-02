package com.innovationportal.pages;

import com.innovationportal.config.ConfigReader;
import com.innovationportal.models.UserCredentials;
import com.innovationportal.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.time.Duration;
import java.util.List;

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
        // The ADFS-to-portal redirect chain can outlast the explicit wait, so allow a full page load.
        new WaitUtils(driver, Duration.ofSeconds(ConfigReader.pageLoadTimeoutSeconds()))
                .waitFor(d -> !d.getCurrentUrl().contains(ADFS_PATH) || isErrorShown(d));
        return new XornetGlobalScreenPage(driver);
    }

    /** Reads the error once per poll; the ADFS page may be navigating away, so a stale element counts as "no error". */
    private boolean isErrorShown(WebDriver d) {
        try {
            List<WebElement> errors = d.findElements(errorText);
            return !errors.isEmpty() && !errors.get(0).getText().isBlank();
        } catch (StaleElementReferenceException e) {
            return false;
        }
    }

    /** True while the browser is still on ADFS (sign-in rejected). */
    public boolean isOnSignInPage() {
        return driver.getCurrentUrl().contains(ADFS_PATH);
    }

    public String getErrorText() {
        return driver.findElements(errorText).isEmpty() ? "" : driver.findElement(errorText).getText();
    }
}
