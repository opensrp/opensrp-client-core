package org.smartregister.view.fragment;

import static org.smartregister.AllConstants.SyncInfo.SYNCED_CLIENTS;
import static org.smartregister.AllConstants.SyncInfo.SYNCED_EVENTS;
import static org.smartregister.AllConstants.SyncInfo.TASK_UNPROCESSED_EVENTS;
import static org.smartregister.AllConstants.SyncInfo.UNSYNCED_CLIENTS;
import static org.smartregister.AllConstants.SyncInfo.UNSYNCED_EVENTS;
import static org.smartregister.AllConstants.SyncInfo.UNSYNCED_HEIGHT_EVENTS;
import static org.smartregister.AllConstants.SyncInfo.UNSYNCED_VACCINE_EVENTS;
import static org.smartregister.AllConstants.SyncInfo.UNSYNCED_WEIGHT_EVENTS;
import static org.smartregister.AllConstants.SyncInfo.VALID_CLIENTS;
import static org.smartregister.AllConstants.SyncInfo.VALID_EVENTS;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.FragmentManager;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseUnitTest;
import org.smartregister.R;
import org.smartregister.view.presenter.StatsFragmentPresenter;
import org.smartregister.view.contract.StatsFragmentContract;


import java.util.HashMap;
import java.util.Map;

public class StatsFragmentTest extends BaseUnitTest {

    @Mock
    private LayoutInflater layoutInflater;

    @Mock
    private ViewGroup container;

    private StatsFragment statsFragment;

    private StatsFragmentPresenter presenter;

    @Mock
    private Bundle savedInstanceState;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        statsFragment = Mockito.mock(StatsFragment.class, Mockito.CALLS_REAL_METHODS);
    }

    @Test
    public void getInstanceReturnsNonNullFragmentInstance() {
        Assert.assertNotNull(StatsFragment.newInstance(Mockito.mock(Bundle.class)));
    }

    @Test
    public void onCreateInitializesPresenter() {
        FragmentManager mChildFragmentManager = Mockito.mock(FragmentManager.class);
        Whitebox.setInternalState(statsFragment, "mChildFragmentManager", mChildFragmentManager);
        statsFragment.onCreate(savedInstanceState);
        presenter = ReflectionHelpers.getField(statsFragment, "presenter");
        Assert.assertNotNull(presenter);
    }

    @Test
    public void onCreateViewInitsViewsAndReturnsCorrectView() {
        LayoutInflater inflater = Mockito.mock(LayoutInflater.class);
        ViewGroup container = Mockito.mock(ViewGroup.class);

        Button btnRefreshStats = Mockito.mock(Button.class);
        View rootView = Mockito.spy(Mockito.mock(View.class));
        statsFragment = Mockito.spy(statsFragment);

        StatsFragmentContract.Interactor interactor = Mockito.mock(StatsFragmentContract.Interactor.class);
        Mockito.doNothing().when(interactor).fetchECSyncInfo();

        presenter = Mockito.spy(Mockito.mock(StatsFragmentPresenter.class));
        ReflectionHelpers.setField(presenter, "interactor", interactor);
        ReflectionHelpers.setField(statsFragment, "presenter", presenter);

        Mockito.doReturn(btnRefreshStats).when(rootView).findViewById(R.id.refresh_button);
        Mockito.doReturn(rootView).when(inflater).inflate(ArgumentMatchers.anyInt(), ArgumentMatchers.any(ViewGroup.class), ArgumentMatchers.anyBoolean());

        View returnedView = statsFragment.onCreateView(inflater, container, savedInstanceState);
        Assert.assertNotNull(returnedView);
        Mockito.verify(presenter).fetchSyncInfo();
    }

    @Test
    public void refreshECSyncInfoUpdatesViews() {
        Map<String, String> syncInfoMap = new HashMap<>();
        syncInfoMap.put(SYNCED_EVENTS, "2");
        syncInfoMap.put(UNSYNCED_EVENTS, "3");
        syncInfoMap.put(TASK_UNPROCESSED_EVENTS, "4");
        syncInfoMap.put(SYNCED_CLIENTS, "5");
        syncInfoMap.put(UNSYNCED_CLIENTS, "6");
        syncInfoMap.put(VALID_EVENTS, "7");
        syncInfoMap.put(VALID_CLIENTS, "8");
        syncInfoMap.put(UNSYNCED_VACCINE_EVENTS, "1");
        syncInfoMap.put(UNSYNCED_WEIGHT_EVENTS, "1");
        syncInfoMap.put(UNSYNCED_HEIGHT_EVENTS, "1");

        View rootView = LayoutInflater.from(RuntimeEnvironment.application).inflate(R.layout.fragment_stats, null);
        Mockito.doReturn(rootView).when(statsFragment).getView();

        TextView tvSyncedEvents = rootView.findViewById(R.id.synced_events);
        TextView tvUnSyncedEvents = rootView.findViewById(R.id.unsynced_events);
        TextView tvUnsyncedHeightEvents = rootView.findViewById(R.id.synced_height_events);
        LinearLayout layoutUnsyncedHeightEvents = rootView.findViewById(R.id.height_stats);

        ReflectionHelpers.setField(statsFragment, "tvSyncedEvents", tvSyncedEvents);
        ReflectionHelpers.setField(statsFragment, "tvUnSyncedEvents", tvUnSyncedEvents);
        ReflectionHelpers.setField(statsFragment, "tvTaskUnprocessedEvents", rootView.findViewById(R.id.task_unprocessed_events));
        ReflectionHelpers.setField(statsFragment, "tvSyncedClient", rootView.findViewById(R.id.synced_clients));
        ReflectionHelpers.setField(statsFragment, "tvUnSyncedClients", rootView.findViewById(R.id.unsynced_clients));
        ReflectionHelpers.setField(statsFragment, "tvValidatedEvents", rootView.findViewById(R.id.validated_events));
        ReflectionHelpers.setField(statsFragment, "tvValidatedClients", rootView.findViewById(R.id.validated_clients));
        ReflectionHelpers.setField(statsFragment, "tvUserName", rootView.findViewById(R.id.user_value));
        ReflectionHelpers.setField(statsFragment, "tvAppVersionName", rootView.findViewById(R.id.app_version_name_value));
        ReflectionHelpers.setField(statsFragment, "tvAppVersionCode", rootView.findViewById(R.id.app_version_code_value));
        ReflectionHelpers.setField(statsFragment, "tvDBVersion", rootView.findViewById(R.id.db_version_value));
        ReflectionHelpers.setField(statsFragment, "tvTeam", rootView.findViewById(R.id.team_value));
        ReflectionHelpers.setField(statsFragment, "tvLocality", rootView.findViewById(R.id.locality_value));
        ReflectionHelpers.setField(statsFragment, "tvManufacturer", rootView.findViewById(R.id.manufacturer_value));
        ReflectionHelpers.setField(statsFragment, "tvDevice", rootView.findViewById(R.id.device_value));
        ReflectionHelpers.setField(statsFragment, "tvOS", rootView.findViewById(R.id.os_value));
        ReflectionHelpers.setField(statsFragment, "tvBuildDate", rootView.findViewById(R.id.build_date_value));
        ReflectionHelpers.setField(statsFragment, "tvCurrentDate", rootView.findViewById(R.id.date_value));
        ReflectionHelpers.setField(statsFragment, "tvUnsyncedVaccineEvents", rootView.findViewById(R.id.synced_vaccine_events));
        ReflectionHelpers.setField(statsFragment, "tvUnsyncedWeightEvents", rootView.findViewById(R.id.synced_weight_events));
        ReflectionHelpers.setField(statsFragment, "tvUnsyncedHeightEvents", tvUnsyncedHeightEvents);

        statsFragment.refreshECSyncInfo(syncInfoMap);
        Assert.assertEquals("2", tvSyncedEvents.getText());
        Assert.assertEquals("3", tvUnSyncedEvents.getText());
        Assert.assertEquals(View.GONE, layoutUnsyncedHeightEvents.getVisibility());
    }

    @Test
    public void assertStatsFragmentInitsCorrectly() {
        Assert.assertNotNull(statsFragment);
    }

    @Test
    public void assertTestLabels() {
        View rootView = LayoutInflater.from(RuntimeEnvironment.application).inflate(R.layout.fragment_stats, null);
        TextView tvSyncedEventsLabel = rootView.findViewById(R.id.synced_events_label);
        TextView tvUnsyncedEventsLabel = rootView.findViewById(R.id.unsynced_events_label);
        TextView tvTaskUnprocessedEventsLabel = rootView.findViewById(R.id.task_unprocessed_events_label);
        TextView tvSyncedClientsLabel = rootView.findViewById(R.id.synced_clients_label);
        TextView tvUnsyncedClientsLabel = rootView.findViewById(R.id.unsynced_clients_label);
        TextView tvValidatedEventsLabel = rootView.findViewById(R.id.validated_events_label);
        TextView tvValidatedClientsLabel = rootView.findViewById(R.id.validated_clients_label);
        Assert.assertNotNull(tvSyncedEventsLabel);
        Assert.assertNotNull(tvUnsyncedEventsLabel);
        Assert.assertNotNull(tvTaskUnprocessedEventsLabel);
        Assert.assertNotNull(tvSyncedClientsLabel);
        Assert.assertNotNull(tvUnsyncedClientsLabel);
        Assert.assertNotNull(tvValidatedEventsLabel);
        Assert.assertNotNull(tvValidatedClientsLabel);
    }

    @Test
    public void assertTestLabelsNotNull() {
        View rootView = LayoutInflater.from(RuntimeEnvironment.application).inflate(R.layout.fragment_stats, null);
        TextView tvSyncedEventsLabel = rootView.findViewById(R.id.synced_events_label);
        TextView tvUnsyncedEventsLabel = rootView.findViewById(R.id.unsynced_events_label);
        TextView tvTaskUnprocessedEventsLabel = rootView.findViewById(R.id.task_unprocessed_events_label);
        TextView tvSyncedClientsLabel = rootView.findViewById(R.id.synced_clients_label);
        TextView tvUnsyncedClientsLabel = rootView.findViewById(R.id.unsynced_clients_label);
        TextView tvValidatedEventsLabel = rootView.findViewById(R.id.validated_events_label);
        TextView tvValidatedClientsLabel = rootView.findViewById(R.id.validated_clients_label);
        Assert.assertNotNull(tvSyncedEventsLabel);
        Assert.assertNotNull(tvUnsyncedEventsLabel);
        Assert.assertNotNull(tvTaskUnprocessedEventsLabel);
        Assert.assertNotNull(tvSyncedClientsLabel);
        Assert.assertNotNull(tvUnsyncedClientsLabel);
        Assert.assertNotNull(tvValidatedEventsLabel);
        Assert.assertNotNull(tvValidatedClientsLabel);
    }

    @Test
    public void testUpdatedLabels() {
        View parentLayout = LayoutInflater.from(RuntimeEnvironment.application.getApplicationContext()).inflate(R.layout.fragment_stats, null, false);
        Mockito.doReturn(parentLayout).when(layoutInflater).inflate(R.layout.fragment_base_register, container, false);

        TextView tvSyncedEventsLabel = parentLayout.findViewById(R.id.synced_events_label);
        TextView tvUnsyncedEventsLabel = parentLayout.findViewById(R.id.unsynced_events_label);
        TextView tvTaskUnprocessedEventsLabel = parentLayout.findViewById(R.id.task_unprocessed_events_label);
        TextView tvSyncedClientsLabel = parentLayout.findViewById(R.id.synced_clients_label);
        TextView tvUnsyncedClientsLabel = parentLayout.findViewById(R.id.unsynced_clients_label);
        TextView tvValidatedEventsLabel = parentLayout.findViewById(R.id.validated_events_label);
        TextView tvValidatedClientsLabel = parentLayout.findViewById(R.id.validated_clients_label);

        Assert.assertNotNull(tvSyncedEventsLabel);
        Assert.assertNotNull(tvUnsyncedEventsLabel);
        Assert.assertNotNull(tvTaskUnprocessedEventsLabel);
        Assert.assertNotNull(tvSyncedClientsLabel);
        Assert.assertNotNull(tvUnsyncedClientsLabel);
        Assert.assertNotNull(tvValidatedEventsLabel);
        Assert.assertNotNull(tvValidatedClientsLabel);

        String synced_events = RuntimeEnvironment.application.getResources().getString(R.string.synced_events);
        String unsynced_events = RuntimeEnvironment.application.getResources().getString(R.string.unsynced_events);
        String task_unprocessed_events = RuntimeEnvironment.application.getResources().getString(R.string.task_unprocessed_events);
        String synced_clients = RuntimeEnvironment.application.getResources().getString(R.string.synced_clients);
        String unsynced_clients = RuntimeEnvironment.application.getResources().getString(R.string.unsynced_clients);
        String validated_events = RuntimeEnvironment.application.getResources().getString(R.string.validated_events);
        String validated_clients = RuntimeEnvironment.application.getResources().getString(R.string.validated_clients);

        Assert.assertEquals(synced_events, tvSyncedEventsLabel.getText().toString());
        Assert.assertEquals(unsynced_events, tvUnsyncedEventsLabel.getText().toString());
        Assert.assertEquals(task_unprocessed_events, tvTaskUnprocessedEventsLabel.getText().toString());
        Assert.assertEquals(synced_clients, tvSyncedClientsLabel.getText().toString());
        Assert.assertEquals(unsynced_clients, tvUnsyncedClientsLabel.getText().toString());
        Assert.assertEquals(validated_events, tvValidatedEventsLabel.getText().toString());
        Assert.assertEquals(validated_clients, tvValidatedClientsLabel.getText().toString());
    }

}
