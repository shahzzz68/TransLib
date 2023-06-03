package com.example.translib.utils.exfuns

import android.content.Context
import android.content.res.ColorStateList
import android.util.DisplayMetrics
import android.view.*
import android.widget.*
import androidx.annotation.AttrRes
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import com.example.translator.utils.exfuns.isInternetConnected
import com.example.translib.R
import com.google.android.material.color.MaterialColors
import kotlin.math.roundToInt


fun EditText.ifEditTextNotBlank(
    errorMsg: String = context.getString(R.string.input_error),
    internetCheck: Boolean = true,
    action: (EditText) -> Unit
) {
    when {
        text.toString().isBlank() -> error = errorMsg
        internetCheck && !context.isInternetConnected() -> error =
            context.getString(R.string.internet_error_msg)

        else -> action.invoke(this)
    }
}

fun EditText.makeEditTextEmpty(hint: String = "") {
    setText("")
    if (hint.isNotBlank())
        setHint(hint)
}

fun View.changeBgColor(color: Int ) {
    background.mutate().setTint(context.getMyColor(color))
}

fun View.getMaterialColorRef(@AttrRes colorAttributeResId: Int) =
    MaterialColors.getColor(this, colorAttributeResId)

fun Context.getMyColor(color: Int) =
    ResourcesCompat.getColor(resources, color, theme)

fun Context.getMyDrawable(drawable: Int) =
    ResourcesCompat.getDrawable(resources, drawable, theme)

fun View.setBackgroudTint(color: Int) {
    ViewCompat.setBackgroundTintList(this, ColorStateList.valueOf(context.getMyColor(color)))
}

fun Float.dpToPixel(context: Context): Int {
    return (this * (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}


fun Float.pixelsToDp(context: Context): Int {
    return (this / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).roundToInt()
}

fun Context.getDisplayMetrics() =
    resources.displayMetrics

fun Context.getWidthPixels() =
    getDisplayMetrics().widthPixels

fun Context.getHeightPixels() =
    getDisplayMetrics().heightPixels

