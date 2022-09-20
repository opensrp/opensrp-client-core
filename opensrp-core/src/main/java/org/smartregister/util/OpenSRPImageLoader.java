package org.smartregister.util;

import android.app.Service;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;

import org.smartregister.AllConstants;
import org.smartregister.CoreLibrary;
import org.smartregister.account.AccountAuthenticatorXml;
import org.smartregister.account.AccountHelper;
import org.smartregister.domain.ProfileImage;
import org.smartregister.repository.ImageRepository;
import org.smartregister.view.activity.DrishtiApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.UUID;

import timber.log.Timber;

/**
 * A class that wraps up remote image loading requests using the Glide library.
 */
public class OpenSRPImageLoader {
    private final Resources mResources;
    private ArrayList<Drawable> mPlaceHolderDrawables;
    private boolean mFadeInImage = true;
    private int mMaxImageHeight = 0;
    private int mMaxImageWidth = 0;
    private WeakReference<Context> contextWeakReference;

    /**
     * Creates an ImageLoader with Bitmap memory cache. No default placeholder image will be shown
     * while the image is being fetched and loaded.
     */
    public OpenSRPImageLoader(FragmentActivity activity) {
        contextWeakReference = new WeakReference<>(activity);
        mResources = activity.getResources();
    }

    /**
     * Creates an ImageLoader with Bitmap memory cache. Default placeholder image passed as second parameter will be shown
     * while the image is being fetched and loaded.
     */
    public OpenSRPImageLoader(Service service, int defaultPlaceHolderResId) {
        contextWeakReference = new WeakReference<>(service.getApplicationContext());
        mResources = service.getResources();

        mPlaceHolderDrawables = new ArrayList<>(1);
        mPlaceHolderDrawables.add(defaultPlaceHolderResId == -1 ? null
                : mResources.getDrawable(defaultPlaceHolderResId));
    }

    public OpenSRPImageLoader(Context context, int defaultPlaceHolderResId) {
        contextWeakReference = new WeakReference<>(context);
        mResources = DrishtiApplication.getInstance().getResources();

        mPlaceHolderDrawables = new ArrayList<>(1);
        mPlaceHolderDrawables.add(defaultPlaceHolderResId == -1 ? null
                : mResources.getDrawable(defaultPlaceHolderResId));
    }

    /**
     * Creates an ImageLoader with Bitmap memory cache and a default placeholder image while the
     * image is being fetched and loaded.
     */
    public OpenSRPImageLoader(FragmentActivity activity, int defaultPlaceHolderResId) {
        this(activity);

        mPlaceHolderDrawables = new ArrayList<>(1);
        mPlaceHolderDrawables.add(defaultPlaceHolderResId == -1 ? null
                : mResources.getDrawable(defaultPlaceHolderResId));
    }

    /**
     * Creates an ImageLoader with Bitmap memory cache and a list of default placeholder drawables.
     */
    public OpenSRPImageLoader(FragmentActivity activity, ArrayList<Drawable> placeHolderDrawables) {
        this(activity);
        mPlaceHolderDrawables = placeHolderDrawables;
    }

    /**
     * Custom implementation of ImageListener which encapsulates basic functionality of showing a
     * default image until the network response is received, at which point it will switch to
     * either the actual image or the error image. Additional functionality also includes saving
     * the received image to disk for future use.
     *
     * @param defaultImageResId Default image resource ID to use, or 0 if it doesn't exist.
     * @param errorImageResId   Error image resource ID to use, or 0 if it doesn't exist.
     */
    public static OpenSRPImageListener getStaticImageListener(ImageView view, int defaultImageResId, int errorImageResId) {
        return new OpenSRPImageListener(view, defaultImageResId, errorImageResId);
    }

    private static CompressFormat getCompressFormat(String absoluteFileName) {
        String[] parts = absoluteFileName.split("\\.");
        String imgExtension = parts[parts.length - 1];
        if (imgExtension.equalsIgnoreCase("png")) {
            return CompressFormat.PNG;
        } else if (imgExtension.equalsIgnoreCase("JPG") || imgExtension.equalsIgnoreCase("JPEG")) {
            return CompressFormat.JPEG;
        } else {
            return null;
        }
    }

    /**
     * Save image to the local storage.If an image is downloaded from the server it's compressed to
     * jpeg format and the entityid becomes the file name
     *
     * @param entityId
     * @param image
     */

    public static void saveStaticImageToDisk(String entityId, Bitmap image) {
        if (image != null) {
            OutputStream os = null;
            try {

                if (entityId != null && !entityId.isEmpty()) {
                    final String absoluteFileName =
                            DrishtiApplication.getAppDir() + File.separator + entityId + ".JPEG";

                    File outputFile = new File(absoluteFileName);
                    os = new FileOutputStream(outputFile);
                    CompressFormat compressFormat = OpenSRPImageLoader
                            .getCompressFormat(absoluteFileName);
                    if (compressFormat != null) {
                        image.compress(compressFormat, 100, os);
                    } else {
                        throw new IllegalArgumentException(
                                "Failed to save static image, could " + "not"
                                        + " retrieve image compression format from name "
                                        + absoluteFileName);
                    }
                    // insert into the db
                    ProfileImage profileImage = new ProfileImage();
                    profileImage.setImageid(UUID.randomUUID().toString());
                    profileImage.setEntityID(entityId);
                    profileImage.setFilepath(absoluteFileName);
                    profileImage.setFilecategory("profilepic");
                    profileImage.setSyncStatus(ImageRepository.TYPE_Synced);
                    ImageRepository imageRepo = CoreLibrary.getInstance().context().imageRepository();
                    imageRepo.add(profileImage);
                }

            } catch (FileNotFoundException e) {
                Timber.e("Failed to save static image to disk");
            } finally {
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e) {
                        Timber.e("Failed to close static images output stream after attempting"
                                + " to write image");
                    }
                }
            }
        }
    }

    /**
     * Save image to the local storage.If an image is downloaded from the server it's compressed to
     * jpeg format and the entityid becomes the file name
     *
     * @param entityId
     * @param imageFile
     */

    public static boolean moveSyncedImageAndSaveProfilePic(@NonNull String syncStatus, @NonNull String entityId, @NonNull File imageFile) {

        boolean successful = false;

        if (!entityId.isEmpty()) {
            final String absoluteFileName =
                    DrishtiApplication.getAppDir() + File.separator + entityId + ".JPEG";
            if (copyFile(imageFile, new File(absoluteFileName))) {

                // insert into the db
                ProfileImage profileImage = new ProfileImage();
                profileImage.setImageid(UUID.randomUUID().toString());
                profileImage.setEntityID(entityId);
                profileImage.setFilepath(absoluteFileName);
                profileImage.setFilecategory("profilepic");
                profileImage.setSyncStatus(syncStatus);
                ImageRepository imageRepo = CoreLibrary.getInstance().context().
                        imageRepository();
                imageRepo.add(profileImage);

                successful = true;
            } else {
                Timber.e("An exception occurred trying to save synced image for entity %s on abs file path %s", entityId, absoluteFileName);
            }
        }

        return successful;
    }

    public static boolean copyFile(File src, File dst) {
        FileChannel inChannel = null;
        FileChannel outChannel = null;

        boolean isSuccessful = true;
        try {
            inChannel = new FileInputStream(src).getChannel();
            outChannel = new FileOutputStream(dst).getChannel();
            inChannel.transferTo(0, inChannel.size(), outChannel);
        } catch (IOException ex) {
            Timber.e(ex, "An error occurred trying to copy file");
            return false;
        } finally {
            try {
                if (inChannel != null) {
                    inChannel.close();
                }

                if (outChannel != null) {
                    outChannel.close();
                }
            } catch (IOException e) {
                Timber.e(e);

                isSuccessful = false;
            }
        }

        return isSuccessful;
    }

    public OpenSRPImageLoader setFadeInImage(boolean fadeInImage) {
        mFadeInImage = fadeInImage;
        return this;
    }

    public OpenSRPImageLoader setMaxImageSize(int maxImageWidth, int maxImageHeight) {
        mMaxImageWidth = maxImageWidth;
        mMaxImageHeight = maxImageHeight;
        return this;
    }

    public OpenSRPImageLoader setMaxImageSize(int maxImageSize) {
        return setMaxImageSize(maxImageSize, maxImageSize);
    }

    /**
     * Retrieves a locally stored image using the id of the image from the images db table. If the
     * file is not present, this function will also attempt to retrieve it using url of the source
     * image. The assumption here is that this method will be used to fetch profile images whereby
     * the name of the file is equals to the client's base entity id.
     *
     * @param entityId - The id of the image to be retrieved
     */
    public void getImageByClientId(String entityId, OpenSRPImageListener opensrpImageListener) {
        try {
            final Context context = contextWeakReference.get();
            if (context != null) {
                if (CoreLibrary.getInstance().context().getAppProperties().isTrue(AllConstants.PROPERTY.DISABLE_PROFILE_IMAGES_FEATURE)
                        || (entityId == null || entityId.isEmpty())) {

                    Glide.with(context).load(opensrpImageListener.getDefaultImageResId()).into(opensrpImageListener.getImageView());

                    return;

                } else {

                    //To do in background

                    ImageRepository imageRepo = CoreLibrary.getInstance().context().imageRepository();
                    ProfileImage imageRecord = imageRepo.findByEntityId(entityId);

                    if (imageRecord != null) {

                        Glide.with(context).load(imageRecord.getFilepath())
                                .apply(new RequestOptions()
                                        .skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE)) //Images already on device so skip caching
                                .transition(DrawableTransitionOptions.withCrossFade(mFadeInImage ? AllConstants.IMAGE_ANIMATION_FADE_IN_TIME : 0))
                                .placeholder(opensrpImageListener.getDefaultImageResId())
                                .error(opensrpImageListener.getErrorImageResId())
                                .into(opensrpImageListener.getImageView());


                    } else {

                        String url = FileUtilities.getImageUrl(entityId);

                        AccountAuthenticatorXml authenticatorXml = CoreLibrary.getInstance().getAccountAuthenticatorXml();
                        String accessToken = AccountHelper.getCachedOAuthToken(CoreLibrary.getInstance().context().allSharedPreferences().fetchRegisteredANM(),
                                authenticatorXml.getAccountType(),
                                AccountHelper.TOKEN_TYPE.PROVIDER);

                        GlideUrl glideUrl = new GlideUrl(url,
                                new LazyHeaders.Builder()
                                        .addHeader(AllConstants.HTTP_REQUEST_HEADERS.AUTHORIZATION, new StringBuilder(AllConstants.HTTP_REQUEST_AUTH_TOKEN_TYPE.BEARER + " ")
                                                .append(accessToken).toString()).build());

                        Glide.with(context).load(glideUrl)
                                .transition(DrawableTransitionOptions.withCrossFade(mFadeInImage ? AllConstants.IMAGE_ANIMATION_FADE_IN_TIME : 0))
                                .placeholder(opensrpImageListener.getDefaultImageResId())
                                .error(opensrpImageListener.getErrorImageResId())
                                .into(opensrpImageListener.getImageView());
                    }
                }
            }
        } catch (Exception e) {
            Timber.e(e.getMessage(), e);
        }
    }

    /**
     * Interface an activity can implement to provide an ImageLoader to its children fragments.
     */
    public interface ImageLoaderProvider {
        OpenSRPImageLoader getImageLoaderInstance();
    }

}
