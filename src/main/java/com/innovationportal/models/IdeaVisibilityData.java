package com.innovationportal.models;

/**
 * Idea List visibility case bound from {@code idealist.json} ({@code approvedNonConfidentialIdea} /
 * {@code approvedConfidentialIdea}): the Confidential choice of the submitted idea and whether the approved idea
 * is expected in the Idea List.
 *
 * <pre>{@code
 * IdeaVisibilityData visible = JsonUtils.read("idealist.json", "approvedNonConfidentialIdea", IdeaVisibilityData.class);
 * }</pre>
 */
public class IdeaVisibilityData {

    private String testCaseId;
    private String confidential;
    private boolean expectedVisible;
    private String note;

    public IdeaVisibilityData() {
    }

    public String getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(String testCaseId) {
        this.testCaseId = testCaseId;
    }

    public String getConfidential() {
        return confidential;
    }

    public void setConfidential(String confidential) {
        this.confidential = confidential;
    }

    public boolean isExpectedVisible() {
        return expectedVisible;
    }

    public void setExpectedVisible(boolean expectedVisible) {
        this.expectedVisible = expectedVisible;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
