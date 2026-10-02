package com.innovationportal.models;

/**
 * Submit-form field limits bound from {@code validation.json} ({@code limits}), taken from the form's
 * {@code maxlength} attributes and hints. {@code maxDomains} is still an open question, so it stays text.
 *
 * <pre>{@code
 * FieldLimits limits = JsonUtils.read("validation.json", "limits", FieldLimits.class);
 * }</pre>
 */
public class FieldLimits {

    private int titleMaxLength;
    private int summaryMaxLength;
    private int innovationDetailsMaxLength;
    private int businessUseCaseMaxLength;
    private int impactAmountMaxLength;
    private int impactDescriptionMaxLength;
    private int maxAttachments;
    private int maxAttachmentSizeMb;
    private String maxDomains;

    public FieldLimits() {
    }

    public int getTitleMaxLength() {
        return titleMaxLength;
    }

    public void setTitleMaxLength(int titleMaxLength) {
        this.titleMaxLength = titleMaxLength;
    }

    public int getSummaryMaxLength() {
        return summaryMaxLength;
    }

    public void setSummaryMaxLength(int summaryMaxLength) {
        this.summaryMaxLength = summaryMaxLength;
    }

    public int getInnovationDetailsMaxLength() {
        return innovationDetailsMaxLength;
    }

    public void setInnovationDetailsMaxLength(int innovationDetailsMaxLength) {
        this.innovationDetailsMaxLength = innovationDetailsMaxLength;
    }

    public int getBusinessUseCaseMaxLength() {
        return businessUseCaseMaxLength;
    }

    public void setBusinessUseCaseMaxLength(int businessUseCaseMaxLength) {
        this.businessUseCaseMaxLength = businessUseCaseMaxLength;
    }

    public int getImpactAmountMaxLength() {
        return impactAmountMaxLength;
    }

    public void setImpactAmountMaxLength(int impactAmountMaxLength) {
        this.impactAmountMaxLength = impactAmountMaxLength;
    }

    public int getImpactDescriptionMaxLength() {
        return impactDescriptionMaxLength;
    }

    public void setImpactDescriptionMaxLength(int impactDescriptionMaxLength) {
        this.impactDescriptionMaxLength = impactDescriptionMaxLength;
    }

    public int getMaxAttachments() {
        return maxAttachments;
    }

    public void setMaxAttachments(int maxAttachments) {
        this.maxAttachments = maxAttachments;
    }

    public int getMaxAttachmentSizeMb() {
        return maxAttachmentSizeMb;
    }

    public void setMaxAttachmentSizeMb(int maxAttachmentSizeMb) {
        this.maxAttachmentSizeMb = maxAttachmentSizeMb;
    }

    public String getMaxDomains() {
        return maxDomains;
    }

    public void setMaxDomains(String maxDomains) {
        this.maxDomains = maxDomains;
    }
}
