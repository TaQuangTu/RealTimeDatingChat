package com.taquangtu.forus.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.IBinder
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.DEFAULT_ALL
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.taquangtu.forus.activities.MainActivity
import com.taquangtu.forus.broadcast.ServiceRestarter
import com.taquangtu.forus.contexts.AppContext
import com.taquangtu.forus.contexts.ForUsApplication
import com.taquangtu.forus.models.Message


class ListenMessageService : Service() {
    private var mHasDestroyed = false
    private val mDatabaseRef =
        FirebaseDatabase.getInstance().reference.child("chats").child(AppContext.roomId)
            .limitToLast(1)
    private val mListener = object : ChildEventListener {
        override fun onCancelled(p0: DatabaseError) {
        }

        override fun onChildMoved(p0: DataSnapshot, p1: String?) {
        }

        override fun onChildChanged(p0: DataSnapshot, p1: String?) {
        }

        override fun onChildAdded(p0: DataSnapshot, p1: String?) {
            val mess = p0.getValue(Message::class.java)
            if (mess!!.userId != AppContext.userId && !AppContext.appIsVisible) {
                System.out.println("testtttt service noti")
                noti(mess?.content ?: "new messages")
            }
        }

        override fun onChildRemoved(p0: DataSnapshot) {
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        mDatabaseRef.addChildEventListener(mListener)
        mHasDestroyed = false
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            startMyOwnForeground()
        else
            startForeground(1, Notification())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun startMyOwnForeground() {
        val NOTIFICATION_CHANNEL_ID = "example.permanence"
        val channelName = "Background Service"
        val chan = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_NONE
        )
        chan.setSound(null, null)
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        chan.enableVibration(false)
        val manager =
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(chan)
        val notificationBuilder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        val notification = notificationBuilder.setOngoing(true)
            .setContentTitle("ForUs app is running in background")
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .setSound(null)
            .build()
        startForeground(2, notification)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    private fun startListening() {
        mDatabaseRef.removeEventListener(mListener)
        val intent = Intent()
        intent.action = "restartservice"
        System.out.println("testtttt onTaskRemoved start service")
        intent.setClass(this, ServiceRestarter::class.java)
        this.sendBroadcast(intent);
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        if (!mHasDestroyed) {
            startListening()
            mHasDestroyed = true
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (!mHasDestroyed) {
            startListening()
            mHasDestroyed = true
        }
    }

    fun noti(content: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        val pendingIntent = PendingIntent.getActivity(this, 0, intent, 0)
        val builder =
            NotificationCompat.Builder(ForUsApplication.context, ForUsApplication.CHANNEL_ID)
                .setSmallIcon(com.taquangtu.forus.R.drawable.ic_launcher_foreground)
                .setContentTitle("New message")
                .setContentText(content).setVibrate(longArrayOf(1000, 1000, 1000, 1000, 1000))
                .setLights(Color.RED, 3000, 3000)
                .setContentIntent(pendingIntent)
                .setVibrate(longArrayOf(100, 400, 400, 400))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setDefaults(DEFAULT_ALL)
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(content)
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(111, builder.build())
        }
    }
}