package org.smartregister.util;

import android.graphics.Bitmap;

import org.smartregister.CoreLibrary;
import org.smartregister.domain.Photo;
import org.smartregister.domain.ProfileImage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

/**
 * Created by ndegwamartin on 12/04/2018.
 */

public class ImageUtils {

    public static Photo profilePhotoByClientID(String clientEntityId, int defaultProfileImage) {
        Photo photo = new Photo();
        ProfileImage profileImage = CoreLibrary.getInstance().context().imageRepository().findByEntityId(clientEntityId);
        if (profileImage != null) {
            photo.setFilePath(profileImage.getFilepath());
        } else {
            photo.setResourceId(defaultProfileImage);
        }
        return photo;
    }


    public static void saveImageAndCloseOutputStream(Bitmap image, File outputFile) throws FileNotFoundException {
        FileOutputStream os = new FileOutputStream(outputFile);
        Bitmap.CompressFormat compressFormat = Bitmap.CompressFormat.JPEG;
        image.compress(compressFormat, 100, os);
    }
}
