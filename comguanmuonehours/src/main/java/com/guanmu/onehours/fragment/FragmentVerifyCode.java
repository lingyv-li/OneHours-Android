package com.guanmu.onehours.fragment;


import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.guanmu.onehours.R;
import com.guanmu.onehours.view.LoadingView;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentVerifyCode extends BaseFragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;
    private EditText mVerifyCodeView;

    private Button mRequestVerifyButton;
    private CountDownTimer countDownTimer;

    @Override
    public View createView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_verify_code, container, false);
        mVerifyCodeView = (EditText) view.findViewById(R.id.verify_code);

        mRequestVerifyButton = (Button) view.findViewById(R.id.request_verify_button);

        countDownTimer = new CountDownTimer(60000, 1000) {
            // A counter to allow user send verification code every 60s
            // Triggers onTick every 10s
            public void onTick(long millisUntilFinished) {
                mRequestVerifyButton.setText(String.format(getString(R.string.tip_wait_for_request), millisUntilFinished / 1000));
                mRequestVerifyButton.setEnabled(false);
            }

            public void onFinish() {
                mRequestVerifyButton.setText(getString(R.string.action_request_verify));
                mRequestVerifyButton.setEnabled(true);
            }
        };
        mRequestVerifyButton.setOnClickListener(this);
        countDownTimer.start();

        LoadingView verifyButton = (LoadingView) view.findViewById(R.id.verify_button);
        verifyButton.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.verify_button:
                String code = mVerifyCodeView.getText().toString();

                // TODO
//                AVUser.verifyMobilePhoneInBackground(code, new AVMobilePhoneVerifyCallback() {
//                    @Override
//                    public void done(AVException e) {
//                        if (e == null) {
//                            success();
//                        } else {
//                            Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//                        }
//                    }
//                });
                break;
            case R.id.request_verify_button:
                mRequestVerifyButton.setText(R.string.tip_sending);
                mRequestVerifyButton.setEnabled(false);
//                AVUser.requestMobilePhoneVerifyInBackground(AVUser.getCurrentUser().getMobilePhoneNumber(), new RequestMobileCodeCallback() {
//                    @Override
//                    public void done(AVException e) {
//                        countDownTimer.start();
//                    }
//                });
        }
    }

    private void success() {
        if (mListener != null) {
            mListener.onFragmentSuccess();
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentSuccess();
    }
}
