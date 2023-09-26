package org.smartregister.sync.intent;

import android.content.Intent;

import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

import org.smartregister.CoreLibrary;
import org.smartregister.domain.FetchStatus;
import org.smartregister.domain.db.EventClient;
import org.smartregister.domain.db.EventClientQueryResult;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.util.Utils;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.List;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 10/05/2019
 */

@Deprecated
public class P2pProcessRecordsService extends BaseSyncIntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's construcztor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public P2pProcessRecordsService(String name) {
        super(name);
    }

    public P2pProcessRecordsService() {
        super("P2pProcessRecordsService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {

        super.onHandleIntent(intent);
        AllSharedPreferences allSharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();

        if (allSharedPreferences.isPeerToPeerUnprocessedEvents()) {
            CoreLibrary.getInstance().setPeerToPeerProcessing(true);

            long eventsMaxRowId = allSharedPreferences.getLastPeerToPeerSyncProcessedEvent();
            EventClientRepository eventClientRepository = CoreLibrary.getInstance().context().getEventClientRepository();

            while (eventsMaxRowId > -1) {
                EventClientQueryResult eventClientQueryResult = eventClientRepository.fetchEventClientsByRowId(eventsMaxRowId);
                List<EventClient> eventClientList = eventClientQueryResult.getEventClientList();

                if (eventClientList.size() > 0) {
                    try {
                        DrishtiApplication.getInstance().getClientProcessor().processClient(eventClientList);
                        int tableMaxRowId = eventClientRepository.getMaxRowId(EventClientRepository.Table.event);

                        if (tableMaxRowId == eventClientQueryResult.getMaxRowId()) {
                            eventsMaxRowId = -1;
                            allSharedPreferences.resetLastPeerToPeerSyncProcessedEvent();
                        } else {
                            eventsMaxRowId = eventClientQueryResult.getMaxRowId();
                            allSharedPreferences.setLastPeerToPeerSyncProcessedEvent(eventClientQueryResult.getMaxRowId());
                        }

                        // Profile images do not have a foreign key to the clients and can therefore be saved during the sync.
                        // They also do not take long to save and therefore happen during sync
                        Timber.i("Finished processing %s EventClients", String.valueOf(eventClientList.size()));
                    } catch (Exception e) {
                        Timber.e(e);
                    }
                } else {
                    allSharedPreferences.resetLastPeerToPeerSyncProcessedEvent();
                    break;
                }

            }

            sendSyncStatusBroadcastMessage(FetchStatus.fetched);
        }
    }

    @VisibleForTesting
    protected void sendSyncStatusBroadcastMessage(FetchStatus fetchStatus) {
        CoreLibrary.getInstance().context().applicationContext().sendBroadcast(Utils.completeSync(fetchStatus));
    }

    @Override
    public void onDestroy() {
        // This ensure that even if the `onHandleIntent` is closed prematurely, we remove the Snackbar since
        // onDestroy will always be called
        if (CoreLibrary.getInstance().isPeerToPeerProcessing()) {
            CoreLibrary.getInstance().setPeerToPeerProcessing(false);
        }
        super.onDestroy();
    }
}
