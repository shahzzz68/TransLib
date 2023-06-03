package com.example.translib.windows

import android.content.Context
import android.graphics.PixelFormat
import android.view.KeyEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import com.example.translib.utils.exfuns.getHeightPixels
import com.example.translib.utils.exfuns.getWidthPixels
import com.example.translator.utils.exfuns.ifNull
import com.example.translator.utils.exfuns.isAndroidL_MR1
import kotlin.math.abs

open class FloatingWindowBase(private val context: Context) {


    private val windowManager: WindowManager by lazy {
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    }

    val floatingWindowParams: WindowManager.LayoutParams by lazy {
        WindowManager.LayoutParams().apply {
            //representing 0 as screen centre in case of window manager params
            x = 0
            y = 0
        }
    }


    fun initializeParent(keyEvent: ((KeyEvent) -> Unit)? = null) = object : LinearLayout(context) {
        override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
            event?.let { keyEvent?.invoke(it) }
            return super.dispatchKeyEvent(event)
        }
    }

    fun View.addViewtoParentSimply() {
//        parent ?: run {
//            windowManager.addView(this, floatingWindowParams)
//        }

        windowToken.ifNull {
            windowManager.addView(this, floatingWindowParams)
        }
    }

    fun ViewGroup.addViewToParent(view: View) {
        try {

            this.apply {
                windowToken.ifNull {
                    removeAllViews()
                    addView(
                        view, ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                    )
                    windowManager.addView(this, floatingWindowParams)
                }
            }

        } catch (ignored: Exception) {
        }
    }

    fun initializeWindowBasicParams(
        isPositionRight: Boolean = true,
        isMatchParent: Boolean = false
    ) {
        floatingWindowParams.apply {
            val size =
                if (isMatchParent) WindowManager.LayoutParams.MATCH_PARENT else WindowManager.LayoutParams.WRAP_CONTENT
            width = size
            height = size
            type = getLayoutType()
            flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
            format = PixelFormat.TRANSLUCENT


            if (!isMatchParent) {
                windowPosition(floatingWindowParams, isPositionRight)
            }
        }
    }

    fun windowPosition(params: WindowManager.LayoutParams, isPositionRight: Boolean) {
        /**
         * Note:
         * window position x or y =0 means center
         */


        // dividing by 2 to get half pixels for left or right because window default position is 0
        val widgetXPosition = ((context.getWidthPixels() - abs(params.width)) / 2)

        params.x = if (isPositionRight)
            widgetXPosition
        else
            -widgetXPosition

        val heightPixes = context.getHeightPixels()
        val widgetYPosition = (((heightPixes - abs(params.height)) / 2) * 0.15).toInt()

        params.y = -widgetYPosition
    }

    fun ViewGroup.toMatchParent() {
        floatingWindowParams.apply {
            width = WindowManager.LayoutParams.MATCH_PARENT
            height = WindowManager.LayoutParams.MATCH_PARENT
            x = 0
            y = 0
        }
        updateViewLayout()
    }

    fun ViewGroup.makeInputFocusable() {
        floatingWindowParams.flags =
            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
        updateViewLayout()

    }

    fun ViewGroup.makeNotFocusable() {
        floatingWindowParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        updateViewLayout()
    }

    private fun getLayoutType() =
        if (isAndroidL_MR1()) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
//            } else
//                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
            WindowManager.LayoutParams.TYPE_ACCESSIBILITY_OVERLAY
        } else {
            WindowManager.LayoutParams.TYPE_APPLICATION
        }


    fun View.updateViewLayout() {
        windowManager.updateViewLayout(this, floatingWindowParams)
    }

    fun View.removeViewFromParent() {
        this.windowToken?.let {
            windowManager.removeView(this)
        }

    }
}