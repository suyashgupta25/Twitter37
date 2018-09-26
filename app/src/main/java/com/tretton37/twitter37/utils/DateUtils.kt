package com.tretton37.twitter37.utils

import com.tretton37.twitter37.utils.AppConstants.Companion.ZERO
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object DateUtils {

    private const val SECOND_MILLIS = 1000
    private const val MINUTE_MILLIS = 60 * SECOND_MILLIS
    private const val HOUR_MILLIS = 60 * MINUTE_MILLIS
    private const val DAY_MILLIS = 24 * HOUR_MILLIS
    private const val dateFormat = "EEE MMM dd HH:mm:ss Z yyyy"

    fun formatDateString(dateValue: String): String {
        var diff = ZERO.toLong()
        try {
            val date = SimpleDateFormat(dateFormat, Locale.getDefault()).parse(dateValue)
            val now = Date()
            diff = now.time - date.time
        } catch (e: ParseException) {
            System.err.print(e)
        }

        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            val div = diff / MINUTE_MILLIS
            return div.toString() + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            val div = diff / HOUR_MILLIS
            return div.toString() + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            val div = diff / DAY_MILLIS
            return div.toString() + " days ago";
        }
    }


}