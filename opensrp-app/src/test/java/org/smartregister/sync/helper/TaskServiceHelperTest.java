package org.smartregister.sync.helper;

import com.google.gson.reflect.TypeToken;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.smartregister.AllConstants;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;
import org.smartregister.domain.Task;
import org.smartregister.repository.BaseRepository;
import org.smartregister.repository.LocationRepository;
import org.smartregister.repository.PlanDefinitionRepository;
import org.smartregister.repository.TaskRepository;
import org.smartregister.service.HTTPAgent;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by Richard Kareko on 6/23/20.
 */

public class TaskServiceHelperTest extends BaseRobolectricUnitTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private PlanDefinitionRepository planDefinitionRepository;

    @Mock
    private  LocationRepository locationRepository;

    @Mock
    private HTTPAgent httpAgent;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    @Captor
    private ArgumentCaptor<Task> taskArgumentCaptor;

    private TaskServiceHelper taskServiceHelper = Mockito.spy(TaskServiceHelper.getInstance());

    private String taskJSon = "{\"for\": \"154167\", \"code\": \"Bednet Distribution\", \"focus\": \"158b73f5-49d0-50a9-8020-0468c1bbabdd\", \"owner\": \"nifiUser\", \"status\": \"Cancelled\", \"priority\": 3, \"authoredOn\": \"2020-03-26T10:47:03.586+02:00\", \"identifier\": \"c256c9d8-fe9b-4763-b5af-26585dcbe6bf\", \"description\": \"Visit 100% of residential structures in the operational area and provide nets\", \"lastModified\": \"2020-03-26T10:52:09.750+02:00\", \"serverVersion\": 1585212830433, \"businessStatus\": \"Not Visited\", \"planIdentifier\": \"eb3cd7e1-c849-5230-8d49-943218018f9f\", \"groupIdentifier\": \"3952\", \"executionEndDate\": \"2020-04-02T00:00:00.000+02:00\", \"executionStartDate\": \"2020-03-26T00:00:00.000+02:00\"}";

    @Before
    public void setUp(){
        MockitoAnnotations.initMocks(this);
        Whitebox.setInternalState(taskServiceHelper, "taskRepository", taskRepository);
        CoreLibrary.getInstance().context().allSharedPreferences().savePreference(AllConstants.DRISHTI_BASE_URL, "https://sample-stage.smartregister.org/opensrp");
        Whitebox.setInternalState(CoreLibrary.getInstance().context(), "planDefinitionRepository" , planDefinitionRepository );
        Whitebox.setInternalState(CoreLibrary.getInstance().context(), "locationRepository" , locationRepository );
        Mockito.doReturn(httpAgent).when(taskServiceHelper).getHttpAgent();

    }

    @Test
    public void testSyncTasks() {
        taskServiceHelper.syncTasks();
        verify(taskServiceHelper).syncCreatedTaskToServer();
        verify(taskServiceHelper).syncTaskStatusToServer();
    }

    @Test
    public void testFetchTasksFromServer() {
        String planId = "eb3cd7e1-c849-5230-8d49-943218018f9f";
        Set<String> planIdSet = new HashSet<>();
        planIdSet.add(planId);
        when(CoreLibrary.getInstance().context().getPlanDefinitionRepository().findAllPlanDefinitionIds()).thenReturn(planIdSet);

        String locationId = "3952";
        List<String> locationIdList = new ArrayList<>();
        locationIdList.add(locationId);
        when(CoreLibrary.getInstance().context().getLocationRepository().getAllLocationIds()).thenReturn(locationIdList);

        Task expectedTask = TaskServiceHelper.taskGson.fromJson(taskJSon, new TypeToken<Task>() {
        }.getType());
        expectedTask.setSyncStatus(BaseRepository.TYPE_Unsynced);
        ArrayList tasks = new ArrayList();
        tasks.add(expectedTask);

        Mockito.doReturn(new Response<>(ResponseStatus.success,    // returned on first call
                        LocationServiceHelper.locationGson.toJson(tasks)),
                new Response<>(ResponseStatus.success,             //returned on second call
                        LocationServiceHelper.locationGson.toJson(new ArrayList<>())))
                .when(httpAgent).post(stringArgumentCaptor.capture(), stringArgumentCaptor.capture());

        List<Task> actualTasks = taskServiceHelper.fetchTasksFromServer();
        assertNotNull(actualTasks);
        assertEquals(1, actualTasks.size());
        Task actualTask = actualTasks.get(0);

        String syncUrl = stringArgumentCaptor.getAllValues().get(0);
        assertEquals("https://sample-stage.smartregister.org/opensrp/rest/task/sync", syncUrl);
        String requestString = stringArgumentCaptor.getAllValues().get(1);
        assertEquals("{\"plan\":[\"eb3cd7e1-c849-5230-8d49-943218018f9f\"],\"group\":[\"3952\"],\"serverVersion\":0}", requestString);

        verify(taskRepository).addOrUpdate(taskArgumentCaptor.capture());
        assertEquals(expectedTask.getIdentifier(), taskArgumentCaptor.getValue().getIdentifier());
        assertEquals(expectedTask.getBusinessStatus(), taskArgumentCaptor.getValue().getBusinessStatus());
        assertEquals(BaseRepository.TYPE_Synced, taskArgumentCaptor.getValue().getSyncStatus());
        assertEquals(expectedTask.getServerVersion(), taskArgumentCaptor.getValue().getServerVersion());
        assertEquals(expectedTask.getCode(), taskArgumentCaptor.getValue().getCode());
        assertEquals(expectedTask.getForEntity(), taskArgumentCaptor.getValue().getForEntity());
        assertEquals(expectedTask.getPlanIdentifier(), taskArgumentCaptor.getValue().getPlanIdentifier());

        assertEquals(expectedTask.getIdentifier(), actualTask.getIdentifier());
        assertEquals(expectedTask.getBusinessStatus(), actualTask.getBusinessStatus());
        assertEquals(BaseRepository.TYPE_Synced, actualTask.getSyncStatus());
        assertEquals(expectedTask.getServerVersion(), actualTask.getServerVersion());
        assertEquals(expectedTask.getCode(), actualTask.getCode());
        assertEquals(expectedTask.getForEntity(), actualTask.getForEntity());
        assertEquals(expectedTask.getPlanIdentifier(), actualTask.getPlanIdentifier());

    }

}
