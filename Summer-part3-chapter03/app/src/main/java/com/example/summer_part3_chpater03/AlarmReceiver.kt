package com.example.summer_part3_chpater03

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

/**
 * 알람에 대한 처리
 * BroadcastReceiver 반환값을 가진다.
 */
class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val NOTIFICATION_ID = 100
        const val NOTIFICATION_CHANNEL_ID = "1000"
    }

    /**
     * 브로드캐스트를 받았을 때,
     * createNotificationChannel 을 통해서 Notification 채널을 만들어준다.
     * notifyNotification 을 통해서 알람을 보여준다.
     */
    override fun onReceive(context: Context, intent: Intent?) {
        createNotificationChannel(context)
        notifyNotification(context)
    }

    /**
     * 알람 채널 설정
     */
    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "기상 알람",
                NotificationManager.IMPORTANCE_HIGH
            )
            NotificationManagerCompat.from(context).createNotificationChannel(notificationChannel)
        }
    }

    /**
     * 알람을 보여준다.
     * NotificationManagerCompat 을 통해서 알람을 설정한다.
     */
    private fun notifyNotification(context: Context) {
        with(NotificationManagerCompat.from(context)) {
            val build = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("알람")
                .setContentText("일어날 시간입니다.")
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
            notify(NOTIFICATION_ID, build.build())
        }
    }
}
