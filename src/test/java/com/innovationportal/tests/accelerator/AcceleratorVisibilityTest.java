package com.innovationportal.tests.accelerator;

import com.innovationportal.base.BaseTest;
import com.innovationportal.constants.Constants;
import com.innovationportal.models.AcceleratorGateData;
import com.innovationportal.models.EvaluationData;
import com.innovationportal.models.IdeaData;
import com.innovationportal.models.IdeaImpact;
import com.innovationportal.models.ProgressUpdateData;
import com.innovationportal.models.UserCredentials;
import com.innovationportal.pages.AcceleratorDashboardPage;
import com.innovationportal.pages.CustomerInitiativeProgressPage;
import com.innovationportal.pages.EvaluationPage;
import com.innovationportal.pages.HomePage;
import com.innovationportal.pages.IdeaDetailPage;
import com.innovationportal.pages.IdeaReportsPage;
import com.innovationportal.pages.IdeaSubmitPage;
import com.innovationportal.pages.LoginPage;
import com.innovationportal.pages.OrganisationInitiativeProgressPage;
import com.innovationportal.pages.XornetGlobalScreenPage;
import com.innovationportal.utils.JsonUtils;
import com.innovationportal.utils.RandomDataUtils;
import io.qameta.allure.Description;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Accelerator feature: the Intellectual Property (Accelerator) radio "Do you want to mark this idea as a Xoriant's
 * Intellectual property?" on the Evaluation page ({@code /idea/<id>/evaluation}). The admin account
 * (accelerator.json {@code admin}, Adtest04 at run time, System Admin) submits a fresh idea and approves it as
 * setup, then saves its progress statuses in the same session and checks the radio after each one: it may only be
 * shown at the gate status (Implemented for a Client idea, POC Completed for an Organisation idea).
 */
class AcceleratorVisibilityTest extends BaseTest {

    private static final String DATA_FILE = "accelerator.json";
    /** Valid idea, approval and progress data are shared with the submit, approval and tracking features. */
    private static final String SUBMIT_DATA_FILE = "submit.json";
    private static final String APPROVAL_DATA_FILE = "approval.json";
    private static final String TRACKING_DATA_FILE = "tracking.json";

    // TODO(TC-NEW-025): role negatives (Vertical/Practice head positive, non-head roles negative) need head-role
    // accounts (Q3); the Practice/Status filter values of step 8 on the dashboard are not specified (Q26).
    @Test
    @Tag(Constants.TAG_P1)
    @Tag(Constants.TAG_FEATURE_ACCELERATOR)
    @DisplayName("TC-NEW-025 - Verify Client Accelerator radio visible only when Status=Implemented to Vertical/"
            + "Practice head and System Admin.")
    @Description("System Admin opens the Intellectual Property Dashboard, then submits and approves a new Client idea "
            + "and saves Proposed to Customer, Accepted and Implemented on Customer Initiative Progress. The "
            + "Intellectual Property (Accelerator) radio on the Evaluation page is hidden at every status before "
            + "Implemented and shown at Implemented.")
    @Story("Show the Client Accelerator radio only at Implemented")
    void clientAcceleratorRadioShownOnlyAtImplemented() {
        // Arrange
        UserCredentials admin = JsonUtils.read(DATA_FILE, "admin", UserCredentials.class);
        AcceleratorGateData gate = JsonUtils.read(DATA_FILE, "clientAccelerator", AcceleratorGateData.class);
        IdeaData idea = JsonUtils.read(SUBMIT_DATA_FILE, "clientIdea", IdeaData.class);
        EvaluationData approve = JsonUtils.read(APPROVAL_DATA_FILE, "approve", EvaluationData.class);
        ProgressUpdateData update = JsonUtils.read(TRACKING_DATA_FILE, "progressUpdate", ProgressUpdateData.class);
        IdeaImpact impact = update.getImpact();

        // Steps 2-6: sign in and reach the portal
        signIn(admin);

        // Step 7: the Intellectual Property Dashboard
        openAcceleratorDashboard();

        // Summary (setup): a fresh Client idea; the radio is hidden while it is Pending Approval
        IdeaDetailPage detailPage = submitIdea(idea);
        String ideaId = detailPage.getIdeaId();
        EvaluationPage evaluationPage = openEvaluation(detailPage);
        assertFalse(evaluationPage.isMarkAcceleratorDisplayed(),
                "Summary: the Client Accelerator radio is hidden while Status is '" + idea.getExpectedInitialStatus()
                        + "' (only shown at " + gate.getGateStatus() + ")");

        // Summary: approved (Customer Status In Progress), the radio is still hidden
        detailPage = approveIdea(evaluationPage, approve);
        assertFalse(openEvaluation(detailPage).isMarkAcceleratorDisplayed(),
                "Summary: the Client Accelerator radio is hidden after approval while Customer Status is "
                        + "'In Progress' (only shown at " + gate.getGateStatus() + ")");

        // Summary: save each Customer Status of the path; the radio is shown only at the gate status
        for (String status : gate.getStatusPath()) {
            CustomerInitiativeProgressPage progressPage = new CustomerInitiativeProgressPage(driver()).open(ideaId);
            assertTrue(progressPage.isLoaded(),
                    "Setup: CIP tracking form loads (actual URL: " + progressPage.getCurrentUrl() + ")");
            List<String> options = progressPage.getCustomerStatusOptions();
            assertTrue(options.contains(status),
                    "Setup: Customer Status offers '" + status + "' (offered: " + options + ")");

            progressPage.selectCustomerStatus(status)
                    .selectActualBenefitOutcomeIfShown(update.getActualBenefitOutcome())
                    .addImpactIfShown(impact.getKey(), impact.getAmount(), impact.getDescription())
                    .enterComment(update.getComment());
            assertTrue(progressPage.waitUntilSaveEnabled(), "Setup: Save is enabled for Customer Status '" + status + "'");
            detailPage = progressPage.save();
            assertTrue(detailPage.isLoaded(),
                    "Setup: Customer Status '" + status + "' is saved (actual URL: " + detailPage.getCurrentUrl() + ")");

            assertRadioGate(openEvaluation(detailPage), status, gate, "Client", "Customer Status");
        }
    }

    // TODO(TC-NEW-026): role negatives (Department/Practice head positive, non-head roles negative) need head-role
    // accounts (Q3); the Practice/Status filter values of step 8 on the dashboard are not specified (Q26).
    @Test
    @Tag(Constants.TAG_P1)
    @Tag(Constants.TAG_FEATURE_ACCELERATOR)
    @DisplayName("TC-NEW-026 - Verify Org Accelerator radio visible only when Status=POC Completed for Dept/Practice "
            + "Head/System Admin.")
    @Description("System Admin opens the Intellectual Property Dashboard, then submits and approves a new "
            + "Organisation idea and saves POC Completed on Organisation Initiative Progress. The Intellectual "
            + "Property (Accelerator) radio on the Evaluation page is hidden at every status before POC Completed and "
            + "shown at POC Completed.")
    @Story("Show the Org Accelerator radio only at POC Completed")
    void orgAcceleratorRadioShownOnlyAtPocCompleted() {
        // Arrange
        UserCredentials admin = JsonUtils.read(DATA_FILE, "admin", UserCredentials.class);
        AcceleratorGateData gate = JsonUtils.read(DATA_FILE, "orgAccelerator", AcceleratorGateData.class);
        IdeaData idea = JsonUtils.read(SUBMIT_DATA_FILE, "orgIdea", IdeaData.class);
        EvaluationData approve = JsonUtils.read(APPROVAL_DATA_FILE, "approve", EvaluationData.class);
        ProgressUpdateData update = JsonUtils.read(TRACKING_DATA_FILE, "progressUpdate", ProgressUpdateData.class);
        IdeaImpact impact = update.getImpact();

        // Steps 2-6: sign in and reach the portal
        signIn(admin);

        // Step 7: the Intellectual Property Dashboard
        openAcceleratorDashboard();

        // Summary (setup): a fresh Organisation idea; the radio is hidden while it is Pending Approval
        IdeaDetailPage detailPage = submitIdea(idea);
        String ideaId = detailPage.getIdeaId();
        EvaluationPage evaluationPage = openEvaluation(detailPage);
        assertFalse(evaluationPage.isMarkAcceleratorDisplayed(),
                "Summary: the Org Accelerator radio is hidden while Status is '" + idea.getExpectedInitialStatus()
                        + "' (only shown at " + gate.getGateStatus() + ")");

        // Summary: approved (Status In Progress), the radio is still hidden
        detailPage = approveIdea(evaluationPage, approve);
        assertFalse(openEvaluation(detailPage).isMarkAcceleratorDisplayed(),
                "Summary: the Org Accelerator radio is hidden after approval while Status is 'In Progress' "
                        + "(only shown at " + gate.getGateStatus() + ")");

        // Summary: save each status of the path; the radio is shown only at the gate status
        for (String status : gate.getStatusPath()) {
            OrganisationInitiativeProgressPage progressPage =
                    new OrganisationInitiativeProgressPage(driver()).open(ideaId);
            assertTrue(progressPage.isLoaded(),
                    "Setup: Organisation Initiative Progress form loads (actual URL: "
                            + progressPage.getCurrentUrl() + ")");
            List<String> options = progressPage.getStatusOptions();
            assertTrue(options.contains(status), "Setup: Status offers '" + status + "' (offered: " + options + ")");

            progressPage.selectStatus(status)
                    .addImpactIfShown(impact.getKey(), impact.getAmount(), impact.getDescription())
                    .enterComment(update.getComment());
            assertTrue(progressPage.waitUntilSaveEnabled(), "Setup: Save is enabled for Status '" + status + "'");
            detailPage = progressPage.save();
            assertTrue(detailPage.isLoaded(),
                    "Setup: Status '" + status + "' is saved (actual URL: " + detailPage.getCurrentUrl() + ")");

            assertRadioGate(openEvaluation(detailPage), status, gate, "Org", "Status");
        }
    }

    // TODO(TC-NEW-027): the sheet has no Expected Result (Q27): how to verify the notifications to the
    // Department/Practice/Vertical heads (mailbox or in-app), which report shows the Accelerator flag (Idea Reports
    // has no IP column) and what the Excel export must contain; the head accounts do not exist (Q3). The sheet user
    // is a Committee member (Adtest08, Q1); the admin account is used until then.
    @Disabled("TC-NEW-027: blocked by Q27 (no Expected Result: how to verify notifications, which report shows the "
            + "Accelerator flag, Excel contents) and Q3 (no head-role accounts)")
    @Test
    @Tag(Constants.TAG_P1)
    @Tag(Constants.TAG_FEATURE_ACCELERATOR)
    @DisplayName("TC-NEW-027 - Verify marking Accelerator notifies Department/Practice/Vertical heads and reflects in "
            + "reports; committee still fills Actual Benefits.")
    @Description("After an idea is marked as Accelerator, the Department/Practice/Vertical heads are notified, the "
            + "flag shows in Idea Reports and its Excel export, and the committee still fills the Actual Benefits.")
    @Story("Notify heads and report a marked Accelerator")
    void markedAcceleratorNotifiesHeadsAndShowsInReports() {
        // Arrange
        UserCredentials admin = JsonUtils.read(DATA_FILE, "admin", UserCredentials.class);

        // Steps 2-6: sign in and reach the portal
        signIn(admin);

        // Step 7: Idea Reports
        IdeaReportsPage reportsPage = new IdeaReportsPage(driver()).open();
        assertTrue(reportsPage.isLoaded(),
                "Step 7: Idea Reports page loads for authorized role (actual URL: " + reportsPage.getCurrentUrl() + ")");

        // TODO(TC-NEW-027): steps 8-9 (filter to the marked idea, export and check the Excel contents) and the
        // notification and Actual Benefits checks once Q27 is answered.
    }

    /** Steps 2-6: sign in through ADFS and reach the portal landing page. */
    private void signIn(UserCredentials user) {
        LoginPage loginPage = new LoginPage(driver());
        assertTrue(loginPage.isLoaded(), "Step 2: Portal or ADFS login page is displayed");

        XornetGlobalScreenPage globalScreen = loginPage.loginAs(user);
        assertFalse(loginPage.isOnSignInPage(),
                "Step 3: ADFS authenticates successfully (ADFS error: '" + loginPage.getErrorText() + "')");

        HomePage homePage = globalScreen.dismissIfShown();
        assertTrue(homePage.isLoaded(),
                "Step 4-5: Global Screen is dismissed and the Innovation Portal landing page is visible");
        assertTrue(homePage.isSubmitIdeaLinkDisplayed(),
                "Step 5: Landing page shows role-appropriate navigation (+ Submit Idea)");
        assertFalse(homePage.getLoggedInUserName().isBlank(),
                "Step 6: Logged-in user context is shown in the header");
    }

    /** Step 7: opens the Intellectual Property Dashboard by direct URL. */
    private void openAcceleratorDashboard() {
        AcceleratorDashboardPage dashboardPage = new AcceleratorDashboardPage(driver()).open();
        assertTrue(dashboardPage.isLoaded(),
                "Step 7: Accelerator dashboard loads (Intellectual Property Dashboard; actual URL: "
                        + dashboardPage.getCurrentUrl() + ")");
    }

    /**
     * Setup: submits a fresh Client or Organisation idea (submit.json {@code clientIdea} / {@code orgIdea}), as in
     * TC-LEGACY-001 / TC-LEGACY-004. Returns its detail page.
     */
    private IdeaDetailPage submitIdea(IdeaData idea) {
        IdeaImpact impact = idea.getImpacts().get(0);
        String title = RandomDataUtils.uniqueString(JsonUtils.read(DATA_FILE, "titlePrefix", String.class));
        IdeaSubmitPage submitPage = new HomePage(driver()).open().clickSubmitIdea();
        assertTrue(submitPage.isLoaded(), "Setup: Submit Idea form loads at /idea-submit");

        submitPage.selectInnovationType(idea.getInnovationType());
        if (idea.getCustomerName() != null) {
            submitPage.selectCustomer(idea.getCustomerName(), idea.getProjectName())
                    .selectProject(idea.getProjectName())
                    .enterTitle(title)
                    .selectClientCategory(idea.getInnovationCategory());
        } else {
            submitPage.selectDepartment(idea.getDepartment())
                    .enterTitle(title)
                    .selectOrgCategory(idea.getInnovationCategory());
        }
        submitPage.addDomain(idea.getDomains().get(0))
                .enterSummary(idea.getSummary())
                .enterInnovationDetails(idea.getInnovationDetails())
                .selectConfidential(idea.getConfidential())
                .addImpact(impact.getKey(), impact.getAmount(), impact.getDescription())
                .enterBusinessUseCaseIfShown(idea.getBusinessUseCase());

        assertTrue(submitPage.isSubmitIdeaEnabled(), "Setup: Submit Idea is enabled for '" + title + "'");
        IdeaDetailPage detailPage = submitPage.submitIdea();
        assertTrue(detailPage.isLoaded(),
                "Setup: '" + title + "' is submitted (actual URL: " + detailPage.getCurrentUrl() + ")");
        return detailPage;
    }

    /** Opens the Evaluation page from the idea detail page (Evaluate). */
    private EvaluationPage openEvaluation(IdeaDetailPage detailPage) {
        EvaluationPage evaluationPage = detailPage.clickEvaluate();
        assertTrue(evaluationPage.isLoaded(),
                "Evaluation page opens (actual URL: " + evaluationPage.getCurrentUrl() + ")");
        return evaluationPage;
    }

    /** Setup: approves the idea on its open Evaluation page (approval.json {@code approve}), as in TC-LEGACY-116. */
    private IdeaDetailPage approveIdea(EvaluationPage evaluationPage, EvaluationData approve) {
        IdeaImpact impact = approve.getImpact();
        evaluationPage.selectApprove()
                .addImpact(impact.getKey(), impact.getAmount(), impact.getDescription())
                .enterReason(approve.getReason())
                .rateAll(approve.getRatings());
        assertTrue(evaluationPage.waitUntilSubmitEvaluationEnabled(), "Setup: Submit Evaluation is enabled to approve");

        IdeaDetailPage approvedPage = evaluationPage.submitEvaluation();
        assertTrue(approvedPage.isLoaded(),
                "Setup: Submit Evaluation returns to the idea detail page (actual URL: "
                        + approvedPage.getCurrentUrl() + ")");
        assertEquals(approve.getExpectedStatusAfterSubmit(), approvedPage.getInnovationStatus(),
                "Setup: the idea is approved");
        return approvedPage;
    }

    /** Summary: the Accelerator radio is shown at the gate status and hidden at every other status. */
    private void assertRadioGate(EvaluationPage evaluationPage, String status, AcceleratorGateData gate,
                                 String ideaKind, String statusName) {
        if (status.equals(gate.getGateStatus())) {
            assertTrue(evaluationPage.isMarkAcceleratorDisplayed(),
                    "Summary: the " + ideaKind + " Accelerator radio is shown once " + statusName + " is '"
                            + gate.getGateStatus() + "'");
        } else {
            assertFalse(evaluationPage.isMarkAcceleratorDisplayed(),
                    "Summary: the " + ideaKind + " Accelerator radio is hidden while " + statusName + " is '"
                            + status + "' (only shown at " + gate.getGateStatus() + ")");
        }
    }
}
