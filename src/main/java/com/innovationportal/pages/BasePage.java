package com.innovationportal.pages;

import com.innovationportal.utils.WaitUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
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
        scrollIntoView(waits.waitForVisible(locator));
        waits.waitForClickable(locator).click();
    }

    protected void type(By locator, String text) {
        WebElement element = waits.waitForVisible(locator);
        scrollIntoView(element);
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

    /**
     * Scrolls the element to the middle of the viewport without animation. Pages with CSS
     * {@code scroll-behavior: smooth} (Bootstrap 5) otherwise animate WebDriver's own scroll, and the
     * click lands before the element is in view ("element click intercepted").
     */
    protected void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center', inline: 'nearest', behavior: 'instant'});", element);
    }

    protected void selectByVisibleText(By locator, String text) {
        new Select(waits.waitForVisible(locator)).selectByVisibleText(text);
    }

    protected void selectByValue(By locator, String value) {
        new Select(waits.waitForVisible(locator)).selectByValue(value);
    }
}
