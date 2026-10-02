package com.innovationportal.pages;

import com.innovationportal.config.ConfigReader;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Optional Xornet Global Screen shown after sign-in for some accounts. It redirects to the Xornet
 * home page when its countdown ends, so "Stay Here" is clicked and the portal is re-opened.
 */
public class XornetGlobalScreenPage extends BasePage {

    private static final String PORTAL_PATH = "/innovation-portal";

    private final By stayHere = By.id("stop");

    public XornetGlobalScreenPage(WebDriver driver) {
        super(driver);
    }

    /** Dismisses the Global Screen if it is shown, then makes sure the portal landing page is open. */
    public HomePage dismissIfShown() {
        waits.waitFor(d -> d.getCurrentUrl().contains(PORTAL_PATH) || !d.findElements(stayHere).isEmpty());
        if (!driver.findElements(stayHere).isEmpty()) {
            click(stayHere);
        }
        if (!driver.getCurrentUrl().contains(PORTAL_PATH)) {
            driver.get(ConfigReader.baseUrl());
        }
        return new HomePage(driver);
    }
}
