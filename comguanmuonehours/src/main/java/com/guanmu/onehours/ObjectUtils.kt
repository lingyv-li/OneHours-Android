package com.guanmu.onehours

/**
 * Created by Larry.
 */

object ObjectUtils {
    @SafeVarargs
    fun <T> coalesce(vararg params: T): T? {
        return params.find { it != null }
    }
}
