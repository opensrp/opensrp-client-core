package org.smartregister.commonregistry;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smartregister.BaseUnitTest;
import org.smartregister.repository.AlertRepository;
import org.smartregister.repository.TimelineEventRepository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

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

}
