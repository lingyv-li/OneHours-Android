package com.guanmu.onehours.adapter;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.guanmu.onehours.DaoSession;
import com.guanmu.onehours.R;
import com.guanmu.onehours.model.MPoint;
import com.guanmu.onehours.model.MTag;

import java.util.ArrayList;
import java.util.List;

public class KnowledgeAdapter extends BaseAdapter implements Filterable {
    private List<MPoint> knowledgeList = new ArrayList<>();
    private Context mContext;
    private Filter mFilter;
    private DaoSession dao;

    public KnowledgeAdapter(Context context, FilterType filter, DaoSession dao) {
        this.mContext = context;
        this.dao = dao;
        switch (filter) {
            case contentFilter:
                mFilter = new KnowledgeContentFilter();
                break;
            case tagFilter:
                mFilter = new KnowledgeTagFilter();
                break;
        }
        resetDataSet();
    }

    public void resetDataSet() {
        knowledgeList = MPoint.loadAll(dao);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return knowledgeList.size();
    }

    @Override
    public MPoint getItem(int position) {
        return knowledgeList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final MPoint item = getItem(position);
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_knowledge_list, parent, false);
            holder = new ViewHolder();
            holder.tags = (TextView) convertView.findViewById(R.id.tags);
            holder.content = (TextView) convertView.findViewById(R.id.content);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        if (item.getAddTagList().isEmpty()) {
            holder.tags.setVisibility(View.GONE);
        } else {
            holder.tags.setText(String.format(mContext.getString(R.string.tags_text), TextUtils.join(", ", item.getAddTagList())));
        }

        holder.content.setText(Html.fromHtml(item.getContent()));
        convertView.setTag(holder);

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    public enum FilterType {
        contentFilter, tagFilter
    }

    private class ViewHolder {
        TextView tags;
        TextView content;
    }

    private class KnowledgeContentFilter extends Filter {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return super.convertResultToString(resultValue);
        }

        @Override
        protected Filter.FilterResults performFiltering(CharSequence constraint) {
            knowledgeList = MPoint.loadAll(dao, constraint.toString(), false, null);

            Filter.FilterResults filterResults = new Filter.FilterResults();
            filterResults.values = knowledgeList;
            filterResults.count = knowledgeList.size();
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }

    private class KnowledgeTagFilter extends Filter {
        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return super.convertResultToString(resultValue);
        }

        @Override
        protected Filter.FilterResults performFiltering(CharSequence constraint) {
            if (TextUtils.isEmpty(constraint)) {
                knowledgeList = MPoint.loadAll(dao, null, true, null);
            } else {
                knowledgeList = new MTag(dao, constraint.toString(), null).getPoints();
            }

            Filter.FilterResults filterResults = new Filter.FilterResults();
            filterResults.values = knowledgeList;
            filterResults.count = knowledgeList.size();
            return filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, Filter.FilterResults results) {
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }

    }
}