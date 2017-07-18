package org.ei.opensrp.domain;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.ei.opensrp.domain.ReportIndicator.CONDOM;
import static org.ei.opensrp.domain.ReportIndicator.parseToReportIndicator;

public class ReportIndicatorTest {

    @Test
    public void shouldParseStringToReportIndicator() throws Exception {
        assertEquals(CONDOM, parseToReportIndicator("condom"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowExceptionWhenValueIsNotAValidReportIndicator() throws Exception {
        parseToReportIndicator("invalid value");
    }
}
