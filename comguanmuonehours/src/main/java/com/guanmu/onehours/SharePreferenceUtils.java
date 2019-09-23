package com.guanmu.onehours;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Date;

/**
 * 用于保存一些基础的用户设置
 *
 * @author zengzyong 2015.7.24
 */
public class SharePreferenceUtils {

    private static final String SHARENAME = "info";
    private static final String PREF_TARGET_COUNT = "target_cont";
    private static final String FIRST_INTRO = "first_intro";
    private static final String FIRST_OPEN = "first_open";
    private static final String FIRST_EDIT = "first_edit";
    private static final String FIRST_RECITE = "first_recite";
    private static final String FIRST_KNOW = "first_know";
    private static final String LAST_UPDATE = "last_update";


    private static SharePreferenceUtils Instance = null;

    private static Context mContext;
    private SharedPreferences preferences;

    private SharePreferenceUtils() {
        preferences = mContext.getSharedPreferences(SHARENAME,
                Context.MODE_PRIVATE);
    }

    public static void init(Context context) {
        mContext = context;
    }

    public static SharePreferenceUtils getInstance() {
        if (null == Instance) {
            synchronized (SharePreferenceUtils.class) {
                if (null == Instance) {
                    Instance = new SharePreferenceUtils();
                }
            }
        }
        return Instance;
    }

    /**
     * 保存每次最大目标数
     *
     * @param count 每次最大目标数
     * @return 是否成功保存
     */
    public boolean setTargetCount(int count) {
        return preferences.edit().putInt(PREF_TARGET_COUNT, count).commit();
    }

    /**
     * 获取每次最大目标数
     *
     * @return 目标
     */
    public int getTargetCount() {
        return preferences.getInt(PREF_TARGET_COUNT, 20);
    }


    public boolean setLastUpdate() {
        return preferences.edit().putLong(LAST_UPDATE, new Date().getTime()).commit();
    }

    public Date getLastUpdate() {
        return new Date(preferences.getLong(LAST_UPDATE, 1));
    }


    public boolean getFirstIntro() {
        if (preferences.getBoolean(FIRST_INTRO, true)) {
            preferences.edit().putBoolean(FIRST_INTRO, false).apply();
            return true;
        } else {
            return false;
        }
    }


    public boolean getFirstOpen() {
        if (preferences.getBoolean(FIRST_OPEN, true)) {
            preferences.edit().putBoolean(FIRST_OPEN, false).apply();
            return true;
        } else {
            return false;
        }
    }

    public boolean getFirstEdit() {
        if (preferences.getBoolean(FIRST_EDIT, true)) {
            preferences.edit().putBoolean(FIRST_EDIT, false).apply();
            return true;
        } else {
            return false;
        }
    }

    public boolean getFirstRecite() {
        if (preferences.getBoolean(FIRST_RECITE, true)) {
            preferences.edit().putBoolean(FIRST_RECITE, false).apply();
            return true;
        } else {
            return false;
        }
    }

    public boolean getFirstKnow() {
        if (preferences.getBoolean(FIRST_KNOW, true)) {
            preferences.edit().putBoolean(FIRST_KNOW, false).apply();
            return true;
        } else {
            return false;
        }
    }

}
