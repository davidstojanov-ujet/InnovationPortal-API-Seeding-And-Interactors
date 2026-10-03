package com.innovationportal.models;

/**
 * Project Manager privileges case bound from {@code tracking.json} ({@code projectManagerPrivileges}).
 * {@code ideaId} is an approved Client idea on the Project Manager's project.
 *
 * <pre>{@code
 * ProjectManagerPrivilegesData pm = JsonUtils.read("tracking.json", "projectManagerPrivileges",
 *         ProjectManagerPrivilegesData.class);
 * }</pre>
 */
public class ProjectManagerPrivilegesData {

    private String testCaseId;
    private String ideaId;
    private String expected;

    public ProjectManagerPrivilegesData() {
    }

    public String getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(String testCaseId) {
        this.testCaseId = testCaseId;
    }

    public String getIdeaId() {
        return ideaId;
    }

    public void setIdeaId(String ideaId) {
        this.ideaId = ideaId;
    }

    public String getExpected() {
        return expected;
    }

    public void setExpected(String expected) {
        this.expected = expected;
    }
}
