package com.example.translib.utils

import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.IO


object CoroutineUtils {
    fun <T> executeIO(backgroundOperation: suspend (() -> T?), result: ((T?) -> Unit)) =
        CoroutineScope(Dispatchers.Main).launch {
            val data = CoroutineScope(IO).async rt@{
                return@rt backgroundOperation()
            }.await()
            result(data)
        }
}