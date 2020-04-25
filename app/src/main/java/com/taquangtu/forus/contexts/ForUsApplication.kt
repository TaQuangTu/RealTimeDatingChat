package com.taquangtu.forus.contexts

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class ForUsApplication : Application() {

    companion object {
        val CHANNEL_ID = "CHANNEL_ID"
        val CHANNEL_NAME_MESSAGE = "CHANNEL_NAME_MESSAGE"
        lateinit var context: Context
    }

    override fun onCreate() {
        context = applicationContext
        createNotiChannel()
        super.onCreate()
    }

    private fun createNotiChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name =
                CHANNEL_NAME_MESSAGE
            val description = "Channel for listening incoming messages"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name,importance)
            channel.description = description
            channel.vibrationPattern = longArrayOf(100, 400, 400, 400)
            val notiManager = getSystemService(NotificationManager::class.java)
            notiManager.createNotificationChannel(channel)
        }
    }
}