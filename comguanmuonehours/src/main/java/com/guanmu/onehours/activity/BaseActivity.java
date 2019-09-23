package com.guanmu.onehours.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.guanmu.onehours.DaoSession;
import com.guanmu.onehours.MyApplication;

/**
 * 自定义Activity的基类。
 *
 * @author zhangweiqiang
 */
public class BaseActivity extends AppCompatActivity {
    public DaoSession dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dao = ((MyApplication) getApplication()).dao;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
