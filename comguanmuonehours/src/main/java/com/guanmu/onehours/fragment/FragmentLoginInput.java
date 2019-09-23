package com.guanmu.onehours.fragment;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.guanmu.onehours.PermissionUtils;
import com.guanmu.onehours.R;
import com.guanmu.onehours.StringUtils;
import com.guanmu.onehours.activity.LoginActivity;
import com.guanmu.onehours.view.LoadingView;

public class FragmentLoginInput extends BaseFragment {

    private static final int PERMISSION_REQUEST_LOGIN = 0;

    private OnFragmentInteractionListener mListener;
    private LoginActivity mActivity;
    private AutoCompleteTextView mPhoneMailView;
    private EditText mPasswordView;
    private LoadingView mLoginButton;
//    // callback of SNS login
//    private SNSCallback myCallback = new SNSCallback() {
//        @Override
//        public void done(final SNSBase object, SNSException e) {
//            if (e == null) {
//                SNS.loginWithAuthData(object.userInfo(), new LogInCallback<AVUser>() {
//                    @Override
//                    public void done(AVUser avUser, AVException e) {
//                        if (e == null) {
//                            success();
//                        } else {
//                            Toast.makeText(mActivity, e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//                            object.logOut(getActivity());
//                        }
//                    }
//                });
//            }
//        }
//    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (LoginActivity) getActivity();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // BEGIN_INCLUDE(onRequestPermissionsResult)
        if (requestCode == PERMISSION_REQUEST_LOGIN) {
            // Request for camera permission.
            if (PermissionUtils.verifyPermissions(grantResults)) {
                // Permission has been granted. Start camera preview Activity.
                login();
            } else {
                // Permission request was denied.
                mListener.onFragmentFinish();
            }
        }
        // END_INCLUDE(onRequestPermissionsResult)
    }

    @Nullable
    @Override
    public View createView(LayoutInflater inflater, @Nullable ViewGroup root, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login_input, root, false);
        view.findViewById(R.id.skip_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onFragmentFinish();
                }
            }
        });
        mPhoneMailView = (AutoCompleteTextView) view.findViewById(R.id.phone_mail);
        mPasswordView = (EditText) view.findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    login();
                    return true;
                }
                return false;
            }
        });
        mLoginButton = (LoadingView) view.findViewById(R.id.phone_mail_sign_in_button);
        mLoginButton.setNextable(true);
        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                login();
            }
        });
        ImageButton qqLoginButton = (ImageButton) view.findViewById(R.id.qq_login);

//        qqLoginButton.setOnClickListener(v -> {
//            try {
//                SNS.setupPlatform(mActivity, SNSType.AVOSCloudSNSQQ, "1104983434", "B4BuU7JPZFbUxpfu", "https://leancloud.cn/1.1/sns/callback/l81cvayiukm11fkz");
//            } catch (AVException e) {
//                e.printStackTrace();
//            }
//            SNS.loginWithCallback(mActivity, SNSType.AVOSCloudSNSQQ, myCallback);
//
//        });
        return view;
    }

    private void requestLoginPermission() {
        // Permission has not been granted and must be requested.
        if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                || shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with a button to request the missing permission.
            new AlertDialog.Builder(getContext()).setCancelable(true)
                    .setMessage(R.string.tip_request_login_permission)
                    .setPositiveButton(R.string.button_allow, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE},
                                    PERMISSION_REQUEST_LOGIN);
                        }
                    }).create();

        } else {
            // Request the permission. The result will be received in onRequestPermissionResult().
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE},
                    PERMISSION_REQUEST_LOGIN);
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    protected void login() {
        if (!PermissionUtils.hasPermissions(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE)) {
            // Permission is missing and must be requested.
            requestLoginPermission();
            return;
        }


        showProgress(true);

        // Reset errors.
        mPhoneMailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String phoneUsername = mPhoneMailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!StringUtils.isValidPassword(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid phone address.
        if (TextUtils.isEmpty(phoneUsername)) {
            mPhoneMailView.setError(getString(R.string.error_field_required));
            focusView = mPhoneMailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
            showProgress(false);
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
//            if (StringUtils.isValidPhone(phoneUsername)) {
//
//                AVUser.loginByMobilePhoneNumberInBackground(phoneUsername, password, new LogInCallback<AVUser>() {
//
//                    @Override
//                    public void done(AVUser avUser, AVException e) {
//                        if (e == null) {
//                            success();
//                        } else if (e.getCode() == AVException.USER_DOESNOT_EXIST) {
//                            attemptSignUp();
//                        } else {
//                            Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//                            showProgress(false);
//                        }
//                    }
//                });
//            } else {
//                AVUser.logInInBackground(phoneUsername, password, new LogInCallback<AVUser>() {
//
//                    @Override
//                    public void done(AVUser avUser, AVException e) {
//                        if (e == null) {
//                            success();
//                        } else if (e.getCode() == AVException.USER_DOESNOT_EXIST) {
//                            attemptSignUp();
//                        } else {
//                            Toast.makeText(getActivity(), e.getLocalizedMessage(), Toast.LENGTH_LONG).show();
//                            showProgress(false);
//                        }
//                    }
//                });
//            }
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    protected void attemptSignUp() {
        // Store values at the time of the login attempt.
        String phoneUsername = mPhoneMailView.getText().toString();
        String password = mPasswordView.getText().toString();

        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        showProgress(true);
        Bundle args = new Bundle();
        args.putString("phone_username", phoneUsername);
        args.putString("password", password);
        mListener.onFragmentAttemptSignUp(args);
    }

    private void success() {
        if (mListener != null) {
            mListener.onFragmentSuccess();
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
        void onFragmentSuccess();

        void onFragmentFinish();

        void onFragmentAttemptSignUp(Bundle args);
    }

}
