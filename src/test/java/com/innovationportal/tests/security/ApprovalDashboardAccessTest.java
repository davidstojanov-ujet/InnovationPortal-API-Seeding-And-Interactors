package com.innovationportal.tests.security;

import com.innovationportal.base.BaseTest;
import com.innovationportal.constants.Constants;
import com.innovationportal.models.UserCredentials;
import com.innovationportal.pages.ApprovalDashboardPage;
import com.innovationportal.pages.HomePage;
import com.innovationportal.pages.LoginPage;
import com.innovationportal.pages.XornetGlobalScreenPage;
import com.innovationportal.utils.JsonUtils;
import io.qameta.allure.Description;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Security feature: role-restricted pages cannot be reached by direct URL. */
class ApprovalDashboardAccessTest extends BaseTest {

    private static final String DATA_FILE = "security.json";

    // TODO(TC-NEW-035): needs an Employee-only account without evaluator rights (EMPLOYEE_ONLY_USERNAME /
    // EMPLOYEE_ONLY_PASSWORD, Q4; the configured employee Adtest04 can open the dashboard) and the expected
    // denial behaviour - redirect, access-denied text or HTTP 403 (Q28; security.json directUrl.expectedBehaviour).
    @Disabled("TC-NEW-035: blocked by Q4 (no Employee-only account) and Q28 (expected denial behaviour not specified)")
    @Test
    @Tag(Constants.TAG_P1)
    @Tag(Constants.TAG_FEATURE_SECURITY)
    @DisplayName("TC-NEW-035 - Verify Employee cannot open Approval Dashboard via direct URL")
    @Description("An employee without evaluator rights signs in and opens /approval-dashboard directly; "
            + "the Approval Dashboard must not load.")
    @Story("Block the Approval Dashboard for employees")
    void employeeCannotOpenApprovalDashboardByDirectUrl() {
        // Arrange
        UserCredentials employee = JsonUtils.read(DATA_FILE, "employeeOnly", UserCredentials.class);
        LoginPage loginPage = new LoginPage(driver());

        // Steps 2-6: sign in and reach the portal
        assertTrue(loginPage.isLoaded(), "Step 2: Portal or ADFS login page is displayed");

        XornetGlobalScreenPage globalScreen = loginPage.loginAs(employee);
        assertFalse(loginPage.isOnSignInPage(),
                "Step 3: ADFS authenticates successfully (ADFS error: '" + loginPage.getErrorText() + "')");

        HomePage homePage = globalScreen.dismissIfShown();
        assertTrue(homePage.isLoaded(),
                "Step 4-5: Global Screen is dismissed and the Innovation Portal landing page is visible");
        assertFalse(homePage.getLoggedInUserName().isBlank(),
                "Step 6: Logged-in user context is shown in the header");

        // Steps 7-9: open the Approval Dashboard by direct URL
        ApprovalDashboardPage dashboardPage = new ApprovalDashboardPage(driver()).open();
        assertFalse(dashboardPage.isLoaded(),
                "Step 9: Employee cannot open the Approval Dashboard via direct URL (actual URL: "
                        + dashboardPage.getCurrentUrl() + ")");
        // TODO(TC-NEW-035): assert the specific denial behaviour once Q28 is answered.
    }
}
