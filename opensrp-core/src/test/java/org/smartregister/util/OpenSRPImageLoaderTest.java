package org.smartregister.util;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

import android.app.Service;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

import androidx.test.core.app.ApplicationProvider;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.robolectric.Robolectric;
import org.robolectric.android.controller.ActivityController;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.BaseUnitTest;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.ProfileImage;
import org.smartregister.domain.SyncStatus;
import org.smartregister.repository.ImageRepository;
import org.smartregister.util.mock.OpenSRPImageLoaderTestActivity;
import org.smartregister.view.activity.DrishtiApplication;

import java.io.File;
import java.io.IOException;

/**
 * Created by kaderchowdhury on 14/11/17.
 */
public class OpenSRPImageLoaderTest extends BaseUnitTest {

    private OpenSRPImageLoaderTestActivity activity;

    @Mock
    private Context context;

    @Mock
    private CoreLibrary coreLibrary;

    private ActivityController<OpenSRPImageLoaderTestActivity> controller;

    private File testFile;

    @Before
    public void setUp() throws Exception {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), OpenSRPImageLoaderTestActivity.class);
        controller = Robolectric.buildActivity(OpenSRPImageLoaderTestActivity.class, intent);
        activity = controller.get();
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", coreLibrary);
        doReturn(context).when(coreLibrary).context();
        controller.setup();
    }

    @Test
    public void assertActivityNotNull() {
        Assert.assertNotNull(activity);
    }

    @Test
    public void assertConstructorInitializationNotNull() throws Exception {
        OpenSRPImageLoader openSRPImageLoader = new OpenSRPImageLoader(activity.getInstance());
        Assert.assertNotNull(openSRPImageLoader);
    }

    @Test
    public void assertFragmentActivityConstructorInitializationNotNull() throws Exception {
        OpenSRPImageLoader openSRPImageLoader = new OpenSRPImageLoader(activity, -1);
        Assert.assertNotNull(openSRPImageLoader);
    }

    @Test
    public void testGetCompressFormatShouldReturnProperCompressFormat() {
        String filePath = "/User/images/file.jpg";
        Bitmap.CompressFormat format = ReflectionHelpers.callStaticMethod(OpenSRPImageLoader.class, "getCompressFormat",
                ReflectionHelpers.ClassParameter.from(String.class, filePath));
        Assert.assertEquals(Bitmap.CompressFormat.JPEG, format);

        filePath =  "/User/images/file.png";
        format = ReflectionHelpers.callStaticMethod(OpenSRPImageLoader.class, "getCompressFormat",
                ReflectionHelpers.ClassParameter.from(String.class, filePath));
        Assert.assertEquals(Bitmap.CompressFormat.PNG, format);

    }

    @Test
    public void testMoveSyncedImageAndSaveProfilePicReturnsSuccessful() throws IOException {
        String path = DrishtiApplication.getAppDir() + File.separator + "123-123.JPEG";
        testFile = new File(path);
        testFile.createNewFile();

        CoreLibrary library = Mockito.mock(CoreLibrary.class);
        ReflectionHelpers.setField(CoreLibrary.getInstance(), "instance", library);
        Context context = Mockito.mock(Context.class);
        doReturn(context).when(library).context();
        ImageRepository imageRepository = Mockito.mock(ImageRepository.class);
        doNothing().when(imageRepository).add(any());
        doReturn(imageRepository).when(context).imageRepository();


        boolean success = OpenSRPImageLoader.moveSyncedImageAndSaveProfilePic(SyncStatus.SYNCED.value(), "123-123", testFile);
        Assert.assertTrue(success);
    }

    @Test
    public void testSaveStaticImageToDisk() throws IOException {
        Bitmap bitmap = Mockito.mock(Bitmap.class);
        doReturn(true).when(bitmap).compress(any(), anyInt(), any());

        CoreLibrary library = Mockito.mock(CoreLibrary.class);
        ReflectionHelpers.setField(CoreLibrary.getInstance(), "instance", library);
        Context context = Mockito.mock(Context.class);
        doReturn(context).when(library).context();
        ImageRepository imageRepository = Mockito.mock(ImageRepository.class);
        doNothing().when(imageRepository).add(any());
        doReturn(imageRepository).when(context).imageRepository();

        OpenSRPImageLoader.saveStaticImageToDisk( "123-123", bitmap);
        Mockito.verify(imageRepository).add(any(ProfileImage.class));
    }

    @Test
    public void testSetMaxImageSizeShouldSetCorrectImageSize() {
        OpenSRPImageLoader openSRPImageLoader = new OpenSRPImageLoader(activity.getInstance());
        openSRPImageLoader.setMaxImageSize(200);
        int width = ReflectionHelpers.getField(openSRPImageLoader, "mMaxImageWidth");
        int height = ReflectionHelpers.getField(openSRPImageLoader, "mMaxImageHeight");
        Assert.assertEquals(200, width);
        Assert.assertEquals(200, height);
    }

    @Test
    public void testConstructorWithServiceInitializedCorrectly() {
        Service service = Mockito.mock(Service.class);
        android.content.Context context = Mockito.mock(android.content.Context.class);
        Resources resources = Mockito.mock(Resources.class);
        doReturn(context).when(service).getApplicationContext();
        doReturn(resources).when(service).getResources();
        doReturn(Mockito.mock(Drawable.class)).when(resources).getDrawable(eq(0));
        OpenSRPImageLoader openSRPImageLoader = new OpenSRPImageLoader(service, 0);
        Assert.assertNotNull(ReflectionHelpers.getField(openSRPImageLoader, "contextWeakReference"));
        Assert.assertNotNull(ReflectionHelpers.getField(openSRPImageLoader, "mPlaceHolderDrawables"));
    }

    @After
    public void destroy() {
        ReflectionHelpers.setField(CoreLibrary.getInstance().context(), "imageRepository", null);
        if (testFile != null && testFile.exists())
            testFile.delete();
    }
}
