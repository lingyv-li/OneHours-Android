package com.guanmu.onehours.adapter

import android.content.Context
import android.text.Html
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import com.guanmu.onehours.DaoSession
import com.guanmu.onehours.R
import com.guanmu.onehours.model.MPoint
import com.guanmu.onehours.model.MTag
import java.util.*

class KnowledgeAdapter(private val mContext: Context, filter: FilterType, private val dao: DaoSession) : BaseAdapter(), Filterable {
    private var knowledgeList: List<MPoint> = ArrayList()
    private var mFilter: Filter? = null

    init {
        mFilter = when (filter) {
            FilterType.ContentFilter -> KnowledgeContentFilter()
            FilterType.TagFilter -> KnowledgeTagFilter()
        }
        resetDataSet()
    }

    fun resetDataSet() {
        knowledgeList = MPoint.loadAll(dao)
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return knowledgeList.size
    }

    override fun getItem(position: Int): MPoint {
        return knowledgeList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        @Suppress("NAME_SHADOWING")
        val convertView = convertView
                ?: LayoutInflater.from(mContext).inflate(
                        R.layout.item_knowledge_list,
                        parent,
                        false
                )!!
        val item = getItem(position)
        val holder: ViewHolder = if (convertView.tag == null) {
            ViewHolder().apply {
                tags = convertView.findViewById<View>(R.id.tags) as TextView
                content = convertView.findViewById<View>(R.id.content) as TextView
            }
        } else {
            convertView.tag as ViewHolder
        }

        if (item.getAddTagList().isEmpty()) {
            holder.tags!!.visibility = View.GONE
        } else {
            holder.tags!!.text = String.format(mContext.getString(R.string.tags_text), TextUtils.join(", ", item.getAddTagList()))
        }

        holder.content!!.text = Html.fromHtml(item.content)
        convertView.tag = holder

        return convertView
    }

    override fun getFilter(): Filter? {
        return mFilter
    }

    enum class FilterType {
        ContentFilter, TagFilter
    }

    private inner class ViewHolder {
        internal var tags: TextView? = null
        internal var content: TextView? = null
    }

    private inner class KnowledgeContentFilter : Filter() {

        override fun performFiltering(constraint: CharSequence): FilterResults {
            knowledgeList = MPoint.loadAll(dao, constraint.toString(), false, null)

            val filterResults = FilterResults()
            filterResults.values = knowledgeList
            filterResults.count = knowledgeList.size
            return filterResults
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            if (results.count > 0) {
                notifyDataSetChanged()
            } else {
                notifyDataSetInvalidated()
            }
        }
    }

    private inner class KnowledgeTagFilter : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            knowledgeList = if (TextUtils.isEmpty(constraint)) {
                MPoint.loadAll(dao, null, true, null)
            } else {
                MTag(dao, constraint.toString(), null).getPoints()
            }

            val filterResults = FilterResults()
            filterResults.values = knowledgeList
            filterResults.count = knowledgeList.size
            return filterResults
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            if (results.count > 0) {
                notifyDataSetChanged()
            } else {
                notifyDataSetInvalidated()
            }
        }

    }
}