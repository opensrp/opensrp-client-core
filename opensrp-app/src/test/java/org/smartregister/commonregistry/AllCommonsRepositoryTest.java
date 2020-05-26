package org.smartregister.commonregistry;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;
import org.smartregister.repository.AlertRepository;
import org.smartregister.repository.TimelineEventRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/*
 by Raihan Ahmed
 */
public class AllCommonsRepositoryTest extends BaseUnitTest {

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
        when(personRepository.allcommon()).thenReturn(expectedpersonobjects);
        assertEquals(expectedpersonobjects, personRepository.allcommon());
    }

    @Test
    public void ShouldShowcount() throws Exception {
        when(personRepository.count()).thenReturn((long) 8);
        assertEquals(personRepository.count(), (long) 8);

    }

    @Test
    public void testAll() {
        allCommonsRepository.all();
        verify(personRepository).allcommon();
    }

    @Test
    public void testFindByCaseId() {
        allCommonsRepository.findByCaseID("case 1");
        verify(personRepository).findByCaseID("case 1");
    }

    @Test
    public void testFindHHByGOBHHID() {
        allCommonsRepository.findHHByGOBHHID("gobhhid 1");
        verify(personRepository).findHHByGOBHHID("gobhhid 1");
    }

    @Test
    public void testCount() {
        allCommonsRepository.count();
        verify(personRepository).count();
    }

    @Test
    public void testFindByCaseIds() {
        List<String> expectedCaseIds = new ArrayList<>();
        expectedCaseIds.add("case 1");
        expectedCaseIds.add("case 2");

        allCommonsRepository.findByCaseIDs(expectedCaseIds);
        verify(personRepository).findByCaseIDs(new String[] {expectedCaseIds.get(0), expectedCaseIds.get(1)});
    }

    @Test
    public void testFindByRelationIds() {
        List<String> expectedRelationIds = new ArrayList<>();
        expectedRelationIds.add("relation id 1");
        expectedRelationIds.add("relation id 2");

        allCommonsRepository.findByRelationalIDs(expectedRelationIds);
        verify(personRepository).findByRelationalIDs(new String[] {expectedRelationIds.get(0), expectedRelationIds.get(1)});
    }

    @Test
    public void testFindByRelation_Ids() {
        List<String> expectedRelationIds = new ArrayList<>();
        expectedRelationIds.add("relation id 1");
        expectedRelationIds.add("relation id 2");

        allCommonsRepository.findByRelational_IDs(expectedRelationIds);
        verify(personRepository).findByRelational_IDs(new String[] {expectedRelationIds.get(0), expectedRelationIds.get(1)});
    }

    @Test
    public void testClose() {
        String entityId = "Entity 1";
        allCommonsRepository.close(entityId);
        verify(alertRepository).deleteAllAlertsForEntity(entityId);
        verify(timelineEventRepository).deleteAllTimelineEventsForEntity(entityId);
        verify(personRepository).close(entityId);
    }

    @Test
    public void testMergeDetails() {
        String entityId = "Entity 1";
        Map<String, String> details = new HashMap<>();
        details.put("case id", "Case 1");

        allCommonsRepository.mergeDetails(entityId, details);
        verify(personRepository).mergeDetails(entityId, details);
    }

}
