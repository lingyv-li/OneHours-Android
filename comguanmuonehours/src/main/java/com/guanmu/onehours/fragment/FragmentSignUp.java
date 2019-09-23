package com.guanmu.onehours.fragment;

import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.guanmu.onehours.R;
import com.guanmu.onehours.StringUtils;
import com.guanmu.onehours.view.LoadingView;

/**
 * A simple {@link Fragment} subclass.
 * <p>
 * Already granted permission
 */
public class FragmentSignUp extends BaseFragment {
    private OnFragmentInteractionListener mListener;
    private EditText mPhoneView, mUsernameView, mPasswordView;
    private LoadingView mLoginButton;

    @Override
    public View createView(LayoutInflater inflater, ViewGroup root,
                           Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_up, root, false);
        Bundle args = getArguments();

        // Set up the login form.
        mPhoneView = (EditText) view.findViewById(R.id.phone_mail);
        mUsernameView = (EditText) view.findViewById(R.id.username);
        mPasswordView = (EditText) view.findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptSignUp();
                    return true;
                }
                return false;
            }
        });
        // Set Info
        String phoneUsername = args.getString("phone_username");
        if (StringUtils.isValidPhone(phoneUsername)) {
            mPhoneView.setText(phoneUsername);
        } else {
            mUsernameView.setText(phoneUsername);
        }
        mPasswordView.setText(args.getString("password"));

        mLoginButton = (LoadingView) view.findViewById(R.id.phone_mail_sign_in_button);
        mLoginButton.setNextable(true);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptSignUp();
            }
        });
        return view;
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptSignUp() {

        // Reset errors.
        mPhoneView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String phone = mPhoneView.getText().toString();
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!StringUtils.isValidPassword(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid username
        if (TextUtils.isEmpty(username)) {
            mUsernameView.setError(getString(R.string.error_field_required));
            focusView = mUsernameView;
            cancel = true;
        }

        // Check for a valid phone address.
        if (TextUtils.isEmpty(phone)) {
            mPhoneView.setError(getString(R.string.error_field_required));
            focusView = mPhoneView;
            cancel = true;
        } else if (!StringUtils.isValidPhone(phone)) {
            mPhoneView.setError(getString(R.string.error_invalid_phone_username));
            focusView = mPhoneView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            // TODO
//            AVUser user = new AVUser();
//            user.setUsername(username);
//            user.setMobilePhoneNumber(phone);
//            user.setPassword(password);
//            user.signUpInBackground(new SignUpCallback() {
//
//                @Override
//                public void done(AVException e) {
//                    if (e == null) {
//                        requestVerify();
//                    } else {
//                        showProgress(false);
//                        Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
//                    }
//                }
//            });
        }
    }

    private void requestVerify() {
        if (mListener != null) {
            mListener.onFragmentRequestVerify();
        }
    }

    private void showProgress(boolean show) {
        mLoginButton.setNextable(!show);
        mLoginButton.setClickable(!show);
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
        void onFragmentRequestVerify();
    }
}
