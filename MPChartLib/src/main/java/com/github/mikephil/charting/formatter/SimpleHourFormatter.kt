package com.github.mikephil.charting.formatter

import android.util.Log
import com.github.mikephil.charting.utils.pp

/**
 * Very basic class to format hour values.
 *
 * The input value is assumed to be the number of hours since midnight, e.g.
 *       1.0 -> " 1:00AM"
 *      11.5 -> "11:30AM"
 *      15.25 -> " 3:15PM"
 *
 * Numbers greater than 24 are rolled over, e.g.
 *      27.3 % 24 -> 3.3 -> " 3:20AM"
 * Numbers smaller than zero return "00:00AM"
 */
private const val LOG_TAG = "SimpleHourFormatter"
private val logEnabled = false
class SimpleHourFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        if (value < 0) return "00:00AM"
        val v = value % 24f
        val hour = Math.floor(v.toDouble()).toFloat()
        val minutes = ((v - hour) * 60).toInt()

        val h = (if (hour < 13f) hour else hour - 12).toInt()
        val amPm = if (hour < 12f) "AM" else "PM"
        val result = "${String.format("%2d", h)}:${String.format("%02d", minutes)}$amPm"
        if (logEnabled) Log.v(LOG_TAG, "${value.pp(2)} -> $result")
        return result
    }

    /**
     * Converts a float representing time since midnight to
     * number of msec since midnight.
     */
    fun toMSec(f: Float): Long = (f * 3600000f).toLong()


    /**
     * Converts a long representing msec since midnight to
     * a float representing hours since midnight.
     */
    fun toHour(l: Long?): Float {
        l ?: return -1f
        val sec = l % 86400000 / 1000 // drop any additional days
        val minutes = sec / 60
        val hour = minutes / 60
        val min = (minutes - (hour * 60)).toFloat() / 60f
        return hour.toFloat() + min
    }

    fun toHour(i: Int) = toHour(i.toLong())
}

