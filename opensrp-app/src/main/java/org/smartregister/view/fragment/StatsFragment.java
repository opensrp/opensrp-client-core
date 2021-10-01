package org.smartregister.view.fragment;

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

import java.util.Map;

import static org.smartregister.AllConstants.SyncInfo.SYNCED_CLIENTS;
import static org.smartregister.AllConstants.SyncInfo.SYNCED_EVENTS;
import static org.smartregister.AllConstants.SyncInfo.TASK_UNPROCESSED_EVENTS;
import static org.smartregister.AllConstants.SyncInfo.UNSYNCED_CLIENTS;
import static org.smartregister.AllConstants.SyncInfo.UNSYNCED_EVENTS;
import static org.smartregister.AllConstants.SyncInfo.VALID_CLIENTS;
import static org.smartregister.AllConstants.SyncInfo.VALID_EVENTS;

public class StatsFragment extends Fragment implements StatsFragmentContract.View {

    private StatsFragmentPresenter presenter;

    private TextView tvSyncedEventsLabel;
    private TextView tvUnsyncedEventsLabel;
    private TextView tvTaskUnprocessedEventsLabel;
    private TextView tvSyncedClientsLabel;
    private TextView tvUnsyncedClientsLabel;
    private TextView tvValidatedEventsLabel;
    private TextView tvValidatedClientsLabel;

    private TextView tvSyncedEvents;
    private TextView tvUnSyncedEvents;
    private TextView tvSyncedClient;
    private TextView tvUnSyncedClients;
    private TextView tvValidatedEvents;
    private TextView tvValidatedClients;
    private TextView tvTaskUnprocessedEvents;

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
        tvSyncedEventsLabel = view.findViewById(R.id.synced_events_label);
        tvUnsyncedEventsLabel = view.findViewById(R.id.unsynced_events_label);
        tvTaskUnprocessedEventsLabel = view.findViewById(R.id.task_unprocessed_events_label);
        tvSyncedClientsLabel = view.findViewById(R.id.synced_clients_label);
        tvUnsyncedClientsLabel = view.findViewById(R.id.unsynced_clients_label);
        tvValidatedEventsLabel = view.findViewById(R.id.validated_events_label);
        tvValidatedClientsLabel = view.findViewById(R.id.validated_clients_label);

        tvSyncedEvents = view.findViewById(R.id.synced_events);
        tvUnSyncedEvents = view.findViewById(R.id.unsynced_events);
        tvSyncedClient = view.findViewById(R.id.synced_clients);
        tvUnSyncedClients = view.findViewById(R.id.unsynced_clients);
        tvValidatedEvents = view.findViewById(R.id.validated_events);
        tvValidatedClients = view.findViewById(R.id.validated_clients);
        tvTaskUnprocessedEvents = view.findViewById(R.id.task_unprocessed_events);

        Button btnRefreshStats = view.findViewById(R.id.refresh_button);
        btnRefreshStats.setText(CoreLibrary.getInstance().context().applicationContext().getString(R.string.refresh));

        btnRefreshStats.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                presenter.fetchSyncInfo();
            }
        });

        presenter.fetchSyncInfo();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void refreshECSyncInfo(Map<String, Integer> syncInfoMap) {

        tvSyncedEventsLabel.setText(CoreLibrary.getInstance().context().applicationContext().getString(R.string.synced_events));
        tvUnsyncedEventsLabel.setText(CoreLibrary.getInstance().context().applicationContext().getString(R.string.unsynced_events));
        tvTaskUnprocessedEventsLabel.setText(CoreLibrary.getInstance().context().applicationContext().getString(R.string.task_unprocessed_events));
        tvSyncedClientsLabel.setText(CoreLibrary.getInstance().context().applicationContext().getString(R.string.synced_clients));
        tvUnsyncedClientsLabel.setText(CoreLibrary.getInstance().context().applicationContext().getString(R.string.unsynced_clients));
        tvValidatedEventsLabel.setText(CoreLibrary.getInstance().context().applicationContext().getString(R.string.validated_events));
        tvValidatedClientsLabel.setText(CoreLibrary.getInstance().context().applicationContext().getString(R.string.validated_clients));

        tvSyncedEvents.setText(syncInfoMap.get(SYNCED_EVENTS) + "");
        tvUnSyncedEvents.setText(syncInfoMap.get(UNSYNCED_EVENTS) + "");
        tvTaskUnprocessedEvents.setText(syncInfoMap.get(TASK_UNPROCESSED_EVENTS) + "");

        tvSyncedClient.setText(syncInfoMap.get(SYNCED_CLIENTS) + "");
        tvUnSyncedClients.setText(syncInfoMap.get(UNSYNCED_CLIENTS) + "");

        tvValidatedEvents.setText(syncInfoMap.get(VALID_EVENTS) + "");
        tvValidatedClients.setText(syncInfoMap.get(VALID_CLIENTS) + "");
    }
}
