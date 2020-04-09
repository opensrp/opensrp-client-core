package org.smartregister.sync.intent;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.ClientForm;
import org.smartregister.domain.Manifest;
import org.smartregister.domain.Response;
import org.smartregister.domain.db.Client;
import org.smartregister.domain.jsonmapping.Time;
import org.smartregister.dto.ClientFormResponse;
import org.smartregister.dto.ManifestDTO;
import org.smartregister.exception.NoHttpResponseException;
import org.smartregister.repository.ClientFormRepository;
import org.smartregister.repository.ManifestRepository;
import org.smartregister.service.HTTPAgent;

import java.net.URLEncoder;
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
    private ClientFormRepository clientFormRepository;

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
        clientFormRepository = CoreLibrary.getInstance().context().getClientFormRepository();
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

        //Note active manifest is null for the first time synchronization of the application
        Manifest activeManifest = manifestRepository.getActiveManifest();

        if (activeManifest == null) {
            saveReceivedManifest(receivedManifest);
        } else if (!activeManifest.getFormVersion().equals(receivedManifest.getFormVersion())) {
            //Untaging the active manifest and saving the received manifest and tagging it as active
            activeManifest.setActive(false);
            activeManifest.setNew(false);
            manifestRepository.addOrUpdate(activeManifest);

            saveReceivedManifest(receivedManifest);

            //Fetching Client Forms for identifiers in the manifest
            for(String identifier : receivedManifest.getIdentifiers()){
                try {
                    fetchClientForm(identifier, receivedManifest.getFormVersion(), clientFormRepository.getActiveClientFormByIdentifier(identifier));
                }catch (Exception e){
                    Timber.e(e);
                }
            }
        }


    }

    protected void fetchClientForm(String identifier, String formVersion, ClientForm activeClientForm) throws Exception {
        HTTPAgent httpAgent = CoreLibrary.getInstance().context().getHttpAgent();
        if (httpAgent == null) {
            throw new IllegalArgumentException(CLIENT_FORM_SYNC_URL + " http agent is null");
        }
        String baseUrl = CoreLibrary.getInstance().context().
                configuration().dristhiBaseURL();
        String endString = "/";
        if (baseUrl.endsWith(endString)) {
            baseUrl = baseUrl.substring(0, baseUrl.lastIndexOf(endString));
        }
        Response resp = httpAgent.fetch(
                MessageFormat.format("{0}{1}{2}",
                        baseUrl,
                        CLIENT_FORM_SYNC_URL,
                        URLEncoder.encode("?form_identifier=" + identifier +
                                "&form_version=" + formVersion +
                                (activeClientForm==null ? "" : "&current_form_version=" + activeClientForm.getVersion()))));

        if (resp.isFailure()) {
            throw new NoHttpResponseException(CLIENT_FORM_SYNC_URL + " not returned data");
        }


        ClientFormResponse clientFormResponse =
                new Gson().fromJson(resp.payload().toString(), ClientFormResponse.class);

        if(activeClientForm==null || !clientFormResponse.getClientFormMetadata().getVersion().equals(activeClientForm.getVersion())){
            //if the previously active client form is not null it should be untagged from being new nor active
            if(activeClientForm!=null){
                activeClientForm.setActive(false);
                activeClientForm.setNew(false);
                clientFormRepository.addOrUpdate(activeClientForm);
            }
            ClientForm clientForm = convertClientFormResponseToClientForm(clientFormResponse);
            saveReceivedClientForm(clientForm);
        }
    }

    public HTTPAgent getHttpAgent() {
        return httpAgent;
    }

    public Context getContext() {
        return this.context;
    }


    private Manifest convertManifestDTOToManifest(ManifestDTO manifestDTO) {
        Manifest manifest = new Manifest();
        manifest.setId(manifestDTO.getId().toString());
        manifest.setAppVersion(manifestDTO.getAppVersion());
        manifest.setCreatedAt(manifestDTO.getCreatedAt());

        JSONObject json = manifestDTO.getJson();
        if (json.has("forms_version")) {
            try {
                manifest.setFormVersion(json.getString("forms_version"));
            } catch (JSONException e) {
                Timber.e(e);
            }
        }

        if (json.has("identifiers")) {
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

    private ClientForm convertClientFormResponseToClientForm(ClientFormResponse clientFormResponse) {
        ClientForm clientForm = new ClientForm();
        clientForm.setId(clientFormResponse.getClientForm().getId().toString());
        clientForm.setCreatedAt(clientFormResponse.getClientFormMetadata().getCreatedAt());
        clientForm.setIdentifier(clientFormResponse.getClientFormMetadata().getIdentifier());
        clientForm.setJson(clientFormResponse.getClientForm().getJson().toString());
        clientForm.setVersion(clientFormResponse.getClientFormMetadata().getVersion());
        clientForm.setLabel(clientFormResponse.getClientFormMetadata().getLabel());
        clientForm.setJurisdiction(clientFormResponse.getClientFormMetadata().getJurisdiction());
        clientForm.setModule(clientFormResponse.getClientFormMetadata().getModule());

        return clientForm;
    }

    private void saveReceivedManifest(Manifest receivedManifest) {
        receivedManifest.setNew(true);
        receivedManifest.setActive(true);
        manifestRepository.addOrUpdate(receivedManifest);

        //deleting the third oldest manifest from the repository
        List<Manifest> manifestsList = manifestRepository.getAllManifestsManifest();
        if (manifestsList.size() > 2) {
            manifestRepository.delete(manifestsList.get(2).getId());
        }
    }

    private void saveReceivedClientForm(ClientForm clientForm) {
        clientForm.setNew(true);
        clientForm.setActive(true);
        clientFormRepository.addOrUpdate(clientForm);

        //deleting the third oldest client Form from the repository
        List<ClientForm> clientFormList = clientFormRepository.getClientFormByIdentifier(clientForm.getIdentifier());
        if (clientFormList.size() > 2) {
            clientFormRepository.delete(clientFormList.get(2).getId());
        }
    }
}
