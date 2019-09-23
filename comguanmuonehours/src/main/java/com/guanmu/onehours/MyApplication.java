package com.guanmu.onehours;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

public class MyApplication extends Application {
    public DaoSession dao;

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化数据库
        SharePreferenceUtils.init(getApplicationContext());

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(getApplicationContext(), "knowledge-db", null);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        dao = daoMaster.newSession();
    }
}