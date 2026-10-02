package com.innovationportal.base;

import com.innovationportal.config.ConfigReader;
import com.innovationportal.driver.DriverFactory;
import com.innovationportal.driver.DriverManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;

/**
 * Minimal base test: opens a browser on the configured base URL before each test
 * and quits it afterwards. Hooks (screenshots, reporting) are added in IPS 2.4.
 */
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

    protected WebDriver driver() {
        return DriverManager.getDriver();
    }
}
