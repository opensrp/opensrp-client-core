package org.opensrp.service;

import android.app.IntentService;
import android.content.Intent;

import org.opensrp.AllConstants;
import org.opensrp.Context;
import org.opensrp.domain.ProfileImage;
import org.opensrp.repository.ImageRepository;

import java.util.List;

import static org.opensrp.util.Log.logError;
import static org.opensrp.util.Log.logInfo;

/**
 * Created by Raihan Ahmed on 10/14/15.
 */
public class ImageUploadSyncService extends IntentService {
    private static final String TAG=ImageUploadSyncService.class.getCanonicalName();
    private ImageRepository imageRepo = null;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * name Used to name the worker thread, important only for debugging.
     */
    public ImageUploadSyncService() {
        super("ImageUploadSyncService");
        imageRepo=(ImageRepository) org.opensrp.Context.getInstance().imageRepository();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            List<ProfileImage> profileImages = imageRepo.findAllUnSynced();
            for(int i = 0;i<profileImages.size();i++){
                String response = Context.getInstance().getHttpAgent().httpImagePost(Context.getInstance().configuration().dristhiBaseURL()+ AllConstants.PROFILE_IMAGES_UPLOAD_PATH,profileImages.get(i));
                if(response.contains("success")){
                    imageRepo.close(profileImages.get(i).getImageid());
                }
            }
        } catch (Exception e) {
            logError(TAG,e.getMessage());
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        logInfo("Started image upload sync service");
        return super.onStartCommand(intent,flags,startId);
    }
}