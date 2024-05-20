package com.example.translator.windows

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import com.example.translib.utils.exfuns.dpToPixel
import kotlin.math.abs
import kotlin.math.roundToInt

class FloatingWindowTouch(
    val context: Context,
    private val floatWindowLayoutParam: WindowManager.LayoutParams,
    private val touch: (Float, Float, Int) -> Unit,  // boolean for checking for click or moving
    private val downAndHold: ((Float, Float) -> Unit)? = null
) :
    View.OnTouchListener {


    companion object {
        const val ACTION_CLICK = 101
    }

    private val movementThreshold = 2f.dpToPixel(context)

    private var currentY = 0f
    private var currentX = 0f

    private var timeStart: Long = 0
    private var timeEnd: Long = 0

    private var windowXaxis = 0
    private var windowYaxis = 0

    private var touchStartCordX = 0.0f
    private var touchStartCordY = 0.0f

    private var touchDownHoldCordY = 0f
    private var touchDownHoldCordX = 0f


    var offset = 0

    private val handler by lazy {
        Handler(Looper.myLooper()!!)
    }
    private val runnable by lazy {
        Runnable {
            Log.d("handler", "x $touchStartCordX y $touchStartCordY ")

            downAndHold?.invoke(
                currentX - (offset * 1.5).roundToInt(),  /// for handling touch according to window position
                currentY - (offset * 0.5).roundToInt()
            )

            touchDownHoldCordX = currentX
            touchDownHoldCordY = currentY
        }
    }


    override fun onTouch(v: View?, event: MotionEvent?): Boolean {


        when (event!!.action) {
            MotionEvent.ACTION_DOWN -> {

//                floatWindowLayoutUpdateParam.gravity = Gravity.CENTER_VERTICAL
//                windowManager.updateViewLayout(binding.root, floatWindowLayoutUpdateParam)

                timeStart = System.currentTimeMillis()

                windowXaxis = floatWindowLayoutParam.x - offset
                windowYaxis = floatWindowLayoutParam.y - offset

                // returns the original raw X and raw Y
                // coordinate of this event
                touchStartCordX = event.rawX - offset
                touchStartCordY = event.rawY - offset

                touchDownHoldCordX = event.rawX - offset
                touchDownHoldCordY = event.rawY - offset

                touch.invoke(currentX, currentY, MotionEvent.ACTION_DOWN)

            }
            MotionEvent.ACTION_MOVE -> {

                currentX = event.rawX - offset
                currentY = event.rawY - offset

                floatWindowLayoutParam.x =
                    (windowXaxis + currentX - touchStartCordX).toInt()
                floatWindowLayoutParam.y =
                    (windowYaxis + currentY - touchStartCordY).toInt()


                // handling for when action down and move then stop but still down
                downAndHold()

                touch.invoke(currentX, currentY, MotionEvent.ACTION_MOVE)

                Log.d(
                    "move",
                    "x ${event.rawX - offset} y ${event.rawY - offset} }"
                )
                Log.d(
                    "windowParams",
                    "x ${floatWindowLayoutParam.x} y ${floatWindowLayoutParam.y} }"
                )
            }

            MotionEvent.ACTION_UP -> {
                touch.invoke(event.rawX - offset, event.rawY - offset, MotionEvent.ACTION_UP)

                if (isClickDetected())
                    touch.invoke(event.rawX - offset, event.rawY - offset, ACTION_CLICK)
            }
        }

        return true
    }

    private fun downAndHold() {
        downAndHold?.let {
            handler.apply {
                removeCallbacks(runnable)
//                if (isValidMovement(touchDownHoldCordX, touchDownHoldCordY)) {
                    Log.d("diff_valid_detected", "true")

                    postDelayed(runnable, 10)
//                }
            }
        }
    }

    private fun isClickDetected(): Boolean {

        if (!isValidMovement(touchStartCordY, touchStartCordY)) {
            timeEnd = System.currentTimeMillis()

            //Also check the difference between start time and end time should be less than 300ms
            if ((timeEnd - timeStart) < 500)
                return true
        }
        return false
    }

     private fun isValidMovement(x: Float, y: Float): Boolean {
        val xDiff: Int = (currentX - x).toInt()
        val yDiff: Int = (currentY - y).toInt()

        Log.d("diff_click", "x $xDiff y $yDiff ")

        return abs(xDiff) > movementThreshold && abs(yDiff) > movementThreshold
    }

}