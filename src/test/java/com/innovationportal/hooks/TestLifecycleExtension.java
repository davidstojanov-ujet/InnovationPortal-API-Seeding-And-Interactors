package com.innovationportal.hooks;

import com.innovationportal.driver.DriverManager;
import com.innovationportal.utils.ScreenshotUtils;
import io.qameta.allure.Allure;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.util.Optional;

/**
 * Test lifecycle hooks: logs the start, end and result of every test and, when a test fails,
 * attaches a screenshot and the current URL to the Allure report.
 * Registered once on {@code BaseTest}; tests never take failure screenshots themselves.
 *
 * <p>The failure evidence is captured in {@link #afterTestExecution}, which runs right after the
 * test method and before {@code BaseTest}'s {@code @AfterEach} quits the driver. By the time
 * {@link #afterEach} and the {@link TestWatcher} callbacks run, the browser is already closed.
 *
 * <pre>{@code
 * @ExtendWith(TestLifecycleExtension.class)
 * public abstract class BaseTest { ... }
 * }</pre>
 */
public class TestLifecycleExtension
        implements BeforeEachCallback, AfterTestExecutionCallback, AfterEachCallback, TestWatcher {

    private static final Logger LOG = LoggerFactory.getLogger(TestLifecycleExtension.class);

    @Override
    public void beforeEach(ExtensionContext context) {
        LOG.info("START  {}", context.getDisplayName());
    }

    @Override
    public void afterTestExecution(ExtensionContext context) {
        if (context.getExecutionException().isPresent()) {
            attachFailureEvidence(context.getDisplayName());
        }
    }

    @Override
    public void afterEach(ExtensionContext context) {
        LOG.info("END    {}", context.getDisplayName());
    }

    @Override
    public void testSuccessful(ExtensionContext context) {
        LOG.info("PASSED {}", context.getDisplayName());
    }

    @Override
    public void testFailed(ExtensionContext context, Throwable cause) {
        LOG.error("FAILED {}: {}", context.getDisplayName(), cause.toString());
    }

    @Override
    public void testAborted(ExtensionContext context, Throwable cause) {
        LOG.warn("ABORTED {}: {}", context.getDisplayName(), cause.toString());
    }

    @Override
    public void testDisabled(ExtensionContext context, Optional<String> reason) {
        LOG.info("DISABLED {}: {}", context.getDisplayName(), reason.orElse("no reason given"));
    }

    /** Never throws: a failed capture must not hide the original test failure. */
    private static void attachFailureEvidence(String testName) {
        WebDriver driver;
        try {
            driver = DriverManager.getDriver();
        } catch (IllegalStateException e) {
            LOG.warn("No WebDriver for '{}'; skipping failure screenshot", testName);
            return;
        }
        try {
            String url = driver.getCurrentUrl();
            Allure.addAttachment("Current URL", "text/plain", String.valueOf(url));
            LOG.info("Failure URL for '{}': {}", testName, url);
        } catch (RuntimeException e) {
            LOG.warn("Cannot read the current URL for '{}': {}", testName, e.toString());
        }
        try {
            byte[] png = ScreenshotUtils.capture(driver, testName);
            Allure.addAttachment("Screenshot on failure", "image/png", new ByteArrayInputStream(png), "png");
        } catch (RuntimeException e) {
            LOG.warn("Cannot take a screenshot for '{}': {}", testName, e.toString());
        }
    }
}
