package com.guanmu.onehours.fragment


import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView

import com.guanmu.onehours.R
import com.guanmu.onehours.activity.EditKnowledgeActivity
import com.guanmu.onehours.activity.SetMissionActivity
import com.guanmu.onehours.activity.TestKnowledgeActivity
import com.guanmu.onehours.model.MPoint
import com.guanmu.onehours.model.MTask

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class FragmentTabMain : BaseFragment(), View.OnClickListener {
    lateinit var tv_all_count: TextView
    lateinit var tv_study_count: TextView
    lateinit var tv_list_left: TextView
    lateinit var tv_percent: TextView
    lateinit var progress: ProgressBar

    override fun createView(inflater: LayoutInflater, container: ViewGroup?,
                            savedInstanceState: Bundle?): View {
        val rootView = inflater.inflate(R.layout.fragment_tab_main, container, false)
        tv_all_count = rootView.findViewById<View>(com.guanmu.onehours.R.id.tv_all_count) as TextView
        tv_study_count = rootView.findViewById<View>(com.guanmu.onehours.R.id.tv_study_count) as TextView
        tv_list_left = rootView.findViewById<View>(com.guanmu.onehours.R.id.tv_list_left) as TextView
        progress = rootView.findViewById<View>(com.guanmu.onehours.R.id.list_progress) as ProgressBar
        tv_percent = rootView.findViewById<View>(com.guanmu.onehours.R.id.tv_percent) as TextView

        rootView.findViewById<View>(R.id.button_my_mission).setOnClickListener(this)
        rootView.findViewById<View>(R.id.button_add_knowledge).setOnClickListener(this)
        rootView.findViewById<View>(R.id.button_start_recitation).setOnClickListener(this)

        return rootView
    }

    override fun onResume() {
        super.onResume()
        updatedData()
    }

    fun updatedData() {
        // 累计知识点数
        tv_all_count.text = Html.fromHtml(String.format(getString(R.string.num_unit),
                MPoint.countAll(dao).toString()))
        // 已学习(熟练度大于5)
        tv_study_count.text = Html.fromHtml(String.format(getString(R.string.num_unit),
                MPoint.countProf(dao).toString()))

        val task = MTask.getToday(dao, false)

        var missionSize = 0
        var doneMissionsSize = 0
        if (task != null) {
            missionSize = task.count
            doneMissionsSize = missionSize - task.pointList.size
        }

        tv_list_left.text = Html.fromHtml(String.format(getString(R.string.num_remain), missionSize - doneMissionsSize))
        val percent = getPercent(missionSize, doneMissionsSize)
        tv_percent.text = String.format(getString(R.string.num_percent), percent.toString())

        progress.progress = percent
    }

    fun getPercent(total: Int, cursor: Int): Int {
        val percent: Int

        if (total == 0) {
            percent = 0
        } else {
            percent = (cursor / total.toDouble() * 100).toInt()
        }
        return percent
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onClick(v: View) {
        val i: Intent
        when (v.id) {
            // add new points
            com.guanmu.onehours.R.id.button_add_knowledge -> {
                i = Intent()
                i.setClass(activity!!, EditKnowledgeActivity::class.java)
                i.flags = EditKnowledgeActivity.REQUEST_ADD
                startActivityForResult(i, EditKnowledgeActivity.REQUEST_ADD) // Request code 0 for add content
            }
            // start recitation
            com.guanmu.onehours.R.id.button_start_recitation -> {
                // Check mission list(on going recitation): when empty, load mission list, check again; otherwise, start recitation
                val pool = Executors.newSingleThreadExecutor()
                pool.execute {
                    val i = Intent()
                    i.setClass(activity!!, TestKnowledgeActivity::class.java)
                    startActivity(i)
                }
            }
            // my mission
            com.guanmu.onehours.R.id.button_my_mission -> {
                i = Intent()
                i.setClass(activity!!, SetMissionActivity::class.java)
                startActivity(i)
            }
            else -> {
            }
        }
    }

}
