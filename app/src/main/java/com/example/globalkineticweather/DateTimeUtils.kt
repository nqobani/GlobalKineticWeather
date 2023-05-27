package com.example.globalkineticweather

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Locale

class DateTimeUtils {
    companion object {
        fun getWeekDay(dateTimeString: String): String {
            val input = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val output = SimpleDateFormat("EEE", Locale.getDefault())
            return try {
                val date = input.parse(dateTimeString)
                val weekdayName: String? = date?.let { output.format(it) }
                weekdayName ?: dateTimeString
            } catch (e: ParseException) {
                dateTimeString
            }
        }

        fun getHour(dateTimeString: String): String {
            val input = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val output = SimpleDateFormat("HH", Locale.getDefault())
            return try {
                val date = input.parse(dateTimeString)
                val weekdayName: String? = date?.let { output.format(it) }
                weekdayName ?: dateTimeString
            } catch (e: ParseException) {
                dateTimeString
            }
        }
    }
}