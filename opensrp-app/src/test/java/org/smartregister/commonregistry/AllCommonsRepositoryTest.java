package org.smartregister.commonregistry;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricTestRunner;
import org.smartregister.repository.AlertRepository;
import org.smartregister.repository.TimelineEventRepository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
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
        Mockito.when(personRepository.allcommon()).thenReturn(expectedpersonobjects);
        Assert.assertEquals(expectedpersonobjects, personRepository.allcommon());
    }

    @Test
    public void ShouldShowcount() throws Exception {
        Mockito.when(personRepository.count()).thenReturn((long) 8);
        Assert.assertEquals(personRepository.count(), (long) 8);

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
