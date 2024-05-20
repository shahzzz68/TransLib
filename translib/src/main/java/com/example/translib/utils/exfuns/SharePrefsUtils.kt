package com.example.translib.utils.exfuns

import android.content.Context
import android.content.SharedPreferences
import com.example.translib.utils.Constants

var prefs: SharedPreferences? = null
fun Context.getMyPrefs(): SharedPreferences {
    return prefs ?: getSharedPreferences("libSharedPrefs", Context.MODE_PRIVATE).also {
        prefs = it
    }
}


fun Context.editMyPrefs(editor: SharedPreferences.Editor.() -> Unit) {
    getMyPrefs().edit().apply {
        editor(this)
        apply()
    }
}

fun SharedPreferences.getUserLang(name: String) =
    getInt(name, 15)



fun Context.onOffTranslator(id: String, onOrOff: Boolean) {
    editMyPrefs {
        putBoolean(id, onOrOff)
    }
}

fun Context.isTranslatorOn(id: String) =
    getMyPrefs().getBoolean(id, false)


fun Context.areTranslatorsOn(): Boolean {

    Constants.constIdsArray.forEach { id ->
        if (isTranslatorOn(id))
            return true
    }
    return false
}

fun Context.isAnyTranslatorOn() =
    areTranslatorsOn() && isTranslatorOn(Constants.MAIN_BTN_ON)


fun Context.isRewardClaimed() =
    getMyPrefs().getLong(Constants.REWARDED_CURRENT_MILLIS, 0) > System.currentTimeMillis()

fun Context.getCompletedTranslations() =
    getMyPrefs().getInt(Constants.S_TRANSLATOR_LIMIT, 0)

fun Context.isNextDay(): Boolean {
    val previousDayTime =
        getMyPrefs().getLong(
            Constants.APP_OPENING_TIME_MILLIS,
            System.currentTimeMillis() - Constants.TWENTY_FOUR_HOURS_TO_MILLIS // minus 24 hours, assuming this is next day for saving value first time
        )
    return com.example.translib.utils.isNextDay(previousDayTime)
}