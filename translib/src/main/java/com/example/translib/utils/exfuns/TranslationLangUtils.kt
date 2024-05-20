package com.example.translib.utils.exfuns

import android.content.Context
import com.example.translib.models.LanguagesModel
import com.example.translib.utils.Constants
import com.google.gson.Gson

fun Context.getSaveInputLang(): LanguagesModel {
    val savedInputLang = getMyPrefs().getString(Constants.S_TRANSLATOR_INPUT, "")

    return if (savedInputLang?.isEmpty() == true) LanguagesModel(
        "en-US",
        "English",
        "English"
    ) else Gson().fromJson(savedInputLang, LanguagesModel::class.java)
}


fun Context.getSaveOutputLang(): LanguagesModel {

    val savedOutputLang = getMyPrefs().getString(Constants.S_TRANSLATOR_OUT, "")

    return if (savedOutputLang?.isEmpty() == true) LanguagesModel(
        "fr-FR",
        "French",
        "Francais"
    ) else
        Gson().fromJson(savedOutputLang, LanguagesModel::class.java)
}