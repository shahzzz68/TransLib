package com.example.translib.models

import com.google.gson.annotations.SerializedName

data class LanguagesModel(
    @SerializedName("code")
    val code: String = "",
    @SerializedName("name")
    val name: String = "",
    @SerializedName("nativeName")
    val nativeName: String = "",
    var isLetter: Boolean = false,
    var isSelected: Boolean = false
) : ISearch {

    override fun word(): Pair<String, String> {
        return Pair(name, nativeName)
    }
}


interface ISearch {
    fun word(): Pair<String, String>
}