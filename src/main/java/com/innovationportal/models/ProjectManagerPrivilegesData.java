package com.innovationportal.models;

/**
 * Project Manager privileges case bound from {@code tracking.json} ({@code projectManagerPrivileges}):
 * the Customer Status and comment the Project Manager saves on Customer Initiative Progress. {@code note}
 * documents how the Project Manager role is played (it is not used by the test).
 *
 * <pre>{@code
 * ProjectManagerPrivilegesData pm = JsonUtils.read("tracking.json", "projectManagerPrivileges",
 *         ProjectManagerPrivilegesData.class);
 * }</pre>
 */
public class ProjectManagerPrivilegesData {

    private String testCaseId;
    private String note;
    private String customerStatus;
    private String comment;

    public ProjectManagerPrivilegesData() {
    }

    public String getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(String testCaseId) {
        this.testCaseId = testCaseId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getCustomerStatus() {
        return customerStatus;
    }

    public void setCustomerStatus(String customerStatus) {
        this.customerStatus = customerStatus;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
