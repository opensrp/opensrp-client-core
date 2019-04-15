package org.smartregister.repository;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.p2p.model.DataType;
import org.smartregister.p2p.model.dao.SenderTransferDao;
import org.smartregister.p2p.sync.JsonData;
import org.smartregister.p2p.sync.MultiMediaData;

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
            Timber.e("The data type provided does not exist");
            return null;
        }
    }

    @Nullable
    @Override
    public MultiMediaData getMultiMediaData(@NonNull DataType dataType, long l) {
        HashMap<String, Object> imageDetails = CoreLibrary.getInstance().context()
                .imageRepository().getImage(l);

        if (imageDetails != null) {
            File inputFile = new File((String) imageDetails.get(ImageRepository.filepath_COLUMN));

            if (inputFile.exists()) {
                new MultiMediaData(
                        inputFile,
                        (long) imageDetails.get(AllConstants.ROWID)
                );
            } else {
                return null;
            }
        }

        return null;
    }
}
