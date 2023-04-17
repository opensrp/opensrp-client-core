package org.smartregister.service;

import android.app.IntentService;
import android.content.Intent;

import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.ProfileImage;
import org.smartregister.domain.ResponseStatus;
import org.smartregister.repository.ImageRepository;

import java.util.List;

import timber.log.Timber;

/**
 * Created by Raihan Ahmed on 10/14/15.
 */
public class ImageUploadSyncService extends IntentService {
    private static final String TAG = ImageUploadSyncService.class.getCanonicalName();
    private ImageRepository imageRepo;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     * <p>
     * name Used to name the worker thread, important only for debugging.
     */
    public ImageUploadSyncService() {
        super("ImageUploadSyncService");
        imageRepo = CoreLibrary.getInstance().context().imageRepository();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            List<ProfileImage> profileImages = imageRepo.findAllUnSynced();
            for (int i = 0; i < profileImages.size(); i++) {
                String response = CoreLibrary.getInstance().context().getHttpAgent().httpImagePost(
                        getImageUploadEndpoint(), profileImages.get(i));
                if (response.contains(ResponseStatus.success.displayValue())) {
                    imageRepo.close(profileImages.get(i).getImageid());
                } else {
                    Timber.e("Image Upload: could NOT upload image ID: %s PATH: %s ", profileImages.get(i).getImageid(), profileImages.get(i).getFilepath());
                }
            }
        } catch (Exception e) {
            Timber.e(e);
        }
    }

    private String getImageUploadEndpoint() {
        return CoreLibrary.getInstance().context().configuration().dristhiBaseURL()
                + AllConstants.PROFILE_IMAGES_UPLOAD_PATH;
    }
}