package com.guanmu.onehours.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.FragmentManager;

import com.guanmu.onehours.R;
import com.guanmu.onehours.fragment.FragmentLoginInput;
import com.guanmu.onehours.fragment.FragmentSignUp;
import com.guanmu.onehours.fragment.FragmentSuccess;
import com.guanmu.onehours.fragment.FragmentVerifyCode;


/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends BaseActivity implements
        FragmentLoginInput.OnFragmentInteractionListener,
        FragmentSignUp.OnFragmentInteractionListener,
        FragmentVerifyCode.OnFragmentInteractionListener,
        FragmentSuccess.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FragmentLoginInput mFragInput = new FragmentLoginInput();
        getSupportFragmentManager().beginTransaction().add(R.id.fragment_frame, mFragInput).commit();
    }

    @Override
    public void onFragmentSuccess() {
        FragmentSuccess mFragSuccess = new FragmentSuccess();
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame, mFragSuccess).commit();
    }

    @Override
    public void onFragmentAttemptSignUp(Bundle args) {
        FragmentSignUp mFragSignUp = new FragmentSignUp();
        mFragSignUp.setArguments(args);
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame, mFragSignUp).commit();
    }

    @Override
    public void onFragmentRequestVerify() {
        FragmentVerifyCode mFragVerify = new FragmentVerifyCode();
        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_frame, mFragVerify).commit();
    }

    @Override
    public void onFragmentFinish() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        SNS.onActivityResult(requestCode, resultCode, data, SNSType.AVOSCloudSNSQQ);
    }
}

