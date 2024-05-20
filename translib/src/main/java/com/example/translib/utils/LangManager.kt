package com.example.translib.utils

import android.content.Context
import com.example.translib.models.LanguagesModel
import com.google.gson.Gson

object LangManager {

    private var languages: List<LanguagesModel>? = null


    fun getLanguagesName(context: Context) =
        getLanguages(context)?.map {
            it.name
        }

    fun getLanguagesCode(context: Context) =
        getLanguages(context)?.map {
            it.code
        }

    fun getLanguages(context: Context) =
        languages ?: fetchLanguages(context)

    private fun fetchLanguages(context: Context): List<LanguagesModel>? {

        val languages: String = try {
            context.assets.open("languages.json").bufferedReader()
                .use { it.readText() }
        } catch (e: Exception) {
            null
        } ?: return null

        try {

            return Gson().fromJson(languages, Array<LanguagesModel>::class.java).toList()
                .sortedBy { it.name }

//            return Moshi.Builder().run {
//
//                add(KotlinJsonAdapterFactory()).build() // building moshi
//                    .adapter<List<LanguagesModel>>( // adding adapter
//                        Types.newParameterizedType( // setting type
//                            List::class.java,
//                            LanguagesModel::class.java
//                        )
//                    )
//                    .fromJson(languages).also {
//                        this@LangManager.languages = it
//                    }  // returning list of model
//            }
        } catch (ignored: java.lang.Exception) {
        }

        return null
    }

}