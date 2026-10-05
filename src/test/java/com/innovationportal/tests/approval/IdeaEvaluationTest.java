package com.innovationportal.tests.approval;

import com.innovationportal.base.BaseTest;
import com.innovationportal.constants.Constants;
import com.innovationportal.models.EvaluationData;
import com.innovationportal.models.IdeaData;
import com.innovationportal.models.IdeaImpact;
import com.innovationportal.models.UserCredentials;
import com.innovationportal.pages.ApprovalDashboardPage;
import com.innovationportal.pages.CommitteePage;
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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Set;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Approval feature: the evaluator works on a Pending Approval idea from the Approval Dashboard.
 * The evaluator account (approval.json {@code evaluator}, Adtest04 at run time) first submits a fresh idea as
 * setup and then evaluates it in the same session. Adtest04 is not the idea's assigned evaluator, so the
 * dashboard is filtered by Idea Title instead of "My Evaluations".
 */
class IdeaEvaluationTest extends BaseTest {

    private static final String DATA_FILE = "approval.json";
    /** Valid idea data is shared with the submit feature instead of being duplicated here. */
    private static final String SUBMIT_DATA_FILE = "submit.json";

    @Test
    @Tag(Constants.TAG_P1)
    @Tag(Constants.TAG_FEATURE_APPROVAL)
    @DisplayName("TC-LEGACY-029 - Verify Approve is blocked when Impact of Idea (option + $ + description) is missing; "
            + "Save as Draft may retain partial data")
    @Description("Evaluator rates a Pending Approval idea and gives a reason but skips Impact of Idea: Submit Evaluation "
            + "stays disabled, Save as Draft keeps the idea Pending Approval, and the reopened draft keeps the ratings "
            + "and the reason without any Impact of Idea.")
    @Story("Save an evaluation draft without Impact of Idea")
    void evaluatorSavesDraftWithoutImpactOfIdea() {
        // Arrange
        UserCredentials evaluator = JsonUtils.read(DATA_FILE, "evaluator", UserCredentials.class);
        EvaluationData draft = JsonUtils.read(DATA_FILE, "draftWithoutImpact", EvaluationData.class);
        String defaultStatusFilter = JsonUtils.read(DATA_FILE, "defaultStatusFilter", String.class);
        IdeaData idea = JsonUtils.read(SUBMIT_DATA_FILE, "clientIdea", IdeaData.class);
        String ideaTitle = RandomDataUtils.uniqueString(JsonUtils.read(DATA_FILE, "titlePrefix", String.class));

        // Steps 2-6: sign in and reach the portal
        signIn(evaluator);

        // Setup: a fresh Pending Approval Client idea to evaluate
        submitIdea(idea, ideaTitle);

        // Steps 7-10: find the idea on the Approval Dashboard and open it
        // TODO(TC-LEGACY-029): step 9 'My Evaluations' not exercised - Adtest04 is not the assigned evaluator (Q1/Q4)
        IdeaDetailPage detailPage = openIdeaFromDashboard(ideaTitle, defaultStatusFilter);

        // Step 11: Approve with every rating and a reason but no Impact of Idea, then Save as Draft
        EvaluationPage evaluationPage = detailPage.clickEvaluate();
        assertTrue(evaluationPage.isLoaded(),
                "Step 11: Evaluation page opens (actual URL: " + evaluationPage.getCurrentUrl() + ")");
        evaluationPage.selectApprove()
                .rateAll(draft.getRatings())
                .enterReason(draft.getReason());
        assertTrue(evaluationPage.isApproveSelected(), "Step 11: Approve is selected");
        assertFalse(evaluationPage.isSubmitEvaluationEnabled(),
                "Step 11: Approve is blocked (Submit Evaluation disabled) while Impact of Idea is missing");
        assertTrue(evaluationPage.isSaveAsDraftEnabled(), "Step 11: Save as Draft is available for the partial evaluation");

        detailPage = evaluationPage.saveAsDraft();
        assertTrue(detailPage.isLoaded(),
                "Step 11: Save as Draft returns to the idea detail page (actual URL: " + detailPage.getCurrentUrl() + ")");
        assertEquals(draft.getExpectedStatusAfterDraft(), detailPage.getInnovationStatus(),
                "Step 11: Evaluation draft saved; status remains Pending Approval");

        // Steps 11-12: reopen the draft - the partial data is kept and Perceived Benefit was skipped
        EvaluationPage draftPage = detailPage.clickEvaluate();
        assertTrue(draftPage.isLoaded(),
                "Step 11: Evaluation page reopens (actual URL: " + draftPage.getCurrentUrl() + ")");
        assertAll("Step 11: The reopened draft keeps the partial evaluation data",
                IntStream.range(0, draft.getRatings().size()).<Executable>mapToObj(question -> () -> assertTrue(
                        draftPage.isRatingSelected(question, draft.getRatings().get(question)),
                        "Step 11: Draft keeps rating " + draft.getRatings().get(question) + " for question " + question)));
        assertEquals(draft.getReason(), draftPage.getReasonValue(), "Step 11: Draft keeps the reason text");
        assertFalse(draftPage.isAnyImpactSelected(),
                "Step 12: The evaluator could skip Perceived Benefit Realization (draft saved without Impact of Idea)");
    }

    @Test
    @Tag(Constants.TAG_P1)
    @Tag(Constants.TAG_FEATURE_APPROVAL)
    @DisplayName("TC-LEGACY-116 - Verify Approve requires ratings 1-10 on Innovation Index (25%), Potential Business "
            + "Impact (35%), Depth (25%), Feasibility (15%) with overall score on 1-10 points scale")
    @Description("Evaluator approves a Pending Approval idea: with Impact of Idea and a reason but no ratings Submit "
            + "Evaluation stays disabled; once every question is rated it becomes enabled, and submitting approves the idea.")
    @Story("Approve an idea only with ratings")
    void evaluatorMustRateIdeaToApprove() {
        // Arrange
        UserCredentials evaluator = JsonUtils.read(DATA_FILE, "evaluator", UserCredentials.class);
        EvaluationData approve = JsonUtils.read(DATA_FILE, "approve", EvaluationData.class);
        String defaultStatusFilter = JsonUtils.read(DATA_FILE, "defaultStatusFilter", String.class);
        IdeaData idea = JsonUtils.read(SUBMIT_DATA_FILE, "clientIdea", IdeaData.class);
        String ideaTitle = RandomDataUtils.uniqueString(JsonUtils.read(DATA_FILE, "titlePrefix", String.class));
        IdeaImpact impact = approve.getImpact();

        // Steps 2-6: sign in and reach the portal
        signIn(evaluator);

        // Setup: a fresh Pending Approval Client idea to evaluate
        submitIdea(idea, ideaTitle);

        // Steps 7-10: find the idea on the Approval Dashboard and open it
        // TODO(TC-LEGACY-116): step 9 'My Evaluations' not exercised - Adtest04 is not the assigned evaluator (Q1/Q4)
        IdeaDetailPage detailPage = openIdeaFromDashboard(ideaTitle, defaultStatusFilter);

        // Step 11: Approve with Impact of Idea and a reason, but no ratings yet
        EvaluationPage evaluationPage = detailPage.clickEvaluate();
        assertTrue(evaluationPage.isLoaded(),
                "Step 11: Evaluation page opens (actual URL: " + evaluationPage.getCurrentUrl() + ")");
        evaluationPage.selectApprove()
                .addImpact(impact.getKey(), impact.getAmount(), impact.getDescription())
                .enterReason(approve.getReason());
        assertTrue(evaluationPage.isApproveSelected(), "Step 11: Approve is selected");
        assertTrue(evaluationPage.isImpactSelected(impact.getKey()), "Step 11: Impact of Idea option is ticked");
        assertFalse(evaluationPage.isSubmitEvaluationEnabled(),
                "Step 11: Rating is mandatory to approve (Submit Evaluation disabled without ratings)");

        // Steps 11-12: rate every question; Approve can then be submitted
        evaluationPage.rateAll(approve.getRatings());
        assertTrue(evaluationPage.waitUntilSubmitEvaluationEnabled(),
                "Step 11: Submit Evaluation becomes enabled once every question is rated 1-10");
        // TODO(TC-LEGACY-116): weighted overall score not displayed, formula unknown (Q22)

        detailPage = evaluationPage.submitEvaluation();
        assertTrue(detailPage.isLoaded(),
                "Step 12: Submit Evaluation returns to the idea detail page (actual URL: "
                        + detailPage.getCurrentUrl() + ")");
        assertEquals(approve.getExpectedStatusAfterSubmit(), detailPage.getInnovationStatus(),
                "Step 12: The rated idea is approved");
    }

    @Test
    @Tag(Constants.TAG_P1)
    @Tag(Constants.TAG_FEATURE_APPROVAL)
    @DisplayName("TC-NEW-011 - Verify Client idea auto-assigns vertical default evaluator; Org assigns department "
            + "default evaluator")
    @Description("A new Client idea and a new Organisation idea are listed as Pending Approval on the Approval Dashboard: "
            + "the Org idea is assigned to its department's default evaluator and the Client idea to a Delivery Unit "
            + "default evaluator, as shown on the Committee List.")
    @Story("Assign the default evaluator on submit")
    void newIdeasAreAssignedToDefaultEvaluators() {
        // Arrange
        UserCredentials evaluator = JsonUtils.read(DATA_FILE, "evaluator", UserCredentials.class);
        String defaultStatusFilter = JsonUtils.read(DATA_FILE, "defaultStatusFilter", String.class);
        String titlePrefix = JsonUtils.read(DATA_FILE, "titlePrefix", String.class);
        IdeaData clientIdea = JsonUtils.read(SUBMIT_DATA_FILE, "clientIdea", IdeaData.class);
        IdeaData orgIdea = JsonUtils.read(SUBMIT_DATA_FILE, "orgIdea", IdeaData.class);
        String clientTitle = RandomDataUtils.uniqueString(titlePrefix);
        String orgTitle = RandomDataUtils.uniqueString(titlePrefix);

        // Steps 2-6: sign in and reach the portal
        signIn(evaluator);

        // Setup: a fresh Client idea and a fresh Organisation idea
        submitIdea(clientIdea, clientTitle);
        submitIdea(orgIdea, orgTitle);

        // Setup: the configured default evaluators, from the Committee List
        CommitteePage committeePage = new CommitteePage(driver()).open();
        assertTrue(committeePage.isLoaded(), "Setup: Committee List loads");
        String departmentDefaultEvaluator = committeePage.getDepartmentDefaultEvaluator(orgIdea.getDepartment());
        Set<String> duDefaultEvaluators = committeePage.getDuDefaultEvaluators();
        assertFalse(departmentDefaultEvaluator.isEmpty(),
                "Setup: Committee List shows a default evaluator for department '" + orgIdea.getDepartment() + "'");
        assertFalse(duDefaultEvaluators.isEmpty(), "Setup: Committee List shows the Delivery Unit default evaluators");

        // Steps 7-8: open the Approval Dashboard with the default status filter
        ApprovalDashboardPage dashboardPage = new ApprovalDashboardPage(driver()).open();
        assertTrue(dashboardPage.isLoaded(), "Step 7: Approval Dashboard loads");
        assertEquals(defaultStatusFilter, dashboardPage.getSelectedInnovationStatus(),
                "Step 8: Status filter defaults to Pending Approval");

        // Steps 9-12: Organisation idea - department default evaluator. The grid shows the evaluator, so the
        // idea is not opened (step 10).
        // TODO(TC-NEW-011): step 9 'My Evaluations' not exercised - Adtest04 is not the assigned evaluator (Q1/Q4)
        dashboardPage.filterByTitle(orgTitle);
        assertTrue(dashboardPage.isIdeaListed(orgTitle), "Step 9: Grid shows the new Organisation idea (filtered by title)");
        assertEquals(orgIdea.getExpectedInitialStatus(), dashboardPage.getStatus(orgTitle),
                "Step 11: Organisation idea is shown as Pending Approval");
        assertEquals(departmentDefaultEvaluator, dashboardPage.getEvaluator(orgTitle),
                "Step 12: Organisation idea is assigned to the default evaluator of department '"
                        + orgIdea.getDepartment() + "'");

        // Steps 9-12: Client idea - vertical (Delivery Unit) default evaluator
        dashboardPage.filterByTitle(clientTitle);
        assertTrue(dashboardPage.isIdeaListed(clientTitle), "Step 9: Grid shows the new Client idea (filtered by title)");
        assertEquals(clientIdea.getExpectedInitialStatus(), dashboardPage.getStatus(clientTitle),
                "Step 11: Client idea is shown as Pending Approval");
        // TODO(TC-NEW-011): exact vertical of the customer unknown (Q23); assert the specific vertical default once known
        String clientEvaluator = dashboardPage.getEvaluator(clientTitle);
        assertTrue(duDefaultEvaluators.contains(clientEvaluator),
                "Step 12: Client idea is assigned to a vertical (Delivery Unit) default evaluator (actual: '"
                        + clientEvaluator + "', DU defaults: " + duDefaultEvaluators + ")");
    }

    @Test
    @Tag(Constants.TAG_P1)
    @Tag(Constants.TAG_FEATURE_APPROVAL)
    @DisplayName("TC-NEW-013 - Verify Reject without comment is blocked")
    @Description("Evaluator chooses Reject on a Pending Approval idea: Submit Evaluation stays disabled while the "
            + "mandatory reason is empty and becomes enabled once the reason is entered. The idea is not rejected.")
    @Story("Require a comment to reject an idea")
    void evaluatorCannotRejectWithoutComment() {
        // Arrange
        UserCredentials evaluator = JsonUtils.read(DATA_FILE, "evaluator", UserCredentials.class);
        EvaluationData reject = JsonUtils.read(DATA_FILE, "rejectWithoutComment", EvaluationData.class);
        String defaultStatusFilter = JsonUtils.read(DATA_FILE, "defaultStatusFilter", String.class);
        IdeaData idea = JsonUtils.read(SUBMIT_DATA_FILE, "orgIdea", IdeaData.class);
        String ideaTitle = RandomDataUtils.uniqueString(JsonUtils.read(DATA_FILE, "titlePrefix", String.class));

        // Steps 2-6: sign in and reach the portal
        signIn(evaluator);

        // Setup: a fresh Pending Approval Organisation idea to evaluate
        submitIdea(idea, ideaTitle);

        // Steps 7-10: find the idea on the Approval Dashboard and open it
        // TODO(TC-NEW-013): step 9 'My Evaluations' not exercised - Adtest04 is not the assigned evaluator (Q1/Q4)
        IdeaDetailPage detailPage = openIdeaFromDashboard(ideaTitle, defaultStatusFilter);

        // Step 11: Reject without a comment
        EvaluationPage evaluationPage = detailPage.clickEvaluate();
        assertTrue(evaluationPage.isLoaded(),
                "Step 11: Evaluation page opens (actual URL: " + evaluationPage.getCurrentUrl() + ")");
        evaluationPage.selectReject();
        assertTrue(evaluationPage.isRejectSelected(), "Step 11: Reject is selected");
        assertTrue(evaluationPage.isRejectReasonDisplayed(), "Step 11: The mandatory reason field is shown for Reject");
        assertFalse(evaluationPage.isSubmitEvaluationEnabled(),
                "Step 11: Reject blocked without mandatory comment (Submit Evaluation disabled)");

        // Step 12: with the mandatory comment, Reject can be submitted
        evaluationPage.enterRejectReason(reject.getReason());
        assertEquals(reject.getReason(), evaluationPage.getRejectReasonValue(),
                "Step 12: The reason field accepts the comment");
        assertTrue(evaluationPage.waitUntilSubmitEvaluationEnabled(),
                "Step 12: With the mandatory comment entered, Reject can be submitted (Submit Evaluation enabled)");
        // TODO(TC-NEW-013): final reject outcome/message not specified (Q24)
    }

    /** Steps 2-6, shared by every test: sign in through ADFS and reach the portal landing page. */
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

    /**
     * Setup: submits a Client or Organisation idea (submit.json {@code clientIdea} / {@code orgIdea}) through the
     * Submit Idea form, as in TC-LEGACY-001 / TC-LEGACY-004, so that a fresh Pending Approval idea exists.
     */
    private void submitIdea(IdeaData idea, String title) {
        IdeaImpact impact = idea.getImpacts().get(0);
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
    }

    /** Steps 7-10: opens the Approval Dashboard, finds the idea by title and opens its detail page. */
    private IdeaDetailPage openIdeaFromDashboard(String title, String defaultStatusFilter) {
        ApprovalDashboardPage dashboardPage = new ApprovalDashboardPage(driver()).open();
        assertTrue(dashboardPage.isLoaded(), "Step 7: Approval Dashboard loads");
        assertEquals(defaultStatusFilter, dashboardPage.getSelectedInnovationStatus(),
                "Step 8: Status filter defaults to Pending Approval");

        // Step 9: filtered by Idea Title instead of "My Evaluations" (see the TODO in each test)
        dashboardPage.filterByTitle(title);
        assertTrue(dashboardPage.isIdeaListed(title), "Step 9: Grid shows the idea (filtered by Idea Title)");

        IdeaDetailPage detailPage = dashboardPage.openIdea(title);
        assertTrue(detailPage.isLoaded(),
                "Step 10: Idea detail opens (actual URL: " + detailPage.getCurrentUrl() + ")");
        return detailPage;
    }
}
