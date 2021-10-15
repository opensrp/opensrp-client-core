package org.smartregister.sync.intent;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.VisibleForTesting;
import androidx.work.WorkerParameters;

import org.jetbrains.annotations.NotNull;
import org.smartregister.CoreLibrary;
import org.smartregister.DristhiConfiguration;
import org.smartregister.repository.ClientFormRepository;
import org.smartregister.repository.ManifestRepository;
import org.smartregister.service.DocumentConfigurationService;
import org.smartregister.service.HTTPAgent;

import java.net.SocketException;

import timber.log.Timber;

/**
 * Created by cozej4 on 2020-04-08.
 *
 * @author cozej4 https://github.com/cozej4
 */
public class DocumentConfigurationIntentWorker extends BaseSyncIntentWorker {
    private HTTPAgent httpAgent;
    private ManifestRepository manifestRepository;
    private ClientFormRepository clientFormRepository;
    private DristhiConfiguration configuration;

    public DocumentConfigurationIntentWorker(@NonNull @NotNull Context context, @NonNull @NotNull WorkerParameters workerParams) {
        super(context, workerParams);
    }


    @Override
    protected void onRunWork() throws SocketException {
        httpAgent = CoreLibrary.getInstance().context().getHttpAgent();
        manifestRepository = CoreLibrary.getInstance().context().getManifestRepository();
        clientFormRepository = CoreLibrary.getInstance().context().getClientFormRepository();
        configuration = CoreLibrary.getInstance().context().configuration();
        try {
            DocumentConfigurationService documentConfigurationService = getDocumentConfigurationService();
            documentConfigurationService.fetchManifest();
        } catch (Exception e) {
            Timber.e(e);
            throw new RuntimeException(e);
        }
    }

    @VisibleForTesting
    @NotNull
    protected DocumentConfigurationService getDocumentConfigurationService() {
        return new DocumentConfigurationService(httpAgent, manifestRepository, clientFormRepository, configuration);
    }
}
