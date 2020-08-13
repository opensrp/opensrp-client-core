package org.smartregister.repository;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.TestApplication;
import org.smartregister.p2p.model.DataType;
import org.smartregister.p2p.sync.data.JsonData;
import org.smartregister.sync.P2PClassifier;

import java.util.TreeSet;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 04-08-2020.
 */
public class P2PSenderTransferDaoTest extends BaseRobolectricUnitTest {

    private P2PSenderTransferDao p2PSenderTransferDao;

    @Before
    public void setUp() throws Exception {
        p2PSenderTransferDao = new P2PSenderTransferDao();
    }

    @Test
    public void getDataTypesShouldReturnACloneOfDataType() {
        TreeSet<DataType> dataTypes = p2PSenderTransferDao.getDataTypes();

        Assert.assertEquals(p2PSenderTransferDao.dataTypes.size(), dataTypes.size());
        Assert.assertTrue(p2PSenderTransferDao.dataTypes != dataTypes);

        // Verify the types & order
        DataType[] expectedDataTypes = p2PSenderTransferDao.dataTypes.toArray(new DataType[0]);
        DataType[] actualDataTypes = dataTypes.toArray(new DataType[0]);

        for (int i = 0; i < expectedDataTypes.length; i++) {
            Assert.assertEquals(expectedDataTypes[i].getName(), actualDataTypes[i].getName());
            Assert.assertEquals(expectedDataTypes[i].getPosition(), actualDataTypes[i].getPosition());
        }
    }

    @Test
    public void getJsonDataShouldCallEventRepositoryGetEventsWhenDataTypeIsEvent() {
        EventClientRepository eventClientRepository = Mockito.spy(CoreLibrary.getInstance().context().getEventClientRepository());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "eventClientRepository", eventClientRepository);

        int lastRecordId = 789;
        int batchSize = 100;

        JsonData jsonData = Mockito.mock(JsonData.class);
        Mockito.doReturn(jsonData).when(eventClientRepository).getEvents(lastRecordId, batchSize);

        // Call the method under test
        JsonData actualJsonData = p2PSenderTransferDao.getJsonData(p2PSenderTransferDao.event, lastRecordId, batchSize);


        // Verify that the repository was called
        Mockito.verify(eventClientRepository).getEvents(lastRecordId, batchSize);
        Assert.assertEquals(jsonData, actualJsonData);
    }

    @Test
    public void getJsonDataShouldCallEventRepositoryGetClientsWhenDataTypeIsClient() {
        EventClientRepository eventClientRepository = Mockito.spy(CoreLibrary.getInstance().context().getEventClientRepository());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "eventClientRepository", eventClientRepository);

        int lastRecordId = 789;
        int batchSize = 100;

        JsonData jsonData = Mockito.mock(JsonData.class);
        Mockito.doReturn(jsonData).when(eventClientRepository).getClients(lastRecordId, batchSize);

        // Call the method under test
        JsonData actualJsonData = p2PSenderTransferDao.getJsonData(p2PSenderTransferDao.client, lastRecordId, batchSize);


        // Verify that the repository was called
        Mockito.verify(eventClientRepository).getClients(lastRecordId, batchSize);
        Assert.assertEquals(jsonData, actualJsonData);
    }

    @Test
    public void getJsonDataShouldCallStructureRepositoryGetStructuresWhenDataTypeIsStructure() {
        StructureRepository structureRepository = Mockito.spy(CoreLibrary.getInstance().context().getStructureRepository());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "structureRepository", structureRepository);

        int lastRecordId = 789;
        int batchSize = 100;

        JsonData jsonData = Mockito.mock(JsonData.class);
        Mockito.doReturn(jsonData).when(structureRepository).getStructures(lastRecordId, batchSize);

        // Call the method under test
        JsonData actualJsonData = p2PSenderTransferDao.getJsonData(p2PSenderTransferDao.structure, lastRecordId, batchSize);


        // Verify that the repository was called
        Mockito.verify(structureRepository).getStructures(lastRecordId, batchSize);
        Assert.assertEquals(jsonData, actualJsonData);
    }

    @Test
    public void getJsonDataShouldCallTaskRepositoryGetTasksWhenDataTypeIsTask() {
        TaskRepository taskRepository = Mockito.spy(CoreLibrary.getInstance().context().getTaskRepository());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "taskRepository", taskRepository);

        int lastRecordId = 789;
        int batchSize = 100;

        JsonData jsonData = Mockito.mock(JsonData.class);
        Mockito.doReturn(jsonData).when(taskRepository).getTasks(lastRecordId, batchSize);

        // Call the method under test
        JsonData actualJsonData = p2PSenderTransferDao.getJsonData(p2PSenderTransferDao.task, lastRecordId, batchSize);

        // Verify that the repository was called
        Mockito.verify(taskRepository).getTasks(lastRecordId, batchSize);
        Assert.assertEquals(jsonData, actualJsonData);
    }

    @Test
    public void getJsonDataShouldCallEventRepositoryGetClientsWhenContextHasForeignEventsAndDataTypeIsForeignClient() {
        ((TestApplication) TestApplication.getInstance()).setP2PClassifier(Mockito.mock(P2PClassifier.class));
        EventClientRepository eventClientRepository = Mockito.spy(CoreLibrary.getInstance().context().getForeignEventClientRepository());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "foreignEventClientRepository", eventClientRepository);

        int lastRecordId = 789;
        int batchSize = 100;

        JsonData jsonData = Mockito.mock(JsonData.class);
        Mockito.doReturn(jsonData).when(eventClientRepository).getClients(lastRecordId, batchSize);

        // Call the method under test
        JsonData actualJsonData = p2PSenderTransferDao.getJsonData(p2PSenderTransferDao.foreignClient, lastRecordId, batchSize);

        // Verify that the repository was called
        Mockito.verify(eventClientRepository).getClients(lastRecordId, batchSize);
        Assert.assertEquals(jsonData, actualJsonData);
    }
}