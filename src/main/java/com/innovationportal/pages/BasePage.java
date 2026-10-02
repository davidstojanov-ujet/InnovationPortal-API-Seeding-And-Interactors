package com.innovationportal.pages;

import com.innovationportal.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

/**
 * Parent of all page objects: wraps common interactions with explicit waits.
 * Page objects hold locators and actions only; assertions stay in tests.
 *
 * <pre>{@code
 * public class LoginPage extends BasePage {
 *     private final By username = By.id("username");
 *
 *     public LoginPage(WebDriver driver) { super(driver); }
 *
 *     public void enterUsername(String value) { type(username, value); }
 * }
 * }</pre>
 */
public abstract class BasePage {

    protected final WebDriver driver;
    protected final WaitUtils waits;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        this.waits = new WaitUtils(driver);
    }

    protected void click(By locator) {
        waits.waitForClickable(locator).click();
    }

    protected void type(By locator, String text) {
        WebElement element = waits.waitForVisible(locator);
        element.clear();
        element.sendKeys(text);
    }

    protected String getText(By locator) {
        return waits.waitForVisible(locator).getText();
    }

    /** True if the element becomes visible within the configured timeout, false otherwise. */
    protected boolean isDisplayed(By locator) {
        try {
            return waits.waitForVisible(locator).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    protected void selectByVisibleText(By locator, String text) {
        new Select(waits.waitForVisible(locator)).selectByVisibleText(text);
    }

    protected void selectByValue(By locator, String value) {
        new Select(waits.waitForVisible(locator)).selectByValue(value);
    }
}
