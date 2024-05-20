package com.example.translib

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.translib.utils.Constants
import com.example.translib.utils.NotificationHelper.makeBroadcastPendingIntent
import com.example.translib.utils.NotificationHelper.showOrHideNotification
import com.example.translib.utils.exfuns.getApplicationIcon
import com.example.translib.utils.exfuns.onOffTranslator
import com.example.translib.utils.exfuns.showToast
import com.example.translib.utils.onPermissionResult
import com.example.translib.utils.requestNotification

class MainActivity : AppCompatActivity() {

    private val requestPermissionLauncher =
        onPermissionResult {
            showOrHideNotification()
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        onOffTranslator(Constants.MAIN_BTN_ON, true)
        onOffTranslator(Constants.WA_TRANSLATOR_ON, true)
        onOffTranslator(Constants.WA_4B_TRANSLATOR_ON, true)
        onOffTranslator(Constants.INSTA_TRANSLATOR_ON, true)


        val textView = findViewById<TextView>(R.id.textView)
        findViewById<TextView>(R.id.button).setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }

        findViewById<ImageView>(R.id.imageView).apply {
            setImageDrawable(getApplicationIcon())

            setOnClickListener {
                requestNotification(requestPermissionLauncher)

                textView.text = "click"
            }
        }


        TranslationUtilBuilder
            .setNotificationParentClass(this::class.java)
            .setNotificationClickedStackClass(this::class.java)
            .setNotificationIcon(R.drawable.ic_launcher_background)
            .setNotificationAction {
                val pendingIntent = Intent(
                    this@MainActivity,
                    NotificationReceiver::class.java
                ).apply {
                    putExtra("a", "1")
                }.makeBroadcastPendingIntent(this@MainActivity)
                addAction(R.drawable.ic_launcher_foreground, "Translator", pendingIntent)
            }


    }


    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        intent?.extras
    }


    class NotificationReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {

            context?.showToast(intent?.getStringExtra("a").toString())
        }
    }
}