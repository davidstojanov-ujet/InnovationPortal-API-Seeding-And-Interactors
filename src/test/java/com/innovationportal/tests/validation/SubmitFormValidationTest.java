package com.innovationportal.tests.validation;

import com.innovationportal.base.BaseTest;
import com.innovationportal.constants.Constants;
import com.innovationportal.models.FieldLimits;
import com.innovationportal.models.FormValidationData;
import com.innovationportal.models.IdeaData;
import com.innovationportal.models.IdeaImpact;
import com.innovationportal.models.UserCredentials;
import com.innovationportal.pages.HomePage;
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

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Submit Idea form validation for Client and Organisation initiatives: red asterisks on mandatory
 * fields, field length limits, and Submit Idea blocked until every mandatory field is filled.
 * The form is never submitted here; the positive submit is covered by the submit feature tests.
 */
class SubmitFormValidationTest extends BaseTest {

    private static final String DATA_FILE = "validation.json";
    /** Valid form values are shared with the submit feature instead of being duplicated here. */
    private static final String SUBMIT_DATA_FILE = "submit.json";

    @Test
    @Tag(Constants.TAG_P1)
    @Tag(Constants.TAG_FEATURE_VALIDATION)
    @DisplayName("TC-LEGACY-007 - To verify the validation for Client Innovation Form")
    @Description("Employee opens the Client Initiative form: every mandatory field shows a red asterisk, text "
            + "beyond the field limits is rejected, and Submit Idea stays disabled until all mandatory fields are filled.")
    @Story("Validate the Client Initiative form")
    void clientFormBlocksSubmitUntilMandatoryFieldsAreFilled() {
        // Arrange
        UserCredentials employee = JsonUtils.read(DATA_FILE, "user", UserCredentials.class);
        FormValidationData form = JsonUtils.read(DATA_FILE, "clientForm", FormValidationData.class);
        FieldLimits limits = JsonUtils.read(DATA_FILE, "limits", FieldLimits.class);
        IdeaData idea = JsonUtils.read(SUBMIT_DATA_FILE, "clientIdea", IdeaData.class);
        String ideaTitle = RandomDataUtils.uniqueString(JsonUtils.read(DATA_FILE, "titlePrefix", String.class));
        IdeaImpact impact = idea.getImpacts().get(0);

        // Steps 2-7: sign in and open the Submit Idea form
        IdeaSubmitPage submitPage = openSubmitForm(employee);

        // Step 8: choose Client Initiative
        submitPage.selectInnovationType(form.getInnovationType());
        assertEquals(form.getInnovationType(), submitPage.getSelectedInnovationType(),
                "Step 8: Client Initiative is selected");
        assertTrue(submitPage.isCustomerNameDisplayed(), "Step 8: Customer Name field appears");
        assertTrue(submitPage.isProjectNameDisplayed(), "Step 8: Project Name field appears");
        assertTrue(submitPage.isDepartmentHidden(), "Step 8: Department is hidden");

        // Steps 9-10: boundary data and missing mandatory fields
        assertTrue(submitPage.isSubmitIdeaDisabled(),
                "Step 10: Submit Idea is disabled while the mandatory fields are empty");
        assertFieldLimitsEnforced(submitPage, limits);
        // TODO(TC-LEGACY-007): validation message texts, the "extra domains" limit and an invalid attachment
        // file are not specified (Q9; maxDomains / invalidAttachmentType in validation.json), so they are not asserted.

        submitPage.selectCustomer(idea.getCustomerName(), idea.getProjectName())
                .selectProject(idea.getProjectName())
                .enterTitle(ideaTitle);
        assertTrue(submitPage.isSubmitIdeaDisabled(),
                "Step 10: Submit Idea stays disabled while Innovation Category, Domain and Brief description are missing");

        // Innovators is pre-filled and Confidential pre-selected ("No") when the form loads, so they are never
        // missing. Impact of Idea has no asterisk and does not gate Submit; it is filled as part of the valid data.
        submitPage.selectClientCategory(idea.getInnovationCategory())
                .addDomain(idea.getDomains().get(0))
                .enterInnovationDetails(idea.getInnovationDetails())
                .selectConfidential(idea.getConfidential())
                .addImpact(impact.getKey(), impact.getAmount(), impact.getDescription());
        boolean submitBlockedWithoutSummary = submitPage.isSubmitIdeaDisabled();

        // Step 11: valid data enables Submit Idea. Not submitted: TC-LEGACY-001 covers the positive
        // submit and Q10/Q12 (submit vs stop, data cleanup) are still open.
        submitPage.enterSummary(idea.getSummary());

        // Steps 10-11 (Expected Result): grouped so that every check is reported, not only the first failure
        assertAll("Steps 10-11: Submit is blocked without mandatory fields and mandatory fields show a red asterisk",
                Stream.concat(
                        Stream.<Executable>of(
                                () -> assertTrue(submitBlockedWithoutSummary,
                                        "Step 10: Submit Idea stays disabled while only Brief description is missing"),
                                () -> assertTrue(submitPage.isSubmitIdeaEnabled(),
                                        "Step 11: Submit Idea becomes enabled once every mandatory field holds valid data")),
                        form.getMandatoryFields().stream().<Executable>map(field -> () -> assertTrue(
                                submitPage.isRequiredMarkerShown(field),
                                "Step 11: Mandatory field '" + field + "' is shown with a red asterisk"))));
    }

    @Test
    @Tag(Constants.TAG_P1)
    @Tag(Constants.TAG_FEATURE_VALIDATION)
    @DisplayName("TC-LEGACY-008 - To verify the validation for Organization Innovation Form")
    @Description("Employee opens the Organisation Initiative form: every mandatory field shows a red asterisk, text "
            + "beyond the field limits is rejected, and Submit Idea stays disabled until all mandatory fields are filled.")
    @Story("Validate the Organisation Initiative form")
    void orgFormBlocksSubmitUntilMandatoryFieldsAreFilled() {
        // Arrange
        UserCredentials employee = JsonUtils.read(DATA_FILE, "user", UserCredentials.class);
        FormValidationData form = JsonUtils.read(DATA_FILE, "orgForm", FormValidationData.class);
        FieldLimits limits = JsonUtils.read(DATA_FILE, "limits", FieldLimits.class);
        IdeaData idea = JsonUtils.read(SUBMIT_DATA_FILE, "orgIdea", IdeaData.class);
        String ideaTitle = RandomDataUtils.uniqueString(JsonUtils.read(DATA_FILE, "titlePrefix", String.class));
        IdeaImpact impact = idea.getImpacts().get(0);

        // Steps 2-7: sign in and open the Submit Idea form
        IdeaSubmitPage submitPage = openSubmitForm(employee);

        // Step 8: choose Organisation Initiative
        submitPage.selectInnovationType(form.getInnovationType());
        assertEquals(form.getInnovationType(), submitPage.getSelectedInnovationType(),
                "Step 8: Organisation Initiative is selected");
        assertTrue(submitPage.isDepartmentDisplayed(), "Step 8: Department field appears");
        assertTrue(submitPage.isCustomerNameHidden(), "Step 8: Customer Name is hidden");
        assertTrue(submitPage.isProjectNameHidden(), "Step 8: Project Name is hidden");

        // Steps 9-10: boundary data and missing mandatory fields
        assertTrue(submitPage.isSubmitIdeaDisabled(),
                "Step 10: Submit Idea is disabled while the mandatory fields are empty");
        assertFieldLimitsEnforced(submitPage, limits);
        // TODO(TC-LEGACY-008): validation message texts, the "extra domains" limit and an invalid attachment
        // file are not specified (Q9; maxDomains / invalidAttachmentType in validation.json), so they are not asserted.

        submitPage.selectDepartment(idea.getDepartment())
                .enterTitle(ideaTitle);
        assertTrue(submitPage.isSubmitIdeaDisabled(),
                "Step 10: Submit Idea stays disabled while Innovation Category, Domain and Brief description are missing");

        // Innovators is pre-filled and Confidential pre-selected ("No") when the form loads, so they are never
        // missing. Impact of Idea has no asterisk and does not gate Submit; it is filled as part of the valid data.
        submitPage.selectOrgCategory(idea.getInnovationCategory())
                .addDomain(idea.getDomains().get(0))
                .enterInnovationDetails(idea.getInnovationDetails())
                .selectConfidential(idea.getConfidential())
                .addImpact(impact.getKey(), impact.getAmount(), impact.getDescription());
        boolean submitBlockedWithoutSummary = submitPage.isSubmitIdeaDisabled();

        // Step 11: valid data enables Submit Idea. Not submitted: TC-LEGACY-004 covers the positive
        // submit and Q10/Q12 (submit vs stop, data cleanup) are still open.
        submitPage.enterSummary(idea.getSummary());

        // Steps 10-11 (Expected Result): grouped so that every check is reported, not only the first failure
        assertAll("Steps 10-11: Submit is blocked without mandatory fields and mandatory fields show a red asterisk",
                Stream.concat(
                        Stream.<Executable>of(
                                () -> assertTrue(submitBlockedWithoutSummary,
                                        "Step 10: Submit Idea stays disabled while only Brief description is missing"),
                                () -> assertTrue(submitPage.isSubmitIdeaEnabled(),
                                        "Step 11: Submit Idea becomes enabled once every mandatory field holds valid data")),
                        form.getMandatoryFields().stream().<Executable>map(field -> () -> assertTrue(
                                submitPage.isRequiredMarkerShown(field),
                                "Step 11: Mandatory field '" + field + "' is shown with a red asterisk"))));
    }

    /** Steps 2-7, shared by both forms: sign in through ADFS and open the Submit Idea form. */
    private IdeaSubmitPage openSubmitForm(UserCredentials employee) {
        LoginPage loginPage = new LoginPage(driver());
        assertTrue(loginPage.isLoaded(), "Step 2: Portal or ADFS login page is displayed");

        XornetGlobalScreenPage globalScreen = loginPage.loginAs(employee);
        assertFalse(loginPage.isOnSignInPage(),
                "Step 3: ADFS authenticates successfully (ADFS error: '" + loginPage.getErrorText() + "')");

        HomePage homePage = globalScreen.dismissIfShown();
        assertTrue(homePage.isLoaded(),
                "Step 4-5: Global Screen is dismissed and the Innovation Portal landing page is visible");
        assertFalse(homePage.getLoggedInUserName().isBlank(),
                "Step 6: Logged-in user context is shown in the header");

        IdeaSubmitPage submitPage = homePage.clickSubmitIdea();
        assertTrue(submitPage.isLoaded(), "Step 7: Submit Idea form loads at /idea-submit");
        return submitPage;
    }

    /** Step 9 boundary data: one character over each limit is cut back to the limit. */
    private void assertFieldLimitsEnforced(IdeaSubmitPage submitPage, FieldLimits limits) {
        submitPage.enterTitle(RandomDataUtils.randomAlphanumeric(limits.getTitleMaxLength() + 1))
                .enterSummary(RandomDataUtils.randomAlphanumeric(limits.getSummaryMaxLength() + 1))
                .enterInnovationDetails(RandomDataUtils.randomAlphanumeric(limits.getInnovationDetailsMaxLength() + 1));
        assertEquals(limits.getTitleMaxLength(), submitPage.getTitleValue().length(),
                "Step 9: Idea Title rejects text beyond " + limits.getTitleMaxLength() + " characters");
        assertEquals(limits.getSummaryMaxLength(), submitPage.getSummaryValue().length(),
                "Step 9: Brief description rejects text beyond " + limits.getSummaryMaxLength() + " characters");
        assertEquals(limits.getInnovationDetailsMaxLength(), submitPage.getInnovationDetailsValue().length(),
                "Step 9: Innovation Details rejects text beyond " + limits.getInnovationDetailsMaxLength() + " characters");
        assertTrue(submitPage.isSubmitIdeaDisabled(),
                "Step 10: Submit Idea stays disabled when only free-text fields are filled");
        // Leave Brief description empty again so that it is the last missing mandatory field in step 10.
        submitPage.clearSummary();
    }
}
