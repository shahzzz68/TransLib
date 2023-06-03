package com.example.translib.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter

class EventBroadcast(private val onAction: (String) -> Unit) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_CLOSE_SYSTEM_DIALOGS,
            Intent.ACTION_SCREEN_OFF -> intent.action?.let { onAction.invoke(it) }
        }
    }

    fun register(context: Context){

        context.registerReceiver(this, IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS))
        context.registerReceiver(this, IntentFilter(Intent.ACTION_SCREEN_OFF))
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