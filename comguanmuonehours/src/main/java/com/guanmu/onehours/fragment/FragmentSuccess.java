package com.guanmu.onehours.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.guanmu.onehours.R;
import com.guanmu.onehours.view.LoadingView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentSuccess.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class FragmentSuccess extends BaseFragment {

    private OnFragmentInteractionListener mListener;

    public FragmentSuccess() {
        // Required empty public constructor
    }


    @Override
    public View createView(LayoutInflater inflater, ViewGroup root,
                           Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_success, root, false);
        LoadingView continueButton = (LoadingView) view.findViewById(R.id.continue_button);
        continueButton.setNextable(true);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishLogin();
            }
        });
        return view;
    }

    public void finishLogin() {
        if (mListener != null) {
            mListener.onFragmentFinish();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentFinish();
    }
}
