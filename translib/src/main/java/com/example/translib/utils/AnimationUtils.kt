package com.example.translib.utils

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AccelerateInterpolator
import androidx.core.animation.addListener

fun View.colorAnimator(vararg colorInt: Int, onEnd: (() -> Unit)? = null) {
    ObjectAnimator.ofArgb(
        this, "backgroundColor",
        *colorInt
    ).apply {

        interpolator = AccelerateInterpolator()
        duration = 200
        repeatCount = 3
        repeatMode = ValueAnimator.REVERSE

        addListener(onEnd = {
//            layout.selectAllChilds(false)
            onEnd?.invoke()
        })

        start()
    }
}


fun View.animate(alpha: Float = 1f, delay: Long = 0) {
    Handler(Looper.myLooper()!!).postDelayed({
        animate().alpha(alpha).setDuration(500).start()
    }, delay)
}