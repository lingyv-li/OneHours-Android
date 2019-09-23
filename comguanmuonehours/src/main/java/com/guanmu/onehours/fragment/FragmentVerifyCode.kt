package com.guanmu.onehours.fragment


import android.content.Context
import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.guanmu.onehours.R
import com.guanmu.onehours.view.LoadingView

/**
 * A simple [Fragment] subclass.
 */
class FragmentVerifyCode : BaseFragment(), View.OnClickListener {

    private var mListener: OnFragmentInteractionListener? = null
    private var mVerifyCodeView: EditText? = null

    private var mRequestVerifyButton: Button? = null
    private var countDownTimer: CountDownTimer? = null

    public override fun createView(inflater: LayoutInflater, container: ViewGroup?,
                                   savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_verify_code, container, false)
        mVerifyCodeView = view.findViewById<View>(R.id.verify_code) as EditText

        mRequestVerifyButton = view.findViewById<View>(R.id.request_verify_button) as Button

        countDownTimer = object : CountDownTimer(60000, 1000) {
            // A counter to allow user send verification code every 60s
            // Triggers onTick every 10s
            override fun onTick(millisUntilFinished: Long) {
                mRequestVerifyButton!!.text = String.format(getString(R.string.tip_wait_for_request), millisUntilFinished / 1000)
                mRequestVerifyButton!!.isEnabled = false
            }

            override fun onFinish() {
                mRequestVerifyButton!!.text = getString(R.string.action_request_verify)
                mRequestVerifyButton!!.isEnabled = true
            }
        }
        mRequestVerifyButton!!.setOnClickListener(this)
        countDownTimer!!.start()

        val verifyButton = view.findViewById<View>(R.id.verify_button) as LoadingView
        verifyButton.setOnClickListener(this)

        return view
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.verify_button -> {
                val code = mVerifyCodeView!!.text.toString()
            }
            R.id.request_verify_button -> {
                mRequestVerifyButton!!.setText(R.string.tip_sending)
                mRequestVerifyButton!!.isEnabled = false
            }
        }// TODO
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
        //                AVUser.requestMobilePhoneVerifyInBackground(AVUser.getCurrentUser().getMobilePhoneNumber(), new RequestMobileCodeCallback() {
        //                    @Override
        //                    public void done(AVException e) {
        //                        countDownTimer.start();
        //                    }
        //                });
    }

    private fun success() {
        if (mListener != null) {
            mListener!!.onFragmentSuccess()
        }
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
    }
}
