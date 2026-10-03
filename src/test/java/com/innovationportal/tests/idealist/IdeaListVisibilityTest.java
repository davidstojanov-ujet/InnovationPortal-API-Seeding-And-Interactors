package com.innovationportal.tests.idealist;

import com.innovationportal.base.BaseTest;
import com.innovationportal.constants.Constants;
import com.innovationportal.models.EvaluationData;
import com.innovationportal.models.IdeaData;
import com.innovationportal.models.IdeaImpact;
import com.innovationportal.models.IdeaVisibilityData;
import com.innovationportal.models.UserCredentials;
import com.innovationportal.pages.EvaluationPage;
import com.innovationportal.pages.HomePage;
import com.innovationportal.pages.IdeaDetailPage;
import com.innovationportal.pages.IdeaListPage;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Idea List feature: which approved ideas are shown in the Idea List ({@code /dashboard/all_idea_list}).
 * The evaluator account (idealist.json {@code evaluator}, Adtest04 at run time) submits a fresh idea with the
 * Confidential choice under test, approves it, and then looks for its title in the Idea List. No shared fixture
 * ideas are used (Q17).
 */
class IdeaListVisibilityTest extends BaseTest {

    private static final String DATA_FILE = "idealist.json";
    /** Valid idea and approval data are shared with the submit and approval features instead of being duplicated. */
    private static final String SUBMIT_DATA_FILE = "submit.json";
    private static final String APPROVAL_DATA_FILE = "approval.json";

    @Test
    @Tag(Constants.TAG_P1)
    @Tag(Constants.TAG_FEATURE_IDEALIST)
    @DisplayName("TC-LEGACY-081 - Verify if this is a dashboard which displays all ideas that have been approved and "
            + "are not confidential.")
    @Description("An approved idea submitted with Confidential = No is shown in the Idea List.")
    @Story("Show approved non-confidential ideas in the Idea List")
    void approvedNonConfidentialIdeaIsShownInIdeaList() {
        IdeaVisibilityData visibility =
                JsonUtils.read(DATA_FILE, "approvedNonConfidentialIdea", IdeaVisibilityData.class);

        String ideaTitle = signInAndApproveNewIdea(visibility.getConfidential());

        // Action: Click On Idea List
        // TODO(TC-LEGACY-081): checked in the evaluator's session; the sheet's Employee user is the same account
        // (Adtest04) at run time. Use a separate employee account once one is configured (Q1).
        IdeaListPage ideaListPage = openIdeaListFilteredBy(ideaTitle);
        assertEquals(visibility.isExpectedVisible(), ideaListPage.isIdeaListed(ideaTitle),
                "Expected result: the approved, non-confidential idea '" + ideaTitle + "' is shown in the Idea List");
    }

    @Test
    @Tag(Constants.TAG_P1)
    @Tag(Constants.TAG_FEATURE_IDEALIST)
    @DisplayName("TC-LEGACY-082 - Verify dashboard displays all ideas that have been approved and confidential.")
    @Description("An approved idea submitted with Confidential = Yes is not shown in the Idea List.")
    @Story("Hide approved confidential ideas from the Idea List")
    void approvedConfidentialIdeaIsNotShownInIdeaList() {
        IdeaVisibilityData visibility = JsonUtils.read(DATA_FILE, "approvedConfidentialIdea", IdeaVisibilityData.class);

        String ideaTitle = signInAndApproveNewIdea(visibility.getConfidential());

        // Action: Click On Idea List
        // TODO(TC-LEGACY-082): checked in the evaluator's session; the sheet's Employee user is the same account
        // (Adtest04) at run time. Use a separate employee account once one is configured (Q1).
        IdeaListPage ideaListPage = openIdeaListFilteredBy(ideaTitle);
        assertEquals(visibility.isExpectedVisible(), ideaListPage.isIdeaListed(ideaTitle),
                "Expected result: the approved, confidential idea '" + ideaTitle + "' is not shown in the Idea List");
    }

    @Disabled("TC-LEGACY-089: blocked by Q19 (where the approver sets Confidential Yes/No is not specified)")
    @Test
    @Tag(Constants.TAG_P1)
    @Tag(Constants.TAG_FEATURE_IDEALIST)
    @DisplayName("TC-LEGACY-089 - Verify if Approver selects ‘Yes’ idea will be kept confidential, In this case it "
            + "will not be displayed in Idea List. If approver selects ‘No’ and approve the idea, it will be displayed "
            + "in Idealist for all employees.")
    @Description("The approver's Confidential choice decides whether the approved idea is shown in the Idea List: "
            + "Yes hides it, No shows it to all employees.")
    @Story("Approver decides Idea List visibility")
    void approverConfidentialChoiceDecidesIdeaListVisibility() {
        IdeaVisibilityData choice = JsonUtils.read(DATA_FILE, "approverConfidentialChoice", IdeaVisibilityData.class);

        // TODO(TC-LEGACY-089): the approver's Confidential Yes/No control is missing. The Evaluation page only has
        // "mark as Intellectual Property" Yes/No; Confidential exists on the Submit Idea form and on the admin edit
        // page (/admin-idea-edit/<id>), and which one "the approver selects" is not specified (Q19). Until then the
        // choice is made at submit, which TC-LEGACY-081 / TC-LEGACY-082 already cover.
        String ideaTitle = signInAndApproveNewIdea(choice.getConfidential());

        // Action: Click On Idea List
        IdeaListPage ideaListPage = openIdeaListFilteredBy(ideaTitle);
        assertEquals(choice.isExpectedVisible(), ideaListPage.isIdeaListed(ideaTitle),
                "Expected result: with the approver's Confidential = '" + choice.getConfidential() + "' the approved "
                        + "idea '" + ideaTitle + "' is " + (choice.isExpectedVisible() ? "" : "not ")
                        + "shown in the Idea List");
    }

    /**
     * Steps 2-6 and setup: signs in as the evaluator, submits a fresh Client idea with the given Confidential choice
     * (submit.json {@code clientIdea}) and approves it (approval.json {@code approve}). Returns the idea title.
     */
    private String signInAndApproveNewIdea(String confidential) {
        UserCredentials evaluator = JsonUtils.read(DATA_FILE, "evaluator", UserCredentials.class);
        IdeaData idea = JsonUtils.read(SUBMIT_DATA_FILE, "clientIdea", IdeaData.class);
        EvaluationData approve = JsonUtils.read(APPROVAL_DATA_FILE, "approve", EvaluationData.class);
        String ideaTitle = RandomDataUtils.uniqueString(JsonUtils.read(DATA_FILE, "titlePrefix", String.class));
        idea.setConfidential(confidential);

        signIn(evaluator);
        IdeaDetailPage detailPage = submitIdea(idea, ideaTitle);
        approveIdea(detailPage, approve);
        return ideaTitle;
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

    /** Setup: submits a Client idea through the Submit Idea form, as in TC-LEGACY-001. */
    private IdeaDetailPage submitIdea(IdeaData idea, String title) {
        IdeaImpact impact = idea.getImpacts().get(0);
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
        assertTrue(submitPage.isConfidentialSelected(idea.getConfidential()),
                "Setup: Confidential '" + idea.getConfidential() + "' is selected for '" + title + "'");

        assertTrue(submitPage.isSubmitIdeaEnabled(), "Setup: Submit Idea is enabled for '" + title + "'");
        IdeaDetailPage detailPage = submitPage.submitIdea();
        assertTrue(detailPage.isLoaded(),
                "Setup: '" + title + "' is submitted (actual URL: " + detailPage.getCurrentUrl() + ")");
        return detailPage;
    }

    /** Setup: approves the idea from its detail page, as in TC-LEGACY-116. */
    private void approveIdea(IdeaDetailPage detailPage, EvaluationData approve) {
        IdeaImpact impact = approve.getImpact();
        EvaluationPage evaluationPage = detailPage.clickEvaluate();
        assertTrue(evaluationPage.isLoaded(),
                "Setup: Evaluation page opens (actual URL: " + evaluationPage.getCurrentUrl() + ")");
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
                "Setup: The idea is approved");
    }

    /** Action "Click On Idea List": opens the Idea List and filters it by the idea's title. */
    private IdeaListPage openIdeaListFilteredBy(String title) {
        IdeaListPage ideaListPage = new IdeaListPage(driver()).open();
        assertTrue(ideaListPage.isLoaded(),
                "Action: Idea List loads (actual URL: " + ideaListPage.getCurrentUrl() + ")");
        return ideaListPage.filterByTitle(title);
    }
}
