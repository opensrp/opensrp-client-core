package org.smartregister.domain;

import org.joda.time.LocalDate;

/**
 * Created by samuelgithengi on 11/22/18.
 */
public class ExecutionPeriod {

    private LocalDate start;

    private LocalDate end;

    public LocalDate getStart() {
        return start;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }
}
