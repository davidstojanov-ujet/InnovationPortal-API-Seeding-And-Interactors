package com.innovationportal.models;

import java.util.List;

/**
 * Submit Idea form data bound from {@code submit.json} ({@code clientIdea} / {@code orgIdea}).
 *
 * <pre>{@code
 * IdeaData idea = JsonUtils.read("submit.json", "clientIdea", IdeaData.class);
 * }</pre>
 */
public class IdeaData {

    private String testCaseId;
    private String innovationType;
    private String customerName;
    private String projectName;
    private String department;
    private String innovationCategory;
    private List<String> domains;
    private String summary;
    private String innovationDetails;
    private String confidential;
    private List<IdeaImpact> impacts;
    private String businessUseCase;
    private String expectedInitialStatus;

    public IdeaData() {
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

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getInnovationCategory() {
        return innovationCategory;
    }

    public void setInnovationCategory(String innovationCategory) {
        this.innovationCategory = innovationCategory;
    }

    public List<String> getDomains() {
        return domains;
    }

    public void setDomains(List<String> domains) {
        this.domains = domains;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getInnovationDetails() {
        return innovationDetails;
    }

    public void setInnovationDetails(String innovationDetails) {
        this.innovationDetails = innovationDetails;
    }

    public String getConfidential() {
        return confidential;
    }

    public void setConfidential(String confidential) {
        this.confidential = confidential;
    }

    public List<IdeaImpact> getImpacts() {
        return impacts;
    }

    public void setImpacts(List<IdeaImpact> impacts) {
        this.impacts = impacts;
    }

    public String getBusinessUseCase() {
        return businessUseCase;
    }

    public void setBusinessUseCase(String businessUseCase) {
        this.businessUseCase = businessUseCase;
    }

    public String getExpectedInitialStatus() {
        return expectedInitialStatus;
    }

    public void setExpectedInitialStatus(String expectedInitialStatus) {
        this.expectedInitialStatus = expectedInitialStatus;
    }
}
