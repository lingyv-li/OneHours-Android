package com.guanmu.onehours.activity

import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.github.amlcurran.showcaseview.ShowcaseView
import com.github.amlcurran.showcaseview.targets.ViewTarget
import com.guanmu.onehours.R
import com.guanmu.onehours.SharePreferenceUtils
import com.guanmu.onehours.model.MPoint
import com.guanmu.onehours.model.MTask
import com.guanmu.onehours.view.TestImageView
import com.guanmu.onehours.view.TestTextView
import com.guanmu.onehours.view.Testable
import java.util.*

class TestKnowledgeActivity : BaseActivity(), OnClickListener, View.OnLongClickListener {
    private var lookLayout: LinearLayout? = null
    private var cardView: ViewGroup? = null
    private var tester: Testable? = null
    private var btnShow: View? = null
    private var btn_not_know: View? = null
    private var btn_know: View? = null
    private var btn_skip: View? = null
    private var selectLayout: LinearLayout? = null
    private var currentItem: MPoint? = null
    private var mTestTextView: TestTextView? = null
    private var mTestImageView: TestImageView? = null

    private var task: MTask? = null
    private val points: MutableList<MPoint>
        get() {
            return task!!.pointList.toMutableList()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        task = MTask.getToday(dao, true)
        if (task == null) {
            Toast.makeText(this@TestKnowledgeActivity, R.string.tip_main_load_fail, Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        setContentView(R.layout.activity_test_peotry)
        intView()

        currentItem = points[0]
        initData(currentItem!!)
        // Show tutor at the first time
        if (SharePreferenceUtils.instance.firstRecite) {
            showTutor(1)
        }

        val seed = System.nanoTime()
        points.shuffle(Random(seed))
    }

    private fun intView() {
        // Preload text view
        mTestTextView = TestTextView(this)

        cardView = findViewById<View>(R.id.card_view) as ViewGroup
        lookLayout = findViewById<View>(R.id.look_layout) as LinearLayout//查看全文
        btnShow = findViewById(R.id.btn_look)
        selectLayout = findViewById<View>(R.id.select_layout) as LinearLayout
        btn_skip = findViewById(R.id.btn_skip)
        btn_not_know = findViewById(R.id.btn_unknow)
        btn_know = findViewById(R.id.btn_know)
        assert(btnShow != null)
        assert(btn_skip != null)
        assert(btn_not_know != null)
        assert(btn_know != null)
        btnShow!!.setOnLongClickListener(this)
        btnShow!!.setOnClickListener(this)
        btn_skip!!.setOnClickListener(this)
        btn_not_know!!.setOnClickListener(this)
        btn_know!!.setOnClickListener(this)

        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        if (supportActionBar != null)
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun initData(item: MPoint) {
        when (item.type) {
            MPoint.Type.normal -> {
                cardView!!.removeAllViews()
                if (mTestTextView == null) {
                    mTestTextView = TestTextView(this)
                }
                cardView!!.addView(mTestTextView)
                tester = mTestTextView
            }
            MPoint.Type.image -> {
                cardView!!.removeAllViews()
                if (mTestImageView == null) {
                    mTestImageView = TestImageView(this)
                }
                cardView!!.addView(mTestImageView)
                tester = mTestImageView
            }
        }
        tester!!.setData(item)
        lookLayout!!.visibility = View.VISIBLE
        selectLayout!!.visibility = View.GONE
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

    override fun onLongClick(view: View): Boolean {
        when (view.id) {
            R.id.btn_look -> {
                lookLayout!!.visibility = View.GONE
                selectLayout!!.visibility = View.VISIBLE

                //                TouchUpAsClick touchUpAsClick = new TouchUpAsClick();
                //                btn_know.setOnTouchListener(touchUpAsClick);
                //                btn_not_know.setOnTouchListener(touchUpAsClick);
                //                btn_skip.setOnTouchListener(touchUpAsClick);

                tester!!.showAll()
                // Show tutor at the first time
                if (SharePreferenceUtils.instance.firstKnow) {
                    showTutor(2)
                }
            }
        }
        return false
    }

    override fun onClick(v: View) {

        when (v.id) {
            R.id.btn_look -> {
                lookLayout!!.visibility = View.GONE
                selectLayout!!.visibility = View.VISIBLE

                tester!!.showAll()
                // Show tutor at the first time
                if (SharePreferenceUtils.instance.firstKnow) {
                    showTutor(2)
                }
            }
            R.id.btn_know -> {
                currentItem!!.proficiency = currentItem!!.proficiency + 1
                task!!.removePoint(currentItem!!)
                points.remove(currentItem!!)
                next()
            }
            R.id.btn_skip -> {
                currentItem!!.proficiency = currentItem!!.proficiency + 5
                task!!.removePoint(currentItem!!)
                points.remove(currentItem!!)
                next()
            }
            R.id.btn_unknow -> {
                points.remove(currentItem!!)
                points.add(currentItem!!)
                next()
            }
            else -> {
            }
        }
    }

    private fun showTutor(type: Int) {
        when (type) {
            1 -> ShowcaseView.Builder(this)
                    .withMaterialShowcase()
                    .setTarget(ViewTarget(btnShow))
                    .setContentTitle(getString(R.string.btn_see_all))
                    .setContentText(getString(R.string.tip_show_button))
                    .hideOnTouchOutside()
                    .build()
            2 -> ShowcaseView.Builder(this)
                    .withMaterialShowcase()
                    .setTarget(ViewTarget(btn_know))
                    .setContentTitle(getString(R.string.know_button_usage))
                    .setContentText(getString(R.string.tip_know_button))
                    .hideOnTouchOutside()
                    .build()
        }


    }

    private operator fun next() {
        if (points.isEmpty()) {
            val builder = androidx.appcompat.app.AlertDialog.Builder(this)
            builder.setTitle(R.string.complete).setMessage(R.string.tip_finish_target)
                    .setPositiveButton(R.string.complete) { _, _ ->
                        // Exit
                        finish()
                    }.setCancelable(false).show()
        } else {
            currentItem = points[0]
            initData(currentItem!!)
        }
    }

}
