package org.smartregister.repository;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.domain.FetchStatus;
import org.smartregister.domain.db.EventClient;
import org.smartregister.p2p.P2PLibrary;
import org.smartregister.p2p.model.DataType;
import org.smartregister.p2p.model.dao.ReceiverTransferDao;
import org.smartregister.p2p.sync.MultiMediaData;
import org.smartregister.receiver.SyncStatusBroadcastReceiver;
import org.smartregister.sync.ClientProcessorForJava;
import org.smartregister.util.OpenSRPImageLoader;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 10/04/2019
 */

public class P2PReceiverTransferDao extends BaseP2PTransferDao implements ReceiverTransferDao {

    @Override
    public TreeSet<DataType> getDataTypes() {
        return (TreeSet<DataType>) dataTypes.clone();
    }

    @Override
    public long receiveJson(@NonNull DataType dataType, @NonNull JSONArray jsonArray) {
        EventClientRepository eventClientRepository = CoreLibrary.getInstance().context().getEventClientRepository();
        int eventsMaxRowId = eventClientRepository.getMaxRowId(EventClientRepository.Table.event);
        long maxTableRowId = 0;

        // Retrieve the max
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                long rowId = jsonObject.getLong(AllConstants.ROWID);
                jsonObject.remove(AllConstants.ROWID);

                if (rowId > maxTableRowId) {
                    maxTableRowId = rowId;
                }
            }

        } catch (JSONException e) {
            Timber.e(e);
            return 0;
        }

        if (dataType.getName().equals(event.getName())) {
            Timber.e("Received %s events", String.valueOf(jsonArray.length()));
            eventClientRepository.batchInsertEvents(jsonArray, 0);
        } else if (dataType.getName().equals(client.getName())) {
            Timber.e("Received %s clients", String.valueOf(jsonArray.length()));
            eventClientRepository.batchInsertClients(jsonArray);
        } else {
            Timber.e("The data type provided does not exist");
            return maxTableRowId;
        }

        List<EventClient> eventClientList = eventClientRepository.fetchEventClientsByRowId(eventsMaxRowId);

        try {
            ClientProcessorForJava.getInstance(P2PLibrary.getInstance().getContext())
                    .processClient(eventClientList);

            Timber.e("Processing %s EventClients", String.valueOf(eventClientList.size()));
        } catch (Exception e) {
            Timber.e(e);
            return 0;
        }

        sendSyncStatusBroadcastMessage(FetchStatus.fetched);

        return maxTableRowId;
    }

    @Override
    public long receiveMultimedia(@NonNull DataType dataType, @NonNull final File file, @Nullable HashMap<String, String> multimediaDetails) {
        if (multimediaDetails != null && file.exists()) {
            // Read the input stream to a file
            final String syncStatus = multimediaDetails.get(ImageRepository.syncStatus_COLUMN);
            final String entityId = multimediaDetails.get(ImageRepository.entityID_COLUMN);
            long rowId = Long.valueOf(multimediaDetails.get(AllConstants.ROWID));

            if (OpenSRPImageLoader.moveSyncedImageAndSaveProfilePic(syncStatus, entityId, file)) {
                return rowId;
            }
        }

        return -1;
    }

    private void sendSyncStatusBroadcastMessage(FetchStatus fetchStatus) {
        Intent intent = new Intent();
        intent.setAction(SyncStatusBroadcastReceiver.ACTION_SYNC_STATUS);
        intent.putExtra(SyncStatusBroadcastReceiver.EXTRA_FETCH_STATUS, fetchStatus);
        CoreLibrary.getInstance().context().applicationContext().sendBroadcast(intent);
    }
}
