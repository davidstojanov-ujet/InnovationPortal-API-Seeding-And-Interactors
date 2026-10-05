package com.innovationportal.base;

import com.innovationportal.config.ConfigReader;
import com.innovationportal.driver.DriverFactory;
import com.innovationportal.driver.DriverManager;
import com.innovationportal.hooks.TestLifecycleExtension;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.openqa.selenium.WebDriver;

/**
 * Base test: opens a browser on the configured base URL before each test and quits it afterwards.
 * This is the only place that creates and quits the driver. Logging and screenshot-on-failure
 * live in {@link TestLifecycleExtension}.
 */
@ExtendWith(TestLifecycleExtension.class)
public abstract class BaseTest {

    @BeforeEach
    void setUpDriver() {
        DriverManager.setDriver(DriverFactory.createDriver());
        driver().get(ConfigReader.baseUrl());
    }

    @AfterEach
    void tearDownDriver() {
        DriverManager.quitDriver();
    }

    /**
     * Quits the browser and opens a new one on the configured base URL. Used when a test must sign in as another
     * user: ADFS single sign-on has no working logout, so only a new browser session drops the old user.
     */
    protected void restartBrowser() {
        DriverManager.quitDriver();
        setUpDriver();
    }

    protected WebDriver driver() {
        return DriverManager.getDriver();
    }
}
