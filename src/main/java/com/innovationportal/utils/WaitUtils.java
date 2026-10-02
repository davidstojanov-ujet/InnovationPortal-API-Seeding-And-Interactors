package com.innovationportal.utils;

import com.innovationportal.config.ConfigReader;
import com.innovationportal.constants.Constants;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Explicit waits built on {@link WebDriverWait} with the configured timeout ({@code explicitWaitSeconds}).
 * No implicit waits and no Thread.sleep anywhere in the framework.
 *
 * <pre>{@code
 * WaitUtils waits = new WaitUtils(driver);
 * waits.waitForClickable(By.id("submit")).click();
 * waits.waitForUrlContains("/dashboard");
 * }</pre>
 */
public class WaitUtils {

    private final WebDriverWait wait;

    public WaitUtils(WebDriver driver) {
        this(driver, Duration.ofSeconds(ConfigReader.explicitWaitSeconds()));
    }

    public WaitUtils(WebDriver driver, Duration timeout) {
        this.wait = new WebDriverWait(driver, timeout, Constants.POLLING_INTERVAL);
    }

    public WebElement waitForVisible(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement waitForClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    public boolean waitForUrlContains(String fragment) {
        return wait.until(ExpectedConditions.urlContains(fragment));
    }

    public boolean waitForTextPresent(By locator, String text) {
        return wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
    }
}
