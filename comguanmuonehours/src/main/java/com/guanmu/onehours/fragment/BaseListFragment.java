package com.guanmu.onehours.fragment;

import android.os.Bundle;
import androidx.fragment.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.guanmu.onehours.DaoSession;
import com.guanmu.onehours.activity.BaseActivity;

/**
 * A base class of the custom fragment.
 *
 */
public abstract class BaseListFragment extends ListFragment {
    public DaoSession dao;
    private View contentView = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null == contentView) {
            dao = ((BaseActivity)  getActivity()).dao;
            contentView = createView(inflater, container, savedInstanceState);
            return contentView;
        } else {
            return contentView;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != contentView) {
            ViewGroup parent = (ViewGroup) contentView.getParent();
            if (null != parent) {
                parent.removeView(contentView);
            }
        }
    }

    protected abstract View createView(LayoutInflater inflater, ViewGroup container,
                                       Bundle savedInstanceState);

}
