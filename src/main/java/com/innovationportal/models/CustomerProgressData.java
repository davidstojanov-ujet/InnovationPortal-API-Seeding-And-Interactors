package com.innovationportal.models;

import java.util.List;

/**
 * Customer Initiative Progress case bound from {@code tracking.json} ({@code implementedLock} /
 * {@code rejectedLock} / {@code customerStatusList}). {@code statusPath} holds the Customer Status values saved
 * one after another; {@code expectedOptions} holds the Customer Status options expected afterwards.
 *
 * <pre>{@code
 * CustomerProgressData lock = JsonUtils.read("tracking.json", "implementedLock", CustomerProgressData.class);
 * }</pre>
 */
public class CustomerProgressData {

    private String testCaseId;
    private String holdReason;
    private String expectedHoldStatus;
    private List<String> actualBenefitStatuses;
    private List<String> statusPath;
    private List<String> expectedOptions;

    public CustomerProgressData() {
    }

    public String getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(String testCaseId) {
        this.testCaseId = testCaseId;
    }

    public String getHoldReason() {
        return holdReason;
    }

    public void setHoldReason(String holdReason) {
        this.holdReason = holdReason;
    }

    public String getExpectedHoldStatus() {
        return expectedHoldStatus;
    }

    public void setExpectedHoldStatus(String expectedHoldStatus) {
        this.expectedHoldStatus = expectedHoldStatus;
    }

    public List<String> getActualBenefitStatuses() {
        return actualBenefitStatuses;
    }

    public void setActualBenefitStatuses(List<String> actualBenefitStatuses) {
        this.actualBenefitStatuses = actualBenefitStatuses;
    }

    public List<String> getStatusPath() {
        return statusPath;
    }

    public void setStatusPath(List<String> statusPath) {
        this.statusPath = statusPath;
    }

    public List<String> getExpectedOptions() {
        return expectedOptions;
    }

    public void setExpectedOptions(List<String> expectedOptions) {
        this.expectedOptions = expectedOptions;
    }
}
