package org.smartregister.domain;

public enum ResponseStatus  {
    failure("failure"),
    success("success");

    private String displayValue;

    ResponseStatus(String failure) {
        displayValue = failure;
    }

    public String displayValue() {
        return displayValue;
    }

    public void setDisplayValue(String displayValue) {
        this.displayValue = displayValue;
    }
}
