package org.smartregister.service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.smartregister.domain.ClientForm;
import org.smartregister.domain.Manifest;
import org.smartregister.dto.ClientFormResponse;
import org.smartregister.dto.ManifestDTO;
import org.smartregister.repository.ClientFormRepository;
import org.smartregister.repository.ManifestRepository;

import java.util.List;

import timber.log.Timber;

import static org.smartregister.service.DocumentConfigurationService.FORM_VERSION;
import static org.smartregister.service.DocumentConfigurationService.IDENTIFIERS;

/**
 * Created by cozej4 on 2020-04-16.
 *
 * @author cozej4 https://github.com/cozej4
 */
public class DocumentConfigurationServiceTest {
    @Mock
    private HTTPAgent httpAgent;

    @Mock
    private ManifestRepository manifestRepository;

    @Mock
    private ClientFormRepository clientFormRepository;


    private DocumentConfigurationService documentConfigurationService;


    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        documentConfigurationService = new DocumentConfigurationService(httpAgent, manifestRepository, clientFormRepository, "0.0.1");
    }

    @Test
    public void convertManifestDTOToManifest() {
        String manifestDTOJson = "{\"json\":\"{\\\"form_versions\\\":\\\"0.0.1\\\",\\\"identifiers\\\":[\\\"anc/member_registration.json\\\",\\\"anc/pregnancy_outcome.json\\\"]}\",\"appId\":\"org.smartregister.chw\",\"appVersion\":\"0.0.1\",\"id\":1}";
        ManifestDTO manifestDTO = new Gson().fromJson(manifestDTOJson, ManifestDTO.class);
        Manifest manifest;
        try {
            manifest = documentConfigurationService.convertManifestDTOToManifest(manifestDTO);
            Assert.assertEquals(manifest.getAppVersion(), manifestDTO.getAppVersion());
            Assert.assertEquals(manifest.getCreatedAt(), manifestDTO.getCreatedAt());

            JSONObject json = new JSONObject(manifestDTO.getJson());
            Assert.assertEquals(manifest.getFormVersion(), json.getString(FORM_VERSION));
            Assert.assertEquals(manifest.getIdentifiers(), new Gson().fromJson(json.getString(IDENTIFIERS), new TypeToken<List<String>>() {
            }.getType()));
        } catch (JSONException e) {
            Timber.e(e);
        }

    }

    @Test
    public void convertClientFormResponseToClientForm() {
        String clientFormResponseJson = "{\"clientForm\":{\"id\":3,\"json\":\"{}\"},\"clientFormMetadata\":{\"id\":3,\"identifier\":\"opd/reg.json\",\"version\":\"0.0.3\"}}";
        ClientFormResponse clientFormResponse = new Gson().fromJson(clientFormResponseJson, ClientFormResponse.class);
        ClientForm clientForm = documentConfigurationService.convertClientFormResponseToClientForm(clientFormResponse);
        Assert.assertEquals(clientForm.getIdentifier(), clientFormResponse.getClientFormMetadata().getIdentifier());
        Assert.assertEquals(clientForm.getCreatedAt(), clientFormResponse.getClientFormMetadata().getCreatedAt());
        Assert.assertEquals(clientForm.getJson(), clientFormResponse.getClientForm().getJson());
        Assert.assertEquals(clientForm.getVersion(), clientFormResponse.getClientFormMetadata().getVersion());
    }
}