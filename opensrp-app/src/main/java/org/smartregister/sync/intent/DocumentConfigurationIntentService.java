package org.smartregister.sync.intent;

import android.content.Intent;

import org.smartregister.CoreLibrary;
import org.smartregister.repository.ClientFormRepository;
import org.smartregister.repository.ManifestRepository;
import org.smartregister.service.DocumentConfigurationService;
import org.smartregister.service.HTTPAgent;

import timber.log.Timber;

/**
 * Created by cozej4 on 2020-04-08.
 *
 * @author cozej4 https://github.com/cozej4
 */
public class DocumentConfigurationIntentService extends BaseSyncIntentService {
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
        httpAgent = CoreLibrary.getInstance().context().getHttpAgent();
        manifestRepository = CoreLibrary.getInstance().context().getManifestRepository();
        clientFormRepository = CoreLibrary.getInstance().context().getClientFormRepository();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        super.onHandleIntent(intent);

        try {
            DocumentConfigurationService documentConfigurationService = new DocumentConfigurationService(httpAgent, manifestRepository, clientFormRepository, getApplicationContext().getPackageName());
            documentConfigurationService.fetchManifest();
        } catch (Exception e) {
            Timber.e(e);
        }
    }
}
