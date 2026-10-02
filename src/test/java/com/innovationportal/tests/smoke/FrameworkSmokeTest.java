package com.innovationportal.tests.smoke;

import com.innovationportal.base.BaseTest;
import com.innovationportal.constants.Constants;
import com.innovationportal.pages.HomePage;
import com.innovationportal.utils.JsonUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Proves the framework end to end: config, driver, page object, JSON data, hooks and reporting. */
class FrameworkSmokeTest extends BaseTest {

    @Test
    @Tag(Constants.TAG_SMOKE)
    @DisplayName("SMOKE-001 - Base URL opens the sign-in page")
    void baseUrlOpensSignInPage() {
        String expectedTitle = JsonUtils.read("smoke.json", "expectedTitle", String.class);
        HomePage homePage = new HomePage(driver());

        assertTrue(homePage.isLoaded(), "Sign-in username field is not visible");
        assertEquals(expectedTitle, homePage.getTitle(), "Page title");
    }
}
