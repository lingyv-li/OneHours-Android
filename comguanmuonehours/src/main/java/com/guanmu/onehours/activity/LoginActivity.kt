package com.guanmu.onehours.activity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.FragmentManager

import com.guanmu.onehours.R
import com.guanmu.onehours.fragment.FragmentLoginInput
import com.guanmu.onehours.fragment.FragmentSignUp
import com.guanmu.onehours.fragment.FragmentSuccess
import com.guanmu.onehours.fragment.FragmentVerifyCode


/**
 * A login screen that offers login via email/password.
 */
class LoginActivity : BaseActivity(), FragmentLoginInput.OnFragmentInteractionListener, FragmentSignUp.OnFragmentInteractionListener, FragmentVerifyCode.OnFragmentInteractionListener, FragmentSuccess.OnFragmentInteractionListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val mFragInput = FragmentLoginInput()
        supportFragmentManager.beginTransaction().add(R.id.fragment_frame, mFragInput).commit()
    }

    override fun onFragmentSuccess() {
        val mFragSuccess = FragmentSuccess()
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_frame, mFragSuccess).commit()
    }

    override fun onFragmentAttemptSignUp(args: Bundle) {
        val mFragSignUp = FragmentSignUp()
        mFragSignUp.arguments = args
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_frame, mFragSignUp).commit()
    }

    override fun onFragmentRequestVerify() {
        val mFragVerify = FragmentVerifyCode()
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.beginTransaction().replace(R.id.fragment_frame, mFragVerify).commit()
    }

    override fun onFragmentFinish() {
        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        //        SNS.onActivityResult(requestCode, resultCode, data, SNSType.AVOSCloudSNSQQ);
    }
}

