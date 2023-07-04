package com.example.translib.utils

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import androidx.core.app.ComponentActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.VISIBILITY_SECRET
import androidx.core.app.TaskStackBuilder
import com.example.translib.R
import com.example.translib.TranslationUtilBuilder
import com.example.translib.utils.exfuns.isAndroidOreo
import com.example.translib.utils.exfuns.isTranslatorOn
import com.example.translib.utils.exfuns.makeSingleTop


object NotificationHelper {

    var notificationAction: (NotificationCompat.Builder.() -> Unit)? = null

    lateinit var parentStackClass: Class<*>
    lateinit var clickedStackClass: Class<*>

    private var pendingIntent: PendingIntent? = null
    private const val NOTIFICATION_ID = 1234

    private lateinit var notificationManager: NotificationManager
    private val isManagerInitialized get() = this::notificationManager.isInitialized

    private fun createNotification(context: Activity) {
        cancelNotification()



        pendingIntent =
            Intent(context, clickedStackClass)
                .makeSingleTop().run {
                    if (parentStackClass == clickedStackClass)
                        makePendingIntent(context)
                    else
                        makeTaskStackBuilder(context)
                }


//        val pendingIntent = PendingIntent.getActivity(
//            context,
//            0, , 0
//        )

        notificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager


        val mBuilder = NotificationCompat.Builder(
            context,
            context.getString(R.string.translator_notification_channel_id)
        )
            .setSmallIcon(TranslationUtilBuilder.notificationIconId)
            .setAutoCancel(false)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setContentTitle(context.getString(R.string.notification_header))
            .setContentText(context.getString(R.string.notification_body))
            .setVisibility(VISIBILITY_SECRET)
            .setPriority(NotificationCompat.PRIORITY_MIN)
            .apply {
                notificationAction?.let { it() }
            }

        createChannel(context)

        notificationManager.notify(NOTIFICATION_ID, mBuilder.build())
    }

    private fun createChannel(context: Context) {
        if (isAndroidOreo()) {

            val notificationChannel = NotificationChannel(
                context.getString(R.string.translator_notification_channel_id),
                context.getString(R.string.notification_channel_name),
                NotificationManager.IMPORTANCE_MIN
            )

            notificationManager.createNotificationChannel(notificationChannel)

        }
    }


    fun cancelNotification() {
        if (isManagerInitialized)
            notificationManager.cancelAll()
    }

    fun ComponentActivity.showOrHideNotification() {
//        pendingIntent?.let {
//            if () {
//
//            }
//        } ?: kotlin.run {
//
//        }

        if (isTranslatorOn(Constants.MAIN_BTN_ON)) {
            if (pendingIntent == null)
                createNotification(this)
        } else {
            cancelNotification()
            pendingIntent = null
        }
    }

    fun Intent.makeTaskStackBuilder(context: Context): PendingIntent? {
        val pendingIntentFLag = getFlag()
        return TaskStackBuilder.create(context).run {
            addParentStack(parentStackClass)
            addNextIntent(Intent(context, parentStackClass).makeSingleTop())
            addNextIntent(this@makeTaskStackBuilder)
            getPendingIntent(0, pendingIntentFLag)
        }

    }

    private fun getFlag(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.FLAG_IMMUTABLE
        } else {
            PendingIntent.FLAG_UPDATE_CURRENT
        }
    }


    fun Intent.makePendingIntent(
        context: Context
    ): PendingIntent? {

        return PendingIntent.getActivity(context, 0, this, getFlag())
    }

    fun Intent.makeBroadcastPendingIntent(
        context: Context
    ): PendingIntent? {

        return PendingIntent.getBroadcast(context, 0, this, getFlag())
    }


}