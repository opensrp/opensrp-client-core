package org.smartregister.repository;

import org.smartregister.p2p.model.DataType;
import java.util.TreeSet;

/**
 * Created by Ephraim Kigamba - ekigamba@ona.io on 11/04/2019
 */

public abstract class BaseP2PTransferDao {

    protected DataType client = new DataType("Client", DataType.Type.NON_MEDIA, 1);
    protected DataType event = new DataType("Event", DataType.Type.NON_MEDIA, 2);
    protected DataType profilePic = new DataType("Profile Pic", DataType.Type.MEDIA, 3);

    protected TreeSet<DataType> dataTypes;

    public BaseP2PTransferDao() {
        dataTypes = new TreeSet<>();

        dataTypes.add(client);
        dataTypes.add(event);
        dataTypes.add(profilePic);
    }
}
