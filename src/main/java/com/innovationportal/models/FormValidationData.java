package com.innovationportal.models;

import java.util.List;

/**
 * Submit-form validation scenario bound from {@code validation.json} ({@code clientForm} / {@code orgForm}).
 * {@code mandatoryFields} are the field keys understood by {@code IdeaSubmitPage.isRequiredMarkerShown}.
 *
 * <pre>{@code
 * FormValidationData form = JsonUtils.read("validation.json", "clientForm", FormValidationData.class);
 * }</pre>
 */
public class FormValidationData {

    private String testCaseId;
    private String innovationType;
    private List<String> mandatoryFields;
    private String expectedMessages;

    public FormValidationData() {
    }

    public String getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(String testCaseId) {
        this.testCaseId = testCaseId;
    }

    public String getInnovationType() {
        return innovationType;
    }

    public void setInnovationType(String innovationType) {
        this.innovationType = innovationType;
    }

    public List<String> getMandatoryFields() {
        return mandatoryFields;
    }

    public void setMandatoryFields(List<String> mandatoryFields) {
        this.mandatoryFields = mandatoryFields;
    }

    public String getExpectedMessages() {
        return expectedMessages;
    }

    public void setExpectedMessages(String expectedMessages) {
        this.expectedMessages = expectedMessages;
    }
}
