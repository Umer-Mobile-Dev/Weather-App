package com.appozee.technologies.supertalassignment.utils

import android.content.Context
import android.widget.Toast
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Extension function to format a temperature value in Kelvin to a string representing Celsius.
 * @return The temperature value formatted as Celsius.
 */
fun Double?.toFormattedCelsiusString(): String {
    this?.let { temperature ->
        val celsiusTemperature = temperature - 273.15
        val integerPart = celsiusTemperature.toInt()
        return integerPart.toString()
    }
    return ""
}

/**
 * Extension function to convert a UNIX timestamp to a formatted date string.
 * @param format The format in which the date string should be formatted.
 * @return The formatted date string.
 */
fun Long.toFormattedDateString(format: String): String {
    val date = Date(this * 1000L)
    val sdf = SimpleDateFormat(format, Locale.getDefault())
    return sdf.format(date)
}

/**
 * Extension function to parse a time string in "hh:mm aa" format to "HHmm" format.
 * @return The parsed time string in "HHmm" format.
 */
fun String.parseToHHmm(): String {
    val sdfInput = SimpleDateFormat("hh:mm aa", Locale.getDefault())
    val sdfOutput = SimpleDateFormat("HHmm", Locale.getDefault())
    try {
        val date = sdfInput.parse(this)
        return sdfOutput.format(date)
    } catch (e: ParseException) {
        e.printStackTrace()
    }
    return ""
}

/**
 * Extension function to get the current time in "HHmm" format from a Calendar object.
 * @return The current time string in "HHmm" format.
 */
fun Calendar.getCurrentTimeHHmm(): String {
    val sdf = SimpleDateFormat("HHmm", Locale.getDefault())
    return sdf.format(time)
}

/**
 * Extension function to display a toast message.
 * @param message The message to be displayed in the toast.
 * @param duration The duration for which the toast should be displayed.
 */
fun Context.showToast(message: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, message, duration).show()
}