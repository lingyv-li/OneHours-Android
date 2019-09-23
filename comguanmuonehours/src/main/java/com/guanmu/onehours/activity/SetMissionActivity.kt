package com.guanmu.onehours.activity

import android.os.Bundle
import android.view.View
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.guanmu.onehours.R
import com.guanmu.onehours.SharePreferenceUtils

class SetMissionActivity : BaseActivity(), SeekBar.OnSeekBarChangeListener {

    private val minProgress = 20
    private val maxProgress = 200
    private var num: Int = 0
    private var et_num: TextView? = null
    private var seekbar: SeekBar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.set_mission_activity)
        seekbar = findViewById<View>(R.id.seekbar) as SeekBar
        assert(seekbar != null)
        seekbar!!.max = maxProgress - minProgress
        seekbar!!.setOnSeekBarChangeListener(this)
        num = SharePreferenceUtils.instance.targetCount
        seekbar!!.progress = num - minProgress

        et_num = findViewById<View>(R.id.tv_num) as TextView
        et_num!!.text = num.toString()

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
    }


    override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
        num = progress + minProgress
        if (et_num != null)
            et_num!!.text = num.toString()
    }

    override fun onStartTrackingTouch(seekBar: SeekBar) {

    }

    override fun onStopTrackingTouch(seekBar: SeekBar) {}


    override fun onDestroy() {
        SharePreferenceUtils.instance.targetCount = num
        Toast.makeText(this, R.string.tip_setting_saved, Toast.LENGTH_SHORT).show()
        finish()
        super.onDestroy()
    }


}
