package org.ei.opensrp.domain;

import java.io.Serializable;

public enum FetchStatus implements Displayable, Serializable{
    fetchStarted("Update started"),
    fetched("Update successful."),
    nothingFetched("Already up to date."),
    fetchedFailed("Update failed. Please try again."),
    noConnection("No network connection detected");
    private String displayValue;

    private FetchStatus(String displayValue) {
        this.displayValue = displayValue;
    }

    public String displayValue() {
        return displayValue;
    }
}
