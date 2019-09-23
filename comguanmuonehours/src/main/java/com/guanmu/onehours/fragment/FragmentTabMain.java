package com.guanmu.onehours.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.guanmu.onehours.R;
import com.guanmu.onehours.activity.EditKnowledgeActivity;
import com.guanmu.onehours.activity.SetMissionActivity;
import com.guanmu.onehours.activity.TestKnowledgeActivity;
import com.guanmu.onehours.model.MPoint;
import com.guanmu.onehours.model.MTask;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class FragmentTabMain extends BaseFragment implements View.OnClickListener {
    public TextView tv_all_count, tv_study_count, tv_list_left, tv_percent;
    public ProgressBar progress;

    @Override
    protected View createView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab_main, container, false);
        tv_all_count = (TextView) rootView.findViewById(com.guanmu.onehours.R.id.tv_all_count);
        tv_study_count = (TextView) rootView.findViewById(com.guanmu.onehours.R.id.tv_study_count);
        tv_list_left = (TextView) rootView.findViewById(com.guanmu.onehours.R.id.tv_list_left);
        progress = (ProgressBar) rootView.findViewById(com.guanmu.onehours.R.id.list_progress);
        tv_percent = (TextView) rootView.findViewById(com.guanmu.onehours.R.id.tv_percent);

        rootView.findViewById(R.id.button_my_mission).setOnClickListener(this);
        rootView.findViewById(R.id.button_add_knowledge).setOnClickListener(this);
        rootView.findViewById(R.id.button_start_recitation).setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        updatedData();
    }

    public void updatedData() {
        // 累计知识点数
        tv_all_count.setText(Html.fromHtml(String.format(getString(R.string.num_unit),
                String.valueOf(
                        MPoint.countAll(dao)
                ))));
        // 已学习(熟练度大于5)
        tv_study_count.setText(Html.fromHtml(String.format(getString(R.string.num_unit),
                String.valueOf(
                        MPoint.countProf(dao)
                ))));

        MTask task = MTask.getToday(dao, false);

        int missionSize = 0;
        int doneMissionsSize = 0;
        if (task != null) {
            missionSize = task.getCount();
            doneMissionsSize = missionSize - task.getPointList().size();
        }

        tv_list_left.setText(Html.fromHtml(String.format(getString(R.string.num_remain), missionSize - doneMissionsSize)));
        int percent = getPercent(missionSize, doneMissionsSize);
        tv_percent.setText(String.format(getString(R.string.num_percent), String.valueOf(percent)));

        progress.setProgress(percent);
    }

    public int getPercent(int total, int cursor) {
        int percent;

        if (total == 0) {
            percent = 0;
        } else {
            percent = (int) (cursor / (double) total * 100);
        }
        return percent;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            // add new points
            case com.guanmu.onehours.R.id.button_add_knowledge:
                i = new Intent();
                i.setClass(getActivity(), EditKnowledgeActivity.class);
                i.setFlags(EditKnowledgeActivity.REQUEST_ADD);
                startActivityForResult(i, EditKnowledgeActivity.REQUEST_ADD); // Request code 0 for add content
                break;
            // start recitation
            case com.guanmu.onehours.R.id.button_start_recitation:
                // Check mission list(on going recitation): when empty, load mission list, check again; otherwise, start recitation
                ExecutorService pool = Executors.newSingleThreadExecutor();
                pool.execute(new Runnable() {
                    @Override
                    public void run() {
                        Intent i = new Intent();
                        i.setClass(getActivity(), TestKnowledgeActivity.class);
                        startActivity(i);
                    }
                });
                break;
            // my mission
            case com.guanmu.onehours.R.id.button_my_mission:
                i = new Intent();
                i.setClass(getActivity(), SetMissionActivity.class);
                startActivity(i);
                break;
            default:
                break;
        }
    }

}
