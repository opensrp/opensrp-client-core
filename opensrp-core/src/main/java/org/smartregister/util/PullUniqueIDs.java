package org.smartregister.util;

import androidx.annotation.VisibleForTesting;

import org.json.JSONArray;
import org.json.JSONObject;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.SyncConfiguration;
import org.smartregister.domain.Response;
import org.smartregister.exception.NoHttpResponseException;
import org.smartregister.repository.UniqueIdRepository;
import org.smartregister.service.HTTPAgent;

import java.util.ArrayList;
import java.util.List;

import timber.log.Timber;

/**
 * Created by ndegwamartin on 07/10/2021.
 * <p>
 * Helper duplicated because we want results synchronously from https://github.com/opensrp/opensrp-client-core/blob/master/opensrp-app/src/main/java/org/smartregister/sync/intent/PullUniqueIdsIntentService.java
 */
public class PullUniqueIDs {
    public static final String ID_URL = "/uniqueids/get";
    public static final String IDENTIFIERS = "identifiers";

    UniqueIdRepository uniqueIdRepo;

    public PullUniqueIDs() {
        uniqueIdRepo = Context.getInstance().getUniqueIdRepository();
        try {
            SyncConfiguration configs = CoreLibrary.getInstance().getSyncConfiguration();
            int numberToGenerate;
            if (uniqueIdRepo.countUnUsedIds() == 0) { // first time pull no ids at all
                numberToGenerate = configs.getUniqueIdInitialBatchSize();
            } else if (uniqueIdRepo.countUnUsedIds() <= 250) { //maintain a minimum of 250 else skip this pull
                numberToGenerate = configs.getUniqueIdBatchSize();
            } else {
                return;
            }
            JSONObject ids = fetchOpenMRSIds(configs.getUniqueIdSource(), numberToGenerate);
            if (ids.has(IDENTIFIERS)) {
                parseResponse(ids);
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private JSONObject fetchOpenMRSIds(int source, int numberToGenerate) throws Exception {
        HTTPAgent httpAgent = getHttpAgent();
        String baseUrl = Context.getInstance().configuration().dristhiBaseURL();
        String endString = "/";
        if (baseUrl.endsWith(endString)) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(endString));
        }

        String url = baseUrl + ID_URL + "?source=" + source + "&numberToGenerate=" + numberToGenerate;
        Timber.i("URL: %s", url);

        if (httpAgent == null) {
            throw new IllegalArgumentException(ID_URL + " http agent is null");
        }

        Response<String> resp = httpAgent.fetch(url);
        if (resp.isFailure()) {
            throw new NoHttpResponseException(ID_URL + " not returned data");
        }

        return new JSONObject((String) resp.payload());
    }

    private void parseResponse(JSONObject idsFromOMRS) throws Exception {
        JSONArray jsonArray = idsFromOMRS.optJSONArray(IDENTIFIERS);
        if (jsonArray != null && jsonArray.length() > 0) {
            List<String> ids = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                ids.add(jsonArray.getString(i));
            }
            uniqueIdRepo.bulkInsertOpenmrsIds(ids);
        }
    }

    @VisibleForTesting
    protected HTTPAgent getHttpAgent() {
        return Context.getInstance().getHttpAgent();
    }

}
