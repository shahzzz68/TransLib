package com.example.translib.windows

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import androidx.core.view.isVisible
import com.example.translator.windows.FloatingWindowTouch
import com.example.translib.R
import com.example.translib.TranslationUtilBuilder
import com.example.translib.databinding.LayoutFloatingTranslationBinding
import com.example.translib.utils.Constants
import com.example.translib.utils.exfuns.editMyPrefs
import com.example.translib.utils.exfuns.getCompletedTranslations
import com.example.translib.utils.exfuns.getStatusBarHeight
import com.example.translib.utils.exfuns.isInternetConnected
import com.example.translib.utils.exfuns.runTranslation
import com.example.translib.utils.getSaveInputLang
import com.example.translib.utils.getSaveOutputLang


class FloatingTranslatorWindow(val context: Context) : FloatingWindowBase(context) {


    private var statusbarHeight: Int = context.resources.getStatusBarHeight()
    private var touchListener: FloatingWindowTouch? = null


    private val translatorWindowBinding: LayoutFloatingTranslationBinding by lazy {
        LayoutFloatingTranslationBinding.inflate(LayoutInflater.from(context))
    }

    var newDataPair: Pair<Rect, String>? = null

    private val handler by lazy {
        Handler(Looper.myLooper()!!)
    }

    val runnable = Runnable {
        translatorWindowBinding.apply {

            Log.d("translationInvoke", "yes")

            root.addViewtoParentSimply()

            translatedText.text = ""
//            translatedText.text = newDataPair?.second


            newDataPair?.first?.let {
                translationLayout.updateRect(it, statusbarHeight)
            }


            if (!context.isInternetConnected()) {
                translatedText.text = context.applicationContext.getString(R.string.no_internet)
                return@apply
            }

            /**
             * checking if daily screen translation limit reach then return
             */

            val isSubscribed = TranslationUtilBuilder.isSubscribed
            val todayCompleted = context.getCompletedTranslations()

            if (todayCompleted >= TranslationUtilBuilder.screenTranslationsLimit && !isSubscribed && !TranslationUtilBuilder.isUnlimitedScreenTranslations) {
                translatedText.text = context.getString(R.string.daily_limit_reached)
                return@apply
            }

            translatingProgress.isVisible = true

            newDataPair?.second?.runTranslation(
                context.getSaveOutputLang().code,
                context.getSaveInputLang().code
            ) {
                translatingProgress.isVisible = false

                translatedText.text = it
                Log.d("screenTranslate", it)

                /**
                 * saving the counter to prefs for daily limit
                 */
                if (!isSubscribed)
                    context.editMyPrefs {
                        putInt(Constants.S_TRANSLATOR_LIMIT, todayCompleted + 1)
                    }
            }
        }
    }

    init {

//        translatorWindowBinding.root.viewTreeObserver.addOnGlobalLayoutListener(object :
//            ViewTreeObserver.OnGlobalLayoutListener {
//            override fun onGlobalLayout() {
////                translatorWindowBinding.root.viewTreeObserver.removeOnGlobalLayoutListener(this);
//
//                val rect = Rect()
//                translatorWindowBinding.root.getWindowVisibleDisplayFrame(rect)
//                // This is the height of the status bar
//                statusbarHeight = rect.top
//            }
//
//        })

        initializeWindowBasicParams(isMatchParent = true)
        setupTouchListener()
    }

//    fun addInfoToWindow() {
//
//
//        nodeInfo?.let { node ->
//            node.textOrContent()?.let {
//
//                val rect = Rect()
//                node.getBoundsInScreen(rect)
//
//
//                showTranslatorWindow(rect, it) // show the translator window with updated values
//            } ?: context.showToast("No text found")
//        }
//    }


    var lastPair: Pair<Rect, String>? = null
    fun showTranslatorWindow(dataPair: Pair<Rect, String>) {

        newDataPair = dataPair
        if (lastPair == newDataPair)
            return

        lastPair = newDataPair

        handler.removeCallbacks(runnable)
        handler.postDelayed(runnable, 400)

    }


    @SuppressLint("ClickableViewAccessibility")
    private fun setupTouchListener() {

        touchListener = FloatingWindowTouch(
            context,
            floatingWindowParams,
            { _, _, action ->
                if (action == MotionEvent.ACTION_MOVE) {
                    lastPair = null
                    translatorWindowBinding.apply {
                        root.removeViewFromParent()
                    }
                }
            })
        translatorWindowBinding.root.setOnTouchListener(touchListener)
    }
}