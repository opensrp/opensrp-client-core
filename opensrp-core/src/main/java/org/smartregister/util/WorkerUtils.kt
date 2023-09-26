package org.smartregister.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import org.smartregister.R
import kotlin.random.Random


object WorkerUtils {
    private const val CHANNEL_ID = "org.smartregisterx"
    private const val CHANNEL_NAME = "OpenSRP"
    private const val CHANNEL_DESC = "OpenSRP Client"

    fun makeStatusNotification(
        context: Context,
        notificationId: Int,
        title: String?,
        message: String?
    ) {
        val notificationManager = NotificationManagerCompat.from(context)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            channel.description = CHANNEL_DESC
            notificationManager.createNotificationChannel(channel)
        }
        val builder: NotificationCompat.Builder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_opensrp_logo)
            .setPriority(NotificationCompat.PRIORITY_LOW)
        notificationManager.notify(notificationId, builder.build())
    }

    fun dismissNotification(context: Context, notificationId: Int){
        NotificationManagerCompat.from(context).cancel(notificationId)
    }
}

class WorkerNotificationDelegate(private val context: Context, private val title: String?) {

    private val notificationId: Int = 100

    fun notify(message: String) {
        WorkerUtils.makeStatusNotification(context, notificationId, title, message)
    }

    fun dismiss() {
        WorkerUtils.dismissNotification(context, notificationId)
    }
}