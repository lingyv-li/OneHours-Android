package com.guanmu.onehours

import android.text.TextUtils

import java.util.regex.Matcher
import java.util.regex.Pattern

object StringUtils {
    fun padRight(s: String, n: Int): String {
        return String.format("%1$-" + n + "s", s)
    }

    fun padLeft(s: String, n: Int): String {
        return String.format("%1$" + n + "s", s)
    }


    fun isValidPhone(phone: String): Boolean {
        if (TextUtils.isEmpty(phone))
            return false

        val p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$")
        val m = p.matcher(phone)
        return m.matches()
    }

    fun isValidPassword(password: String?): Boolean {
        return password != null && password.length > 6
    }

}
