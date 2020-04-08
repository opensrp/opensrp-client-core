package org.smartregister.sync.intent;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.Manifest;
import org.smartregister.domain.Response;
import org.smartregister.dto.ManifestDTO;
import org.smartregister.exception.NoHttpResponseException;
import org.smartregister.repository.ManifestRepository;
import org.smartregister.service.HTTPAgent;

import java.text.MessageFormat;
import java.util.List;

import timber.log.Timber;

/**
 * Created by cozej4 on 2020-04-08.
 *
 * @author cozej4 https://github.com/cozej4
 */
public class DocumentConfigurationIntentService extends BaseSyncIntentService {
    public static final String MANIFEST_SYNC_URL = "rest/manifest/";
    public static final String CLIENT_FORM_SYNC_URL = "rest/clientForm";
    private Context context;
    private HTTPAgent httpAgent;
    private ManifestRepository manifestRepository;

    public DocumentConfigurationIntentService() {
        super("DocumentConfigurationIntentService");
    }

    public DocumentConfigurationIntentService(String name) {
        super(name);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        context = getBaseContext();
        httpAgent = CoreLibrary.getInstance().context().getHttpAgent();
        manifestRepository = CoreLibrary.getInstance().context().getManifestRepository();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        super.onHandleIntent(intent);

        try {
            fetchManifest();
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    protected void fetchManifest() throws Exception {
        HTTPAgent httpAgent = CoreLibrary.getInstance().context().getHttpAgent();
        if (httpAgent == null) {
            throw new IllegalArgumentException(MANIFEST_SYNC_URL + " http agent is null");
        }
        String baseUrl = CoreLibrary.getInstance().context().
                configuration().dristhiBaseURL();
        String endString = "/";
        if (baseUrl.endsWith(endString)) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(endString));
        }
        Response resp = httpAgent.fetch(
                MessageFormat.format("{0}{1}{2}",
                        baseUrl, MANIFEST_SYNC_URL, getApplicationContext().getPackageName()));

        if (resp.isFailure()) {
            throw new NoHttpResponseException(MANIFEST_SYNC_URL + " not returned data");
        }

        ManifestDTO receivedManifestDTO =
                new Gson().fromJson(resp.payload().toString(), ManifestDTO.class);

        Manifest receivedManifest = convertManifestDTOToManifest(receivedManifestDTO);

        Manifest activeManifest = manifestRepository.getActiveManifest();

        if(!activeManifest.getFormVersion().equals(receivedManifest.getFormVersion())){
            //Untaging the active manifest and saving the received manifest and tagging it as active
            activeManifest.setActive(false);
            activeManifest.setNew(false);
            manifestRepository.addOrUpdate(activeManifest);

            receivedManifest.setNew(true);
            receivedManifest.setActive(true);
            manifestRepository.addOrUpdate(receivedManifest);

        }


    }

    public HTTPAgent getHttpAgent() {
        return httpAgent;
    }

    public Context getContext() {
        return this.context;
    }


    private Manifest convertManifestDTOToManifest(ManifestDTO manifestDTO){
        Manifest manifest = new Manifest();
        manifest.setId(manifestDTO.getId().toString());
        manifest.setAppVersion(manifestDTO.getAppVersion());

        JSONObject json = manifestDTO.getJson();
        if(json.has("forms_version")){
            try {
                manifest.setFormVersion(json.getString("forms_version"));
            } catch (JSONException e) {
                Timber.e(e);
            }
        }

        if(json.has("identifiers")){
            List<String> identifiers = null;
            try {
                identifiers = new Gson().fromJson(json.getString("identifiers"), new TypeToken<List<String>>() {
                }.getType());
            } catch (JSONException e) {
                Timber.e(e);
            }

            manifest.setIdentifiers(identifiers);
        }
        return manifest;
    }
}
