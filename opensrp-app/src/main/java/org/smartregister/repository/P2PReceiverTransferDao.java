package org.smartregister.repository;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.p2p.model.DataType;
import org.smartregister.p2p.model.dao.ReceiverTransferDao;
import org.smartregister.util.OpenSRPImageLoader;

import java.io.File;
import java.util.HashMap;
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

        AllSharedPreferences allSharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();
        if (!allSharedPreferences.isPeerToPeerUnprocessedEvents()) {
            allSharedPreferences.setLastPeerToPeerSyncProcessedEvent(eventsMaxRowId);
        }

        return maxTableRowId;
    }

    @Override
    public long receiveMultimedia(@NonNull DataType dataType, @NonNull File file, @Nullable HashMap<String, Object> multimediaDetails, long fileRecordId) {
        if (multimediaDetails != null && file.exists()) {
            // Read the input stream to a file
            final String syncStatus = (String) multimediaDetails.get(ImageRepository.syncStatus_COLUMN);
            final String entityId = (String) multimediaDetails.get(ImageRepository.entityID_COLUMN);

            if (OpenSRPImageLoader.moveSyncedImageAndSaveProfilePic(syncStatus, entityId, file)) {
                return fileRecordId;
            }
        }

        return -1;
    }


}
