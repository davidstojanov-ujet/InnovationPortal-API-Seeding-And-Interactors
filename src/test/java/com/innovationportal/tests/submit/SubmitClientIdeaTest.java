package com.innovationportal.tests.submit;

import com.innovationportal.base.BaseTest;
import com.innovationportal.constants.Constants;
import com.innovationportal.models.IdeaData;
import com.innovationportal.models.IdeaImpact;
import com.innovationportal.models.UserCredentials;
import com.innovationportal.pages.HomePage;
import com.innovationportal.pages.IdeaDetailPage;
import com.innovationportal.pages.IdeaSubmitPage;
import com.innovationportal.pages.LoginPage;
import com.innovationportal.pages.MyIdeasPage;
import com.innovationportal.pages.XornetGlobalScreenPage;
import com.innovationportal.utils.JsonUtils;
import com.innovationportal.utils.RandomDataUtils;
import io.qameta.allure.Description;
import io.qameta.allure.Story;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** Golden path: an employee submits a Client Initiative idea and finds it in My Ideas. */
class SubmitClientIdeaTest extends BaseTest {

    private static final String DATA_FILE = "submit.json";

    @Test
    @Tag(Constants.TAG_P1)
    @Tag(Constants.TAG_FEATURE_SUBMIT)
    @DisplayName("TC-LEGACY-001 - Verify if an employee who is allocated to a project is able to submit the Client initiative innovation")
    @Description("Employee signs in through ADFS, submits a Client Initiative idea with all mandatory fields, "
            + "lands on the new idea's detail page and finds the idea in My Ideas with its initial status.")
    @Story("Submit a Client Initiative idea")
    void employeeSubmitsClientInitiativeIdea() {
        // Arrange
        UserCredentials employee = JsonUtils.read(DATA_FILE, "user", UserCredentials.class);
        IdeaData idea = JsonUtils.read(DATA_FILE, "clientIdea", IdeaData.class);
        String ideaTitle = RandomDataUtils.uniqueString(JsonUtils.read(DATA_FILE, "titlePrefix", String.class));
        String domain = idea.getDomains().get(0);
        IdeaImpact impact = idea.getImpacts().get(0);
        LoginPage loginPage = new LoginPage(driver());

        // Steps 2-6: sign in and reach the portal
        assertTrue(loginPage.isLoaded(), "Step 2: Portal or ADFS login page is displayed");

        XornetGlobalScreenPage globalScreen = loginPage.loginAs(employee);
        assertFalse(loginPage.isOnSignInPage(),
                "Step 3: ADFS authenticates successfully (ADFS error: '" + loginPage.getErrorText() + "')");

        HomePage homePage = globalScreen.dismissIfShown();
        assertTrue(homePage.isLoaded(),
                "Step 4-5: Global Screen is dismissed and the Innovation Portal landing page is visible");
        assertTrue(homePage.isSubmitIdeaLinkDisplayed(),
                "Step 5: Landing page shows role-appropriate navigation (+ Submit Idea)");
        assertFalse(homePage.getLoggedInUserName().isBlank(),
                "Step 6: Logged-in user context is shown in the header");

        // Steps 7-8: open the form and choose Client Initiative
        IdeaSubmitPage submitPage = homePage.clickSubmitIdea();
        assertTrue(submitPage.isLoaded(), "Step 7: Submit Idea form loads at /idea-submit");

        submitPage.selectInnovationType(idea.getInnovationType());
        assertEquals(idea.getInnovationType(), submitPage.getSelectedInnovationType(),
                "Step 8: Client Initiative is selected");
        assertTrue(submitPage.isCustomerNameDisplayed(), "Step 8: Customer Name field appears");
        assertTrue(submitPage.isProjectNameDisplayed(), "Step 8: Project Name field appears");
        assertTrue(submitPage.isDepartmentHidden(), "Step 8: Department is hidden");

        // Steps 9-11: fill every mandatory field and the optional Business Use Case
        submitPage.selectCustomer(idea.getCustomerName(), idea.getProjectName())
                .selectProject(idea.getProjectName())
                .enterTitle(ideaTitle)
                .selectClientCategory(idea.getInnovationCategory())
                .addDomain(domain)
                .enterSummary(idea.getSummary())
                .enterInnovationDetails(idea.getInnovationDetails())
                .selectConfidential(idea.getConfidential())
                .addImpact(impact.getKey(), impact.getAmount(), impact.getDescription())
                .enterBusinessUseCaseIfShown(idea.getBusinessUseCase());

        assertEquals(idea.getCustomerName(), submitPage.getSelectedCustomer(),
                "Step 9: Customer Name shows a valid selection");
        assertEquals(idea.getProjectName(), submitPage.getSelectedProjectValue(),
                "Step 9: Project Name shows a valid selection");
        assertEquals(ideaTitle, submitPage.getTitleValue(), "Step 9: Idea Title accepts the unique value");
        assertTrue(submitPage.isClientCategorySelected(idea.getInnovationCategory()),
                "Step 9: Innovation Category is selected");
        assertTrue(submitPage.getDomainValue().contains(domain), "Step 9: Domain holds a valid selection");
        assertEquals(idea.getSummary(), submitPage.getSummaryValue(), "Step 9: Brief description accepts text");
        assertEquals(idea.getInnovationDetails(), submitPage.getInnovationDetailsValue(),
                "Step 9: Innovation Details accepts text");
        assertTrue(submitPage.isConfidentialSelected(idea.getConfidential()),
                "Step 9: Confidential choice is selected");
        assertTrue(submitPage.isImpactSelected(impact.getKey()), "Step 9: Impact of Idea option is ticked");
        assertEquals(impact.getAmount(), submitPage.getImpactAmount(impact.getKey()),
                "Step 9: Impact of Idea Amount accepts the value");
        assertEquals(idea.getBusinessUseCase(), submitPage.getBusinessUseCaseValue(),
                "Step 10: Optional Business Use Case accepts text");

        // Steps 12-14: submit
        assertTrue(submitPage.isSubmitIdeaEnabled(),
                "Step 11-12: No mandatory field remains empty and Submit Idea becomes enabled");
        IdeaDetailPage detailPage = submitPage.submitIdea();
        assertTrue(detailPage.isLoaded(),
                "Step 13-14: Form submits and the browser navigates to the idea detail page (actual URL: "
                        + detailPage.getCurrentUrl() + ")");
        assertFalse(detailPage.getIdeaId().isEmpty(), "Step 14: Detail page URL carries a new idea ID");

        // Step 15: the idea is listed in My Ideas
        MyIdeasPage myIdeasPage = new MyIdeasPage(driver()).open();
        assertTrue(myIdeasPage.isIdeaListed(ideaTitle), "Step 15: Submitted idea appears in My Ideas");
        assertEquals(idea.getInnovationType(), myIdeasPage.getInnovationType(ideaTitle),
                "Step 15: My Ideas shows Innovation Type 'Client Initiative'");
        assertEquals(idea.getExpectedInitialStatus(), myIdeasPage.getStatus(ideaTitle),
                "Step 15: My Ideas shows the initial status 'Pending Approval'");
    }
}
