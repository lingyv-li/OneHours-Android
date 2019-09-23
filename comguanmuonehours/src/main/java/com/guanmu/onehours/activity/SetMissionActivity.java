package com.guanmu.onehours.activity;

import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.guanmu.onehours.R;
import com.guanmu.onehours.SharePreferenceUtils;

public class SetMissionActivity extends BaseActivity implements SeekBar.OnSeekBarChangeListener {

    private final int minProgress = 20;
    private final int maxProgress = 200;
    private int num;
    private TextView et_num;
    private SeekBar seekbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_mission_activity);
        seekbar = (SeekBar) findViewById(R.id.seekbar);
        assert seekbar != null;
        seekbar.setMax(maxProgress - minProgress);
        seekbar.setOnSeekBarChangeListener(this);
        num = SharePreferenceUtils.getInstance().getTargetCount();
        seekbar.setProgress(num - minProgress);

        et_num = (TextView) findViewById(R.id.tv_num);
        et_num.setText(String.valueOf(num));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        assert toolbar != null;
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        num = progress + minProgress;
        if (et_num != null)
            et_num.setText(String.valueOf(num));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
    }


    @Override
    protected void onDestroy() {
        SharePreferenceUtils.getInstance().setTargetCount(num);
        Toast.makeText(this, R.string.tip_setting_saved, Toast.LENGTH_SHORT).show();
        finish();
        super.onDestroy();
    }


}
