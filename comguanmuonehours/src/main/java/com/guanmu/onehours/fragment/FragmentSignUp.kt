package com.guanmu.onehours.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView

import com.guanmu.onehours.R
import com.guanmu.onehours.StringUtils
import com.guanmu.onehours.view.LoadingView

/**
 * A simple [Fragment] subclass.
 *
 *
 * Already granted permission
 */
class FragmentSignUp : BaseFragment() {
    private var mListener: OnFragmentInteractionListener? = null
    private var mPhoneView: EditText? = null
    private var mUsernameView: EditText? = null
    private var mPasswordView: EditText? = null
    private var mLoginButton: LoadingView? = null

    public override fun createView(inflater: LayoutInflater, container: ViewGroup?,
                                   savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_sign_up, container, false)
        val args = arguments

        // Set up the login form.
        mPhoneView = view.findViewById<View>(R.id.phone_mail) as EditText
        mUsernameView = view.findViewById<View>(R.id.username) as EditText
        mPasswordView = view.findViewById<View>(R.id.password) as EditText
        mPasswordView!!.setOnEditorActionListener(TextView.OnEditorActionListener { textView, id, keyEvent ->
            if (id == R.id.login || id == EditorInfo.IME_NULL) {
                attemptSignUp()
                return@OnEditorActionListener true
            }
            false
        })
        // Set Info
        val phoneUsername = args!!.getString("phone_username")
        if (StringUtils.isValidPhone(phoneUsername!!)) {
            mPhoneView!!.setText(phoneUsername)
        } else {
            mUsernameView!!.setText(phoneUsername)
        }
        mPasswordView!!.setText(args.getString("password"))

        mLoginButton = view.findViewById<View>(R.id.phone_mail_sign_in_button) as LoadingView
        mLoginButton!!.setNextable(true)
        mLoginButton!!.setOnClickListener { attemptSignUp() }
        return view
    }


    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private fun attemptSignUp() {

        // Reset errors.
        mPhoneView!!.error = null
        mPasswordView!!.error = null

        // Store values at the time of the login attempt.
        val phone = mPhoneView!!.text.toString()
        val username = mUsernameView!!.text.toString()
        val password = mPasswordView!!.text.toString()

        var cancel = false
        var focusView: View? = null

        // Check for a valid password, if the user entered one.
        if (!StringUtils.isValidPassword(password)) {
            mPasswordView!!.error = getString(R.string.error_invalid_password)
            focusView = mPasswordView
            cancel = true
        }

        // Check for a valid username
        if (TextUtils.isEmpty(username)) {
            mUsernameView!!.error = getString(R.string.error_field_required)
            focusView = mUsernameView
            cancel = true
        }

        // Check for a valid phone address.
        if (TextUtils.isEmpty(phone)) {
            mPhoneView!!.error = getString(R.string.error_field_required)
            focusView = mPhoneView
            cancel = true
        } else if (!StringUtils.isValidPhone(phone)) {
            mPhoneView!!.error = getString(R.string.error_invalid_phone_username)
            focusView = mPhoneView
            cancel = true
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView!!.requestFocus()
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true)
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

    private fun requestVerify() {
        if (mListener != null) {
            mListener!!.onFragmentRequestVerify()
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
        fun onFragmentRequestVerify()
    }
}
