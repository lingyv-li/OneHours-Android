package com.guanmu.onehours.fragment

import android.os.Bundle
import androidx.fragment.app.ListFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.guanmu.onehours.DaoSession
import com.guanmu.onehours.activity.BaseActivity

/**
 * A base class of the custom fragment.
 *
 */
abstract class BaseListFragment : ListFragment() {
    lateinit var dao: DaoSession
    private var contentView: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        if (null == contentView) {
            dao = (activity as BaseActivity).dao
            contentView = createView(inflater, container, savedInstanceState)
            return contentView
        } else {
            return contentView
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        if (null != contentView) {
            val parent = contentView!!.parent as ViewGroup
            parent.removeView(contentView)
        }
    }

    protected abstract fun createView(inflater: LayoutInflater, container: ViewGroup?,
                                      savedInstanceState: Bundle?): View

}
