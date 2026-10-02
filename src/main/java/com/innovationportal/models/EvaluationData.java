package com.innovationportal.models;

import java.util.List;

/**
 * Evaluation page data bound from {@code approval.json} ({@code approve} / {@code draftWithoutImpact} /
 * {@code rejectWithoutComment}). {@code ratings} holds one value (1-10) per rating question, in question order.
 *
 * <pre>{@code
 * EvaluationData approve = JsonUtils.read("approval.json", "approve", EvaluationData.class);
 * }</pre>
 */
public class EvaluationData {

    private String testCaseId;
    private List<Integer> ratings;
    private IdeaImpact impact;
    private String businessUseCase;
    private String reason;
    private String expectedStatusAfterDraft;
    private String expectedStatusAfterSubmit;
    private String expectedOverallScore;
    private String expectedMessage;

    public EvaluationData() {
    }

    public String getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(String testCaseId) {
        this.testCaseId = testCaseId;
    }

    public List<Integer> getRatings() {
        return ratings;
    }

    public void setRatings(List<Integer> ratings) {
        this.ratings = ratings;
    }

    public IdeaImpact getImpact() {
        return impact;
    }

    public void setImpact(IdeaImpact impact) {
        this.impact = impact;
    }

    public String getBusinessUseCase() {
        return businessUseCase;
    }

    public void setBusinessUseCase(String businessUseCase) {
        this.businessUseCase = businessUseCase;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getExpectedStatusAfterDraft() {
        return expectedStatusAfterDraft;
    }

    public void setExpectedStatusAfterDraft(String expectedStatusAfterDraft) {
        this.expectedStatusAfterDraft = expectedStatusAfterDraft;
    }

    public String getExpectedStatusAfterSubmit() {
        return expectedStatusAfterSubmit;
    }

    public void setExpectedStatusAfterSubmit(String expectedStatusAfterSubmit) {
        this.expectedStatusAfterSubmit = expectedStatusAfterSubmit;
    }

    public String getExpectedOverallScore() {
        return expectedOverallScore;
    }

    public void setExpectedOverallScore(String expectedOverallScore) {
        this.expectedOverallScore = expectedOverallScore;
    }

    public String getExpectedMessage() {
        return expectedMessage;
    }

    public void setExpectedMessage(String expectedMessage) {
        this.expectedMessage = expectedMessage;
    }
}
