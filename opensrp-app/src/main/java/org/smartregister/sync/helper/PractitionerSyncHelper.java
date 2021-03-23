package org.smartregister.sync.helper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.Practitioner;
import org.smartregister.domain.Response;
import org.smartregister.exception.NoHttpResponseException;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.PractitionerRepository;
import org.smartregister.service.HTTPAgent;

import java.text.MessageFormat;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Allan Onchuru on 20/03/2021
 */

public class PractitionerSyncHelper {

    private final PractitionerRepository practitionerRepository;
    private HTTPAgent httpAgent;
    private Gson gson;
    protected static PractitionerSyncHelper instance;
    private final AllSharedPreferences allSharedPreferences;
    private static final String PRACTITIONER_URL = "/rest/practitioner";
    private static final String PRACTITIONERS_BY_ID_AND_ROLE_URL = PRACTITIONER_URL + "/report-to";

    public static PractitionerSyncHelper getInstance() {
        if (instance == null) {
            instance = new PractitionerSyncHelper(CoreLibrary.getInstance().context().getPractitionerRepository());
        }
        return instance;
    }

    private PractitionerSyncHelper(PractitionerRepository practitionerRepository) {
        this.practitionerRepository = practitionerRepository;
        this.allSharedPreferences = CoreLibrary.getInstance().context().allSharedPreferences();
        this.httpAgent = CoreLibrary.getInstance().context().getHttpAgent();
        this.gson = new Gson();
    }


    public void syncPractitionersByIdAndRoleFromServer() {
        String practitionerIdentifier = allSharedPreferences.getUserPractitionerIdentifier();
        String code = allSharedPreferences.getUserPractitionerRole();
        try {
            String response = syncPractitioners(practitionerIdentifier, code);
            List<Practitioner> practitioners = gson.fromJson(response, new TypeToken<List<Practitioner>>() {
            }.getType());
            for (Practitioner practitioner : practitioners) {
                practitionerRepository.addOrUpdate(practitioner);
            }
        } catch (Exception ex) {
            Timber.e(ex);
        }
    }

    private String syncPractitioners(String practitionerIdentifier, String code) throws JSONException, NoHttpResponseException {
        if (httpAgent == null) {
            throw new IllegalArgumentException(PRACTITIONERS_BY_ID_AND_ROLE_URL + " HTTPAgent agent is null");
        }

        String baseUrl = getFormattedBaseUrl();

        JSONObject request = new JSONObject();
        request.put("practitionerIdentifier", practitionerIdentifier);
        request.put("code", code);

        Response resp = httpAgent.post(
                MessageFormat.format("{0}{1}",
                        baseUrl,
                        PRACTITIONERS_BY_ID_AND_ROLE_URL),
                request.toString());

        if (resp.isFailure()) {
            throw new NoHttpResponseException(PRACTITIONERS_BY_ID_AND_ROLE_URL + " not data returned");
        }

        return resp.payload().toString();
    }

    public String getFormattedBaseUrl() {
        String baseUrl = CoreLibrary.getInstance().context().configuration().dristhiBaseURL();
        String endString = "/";
        if (baseUrl.endsWith(endString)) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(endString));
        }
        return baseUrl;
    }

}
