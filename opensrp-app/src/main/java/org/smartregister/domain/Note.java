package org.smartregister.domain;

import org.joda.time.DateTime;

/**
 * Created by samuelgithengi on 11/22/18.
 */
public class Note {
    private String authorString;

    private DateTime time;

    private String text;

    public String getAuthorString() {
        return authorString;
    }

    public void setAuthorString(String authorString) {
        this.authorString = authorString;
    }

    public DateTime getTime() {
        return time;
    }

    public void setTime(DateTime time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
