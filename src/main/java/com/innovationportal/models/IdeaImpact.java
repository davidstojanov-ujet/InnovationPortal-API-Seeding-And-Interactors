package com.innovationportal.models;

/** One "Impact of Idea" entry of the Submit Idea form, bound from JSON test data. */
public class IdeaImpact {

    private String key;
    private String label;
    private String amount;
    private String description;

    public IdeaImpact() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
