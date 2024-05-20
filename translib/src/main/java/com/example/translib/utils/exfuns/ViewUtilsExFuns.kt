package com.example.translib.utils.exfuns

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.annotation.AttrRes
import androidx.annotation.ColorInt
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.setPadding
import com.example.translib.R
import com.example.translib.utils.LangManager
import com.google.android.material.color.MaterialColors
import kotlin.math.roundToInt


fun View.makeVisible() {
    visibility = View.VISIBLE
}

fun View.makeHide() {
    visibility = View.GONE
}

fun EditText.ifEditTextNotBlank(
    errorMsg: String = context.getString(R.string.input_error),
    internetCheck: Boolean = true,
    action: (EditText) -> Unit
) {
    when {
        text.toString().isBlank() -> {
            error = errorMsg
            return
        }

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

fun View.changeBgColor(color: Int) {
    background.mutate().setTint(context.getMyColor(color))
}

fun View.getMaterialColorRef(@AttrRes colorAttributeResId: Int) =
    MaterialColors.getColor(this, colorAttributeResId)


@ColorInt
fun Context.getThemeColor(@AttrRes attrResId: Int): Int {
    val a = obtainStyledAttributes(null, intArrayOf(attrResId))
    return try {
        a.getColor(0, Color.BLUE)
    } finally {
        a.recycle()
    }
}

fun Context.getMyColor(color: Int) =
    ResourcesCompat.getColor(resources, color, theme)

fun Context.getMyDrawable(drawable: Int) =
    ResourcesCompat.getDrawable(resources, drawable, theme)

fun View.setBackgroudTint(@ColorInt color: Int) {
    ViewCompat.setBackgroundTintList(this, ColorStateList.valueOf(color))
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


//// spinner setup
var spinnerAdapter: ArrayAdapter<*>? = null
fun Spinner.setUpSpinner(
    onSelected: (Int) -> Unit,
    @ColorInt colorInt: Int
) {


    setBackgroudTint(colorInt)
    spinnerAdapter ?: kotlin.run {

        object : ArrayAdapter<Any?>(
            context,
            android.R.layout.simple_list_item_1,
            LangManager.getLanguagesName(context)!!
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                return super.getView(position, convertView, parent).apply {
                    if (this is TextView)
                        this.apply {
//                            layoutParams.height = 32f.dpToPixel(context)
                            setTextColor(colorInt)
                            setPadding(0)
                        }
                }
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                return super.getDropDownView(position, convertView, parent).apply {
                    if (this is TextView)
                        this.apply {
                            layoutParams.height = 32f.dpToPixel(context)
                        }
                }
            }
        }.apply {

            setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
            adapter = this
            onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    onSelected.invoke(position)
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }
            }
        }
    }
}