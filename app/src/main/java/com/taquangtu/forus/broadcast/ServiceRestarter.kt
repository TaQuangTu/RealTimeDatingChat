package com.taquangtu.forus.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.taquangtu.forus.services.ListenMessageService


class ServiceRestarter : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        Toast.makeText(context, "Service is restart to listen to incoming messages", LENGTH_SHORT)
            .show()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(Intent(context, ListenMessageService::class.java))
        } else {
            context.startService(Intent(context, ListenMessageService::class.java))
        }
    }
}