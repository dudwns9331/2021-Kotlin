package com.example.summer_part3_chapter01

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


/**
 * https://firebase.google.com/docs/reference/fcm/rest/v1/projects.messages/send?apix=true
 * 다음 링크의 API 를 호출해주는 서비스를 통해서 구현한다.
 * 호출되는 메시지는 onMessageReceived 에서 처리하도록 한다.
 *
 * 안드로이드 8.0 이상에서는 알림 채널을 만들어야 한다.
 * 채널은 메시지를 발송하기 전에 만들어져야 한다.
 *
 */

class MyFirebaseMessagingService : FirebaseMessagingService() {

    /* 토큰이 항상 바뀔 수 있기 때문에 토큰이 갱신될 때마다 서버에 갱신하는 작업을 한다.*/
    override fun onNewToken(p0: String) {
        super.onNewToken(p0)
    }

    /* 메세지를 수신할 때마다 동작한다. 메세지에 대한 처리를 한다.*/
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        createNotificationChannel()

        val type = remoteMessage.data["type"]
            ?.let { NotificationType.valueOf(it) }
        val title = remoteMessage.data["title"]
        val message = remoteMessage.data["message"]

        type ?: return

        NotificationManagerCompat.from(this)
            .notify(type.id, createNotification(type, title, message))

    }


    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            channel.description = CHANNEL_DESCRIPTION
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)

        }

    }

    private fun createNotification(
        type: NotificationType,
        title: String?,
        message: String?
    ): Notification {

        val intent = Intent(this, MainActivity::class.java)
            .apply {
                putExtra("notificationType", "${type.title} 타입")
                addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }

        val pendingIntent = PendingIntent.getActivity(this, type.id, intent, FLAG_UPDATE_CURRENT)


        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_baseline_notifications_24)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        when (type) {
            NotificationType.NORMAL -> Unit
            NotificationType.EXPANDABLE -> {
                notificationBuilder.setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(
                            "😀😁😂🤣😃😄😅😆" +
                                    "😉😊😋😎😍😘🥰😗" +
                                    "😙😚☺🙂🤗🤩🤔🤨" +
                                    "😐😑😶🙄😏😣😥😮"
                        )
                )
            }
            NotificationType.CUSTOM -> {
                notificationBuilder
                    .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                    .setCustomContentView(
                        RemoteViews(
                            packageName,
                            R.layout.view_custom_notification
                        ).apply {
                            setTextViewText(R.id.title, title)
                            setTextViewText(R.id.message, message)
                        }
                    )
            }
        }

        return notificationBuilder.build()
    }

    companion object {
        private const val CHANNEL_NAME = "Emoji Party"
        private const val CHANNEL_DESCRIPTION = "Emoji Party 를 위한 채널"
        private const val CHANNEL_ID = "Channel Id"
    }


}