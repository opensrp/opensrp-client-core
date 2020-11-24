package org.smartregister.multitenant.check;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.repository.StructureRepository;
import org.smartregister.view.activity.DrishtiApplication;

import static org.junit.Assert.*;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 24-11-2020.
 */
public class StructureSyncedCheckTest extends BaseRobolectricUnitTest {

    private StructureSyncedCheck structureSyncedCheck;

    @Before
    public void setUp() throws Exception {
        structureSyncedCheck = new StructureSyncedCheck();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void isCheckOk() {

    }

    @Test
    public void performPreResetAppOperations() {
    }

    @Test
    public void isStructuresSyncedShouldReturnFalseWhenUnsyncedStructuresCountIsMoreThan0() {
        StructureRepository structureRepository = Mockito.spy(DrishtiApplication.getInstance().getContext().getStructureRepository());
        ReflectionHelpers.setField(DrishtiApplication.getInstance().getContext(), "structureRepository", structureRepository);

        Mockito.doReturn(2).when(structureRepository).getUnsyncedStructuresCount();

        Assert.assertFalse(structureSyncedCheck.isStructuresSynced(DrishtiApplication.getInstance()));
    }


    @Test
    public void isStructuresSyncedShouldReturnTrueWhenUnsyncedStructuresCountIsMoreThan0() {
        StructureRepository structureRepository = Mockito.spy(DrishtiApplication.getInstance().getContext().getStructureRepository());
        ReflectionHelpers.setField(DrishtiApplication.getInstance().getContext(), "structureRepository", structureRepository);

        Mockito.doReturn(0).when(structureRepository).getUnsyncedStructuresCount();

        Assert.assertTrue(structureSyncedCheck.isStructuresSynced(DrishtiApplication.getInstance()));
    }

    @Test
    public void onSyncStart() {
    }

    @Test
    public void onSyncInProgress() {
    }

    @Test
    public void onSyncComplete() {
    }

    @Test
    public void getUniqueName() {
    }
}