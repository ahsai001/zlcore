package com.zaitunlabs.zlcore.utils

import android.text.TextUtils

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Created by ahmad s on 3/8/2016.
 */

object DateStringUtils {
    fun getDateTimeInString(dateFormat: String, date: Date, locale: Locale?): String {
        val cal = Calendar.getInstance()
        cal.time = date
        val sdf = SimpleDateFormat(dateFormat, locale ?: Locale.getDefault())
        return sdf.format(date)
    }

    fun getDateTimeInString(date: Date?, locale: Locale?): String {
        if (date == null) return "no record"
        val cal = Calendar.getInstance()
        val today = cal.time

        if (compareToDay(date, today, locale) == 0) {
            //today
            val sdf = SimpleDateFormat("h:mm a", locale ?: Locale.getDefault())
            return sdf.format(date)
        } else if (compareToYear(date, today, locale) == 0) {
            val sdf = SimpleDateFormat("MMM d h:mm a", locale ?: Locale.getDefault())
            return sdf.format(date)
        } else {
            //previuos year
            val sdf = SimpleDateFormat("MMM d yyyy", locale ?: Locale.getDefault())
            return sdf.format(date)
        }
    }

    fun getDurationInString(diffInSecond: Long): String {
        var seconds = diffInSecond

        val days = seconds / (60 * 60 * 24)
        val modulo1 = seconds % (60 * 60 * 24)
        val hours = modulo1 / (60 * 60)
        val modulo2 = modulo1 % (60 * 60)
        val minutes = modulo2 / 60
        val modulo3 = modulo2 % 60
        seconds = modulo3


        var result = ""
        if (days > 0) {
            result += days.toString() + " day" + if (days == 1L) "" else "s"
        }

        var hoursString = hours.toString()
        var minutesString = minutes.toString()
        var secondsString = seconds.toString()

        if (hoursString.length == 1) hoursString = "0$hoursString"
        if (minutesString.length == 1) minutesString = "0$minutesString"
        if (secondsString.length == 1) secondsString = "0$secondsString"

        result += (if (days > 0) " " else "") + hoursString + ":" + minutesString + ":" + secondsString
        return result
    }

    fun getDurationInString(startDate: Date, endDate: Date): String {
        val diff = endDate.time - startDate.time
        var seconds = diff / 1000

        val days = seconds / (60 * 60 * 24)
        val modulo1 = seconds % (60 * 60 * 24)
        val hours = modulo1 / (60 * 60)
        val modulo2 = modulo1 % (60 * 60)
        val minutes = modulo2 / 60
        val modulo3 = modulo2 % 60
        seconds = modulo3


        var result = ""
        if (days > 0) {
            result += days.toString() + " day" + if (days == 1L) "" else "s"
        }

        var hoursString = hours.toString()
        var minutesString = minutes.toString()
        var secondsString = seconds.toString()

        if (hoursString.length == 1) hoursString = "0$hoursString"
        if (minutesString.length == 1) minutesString = "0$minutesString"
        if (secondsString.length == 1) secondsString = "0$secondsString"

        result += (if (days > 0) " " else "") + hoursString + ":" + minutesString + ":" + secondsString
        return result
    }

    fun compareToDay(date1: Date?, date2: Date?, locale: Locale?): Int {
        if (date1 == null || date2 == null) {
            return 0
        }
        val sdf = SimpleDateFormat("yyyyMMdd", locale ?: Locale.getDefault())
        return sdf.format(date1).compareTo(sdf.format(date2))
    }

    fun compareToYear(date1: Date?, date2: Date?, locale: Locale?): Int {
        if (date1 == null || date2 == null) {
            return 0
        }
        val sdf = SimpleDateFormat("yyyy", locale ?: Locale.getDefault())
        return sdf.format(date1).compareTo(sdf.format(date2))
    }

    fun getMessageDateWithDefaultNow(messageDateInString: String, locale: Locale?): Date {
        var messageDate = Calendar.getInstance().time

        if (TextUtils.isEmpty(messageDateInString)) {
            return messageDate
        }

        val sf = SimpleDateFormat("yyyy-MM-dd HH:mm:ssZ", locale ?: Locale.getDefault())
        sf.isLenient = true
        try {
            messageDate = sf.parse(messageDateInString)
        } catch (e: ParseException) {
            //do nothing
            DebugUtils.logD("smart-x", "exception parse date")
        }

        return messageDate
    }


    fun getTimeRange(start: String, end: String): String {
        if (TextUtils.isEmpty(start) || TextUtils.isEmpty(end)) {
            return ""
        }

        var result = ""
        result += start.split(" ".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[1]
        result += " - "
        result += end.split(" ".toRegex()).dropLastWhile({ it.isEmpty() }).toTypedArray()[1]

        return result
    }

    fun getDateFromString(format: String, dateString: String, locale: Locale?): Date? {
        val sf = SimpleDateFormat(format, locale ?: Locale.getDefault())
        sf.isLenient = true
        var date: Date? = null
        try {
            date = sf.parse(dateString)
        } catch (e: ParseException) {
            //do nothing
            DebugUtils.logD("zlcore", "exception parse date")
        }

        return date
    }
}
