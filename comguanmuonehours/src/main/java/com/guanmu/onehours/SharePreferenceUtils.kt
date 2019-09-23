package com.guanmu.onehours

import android.content.Context
import android.content.SharedPreferences
import java.util.*

/**
 * 用于保存一些基础的用户设置
 *
 * @author zengzyong 2015.7.24
 */
class SharePreferenceUtils(mContext: Context) {
    private val preferences: SharedPreferences =
            mContext.getSharedPreferences(SHARENAME, Context.MODE_PRIVATE)

    /**
     * 获取每次最大目标数
     *
     * @return 目标
     */
    var targetCount: Int
        get() = preferences.getInt(PREF_TARGET_COUNT, 20)
        set(v) = preferences.edit().putInt(PREF_TARGET_COUNT, v).apply()

    val lastUpdate: Date
        get() = Date(preferences.getLong(LAST_UPDATE, 1))


    val firstIntro: Boolean
        get() {
            return if (preferences.getBoolean(FIRST_INTRO, true)) {
                preferences.edit().putBoolean(FIRST_INTRO, false).apply()
                true
            } else {
                false
            }
        }


    val firstOpen: Boolean
        get() {
            return if (preferences.getBoolean(FIRST_OPEN, true)) {
                preferences.edit().putBoolean(FIRST_OPEN, false).apply()
                true
            } else {
                false
            }
        }

    val firstEdit: Boolean
        get() {
            return if (preferences.getBoolean(FIRST_EDIT, true)) {
                preferences.edit().putBoolean(FIRST_EDIT, false).apply()
                true
            } else {
                false
            }
        }

    val firstRecite: Boolean
        get() {
            if (preferences.getBoolean(FIRST_RECITE, true)) {
                preferences.edit().putBoolean(FIRST_RECITE, false).apply()
                return true
            } else {
                return false
            }
        }

    val firstKnow: Boolean
        get() {
            if (preferences.getBoolean(FIRST_KNOW, true)) {
                preferences.edit().putBoolean(FIRST_KNOW, false).apply()
                return true
            } else {
                return false
            }
        }

    /**
     * 保存每次最大目标数
     *
     * @param count 每次最大目标数
     * @return 是否成功保存
     */
    fun setTargetCount(count: Int): Boolean {
        return preferences.edit().putInt(PREF_TARGET_COUNT, count).commit()
    }


    fun setLastUpdate(): Boolean {
        return preferences.edit().putLong(LAST_UPDATE, Date().time).commit()
    }

    companion object {

        private val SHARENAME = "info"
        private val PREF_TARGET_COUNT = "target_cont"
        private val FIRST_INTRO = "first_intro"
        private val FIRST_OPEN = "first_open"
        private val FIRST_EDIT = "first_edit"
        private val FIRST_RECITE = "first_recite"
        private val FIRST_KNOW = "first_know"
        private val LAST_UPDATE = "last_update"


        private lateinit var Instance: SharePreferenceUtils

        fun init(context: Context) {
            Instance = SharePreferenceUtils(context)
        }

        val instance: SharePreferenceUtils
            get() {
                return Instance
            }
    }
}
