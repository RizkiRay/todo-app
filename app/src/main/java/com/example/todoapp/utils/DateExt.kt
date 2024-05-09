package com.example.todoapp.utils

import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

fun Date.toISODate(): String {
    return SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ", Locale.getDefault()).format(this)
}

fun Date.toHumanDate(): String {
    return SimpleDateFormat("dd MMMM yyyy HH:mm", Locale.getDefault()).format(this)
}

fun String.toDate(format: String = "yyyy-MM-dd HH:mm:ss.SSSZ"): Date? {
    val formatter = SimpleDateFormat(format, Locale.getDefault())
    return formatter.parse(this)
}