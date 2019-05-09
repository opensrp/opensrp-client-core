package org.smartregister.sync.helper;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.apache.http.NoHttpResponseException;
import org.joda.time.LocalDate;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.FetchStatus;
import org.smartregister.domain.PlanDefinition;
import org.smartregister.domain.Response;
import org.smartregister.domain.PlanDefinition;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.PlanDefinitionRepository;
import org.smartregister.service.HTTPAgent;
import org.smartregister.util.DateTypeConverter;
import org.smartregister.util.Utils;

import java.util.List;

import static org.smartregister.AllConstants.CURRENT_OPERATIONAL_AREA;

/**
 * Created by Vincent Karuri on 08/05/2019
 */
public class PlanIntentServiceHelper {

    private PlanDefinitionRepository planDefinitionRepository;
    private AllSharedPreferences allSharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();
    private static final Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, new DateTypeConverter()).create();
    private final String TAG = PlanIntentServiceHelper.class.getName();

    protected final Context context;
    protected static PlanIntentServiceHelper instance;

    public static final String SYNC_PLANS_URL = "/rest/plans/sync";
    public static final String PLAN_LAST_SYNC_DATE = "plan_last_sync_date";

    public static PlanIntentServiceHelper getInstance() {
        if (instance == null) {
            instance = new PlanIntentServiceHelper(CoreLibrary.getInstance().context().getPlanDefinitionRepository());
        }
        return instance;
    }

    private PlanIntentServiceHelper(PlanDefinitionRepository planRepository) {
        this.context = CoreLibrary.getInstance().context().applicationContext();
        this.planDefinitionRepository = planRepository;
    }

    public void syncPlans() {
        try {
            long serverVersion = 0;
            try {
                serverVersion = Long.parseLong(allSharedPreferences.getPreference(PLAN_LAST_SYNC_DATE));
            } catch (NumberFormatException e) {
                Log.e(TAG, e.getMessage(), e);
            }
            if (serverVersion > 0) { serverVersion += 1; };
            // fetch and save plans
            String plansResponse = fetchPlans(allSharedPreferences.getPreference(CURRENT_OPERATIONAL_AREA), serverVersion);
            List<PlanDefinition> plans = gson.fromJson(plansResponse, new TypeToken<List<PlanDefinition>>() {}.getType());
            for (PlanDefinition plan : plans) {
                try {
                    planDefinitionRepository.addOrUpdate(plan);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // update most recent server version
            if (!Utils.isEmptyCollection(plans)) {
                allSharedPreferences.savePreference(PLAN_LAST_SYNC_DATE, getPlanDefinitionMaxServerVersion(plans));
            }
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    private String fetchPlans(String operationalAreaId, long serverVersion) throws Exception {
        HTTPAgent httpAgent = CoreLibrary.getInstance().context().getHttpAgent();
        String baseUrl = CoreLibrary.getInstance().context().configuration().dristhiBaseURL();
        String endString = "/";
        if (baseUrl.endsWith(endString)) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(endString));
        }
        
        String url = baseUrl + SYNC_PLANS_URL + "?operational_area_id=" + operationalAreaId + "&serverVersion=" + serverVersion;
        if (httpAgent == null) {
            context.sendBroadcast(Utils.completeSync(FetchStatus.noConnection));
            throw new IllegalArgumentException(SYNC_PLANS_URL + " http agent is null");
        }

        Response resp = httpAgent.fetch(url);
        if (resp.isFailure()) {
            context.sendBroadcast(Utils.completeSync(FetchStatus.nothingFetched));
            throw new NoHttpResponseException(SYNC_PLANS_URL + " did not return any data");
        }
        return resp.payload().toString();
    }

    private String getPlanDefinitionMaxServerVersion(List<PlanDefinition> planDefinitions) {
        long maxServerVersion = 0;
        for (PlanDefinition planDefinition : planDefinitions) {
            long serverVersion = planDefinition.getServerVersion();
            if (serverVersion > maxServerVersion) {
                maxServerVersion = serverVersion;
            }
        }
        return String.valueOf(maxServerVersion);
    }
}
