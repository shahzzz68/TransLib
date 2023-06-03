package com.example.translib.windows

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import androidx.annotation.DrawableRes
import com.example.translib.utils.exfuns.dpToPixel
import com.example.translator.windows.FloatingWindowTouch
import com.example.translib.R
import com.example.translib.TranslationUtilBuilder
import com.example.translib.databinding.LayoutTranslatorSearchBinding
import com.example.translib.utils.animate

class FloatingSearchWidget(
    val context: Context,
    private val translateWindowEventListener: TranslateWindowEventListener
) : FloatingWindowBase(context) {

    var touchListener: FloatingWindowTouch? = null

    private val translatorSearchWidgetBinding by lazy {
        LayoutTranslatorSearchBinding.inflate(LayoutInflater.from(context))
    }

    fun createOrHideWidget(isCreate: Boolean) {
        if (isCreate)
            createFloatingWidget()
        else
            translatorSearchWidgetBinding.root.removeViewFromParent()
    }

    @SuppressLint("ClickableViewAccessibility")
    fun createFloatingWidget() {

        setupTouchListener()
        initializeWindowBasicParams(false)
        translatorSearchWidgetBinding.root.addViewtoParentSimply()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupTouchListener() {

        touchListener = FloatingWindowTouch(
            context,
            floatingWindowParams,
            { _, _, action ->

                when (action) {

                    MotionEvent.ACTION_DOWN -> {
                        translatorSearchWidgetBinding.translatorSearch.animate(1f)
                        toggleIcon(TranslationUtilBuilder.searchIconId)
                    }

                    MotionEvent.ACTION_MOVE -> updateViewLayout()

                    MotionEvent.ACTION_UP -> {
//                        if (!touchListener!!.isClickDetected()) {
//                            resetPosition()
//                        }
                        resetPosition()

                        toggleIcon(TranslationUtilBuilder.searchHandleId)
                        translatorSearchWidgetBinding.translatorSearch.animate(0.6f, 1500)
                    }
                }
            },
            { x, y -> /// when action is down , move and stop but still down

                translateWindowEventListener.touchCords(x.toInt(), y.toInt())

                val newX = x - 2f.dpToPixel(context)
                val newY = y - 2f.dpToPixel(context)
//                translateWindowEventListener.touchCords(newX.toInt(), newY.toInt())


                Log.d("downhold", "x $x y $y ")

            })


        /**
         * setting offset to touch lister to move cords to top left little bit for clear visibility
         */
        touchListener!!.offset = 18f.dpToPixel(context)

        translatorSearchWidgetBinding.root.setOnTouchListener(touchListener)
    }

    private fun toggleIcon(@DrawableRes res: Int) {
        translatorSearchWidgetBinding.translatorSearch.setImageResource(
            res
        )
    }

    private fun resetPosition() {
        windowPosition(floatingWindowParams, false)
        updateViewLayout()
    }

    private fun updateViewLayout() {
        translatorSearchWidgetBinding.root.updateViewLayout()
    }

    interface TranslateWindowEventListener {
        fun touchCords(x: Int, y: Int)
    }
}