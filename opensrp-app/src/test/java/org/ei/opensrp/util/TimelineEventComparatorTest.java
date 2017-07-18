package org.ei.opensrp.util;

import org.ei.opensrp.domain.TimelineEvent;
import org.joda.time.LocalDate;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class TimelineEventComparatorTest {
    @Test
    public void shouldReturnMinusOneWhenFirstTimeLineEventOccursLater() throws Exception {
        int result = compareTimelineEvents(LocalDate.now(), LocalDate.now().minusDays(1));

        assertEquals(-1, result);
    }

    @Test
    public void shouldReturnOneWhenFirstTimeLineEventOccursEarlier() throws Exception {
        int result = compareTimelineEvents(LocalDate.now().minusDays(1), LocalDate.now());

        assertEquals(1, result);
    }

    @Test
    public void shouldReturnZeroWhenFirstTimeLineEventOccurAtSameTime() throws Exception {
        int result = compareTimelineEvents(LocalDate.now(), LocalDate.now());

        assertEquals(0, result);
    }

    private int compareTimelineEvents(LocalDate date1, LocalDate date2) {
        return new TimelineEventComparator().compare(
                new TimelineEvent("CASE A", "FPCHANGE", date1, "FP Method Change", null, null),
                new TimelineEvent("CASE A", "PREGNANT", date2, "GOT PREGNANT", null, null));
    }
}
