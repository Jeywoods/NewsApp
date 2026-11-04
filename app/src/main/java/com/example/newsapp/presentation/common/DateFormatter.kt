
package com.example.newsapp.presentation.common

import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

fun formatNewsDate(value: String): String {
    return try {
        val inputFormat = SimpleDateFormat(
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            Locale.getDefault()
        ).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        val outputFormat = SimpleDateFormat(
            "dd.MM.yyyy HH:mm",
            Locale.getDefault()
        ).apply {
            timeZone = TimeZone.getDefault()
        }

        val date = inputFormat.parse(value)

        if (date != null) {
            outputFormat.format(date)
        } else {
            value
        }
    } catch (_: Exception) {
        value
    }
}
