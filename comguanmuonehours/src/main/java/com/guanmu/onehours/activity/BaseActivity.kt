package com.guanmu.onehours.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import com.guanmu.onehours.DaoSession
import com.guanmu.onehours.MyApplication

/**
 * 自定义Activity的基类。
 *
 * @author zhangweiqiang
 */
abstract class BaseActivity : AppCompatActivity() {
    lateinit var dao: DaoSession

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dao = (application as MyApplication).dao
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
    }

}
