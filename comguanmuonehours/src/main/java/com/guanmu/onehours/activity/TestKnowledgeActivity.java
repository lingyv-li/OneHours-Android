package com.guanmu.onehours.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.guanmu.onehours.R;
import com.guanmu.onehours.SharePreferenceUtils;
import com.guanmu.onehours.model.MPoint;
import com.guanmu.onehours.model.MTask;
import com.guanmu.onehours.view.TestImageView;
import com.guanmu.onehours.view.TestTextView;
import com.guanmu.onehours.view.Testable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class TestKnowledgeActivity extends BaseActivity implements OnClickListener, View.OnLongClickListener {
    private LinearLayout look_layout;
    private ViewGroup cardView;
    private Testable tester;
    private View btn_show, btn_not_know, btn_know, btn_skip;
    private LinearLayout select_layout;
    private MPoint currentItem;
    private TestTextView mTestTextView;
    private TestImageView mTestImageView;

    private MTask task;
    private List<MPoint> points;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        task = MTask.getToday(dao, true);
        if (task == null) {
            Toast.makeText(TestKnowledgeActivity.this, R.string.tip_main_load_fail, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setContentView(R.layout.activity_test_peotry);
        intView();

        points = task.getPointList();

        currentItem = points.get(0);
        initData(currentItem);
        // Show tutor at the first time
        if (SharePreferenceUtils.getInstance().getFirstRecite()) {
            showTutor(1);
        }

        long seed = System.nanoTime();
        Collections.shuffle(points, new Random(seed));
    }

    private void intView() {
        // Preload text view
        mTestTextView = new TestTextView(this);

        cardView = (ViewGroup) findViewById(R.id.card_view);
        look_layout = (LinearLayout) findViewById(R.id.look_layout);//查看全文
        btn_show = findViewById(R.id.btn_look);
        select_layout = (LinearLayout) findViewById(R.id.select_layout);
        btn_skip = findViewById(R.id.btn_skip);
        btn_not_know = findViewById(R.id.btn_unknow);
        btn_know = findViewById(R.id.btn_know);
        assert btn_show != null;
        assert btn_skip != null;
        assert btn_not_know != null;
        assert btn_know != null;
        btn_show.setOnLongClickListener(this);
        btn_show.setOnClickListener(this);
        btn_skip.setOnClickListener(this);
        btn_not_know.setOnClickListener(this);
        btn_know.setOnClickListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        assert toolbar != null;
        toolbar.setNavigationOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });
    }

    private void initData(MPoint item) {
        switch (item.getType()) {
            case normal:
                cardView.removeAllViews();
                if (mTestTextView == null) {
                    mTestTextView = new TestTextView(this);
                }
                cardView.addView(mTestTextView);
                tester = mTestTextView;
                break;
            case image:
                cardView.removeAllViews();
                if (mTestImageView == null) {
                    mTestImageView = new TestImageView(this);
                }
                cardView.addView(mTestImageView);
                tester = mTestImageView;
                break;
        }
        tester.setData(item);
        look_layout.setVisibility(View.VISIBLE);
        select_layout.setVisibility(View.GONE);
    }

//    private class TouchUpAsClick implements View.OnTouchListener {
//        public boolean onTouch (View v, MotionEvent event){
//            switch (event.getAction()) {
//                case MotionEvent.ACTION_UP:
//                    if (Arrays.asList(R.id.btn_know, R.id.btn_unknow, R.id.btn_skip).contains((v.getId()))) {
//                        v.performClick();
//                        v.setOnTouchListener(null);
//                        return true;
//                    }
//                    break;
//            }
//            return false;
//        }
//    }

    @Override
    public boolean onLongClick(View view) {
        switch (view.getId()) {
            case R.id.btn_look:
                look_layout.setVisibility(View.GONE);
                select_layout.setVisibility(View.VISIBLE);

//                TouchUpAsClick touchUpAsClick = new TouchUpAsClick();
//                btn_know.setOnTouchListener(touchUpAsClick);
//                btn_not_know.setOnTouchListener(touchUpAsClick);
//                btn_skip.setOnTouchListener(touchUpAsClick);

                tester.showAll();
                // Show tutor at the first time
                if (SharePreferenceUtils.getInstance().getFirstKnow()) {
                    showTutor(2);
                }
                break;
        }
        return false;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_look:
                look_layout.setVisibility(View.GONE);
                select_layout.setVisibility(View.VISIBLE);

                tester.showAll();
                // Show tutor at the first time
                if (SharePreferenceUtils.getInstance().getFirstKnow()) {
                    showTutor(2);
                }
                break;
            case R.id.btn_know:
                currentItem.setProficiency(currentItem.getProficiency() + 1);
                task.removePoint(currentItem);
                points.remove(this.currentItem);
                next();
                break;
            case R.id.btn_skip:
                currentItem.setProficiency(currentItem.getProficiency() + 5);
                task.removePoint(currentItem);
                points.remove(currentItem);
                next();
                break;
            case R.id.btn_unknow:
                points.remove(currentItem);
                points.add(currentItem);
                next();
                break;
            default:
                break;
        }
    }

    private void showTutor(int type) {
        switch (type) {
            case 1:
                new ShowcaseView.Builder(this)
                        .withMaterialShowcase()
                        .setTarget(new ViewTarget(btn_show))
                        .setContentTitle(getString(R.string.btn_see_all))
                        .setContentText(getString(R.string.tip_show_button))
                        .hideOnTouchOutside()
                        .build();
                break;
            case 2:
                new ShowcaseView.Builder(this)
                        .withMaterialShowcase()
                        .setTarget(new ViewTarget(btn_know))
                        .setContentTitle(getString(R.string.know_button_usage))
                        .setContentText(getString(R.string.tip_know_button))
                        .hideOnTouchOutside()
                        .build();
                break;
        }


    }

    private void next() {
        if (points.isEmpty()) {
            androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(this);
            builder.setTitle(R.string.complete).setMessage(R.string.tip_finish_target)
                    .setPositiveButton(R.string.complete, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // Exit
                            finish();
                        }
                    }).setCancelable(false).show();
        } else {
            currentItem = points.get(0);
            initData(currentItem);
        }
    }

}
