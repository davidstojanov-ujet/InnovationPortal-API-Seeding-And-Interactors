package com.innovationportal.tests.tracking;

import com.innovationportal.base.BaseTest;
import com.innovationportal.constants.Constants;
import com.innovationportal.models.CustomerProgressData;
import com.innovationportal.models.EvaluationData;
import com.innovationportal.models.IdeaData;
import com.innovationportal.models.IdeaImpact;
import com.innovationportal.models.ProgressUpdateData;
import com.innovationportal.models.ProjectManagerPrivilegesData;
import com.innovationportal.models.UserCredentials;
import com.innovationportal.pages.CustomerInitiativeProgressPage;
import com.innovationportal.pages.EvaluationPage;
import com.innovationportal.pages.HomePage;
import com.innovationportal.pages.IdeaDetailPage;
import com.innovationportal.pages.IdeaSubmitPage;
import com.innovationportal.pages.LoginPage;
import com.innovationportal.pages.XornetGlobalScreenPage;
import com.innovationportal.utils.JsonUtils;
import com.innovationportal.utils.RandomDataUtils;
import io.qameta.allure.Description;
import io.qameta.allure.Story;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tracking feature: Customer Status on the Customer Initiative Progress page
 * ({@code /actual_benefit_realization/<id>}) of an approved Client idea. The evaluator account
 * (tracking.json {@code evaluator}, Adtest04 at run time) submits a fresh idea and approves it as setup, then
 * tracks its customer progress in the same session. Customer Status only offers the next allowed states of the
 * current one, so the tests save statuses one after another and read the options after each save.
 */
class CustomerInitiativeProgressTest extends BaseTest {

    private static final String DATA_FILE = "tracking.json";
    /** Valid idea and approval data are shared with the submit and approval features instead of being duplicated. */
    private static final String SUBMIT_DATA_FILE = "submit.json";
    private static final String APPROVAL_DATA_FILE = "approval.json";

    @Test
    @Tag(Constants.TAG_P1)
    @Tag(Constants.TAG_FEATURE_TRACKING)
    @DisplayName("TC-LEGACY-101 - Verify Implemented locks further Customer Progress updates; POC Completed and "
            + "Proposed also support Actual Benefits (Phase 5).")
    @Description("On a newly approved Client idea, Proposed to Customer and POC Completed ask for the Actual Benefit "
            + "Realization outcome. Once Implemented or Rejected is saved, Customer Status offers no other value: both "
            + "are end states.")
    @Story("Implemented and Rejected end the customer progress")
    void implementedAndRejectedAreEndStates() {
        // Arrange
        UserCredentials evaluator = JsonUtils.read(DATA_FILE, "evaluator", UserCredentials.class);
        CustomerProgressData implemented = JsonUtils.read(DATA_FILE, "implementedLock", CustomerProgressData.class);
        CustomerProgressData rejected = JsonUtils.read(DATA_FILE, "rejectedLock", CustomerProgressData.class);
        ProgressUpdateData update = JsonUtils.read(DATA_FILE, "progressUpdate", ProgressUpdateData.class);

        // Steps 2-6: sign in and reach the portal
        signIn(evaluator);

        // Step 7 (setup): a fresh approved Client idea
        // TODO(TC-LEGACY-101): runs as Adtest04; the sheet's committee member (Adtest08/09/10) is not configured (Q1/Q20)
        String implementedIdeaId = submitAndApproveIdea();

        // Step 8: the progress page; Proposed to Customer and POC Completed support Actual Benefits
        CustomerInitiativeProgressPage progressPage = openProgressPage(implementedIdeaId);
        for (String status : implemented.getActualBenefitStatuses()) {
            progressPage.selectCustomerStatus(status);
            assertTrue(progressPage.isActualBenefitOutcomeDisplayed(),
                    "Step 9: '" + status + "' supports Actual Benefits (Actual Benefit Realization - Outcome is shown)");
        }

        // Step 9: Proposed to Customer -> Accepted -> Implemented, then re-open: Implemented is an end state
        saveStatusPath(implementedIdeaId, implemented.getStatusPath(), update);
        assertEndState(implementedIdeaId, implemented);

        // Step 9: Proposed to Customer -> Rejected on a second idea, then re-open: Rejected is an end state
        String rejectedIdeaId = submitAndApproveIdea();
        saveStatusPath(rejectedIdeaId, rejected.getStatusPath(), update);
        assertEndState(rejectedIdeaId, rejected);
    }

    @Test
    @Tag(Constants.TAG_P1)
    @Tag(Constants.TAG_FEATURE_TRACKING)
    @DisplayName("TC-LEGACY-107 - Verify Customer Status options include In Progress, Proposed to Customer, Accepted, "
            + "Rejected, On Hold, POC Completed, Implemented.")
    @Description("The evaluator puts a new Client idea On Hold, then approves it. On its progress page Customer Status "
            + "offers In Progress, Proposed to Customer, POC Completed and Implemented, and after Proposed to Customer "
            + "is saved also Accepted, Rejected and On Hold: all seven options of the sheet.")
    @Story("List the Customer Status options")
    void customerStatusOffersAllOptions() {
        // Arrange
        UserCredentials evaluator = JsonUtils.read(DATA_FILE, "evaluator", UserCredentials.class);
        CustomerProgressData statusList = JsonUtils.read(DATA_FILE, "customerStatusList", CustomerProgressData.class);
        ProgressUpdateData update = JsonUtils.read(DATA_FILE, "progressUpdate", ProgressUpdateData.class);
        EvaluationData approve = JsonUtils.read(APPROVAL_DATA_FILE, "approve", EvaluationData.class);

        // Steps 2-6: sign in and reach the portal
        signIn(evaluator);

        // Steps 7-10 (setup): a fresh Pending Approval Client idea, opened on its detail page
        // TODO(TC-LEGACY-107): the idea is opened right after submit, not from 'My Evaluations' on the Approval
        // Dashboard - Adtest04 is not the assigned evaluator (Q1/Q4)
        IdeaDetailPage detailPage = submitIdea();
        String ideaId = detailPage.getIdeaId();

        // Step 11: Evaluate -> On Hold with a reason -> Submit Evaluation
        EvaluationPage evaluationPage = detailPage.clickEvaluate();
        assertTrue(evaluationPage.isLoaded(),
                "Step 11: Evaluation page opens (actual URL: " + evaluationPage.getCurrentUrl() + ")");
        evaluationPage.selectHold().enterReason(statusList.getHoldReason());
        assertTrue(evaluationPage.isHoldSelected(), "Step 11: On Hold is selected");
        assertTrue(evaluationPage.waitUntilSubmitEvaluationEnabled(),
                "Step 11: Submit Evaluation is enabled for On Hold with a reason");
        detailPage = evaluationPage.submitEvaluation();
        assertTrue(detailPage.isLoaded(),
                "Step 11: Submit Evaluation returns to the idea detail page (actual URL: "
                        + detailPage.getCurrentUrl() + ")");
        assertEquals(statusList.getExpectedHoldStatus(), detailPage.getInnovationStatus(),
                "Step 11: Status changes to On Hold");

        // Step 12: re-open the same idea and approve it
        approveIdea(detailPage, approve);

        // Step 12: Customer Status options of the approved idea, before and after Proposed to Customer is saved
        CustomerInitiativeProgressPage progressPage = openProgressPage(ideaId);
        List<String> approvedOptions = progressPage.getCustomerStatusOptions();
        saveStatusPath(ideaId, statusList.getStatusPath(), update);
        List<String> proposedOptions = openProgressPage(ideaId).getCustomerStatusOptions();

        Set<String> offeredOptions = new LinkedHashSet<>(approvedOptions);
        offeredOptions.addAll(proposedOptions);
        assertEquals(Set.copyOf(statusList.getExpectedOptions()), Set.copyOf(offeredOptions),
                "Step 12: Customer Status offers exactly " + statusList.getExpectedOptions() + " (after approval: "
                        + approvedOptions + "; after Proposed to Customer: " + proposedOptions + ")");
    }

    // TODO(TC-NEW-022): the sheet has no Expected Result and its steps run evaluator actions as a Committee member
    // (Q25); there is no Project Manager account and no idea on a PM's project (Q3/Q25: PROJECT_MANAGER_USERNAME /
    // PROJECT_MANAGER_PASSWORD, tracking.json projectManagerPrivileges.ideaId). Adtest04 is admin, so "cannot
    // approve" cannot be checked with it.
    @Disabled("TC-NEW-022: blocked by Q25 (no Expected Result, no PM idea) and Q3 (no Project Manager account; "
            + "Adtest04 is admin, so 'cannot approve' can't be checked)")
    @Test
    @Tag(Constants.TAG_P1)
    @Tag(Constants.TAG_FEATURE_TRACKING)
    @DisplayName("TC-NEW-022 - Verify Project Manager (Rel 5.1) can update Customer Progress only (not full evaluator "
            + "approve privileges).")
    @Description("A Project Manager opens Customer Initiative Progress for an approved Client idea on their project "
            + "and can update it, but cannot approve ideas like an evaluator.")
    @Story("Let the Project Manager update Customer Progress only")
    void projectManagerUpdatesCustomerProgressOnly() {
        // Arrange
        UserCredentials projectManager = JsonUtils.read(DATA_FILE, "projectManager", UserCredentials.class);
        ProjectManagerPrivilegesData privileges =
                JsonUtils.read(DATA_FILE, "projectManagerPrivileges", ProjectManagerPrivilegesData.class);

        // Steps 2-6: sign in and reach the portal
        signIn(projectManager);

        // Summary: the Project Manager can update Customer Progress of an idea on their project
        CustomerInitiativeProgressPage progressPage =
                new CustomerInitiativeProgressPage(driver()).open(privileges.getIdeaId());
        assertTrue(progressPage.isLoaded(),
                "Summary: Project Manager can update Customer Progress (CIP tracking form loads; actual URL: "
                        + progressPage.getCurrentUrl() + ")");

        // TODO(TC-NEW-022): assert the Project Manager cannot approve (no Evaluate / approve control) once Q25
        // gives the expected UI.
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

    /** Setup: submits a fresh Client idea (submit.json {@code clientIdea}), as in TC-LEGACY-001. */
    private IdeaDetailPage submitIdea() {
        IdeaData idea = JsonUtils.read(SUBMIT_DATA_FILE, "clientIdea", IdeaData.class);
        IdeaImpact impact = idea.getImpacts().get(0);
        String title = RandomDataUtils.uniqueString(JsonUtils.read(DATA_FILE, "titlePrefix", String.class));
        IdeaSubmitPage submitPage = new HomePage(driver()).open().clickSubmitIdea();
        assertTrue(submitPage.isLoaded(), "Setup: Submit Idea form loads at /idea-submit");

        submitPage.selectInnovationType(idea.getInnovationType())
                .selectCustomer(idea.getCustomerName(), idea.getProjectName())
                .selectProject(idea.getProjectName())
                .enterTitle(title)
                .selectClientCategory(idea.getInnovationCategory())
                .addDomain(idea.getDomains().get(0))
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

    /** Setup: submits a fresh Client idea and approves it. Returns the idea id. */
    private String submitAndApproveIdea() {
        IdeaDetailPage detailPage = submitIdea();
        String ideaId = detailPage.getIdeaId();
        approveIdea(detailPage, JsonUtils.read(APPROVAL_DATA_FILE, "approve", EvaluationData.class));
        return ideaId;
    }

    /** Approves the idea from its detail page (approval.json {@code approve}), as in TC-LEGACY-116. */
    private void approveIdea(IdeaDetailPage detailPage, EvaluationData approve) {
        IdeaImpact impact = approve.getImpact();
        EvaluationPage evaluationPage = detailPage.clickEvaluate();
        assertTrue(evaluationPage.isLoaded(),
                "Evaluation page opens to approve (actual URL: " + evaluationPage.getCurrentUrl() + ")");
        evaluationPage.selectApprove()
                .addImpact(impact.getKey(), impact.getAmount(), impact.getDescription())
                .enterReason(approve.getReason())
                .rateAll(approve.getRatings());
        assertTrue(evaluationPage.waitUntilSubmitEvaluationEnabled(), "Submit Evaluation is enabled to approve");

        IdeaDetailPage approvedPage = evaluationPage.submitEvaluation();
        assertTrue(approvedPage.isLoaded(),
                "Submit Evaluation returns to the idea detail page (actual URL: " + approvedPage.getCurrentUrl() + ")");
        assertEquals(approve.getExpectedStatusAfterSubmit(), approvedPage.getInnovationStatus(), "The idea is approved");
    }

    /** Step 8: opens Customer Initiative Progress (CIP) for the idea. */
    private CustomerInitiativeProgressPage openProgressPage(String ideaId) {
        CustomerInitiativeProgressPage progressPage = new CustomerInitiativeProgressPage(driver()).open(ideaId);
        assertTrue(progressPage.isLoaded(),
                "Step 8: CIP tracking form loads (actual URL: " + progressPage.getCurrentUrl() + ")");
        return progressPage;
    }

    /** Step 9: saves each Customer Status in turn, re-opening the progress page before each one. */
    private void saveStatusPath(String ideaId, List<String> statusPath, ProgressUpdateData update) {
        IdeaImpact impact = update.getImpact();
        for (String status : statusPath) {
            CustomerInitiativeProgressPage progressPage = openProgressPage(ideaId);
            List<String> options = progressPage.getCustomerStatusOptions();
            assertTrue(options.contains(status),
                    "Step 9: Customer Status offers '" + status + "' (offered: " + options + ")");

            progressPage.selectCustomerStatus(status)
                    .selectActualBenefitOutcomeIfShown(update.getActualBenefitOutcome())
                    .addImpactIfShown(impact.getKey(), impact.getAmount(), impact.getDescription())
                    .enterComment(update.getComment());
            assertTrue(progressPage.waitUntilSaveEnabled(), "Step 9: Save is enabled for Customer Status '" + status + "'");

            IdeaDetailPage detailPage = progressPage.save();
            assertTrue(detailPage.isLoaded(),
                    "Step 9: Customer Status '" + status + "' is saved (actual URL: " + detailPage.getCurrentUrl() + ")");
        }
    }

    /** Step 9: re-opens the progress page; the saved end state is the only Customer Status offered. */
    private void assertEndState(String ideaId, CustomerProgressData endState) {
        String expectedStatus = endState.getStatusPath().get(endState.getStatusPath().size() - 1);
        CustomerInitiativeProgressPage progressPage = openProgressPage(ideaId);
        assertEquals(expectedStatus, progressPage.getSelectedCustomerStatus(),
                "Step 9: Customer Status '" + expectedStatus + "' is kept after Save");
        assertEquals(endState.getExpectedOptions(), progressPage.getCustomerStatusOptions(),
                "Step 9: '" + expectedStatus + "' is an end state: no further Customer Status update is offered");
    }
}
