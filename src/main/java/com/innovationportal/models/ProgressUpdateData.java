package com.innovationportal.models;

/**
 * Values used to save a Customer Status on the Customer Initiative Progress page, bound from {@code tracking.json}
 * ({@code progressUpdate}). The outcome and the Impact of idea are only filled when the page shows them.
 *
 * <pre>{@code
 * ProgressUpdateData update = JsonUtils.read("tracking.json", "progressUpdate", ProgressUpdateData.class);
 * }</pre>
 */
public class ProgressUpdateData {

    private String actualBenefitOutcome;
    private IdeaImpact impact;
    private String comment;

    public ProgressUpdateData() {
    }

    public String getActualBenefitOutcome() {
        return actualBenefitOutcome;
    }

    public void setActualBenefitOutcome(String actualBenefitOutcome) {
        this.actualBenefitOutcome = actualBenefitOutcome;
    }

    public IdeaImpact getImpact() {
        return impact;
    }

    public void setImpact(IdeaImpact impact) {
        this.impact = impact;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
