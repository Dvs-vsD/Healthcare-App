package com.app.consultationpoint.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.app.consultationpoint.R
import timber.log.Timber

class RemainderBroadcast : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (context != null) {
            val channelId = intent?.getIntExtra("channel_id",100)
            val time = intent?.getStringExtra("time")
            val notification = NotificationCompat.Builder(context, channelId.toString())
                .setSmallIcon(R.drawable.doctor)
                .setLargeIcon(BitmapFactory.decodeResource(context.resources,R.drawable.appointment))
                .setContentTitle("Be ready!!!")
                .setContentText("You have upcoming appointment at $time")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

            val nManager = NotificationManagerCompat.from(context)
            nManager.notify(channelId?:0, notification.build())
        }
    }

}