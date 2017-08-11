package org.smartregister.commonregistry;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;
import org.smartregister.repository.AlertRepository;
import org.smartregister.repository.TimelineEventRepository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
/*
 by Raihan Ahmed
 */
@RunWith(RobolectricTestRunner.class)
public class AllCommonsRepositoryTest {
    @Mock
    private CommonRepository personRepository;
    @Mock
    private TimelineEventRepository timelineEventRepository;
    @Mock
    private AlertRepository alertRepository;

    private AllCommonsRepository allCommonsRepository;
    @Before
    public void setUp() throws Exception{
        initMocks(this);
        allCommonsRepository = new AllCommonsRepository(personRepository,
                                                        alertRepository,
                                                        timelineEventRepository);
    }

    @Test
    public void ShouldReturnAllPersonObjectOfaCertainTypeFromRepository() {
        List<CommonPersonObject> expectedpersonobjects = Arrays.asList(new CommonPersonObject(
                "case1",
                "relationid1",
                new HashMap<String, String>(),
                ""));
        when(personRepository.allcommon()).thenReturn(expectedpersonobjects);
        assertEquals(expectedpersonobjects, personRepository.allcommon());
    }

    @Test
    public void ShouldShowcount() throws Exception {
        when(personRepository.count()).thenReturn((long) 8);
        assertEquals(personRepository.count(), (long) 8);

    }
//    public List<CommonPersonObject> findByCaseIDs(List<String> caseIds) {
//        return personRepository.findByCaseIDs(caseIds.toArray(new String[caseIds.size()]));
//    }
//
//
//
//    public void close(String entityId) {
//        alertRepository.deleteAllAlertsForEntity(entityId);
//        timelineEventRepository.deleteAllTimelineEventsForEntity(entityId);
//        personRepository.close(entityId);
//    }
//
//    public void mergeDetails(String entityId, Map<String, String> details) {
//        personRepository.mergeDetails(entityId, details);
//    }
}
