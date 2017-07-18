package org.ei.opensrp.repository;

import org.robolectric.RobolectricTestRunner;
import org.ei.opensrp.domain.EligibleCouple;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(RobolectricTestRunner.class)
public class AllEligibleCouplesTest {
    @Mock
    private EligibleCoupleRepository eligibleCoupleRepository;
    @Mock
    private TimelineEventRepository timelineEventRepository;
    @Mock
    private AlertRepository alertRepository;

    private AllEligibleCouples allEligibleCouples;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        allEligibleCouples = new AllEligibleCouples(eligibleCoupleRepository, alertRepository, timelineEventRepository);
    }

    @Test
    public void shouldFetchAllAlertsFromRepository() throws Exception {
        List<EligibleCouple> expectedCouples = Arrays.asList(new EligibleCouple("Case X", "Wife 1", "Husband 1", "EC Number 1", "village", "subcenter", new HashMap<String, String>()),
                new EligibleCouple("Case Y", "Wife 2", "Husband 2", "EC Number 2", "village", "subcenter", new HashMap<String, String>()));
        when(eligibleCoupleRepository.allEligibleCouples()).thenReturn(expectedCouples);

        List<EligibleCouple> couples = allEligibleCouples.all();

        assertEquals(expectedCouples, couples);
    }

    @Test
    public void shouldCloseEC() throws Exception {
        allEligibleCouples.close("entity id 1");

        verify(alertRepository).deleteAllAlertsForEntity("entity id 1");
        verify(eligibleCoupleRepository).close("entity id 1");
        verify(timelineEventRepository).deleteAllTimelineEventsForEntity("entity id 1");
    }
}
