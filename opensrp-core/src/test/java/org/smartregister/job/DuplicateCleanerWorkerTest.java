package org.smartregister.job;

import android.content.Context;
import android.util.Log;

import androidx.work.Configuration;
import androidx.work.ListenableWorker;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.testing.SynchronousExecutor;
import androidx.work.testing.TestWorkerBuilder;
import androidx.work.testing.WorkManagerTestInitHelper;

import com.google.common.util.concurrent.ListenableFuture;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseRobolectricUnitTest;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.DuplicateZeirIdStatus;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.EventClientRepository;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 02-11-2022.
 */

public class DuplicateCleanerWorkerTest extends BaseRobolectricUnitTest {

    @Before
    public void setUp() throws Exception {
        //super.setUp();
        initCoreLibrary();
        initializeWorkManager();
    }

    @Test
    public void shouldScheduleShouldReturnFalseAndCallDuplicateIdsFixed() {
        AllSharedPreferences allSharedPreferences = Mockito.spy(CoreLibrary.getInstance().context().userService().getAllSharedPreferences());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "allSharedPreferences", allSharedPreferences);

        Mockito.doReturn(true).when(allSharedPreferences).getBooleanPreference("duplicate-ids-fixed");

        Assert.assertFalse(DuplicateCleanerWorker.shouldSchedule());

        Mockito.verify(allSharedPreferences).getBooleanPreference("duplicate-ids-fixed");
    }

    @Test
    public void schedulePeriodicallyShouldScheduleJobInWorkManager() throws ExecutionException, InterruptedException {
        Context context = RuntimeEnvironment.application;
        DuplicateCleanerWorker.schedulePeriodically(context, 15);

        ListenableFuture<List<WorkInfo>> listenableFuture = WorkManager.getInstance(context)
                .getWorkInfosForUniqueWork(DuplicateCleanerWorker.TAG);

        Assert.assertEquals(1, listenableFuture.get().size());
    }

    @Test
    public void doWorkShouldReturnSuccess() {
        DuplicateCleanerWorker duplicateCleanerWorker = TestWorkerBuilder.from(RuntimeEnvironment.application, DuplicateCleanerWorker.class)
                .build();
        Assert.assertEquals(ListenableWorker.Result.success(), duplicateCleanerWorker.doWork());
    }

    @Test
    public void doWorkShouldCallCleanDuplicateUniqueZeirIdsWhenDuplicateIdsFixedPreferenceIsFalse() throws Exception {
        AllSharedPreferences allSharedPreferences = Mockito.spy(CoreLibrary.getInstance().context().userService().getAllSharedPreferences());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "allSharedPreferences", allSharedPreferences);
        Mockito.doReturn(false).when(allSharedPreferences).getBooleanPreference("duplicate-ids-fixed");

        EventClientRepository eventClientRepository = Mockito.spy(CoreLibrary.getInstance().context().getEventClientRepository());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "eventClientRepository", eventClientRepository);
        Mockito.doReturn(DuplicateZeirIdStatus.PENDING).when(eventClientRepository).cleanDuplicateMotherIds();

        DuplicateCleanerWorker duplicateCleanerWorker = TestWorkerBuilder.from(RuntimeEnvironment.application, DuplicateCleanerWorker.class)
                .build();
        Assert.assertEquals(ListenableWorker.Result.success(), duplicateCleanerWorker.doWork());

        Mockito.verify(eventClientRepository).cleanDuplicateMotherIds();
        Mockito.verify(allSharedPreferences, Mockito.times(0)).saveBooleanPreference("duplicate-ids-fixed", true);
    }

    private void initializeWorkManager() {
        Configuration config = new Configuration.Builder()
                .setMinimumLoggingLevel(Log.DEBUG)
                .setExecutor(new SynchronousExecutor())
                .build();

        // Initialize WorkManager for instrumentation tests.
        WorkManagerTestInitHelper.initializeTestWorkManager(
                RuntimeEnvironment.application, config);
    }

    @Test
    public void doWorkShouldSetDuplicateIdsFixedPreferenceTrueWhenCleanUniqueZeirIdsReturnsCleaned() throws Exception {
        AllSharedPreferences allSharedPreferences = Mockito.spy(CoreLibrary.getInstance().context().userService().getAllSharedPreferences());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "allSharedPreferences", allSharedPreferences);
        Mockito.doReturn(false).when(allSharedPreferences).getBooleanPreference("duplicate-ids-fixed");

        EventClientRepository eventClientRepository = Mockito.spy(CoreLibrary.getInstance().context().getEventClientRepository());
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "eventClientRepository", eventClientRepository);
        Mockito.doReturn(DuplicateZeirIdStatus.CLEANED).when(eventClientRepository).cleanDuplicateMotherIds();

        //WorkManagerInitializer
        DuplicateCleanerWorker duplicateCleanerWorker = TestWorkerBuilder.from(RuntimeEnvironment.application, DuplicateCleanerWorker.class)
                .build();
        Assert.assertEquals(ListenableWorker.Result.success(), duplicateCleanerWorker.doWork());

        Mockito.verify(eventClientRepository).cleanDuplicateMotherIds();
        Mockito.verify(allSharedPreferences).saveBooleanPreference("duplicate-ids-fixed", true);
    }

}