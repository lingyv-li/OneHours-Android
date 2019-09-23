package com.guanmu.onehours

import android.app.Application
import android.database.sqlite.SQLiteDatabase

class MyApplication : Application() {
    lateinit var dao: DaoSession

    override fun onCreate() {
        super.onCreate()
        //初始化数据库
        SharePreferenceUtils.init(applicationContext)

        val helper = DaoMaster.DevOpenHelper(applicationContext, "knowledge-db", null)
        val db = helper.writableDatabase
        val daoMaster = DaoMaster(db)
        dao = daoMaster.newSession()
    }
}