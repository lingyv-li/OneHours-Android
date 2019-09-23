package com.guanmu.onehours.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ListView;

import com.guanmu.onehours.R;
import com.guanmu.onehours.activity.EditKnowledgeActivity;
import com.guanmu.onehours.adapter.KnowledgeAdapter;
import com.guanmu.onehours.model.MPoint;
import com.guanmu.onehours.model.MTag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import me.gujun.android.taggroup.TagGroup;

public class FragmentTabList extends BaseListFragment implements AbsListView.MultiChoiceModeListener {

    public KnowledgeAdapter adapter;
    private ListView listView;

    private TagGroup mTagGroup;
    private View mTagGrouper;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new KnowledgeAdapter(getActivity(), KnowledgeAdapter.FilterType.tagFilter, dao);
        setListAdapter(adapter);
        listView = getListView();
        listView.setMultiChoiceModeListener(this);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
    }

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_tab_list, container, false);
        mTagGroup = (TagGroup) rootView.findViewById(R.id.tag_group);
        mTagGroup.setOnTagClickListener(new TagGroup.OnTagClickListener() {
            @Override
            public void onTagClick(String tag) {
                if (tag.equals(getString(R.string.all))) {
                    adapter.getFilter().filter(null);
                } else {
                    adapter.getFilter().filter(tag);
                }
            }
        });
        mTagGrouper = rootView.findViewById(R.id.tag_grouper);

        return rootView;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent();

        MPoint item = adapter.getItem(position);

        // Deselect item
        listView.setItemChecked(position, false);

        i.setClass(getActivity(), EditKnowledgeActivity.class);
        i.putExtra("point_id", item.getGUID().toString());
        i.putExtra("position", position);
        startActivityForResult(i, 0);
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position,
                                          long id, boolean checked) {
        // Here you can do something when items are selected/de-selected,
        // such as update the title in the CAB
    }

    @Override
    public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
        // Respond to clicks on the actions in the CAB
        switch (item.getItemId()) {
            case R.id.context_delete:

                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.delete).setMessage(getString(R.string.tip_list_delete))
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // Delete knowledge
                                deleteItems(listView.getCheckedItemPositions());
                                mode.finish(); // Action picked, so close the CAB
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                                mode.finish(); // Action picked, so close the CAB
                            }
                        }).show();
                return true;
            default:
                return false;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updatedData();
    }

    public void updatedData() {
        adapter.resetDataSet();
        refreshTags();
    }

    public void deleteItems(SparseBooleanArray checkedState) {
        // Loop up all checked items
        int checkedItemCount = checkedState.size();
        for (int i = 0; i < checkedItemCount; i++) {
            // Get the position(key) & the state(value) of the knowledge in the list
            int key = checkedState.keyAt(i);
            if (checkedState.get(key)) {
                // Delete the knowledge
                MPoint item = (MPoint) listView.getItemAtPosition(key);
                item.delete();
            }
        }
        updatedData();
    }

    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        // Inflate the menu for the CAB
        MenuInflater inflater = mode.getMenuInflater();
        inflater.inflate(R.menu.list_context_menu, menu);
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
        // Here you can make any necessary updates to the activity when
        // the CAB is removed. By default, selected items are deselected/unchecked.
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        // Here you can perform updates to the CAB due to
        // an invalidate() request
        return false;
    }

    @SuppressWarnings("unchecked")
    public void refreshTags() {
        HashSet<String> tagSet = new HashSet<>();
        for (MTag tag :
                MTag.loadAll(dao, true, null)) {
            tagSet.add(tag.getName());
        }
        if (!tagSet.isEmpty()) {
            mTagGrouper.setVisibility(View.VISIBLE);
            List<String> list = new ArrayList<>();
            list.add(getString(R.string.all));
            list.addAll(tagSet);
            mTagGroup.setTags(list);
        } else {
            mTagGrouper.setVisibility(View.GONE);
        }
    }
}
