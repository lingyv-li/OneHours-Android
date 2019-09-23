package com.guanmu.onehours

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * Created by bartl on 10/24/2015.
 *
 * Utils of date & time(for mission property)
 */
object DateUtils {
    val today: Date?
        get() {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            try {
                return sdf.parse(sdf.format(Calendar.getInstance().time))
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            return Calendar.getInstance().time
        }
}
