package org.smartregister.view.fragment;

import static org.smartregister.AllConstants.DatabaseKeys.DB_VERSION;
import static org.smartregister.AllConstants.DeviceInfo.MANUFACTURER;
import static org.smartregister.AllConstants.DeviceInfo.MODEL;
import static org.smartregister.AllConstants.DeviceInfo.OS_VERSION;
import static org.smartregister.AllConstants.SyncInfo.APP_BUILD_DATE;
import static org.smartregister.AllConstants.SyncInfo.APP_VERSION_CODE;
import static org.smartregister.AllConstants.SyncInfo.APP_VERSION_NAME;
import static org.smartregister.AllConstants.SyncInfo.SYNCED_CLIENTS;
import static org.smartregister.AllConstants.SyncInfo.SYNCED_EVENTS;
import static org.smartregister.AllConstants.SyncInfo.TASK_UNPROCESSED_EVENTS;
import static org.smartregister.AllConstants.SyncInfo.UNSYNCED_CLIENTS;
import static org.smartregister.AllConstants.SyncInfo.UNSYNCED_EVENTS;
import static org.smartregister.AllConstants.SyncInfo.UNSYNCED_HEIGHT_EVENTS;
import static org.smartregister.AllConstants.SyncInfo.UNSYNCED_VACCINE_EVENTS;
import static org.smartregister.AllConstants.SyncInfo.UNSYNCED_WEIGHT_EVENTS;
import static org.smartregister.AllConstants.SyncInfo.USER_LOCALITY;
import static org.smartregister.AllConstants.SyncInfo.USER_NAME;
import static org.smartregister.AllConstants.SyncInfo.USER_TEAM;
import static org.smartregister.AllConstants.SyncInfo.VALID_CLIENTS;
import static org.smartregister.AllConstants.SyncInfo.VALID_EVENTS;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.view.contract.StatsFragmentContract;
import org.smartregister.view.presenter.StatsFragmentPresenter;

import java.text.DateFormat;
import java.util.Date;
import java.util.Map;

public class StatsFragment extends Fragment implements StatsFragmentContract.View {

    private StatsFragmentPresenter presenter;

    private TextView tvSyncedEvents;
    private TextView tvUnSyncedEvents;
    private TextView tvSyncedClient;
    private TextView tvUnSyncedClients;
    private TextView tvValidatedEvents;
    private TextView tvValidatedClients;
    private TextView tvTaskUnprocessedEvents;
    private TextView tvUserName;
    private TextView tvAppVersionName;
    private TextView tvAppVersionCode;
    private TextView tvDBVersion;
    private TextView tvTeam;
    private TextView tvLocality;
    private TextView tvManufacturer;
    private TextView tvDevice;
    private TextView tvOS;
    private TextView tvBuildDate;
    private TextView tvCurrentDate;
    private TextView tvUnsyncedVaccineEvents;
    private TextView tvUnsyncedWeightEvents;
    private TextView tvUnsyncedHeightEvents;

    public static StatsFragment newInstance(Bundle bundle) {
        StatsFragment fragment = new StatsFragment();
        if (bundle != null) {
            fragment.setArguments(bundle);
        }
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new StatsFragmentPresenter(this);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stats, container, false);
        initializeViews(rootView);
        return rootView;
    }

    private void initializeViews(View view) {

        tvSyncedEvents = view.findViewById(R.id.synced_events);
        tvUnSyncedEvents = view.findViewById(R.id.unsynced_events);
        tvSyncedClient = view.findViewById(R.id.synced_clients);
        tvUnSyncedClients = view.findViewById(R.id.unsynced_clients);
        tvValidatedEvents = view.findViewById(R.id.validated_events);
        tvValidatedClients = view.findViewById(R.id.validated_clients);
        tvTaskUnprocessedEvents = view.findViewById(R.id.task_unprocessed_events);
        tvUserName = view.findViewById(R.id.user_value);
        tvAppVersionName = view.findViewById(R.id.app_version_name_value);
        tvAppVersionCode = view.findViewById(R.id.app_version_code_value);
        tvDBVersion = view.findViewById(R.id.db_version_value);
        tvTeam = view.findViewById(R.id.team_value);
        tvLocality = view.findViewById(R.id.locality_value);
        tvManufacturer = view.findViewById(R.id.manufacturer_value);
        tvDevice = view.findViewById(R.id.device_value);
        tvOS = view.findViewById(R.id.os_value);
        tvBuildDate = view.findViewById(R.id.build_date_value);
        tvCurrentDate = view.findViewById(R.id.date_value);
        tvUnsyncedVaccineEvents = view.findViewById(R.id.synced_vaccine_events);
        tvUnsyncedHeightEvents = view.findViewById(R.id.synced_height_events);
        tvUnsyncedWeightEvents = view.findViewById(R.id.synced_weight_events);

        Button btnRefreshStats = view.findViewById(R.id.refresh_button);
        btnRefreshStats.setOnClickListener(v -> presenter.fetchSyncInfo());

        presenter.fetchSyncInfo();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void refreshECSyncInfo(Map<String, String> syncInfoMap) {

        tvSyncedEvents.setText(syncInfoMap.get(SYNCED_EVENTS));
        tvUnSyncedEvents.setText(syncInfoMap.get(UNSYNCED_EVENTS));
        tvTaskUnprocessedEvents.setText(syncInfoMap.get(TASK_UNPROCESSED_EVENTS));

        tvSyncedClient.setText(syncInfoMap.get(SYNCED_CLIENTS));
        tvUnSyncedClients.setText(syncInfoMap.get(UNSYNCED_CLIENTS));

        tvValidatedEvents.setText(syncInfoMap.get(VALID_EVENTS));
        tvValidatedClients.setText(syncInfoMap.get(VALID_CLIENTS));

        tvUnsyncedVaccineEvents.setText(syncInfoMap.get(UNSYNCED_VACCINE_EVENTS));
        tvUnsyncedWeightEvents.setText(syncInfoMap.get(UNSYNCED_WEIGHT_EVENTS));
        // Only show height stats if they are record height is enabled
        if (CoreLibrary.getInstance().context().getAppProperties().isTrue("monitor.height")) // Constant is defined in growth-monitoring module
            tvUnsyncedHeightEvents.setText(syncInfoMap.get(UNSYNCED_HEIGHT_EVENTS));
        else {
            if (getView() != null) {
                getView().findViewById(R.id.height_stats).setVisibility(View.GONE);
            }
        }
        tvUserName.setText(syncInfoMap.get(USER_NAME));
        tvAppVersionName.setText(syncInfoMap.get(APP_VERSION_NAME));
        tvAppVersionCode.setText(syncInfoMap.get(APP_VERSION_CODE));
        tvDBVersion.setText(syncInfoMap.get(DB_VERSION));
        tvBuildDate.setText(syncInfoMap.get(APP_BUILD_DATE));

        tvTeam.setText(syncInfoMap.get(USER_TEAM));
        tvLocality.setText(syncInfoMap.get(USER_LOCALITY));

        tvManufacturer.setText(syncInfoMap.get(MANUFACTURER));
        tvDevice.setText(syncInfoMap.get(MODEL));
        tvOS.setText(syncInfoMap.get(OS_VERSION));
        tvCurrentDate.setText(DateFormat.getDateTimeInstance().format(new Date()));
    }

}
