package com.example.translator.utils.translation

import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL

object TranslationApi {


    var selectedLanguage: String? = null


    fun executeTranslation(
        inputText: String,
        outputCode: String,
        inputCode: String? = null
    ): String {
//        return if (type == "auto")
//            callUrlAndParseResult(
//                inputText,
//                outputCode
//            )
//        else callUrlAndParseResult(inputText, outputCode, inputCode!!)

        return inputCode?.let {
            callUrlAndParseResult(inputText, outputCode, inputCode)
        } ?: kotlin.run {
            callUrlAndParseResult(
                inputText,
                outputCode
            )
        }
    }

    private fun clearString(word: String): String {
        var text: String
        if (word.contains("&") || word.contains("\n")) {
            text = word.trim { it <= ' ' }.replace("&", "^~^")
            text = text.trim { it <= ' ' }.replace("%", "!^")
            text = text.trim { it <= ' ' }.replace("\n", "~~")
            text = text.trim { it <= ' ' }.replace("-", "")
            text = text.trim { it <= ' ' }.replace("#", "")
        } else {
            text = word
        }
        return text
    }

    private fun apiCall(url: String, isAuto: Boolean): String {

        val response: StringBuffer
        val obj: URL
        try {
            obj = URL(url)

            val con = obj.openConnection() as HttpURLConnection

            con.apply {
                connectTimeout = 1000

                setRequestProperty("User-Agent", "Mozilla/5.0")
                val `in` =
                    BufferedReader(InputStreamReader(con.inputStream))
                var inputLine: String?
                response = StringBuffer()
                while (`in`.readLine().also { inputLine = it } != null) {
                    response.append(inputLine)
                }
                `in`.close()
            }

        } catch (e: MalformedURLException) {
            e.printStackTrace()
            return ""
        } catch (e: IOException) {
            e.printStackTrace()
            return ""
        }
        val outputString: String = parseResult(response.toString(), isAuto)
        return if (outputString.isEmpty()) "Fail to Translate" else outputString
    }

    private fun callUrlAndParseResult(
        word: String,
        outputLanguageCode: String
    ): String {
        val text = clearString(word)

        val url: String = "https://translate.googleapis.com/translate_a/single?client=gtx&" +
                "sl=auto&" +
                "tl=" + outputLanguageCode +
                "&dt=t&q=" + text.trim { it <= ' ' }.replace(" ", "%20") + "&ie=UTF-8&oe=UTF-8"

        Log.e("TranslationUrl__", url)

        return apiCall(url, true)
    }

    private fun callUrlAndParseResult(
        word: String,
        outputLanguageCode: String,
        inputLanguageCode: String
    ): String {
        val text = clearString(word)
        val url: String = "https://translate.googleapis.com/translate_a/single?client=gtx&" +
                "sl=" + inputLanguageCode + "&" +
                "tl=" + outputLanguageCode +
                "&dt=t&q=" + text.trim { it <= ' ' }.replace(" ", "%20") + "&ie=UTF-8&oe=UTF-8"
        Log.d("uri", url)
        return apiCall(url, false)
    }

    private fun parseResult(
        inputJson: String,
        isAuto: Boolean
    ): String {
//        Log.e("result", inputJson)
        val tempData = StringBuilder()
        var data = ""
        try {
            val jsonArray = JSONArray(inputJson)
            val jsonArray2 = jsonArray[0] as JSONArray
            if (isAuto) {
                val language = jsonArray[jsonArray.length() - 1] as JSONArray
                val lang = language[0] as JSONArray
                selectedLanguage = lang[0].toString()
            }
//            Log.d("Language_", language)
            for (i in 0 until jsonArray2.length()) {
                val jsonArray3 = jsonArray2[i] as JSONArray
                tempData.append(jsonArray3[0].toString())
            }
            data = tempData.toString()
        } catch (e: JSONException) {
//            Log.e("error_", e.toString())
        } catch (e: Exception) {
            // Log.e("error_s", e.toString())
            /*  (context as Activity).runOnUiThread {
                  Toast.makeText(
                      context,
                      "Something went wrong", Toast.LENGTH_SHORT
                  ).show()
              }*/
        }
        data = data.replace("~~ ", "\n")
        data = data.replace("~ ~ ", "\n")
        data = data.replace("~ ~", "\n")
        data = data.replace("~~", "\n")
        data = data.replace(" !^ ", "%")
        data = data.replace(" ! ^ ", "%")
        data = data.replace("! ^", "%")
        data = data.replace(" ^ ~ ^ ", "&")
        data = data.replace("^ ~ ^", "&")
        data = data.replace(" ^~^ ", "&")
        data = data.replace("^~^", "&")
        return data
    }


}