package org.smartregister.repository;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.p2p.P2PLibrary;
import org.smartregister.p2p.model.DataType;
import org.smartregister.p2p.model.dao.SenderTransferDao;
import org.smartregister.p2p.sync.data.JsonData;
import org.smartregister.p2p.sync.data.MultiMediaData;

import java.io.File;
import java.util.HashMap;
import java.util.TreeSet;

import timber.log.Timber;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 10/04/2019
 */

public class P2PSenderTransferDao extends BaseP2PTransferDao implements SenderTransferDao {

    @Nullable
    @Override
    public TreeSet<DataType> getDataTypes() {
        return (TreeSet<DataType>) dataTypes.clone();
    }

    @Nullable
    @Override
    public JsonData getJsonData(@NonNull DataType dataType, long l, int i) {
        if (dataType.getName().equals(event.getName())) {
            return CoreLibrary.getInstance().context()
                    .getEventClientRepository().getEvents(l, i);
        } else if (dataType.getName().equals(client.getName())) {
            return CoreLibrary.getInstance().context()
                    .getEventClientRepository().getClients(l, i);
        } else {
            Timber.e(P2PLibrary.getInstance().getContext().getString(R.string.log_data_type_provided_does_not_exist_in_the_sender)
                    , dataType.getName());
            return null;
        }
    }

    @Nullable
    @Override
    public MultiMediaData getMultiMediaData(@NonNull DataType dataType, long l) {
        if (dataType.getName().equalsIgnoreCase(profilePic.getName())) {
            HashMap<String, Object> imageDetails = CoreLibrary.getInstance().context()
                    .imageRepository().getImage(l);

            if (imageDetails != null) {
                File inputFile = new File((String) imageDetails.get(ImageRepository.filepath_COLUMN));

                if (inputFile.exists()) {
                    MultiMediaData multiMediaData = new MultiMediaData(
                            inputFile,
                            (long) imageDetails.get(AllConstants.ROWID)
                    );

                    imageDetails.remove(ImageRepository.filepath_COLUMN);

                    HashMap<String, String> multimediaDataDetails = new HashMap<>();
                    multimediaDataDetails.put(ImageRepository.syncStatus_COLUMN, (String) imageDetails.get(ImageRepository.syncStatus_COLUMN));
                    multimediaDataDetails.put(AllConstants.ROWID, String.valueOf((long) imageDetails.get(AllConstants.ROWID)));
                    multimediaDataDetails.put(ImageRepository.filecategory_COLUMN, (String) imageDetails.get(ImageRepository.filecategory_COLUMN));
                    multimediaDataDetails.put(ImageRepository.anm_ID_COLUMN, (String) imageDetails.get(ImageRepository.anm_ID_COLUMN));
                    multimediaDataDetails.put(ImageRepository.entityID_COLUMN, (String) imageDetails.get(ImageRepository.entityID_COLUMN));

                    multiMediaData.setMediaDetails(multimediaDataDetails);

                    return multiMediaData;
                } else {
                    return null;
                }
            }

            return null;
        } else {
            Timber.e(P2PLibrary.getInstance().getContext().getString(R.string.log_data_type_provided_does_not_exist_in_the_sender)
                    , dataType.getName());
            return null;
        }
    }

}
