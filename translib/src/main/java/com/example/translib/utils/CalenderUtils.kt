package com.example.translib.utils

import java.util.*

fun isNextDay(time: Long): Boolean {
    val c1: Calendar = Calendar.getInstance() // today
    c1.add(Calendar.DAY_OF_YEAR, -1) // yesterday
    val c2: Calendar = Calendar.getInstance()
    c2.timeInMillis = time
    return (c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
            && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR))
}