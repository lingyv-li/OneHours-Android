package com.guanmu.onehours.fragment

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import android.util.SparseBooleanArray
import android.view.ActionMode
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ListView

import com.guanmu.onehours.R
import com.guanmu.onehours.activity.EditKnowledgeActivity
import com.guanmu.onehours.adapter.KnowledgeAdapter
import com.guanmu.onehours.model.MPoint
import com.guanmu.onehours.model.MTag

import java.util.ArrayList
import java.util.HashSet

import me.gujun.android.taggroup.TagGroup

class FragmentTabList : BaseListFragment(), AbsListView.MultiChoiceModeListener {

    lateinit var adapter: KnowledgeAdapter
    private lateinit var lv: ListView

    private var mTagGroup: TagGroup? = null
    private var mTagGrouper: View? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = KnowledgeAdapter(activity!!, KnowledgeAdapter.FilterType.TagFilter, dao)
        listAdapter = adapter
        lv = getListView()
        lv.setMultiChoiceModeListener(this)
        lv.choiceMode = ListView.CHOICE_MODE_MULTIPLE_MODAL
    }

    public override fun createView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val rootView = inflater.inflate(R.layout.fragment_tab_list, container, false)
        mTagGroup = rootView.findViewById<View>(R.id.tag_group) as TagGroup
        mTagGroup!!.setOnTagClickListener { tag ->
            if (tag == getString(R.string.all)) {
                adapter.filter!!.filter(null)
            } else {
                adapter.filter!!.filter(tag)
            }
        }
        mTagGrouper = rootView.findViewById(R.id.tag_grouper)

        return rootView
    }

    override fun onListItemClick(l: ListView, v: View, position: Int, id: Long) {
        super.onListItemClick(l, v, position, id)
        val i = Intent()

        val item = adapter.getItem(position)

        // Deselect item
        lv.setItemChecked(position, false)

        i.setClass(activity!!, EditKnowledgeActivity::class.java)
        i.putExtra("point_id", item.guid!!.toString())
        i.putExtra("position", position)
        startActivityForResult(i, 0)
    }

    override fun onItemCheckedStateChanged(mode: ActionMode, position: Int,
                                           id: Long, checked: Boolean) {
        // Here you can do something when items are selected/de-selected,
        // such as update the title in the CAB
    }

    override fun onActionItemClicked(mode: ActionMode, item: MenuItem): Boolean {
        // Respond to clicks on the actions in the CAB
        when (item.itemId) {
            R.id.context_delete -> {

                val builder = AlertDialog.Builder(activity!!)
                builder.setTitle(R.string.delete).setMessage(getString(R.string.tip_list_delete))
                        .setPositiveButton(R.string.delete) { dialog, id ->
                            // Delete knowledge
                            deleteItems(lv!!.checkedItemPositions)
                            mode.finish() // Action picked, so close the CAB
                        }
                        .setNegativeButton(R.string.cancel) { dialog, id ->
                            // User cancelled the dialog
                            mode.finish() // Action picked, so close the CAB
                        }.show()
                return true
            }
            else -> return false
        }
    }

    override fun onResume() {
        super.onResume()
        updatedData()
    }

    fun updatedData() {
        adapter.resetDataSet()
        refreshTags()
    }

    fun deleteItems(checkedState: SparseBooleanArray) {
        // Loop up all checked items
        val checkedItemCount = checkedState.size()
        for (i in 0 until checkedItemCount) {
            // Get the position(key) & the state(value) of the knowledge in the list
            val key = checkedState.keyAt(i)
            if (checkedState.get(key)) {
                // Delete the knowledge
                val item = lv.getItemAtPosition(key) as MPoint
                item.delete()
            }
        }
        updatedData()
    }

    override fun onCreateActionMode(mode: ActionMode, menu: Menu): Boolean {
        // Inflate the menu for the CAB
        val inflater = mode.menuInflater
        inflater.inflate(R.menu.list_context_menu, menu)
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode) {
        // Here you can make any necessary updates to the activity when
        // the CAB is removed. By default, selected items are deselected/unchecked.
    }

    override fun onPrepareActionMode(mode: ActionMode, menu: Menu): Boolean {
        // Here you can perform updates to the CAB due to
        // an invalidate() request
        return false
    }

    fun refreshTags() {
        val tagSet = HashSet<String>()
        for (tag in MTag.loadAll(dao, true, null)) {
            tagSet.add(tag.name)
        }
        if (!tagSet.isEmpty()) {
            mTagGrouper!!.visibility = View.VISIBLE
            val list = ArrayList<String>()
            list.add(getString(R.string.all))
            list.addAll(tagSet)
            mTagGroup!!.setTags(list)
        } else {
            mTagGrouper!!.visibility = View.GONE
        }
    }
}
