package org.smartregister.service;

import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseUnitTest;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.ProfileImage;
import org.smartregister.domain.ResponseStatus;
import org.smartregister.repository.ImageRepository;

import java.util.ArrayList;

/**
 * Created by Vincent Karuri on 27/04/2021
 */
public class ImageUploadSyncServiceTest extends BaseUnitTest {

    @Test
    public void onHandleIntent() {
        // mock dependencies
        Context opensrpContext = CoreLibrary.getInstance().context();

        HTTPAgent httpAgent = Mockito.mock(HTTPAgent.class);
        Mockito.doReturn(ResponseStatus.success.displayValue()).when(httpAgent).httpImagePost(ArgumentMatchers.anyString(),
                ArgumentMatchers.any(ProfileImage.class));
        ReflectionHelpers.setField(opensrpContext, "httpAgent", httpAgent);

        final String IMAGE_ID_1 = "image_id_1";
        final String IMAGE_ID_2 = "image_id_2";
        ImageRepository imageRepository = Mockito.mock(ImageRepository.class);
        ReflectionHelpers.setField(opensrpContext, "imageRepository", imageRepository);
        Mockito.doReturn(new ArrayList<ProfileImage>() {{
            add(new ProfileImage(IMAGE_ID_1));
            add(new ProfileImage(IMAGE_ID_2));
        }}).when(imageRepository).findAllUnSynced();

        // verify uploads
        ImageUploadSyncService imageUploadSyncService = new ImageUploadSyncService();
        imageUploadSyncService.onHandleIntent(null);

        Mockito.verify(imageRepository).close(IMAGE_ID_1);
        Mockito.verify(imageRepository).close(IMAGE_ID_2);
    }
}