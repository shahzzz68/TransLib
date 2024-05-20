package com.example.translib.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build

class EventBroadcast(private val onAction: (String) -> Unit) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_CLOSE_SYSTEM_DIALOGS,
            Intent.ACTION_SCREEN_OFF -> intent.action?.let { onAction.invoke(it) }
        }
    }

    fun register(context: Context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(
                this, IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS),
                Context.RECEIVER_NOT_EXPORTED
            )
            context.registerReceiver(
                this,
                IntentFilter(Intent.ACTION_SCREEN_OFF),
                Context.RECEIVER_NOT_EXPORTED
            )
        } else {
            context.registerReceiver(this, IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
            context.registerReceiver(this, IntentFilter(Intent.ACTION_SCREEN_OFF))
        }

    }

    fun unRegister(context: Context) {
        try {
            context.unregisterReceiver(this)
        } catch (ignore: Exception) {
            // reciever may be null or not register
//            e.printStackTrace()
        }
    }
}