package com.guanmu.onehours.fragment

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.AutoCompleteTextView
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.guanmu.onehours.PermissionUtils
import com.guanmu.onehours.R
import com.guanmu.onehours.StringUtils
import com.guanmu.onehours.activity.LoginActivity
import com.guanmu.onehours.view.LoadingView

class FragmentLoginInput : BaseFragment() {

    private var mListener: OnFragmentInteractionListener? = null
    private var mActivity: LoginActivity? = null
    private var mPhoneMailView: AutoCompleteTextView? = null
    private var mPasswordView: EditText? = null
    private var mLoginButton: LoadingView? = null
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivity = activity as LoginActivity?
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        // BEGIN_INCLUDE(onRequestPermissionsResult)
        if (requestCode == PERMISSION_REQUEST_LOGIN) {
            // Request for camera permission.
            if (PermissionUtils.verifyPermissions(grantResults)) {
                // Permission has been granted. Start camera preview Activity.
                login()
            } else {
                // Permission request was denied.
                mListener!!.onFragmentFinish()
            }
        }
        // END_INCLUDE(onRequestPermissionsResult)
    }

    public override fun createView(inflater: LayoutInflater, root: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.fragment_login_input, root, false)
        view.findViewById<View>(R.id.skip_button).setOnClickListener {
            if (mListener != null) {
                mListener!!.onFragmentFinish()
            }
        }
        mPhoneMailView = view.findViewById<View>(R.id.phone_mail) as AutoCompleteTextView
        mPasswordView = view.findViewById<View>(R.id.password) as EditText
        mPasswordView!!.setOnEditorActionListener(TextView.OnEditorActionListener { textView, id, keyEvent ->
            if (id == R.id.login || id == EditorInfo.IME_NULL) {
                login()
                return@OnEditorActionListener true
            }
            false
        })
        mLoginButton = view.findViewById<View>(R.id.phone_mail_sign_in_button) as LoadingView
        mLoginButton!!.setNextable(true)
        mLoginButton!!.setOnClickListener { login() }
        val qqLoginButton = view.findViewById<View>(R.id.qq_login) as ImageButton

        //        qqLoginButton.setOnClickListener(v -> {
        //            try {
        //                SNS.setupPlatform(mActivity, SNSType.AVOSCloudSNSQQ, "1104983434", "B4BuU7JPZFbUxpfu", "https://leancloud.cn/1.1/sns/callback/l81cvayiukm11fkz");
        //            } catch (AVException e) {
        //                e.printStackTrace();
        //            }
        //            SNS.loginWithCallback(mActivity, SNSType.AVOSCloudSNSQQ, myCallback);
        //
        //        });
        return view
    }

    private fun requestLoginPermission() {
        // Permission has not been granted and must be requested.
        if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) || shouldShowRequestPermissionRationale(Manifest.permission.READ_PHONE_STATE)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with a button to request the missing permission.
            AlertDialog.Builder(context!!).setCancelable(true)
                    .setMessage(R.string.tip_request_login_permission)
                    .setPositiveButton(R.string.button_allow) { dialogInterface, i ->
                        requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE),
                                PERMISSION_REQUEST_LOGIN)
                    }.create()

        } else {
            // Request the permission. The result will be received in onRequestPermissionResult().
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE),
                    PERMISSION_REQUEST_LOGIN)
        }
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun login() {
        if (!PermissionUtils.hasPermissions(context!!, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE)) {
            // Permission is missing and must be requested.
            requestLoginPermission()
            return
        }


        showProgress(true)

        // Reset errors.
        mPhoneMailView!!.error = null
        mPasswordView!!.error = null

        // Store values at the time of the login attempt.
        val phoneUsername = mPhoneMailView!!.text.toString()
        val password = mPasswordView!!.text.toString()

        var cancel = false
        var focusView: View? = null

        // Check for a valid password, if the user entered one.
        if (!StringUtils.isValidPassword(password)) {
            mPasswordView!!.error = getString(R.string.error_invalid_password)
            focusView = mPasswordView
            cancel = true
        }

        // Check for a valid phone address.
        if (TextUtils.isEmpty(phoneUsername)) {
            mPhoneMailView!!.error = getString(R.string.error_field_required)
            focusView = mPhoneMailView
            cancel = true
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView!!.requestFocus()
            showProgress(false)
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true)
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
    private fun attemptSignUp() {
        // Store values at the time of the login attempt.
        val phoneUsername = mPhoneMailView!!.text.toString()
        val password = mPasswordView!!.text.toString()

        // Show a progress spinner, and kick off a background task to
        // perform the user login attempt.
        showProgress(true)
        val args = Bundle()
        args.putString("phone_username", phoneUsername)
        args.putString("password", password)
        mListener!!.onFragmentAttemptSignUp(args)
    }

    private fun success() {
        if (mListener != null) {
            mListener!!.onFragmentSuccess()
        }
    }

    private fun showProgress(show: Boolean) {
        mLoginButton!!.setNextable(!show)
        mLoginButton!!.isClickable = !show
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            mListener = context
        } else {
            throw RuntimeException("$context must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        mListener = null
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     *
     * See the Android Training lesson [Communicating with Other Fragments](http://developer.android.com/training/basics/fragments/communicating.html) for more information.
     */
    interface OnFragmentInteractionListener {
        fun onFragmentSuccess()

        fun onFragmentFinish()

        fun onFragmentAttemptSignUp(args: Bundle)
    }

    companion object {

        private const val PERMISSION_REQUEST_LOGIN = 0
    }

}
