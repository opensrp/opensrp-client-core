package org.smartregister.sync.wm.worker

import android.content.Context
import androidx.work.WorkerParameters
import org.smartregister.AllConstants
import org.smartregister.CoreLibrary
import org.smartregister.domain.ProfileImage
import org.smartregister.domain.ResponseStatus
import org.smartregister.util.WorkerNotificationDelegate
import timber.log.Timber

class ImageUploadWorkerWorker(context: Context, workerParams: WorkerParameters) :
    BaseWorker(context, workerParams) {

    override fun getTitle(): String  = "Doing Image Upload"

    override fun doWork(): Result {
        beforeWork()

        notificationDelegate.notify("Running \u8086")

        val imageRepo = CoreLibrary.getInstance().context().imageRepository()
        return try {
            val profileImages: List<ProfileImage> = imageRepo.findAllUnSynced()
            for (i in profileImages.indices) {
                val response = CoreLibrary.getInstance().context().httpAgent.httpImagePost(
                    getImageUploadEndpoint(), profileImages[i]
                )
                if (response.contains(ResponseStatus.success.displayValue())) {
                    imageRepo.close(profileImages[i].imageid)
                } else {
                    Timber.e(
                        "Image Upload: could NOT upload image ID: %s %s %s ",
                        profileImages[i].imageid,
                        " PATH: ",
                        profileImages[i].filepath
                    )
                }
            }
            Result.success().apply {
                notificationDelegate.notify("Success!!")
            }
        } catch (e: Exception) {
            Timber.e(e)
            Result.failure().apply {
                notificationDelegate.notify("Error: ${e.message}")
            }
        }
    }

    private fun getImageUploadEndpoint(): String {
        return (CoreLibrary.getInstance().context().configuration()
            .dristhiBaseURL() + AllConstants.PROFILE_IMAGES_UPLOAD_PATH)
    }

    companion object {
        const val TAG = "ImageUploadWorkerWorker"
    }
}