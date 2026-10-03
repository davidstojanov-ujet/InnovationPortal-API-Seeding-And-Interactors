package com.innovationportal.models;

import java.util.List;

/**
 * Accelerator (Intellectual Property) radio gate bound from {@code accelerator.json} ({@code clientAccelerator} /
 * {@code orgAccelerator}). {@code statusPath} holds the progress statuses saved one after another;
 * {@code gateStatus} is the only status at which the radio may be shown.
 *
 * <pre>{@code
 * AcceleratorGateData gate = JsonUtils.read("accelerator.json", "clientAccelerator", AcceleratorGateData.class);
 * }</pre>
 */
public class AcceleratorGateData {

    private String testCaseId;
    private String practice;
    private String rolesThatSeeRadio;
    private List<String> statusPath;
    private String gateStatus;

    public AcceleratorGateData() {
    }

    public String getTestCaseId() {
        return testCaseId;
    }

    public void setTestCaseId(String testCaseId) {
        this.testCaseId = testCaseId;
    }

    public String getPractice() {
        return practice;
    }

    public void setPractice(String practice) {
        this.practice = practice;
    }

    public String getRolesThatSeeRadio() {
        return rolesThatSeeRadio;
    }

    public void setRolesThatSeeRadio(String rolesThatSeeRadio) {
        this.rolesThatSeeRadio = rolesThatSeeRadio;
    }

    public List<String> getStatusPath() {
        return statusPath;
    }

    public void setStatusPath(List<String> statusPath) {
        this.statusPath = statusPath;
    }

    public String getGateStatus() {
        return gateStatus;
    }

    public void setGateStatus(String gateStatus) {
        this.gateStatus = gateStatus;
    }
}
