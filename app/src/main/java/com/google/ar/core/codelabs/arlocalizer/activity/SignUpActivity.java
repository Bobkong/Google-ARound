package com.google.ar.core.codelabs.arlocalizer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.ar.core.codelabs.arlocalizer.R;
import com.google.ar.core.codelabs.arlocalizer.model.GeneralResponse;
import com.google.ar.core.codelabs.arlocalizer.netservice.Api.SignService;
import com.google.ar.core.codelabs.arlocalizer.utils.PasswordEncryptUtil;
import com.google.ar.core.codelabs.arlocalizer.utils.PreferenceUtils;
import com.google.ar.core.codelabs.arlocalizer.utils.PushUtils;
import com.google.ar.core.codelabs.arlocalizer.widgets.WaitingDialog;
import com.sendbird.uikit.SendbirdUIKit;
import com.sendbird.uikit.log.Logger;
import com.sendbird.uikit.utils.TextUtils;

import java.util.Date;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import qiu.niorgai.StatusBarCompat;


public class SignUpActivity extends Activity {

    private static String TAG = "SignUpActivity";
    private ConstraintLayout signUp;
    private LinearLayout logIn;
    EditText username, password1, password2;
    private ConstraintLayout submit;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        StatusBarCompat.setStatusBarColor(this, this.getResources().getColor(R.color.bg_color));
        username = (EditText) findViewById(R.id.editText1);
        password1 = (EditText) findViewById(R.id.editText2);
        password2 = (EditText) findViewById(R.id.editText3);

        signUp = findViewById(R.id.signup_button);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username.getText().toString().isEmpty() || password1.getText().toString().isEmpty() || password2.getText().toString().isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Enter the Data wrong!", Toast.LENGTH_SHORT).show();
                } else if (!password1.getText().toString().equals(password2.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Passwords don't match!", Toast.LENGTH_SHORT).show();
                } else {

                    String userId = username.getText().toString();
                    // Remove all spaces from userID
                    String userIdString = userId.replaceAll("\\s", "");

                    String userNickname = username.getText().toString();
                    if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(userNickname)) {
                        return;
                    }

                    PreferenceUtils.setUserId(userIdString);
                    PreferenceUtils.setNickname(userNickname);

                    WaitingDialog.show(SignUpActivity.this);
                    SendbirdUIKit.connect((user, e) -> {
                        if (e != null) {
                            Logger.e(e);
                            WaitingDialog.dismiss();
                            PreferenceUtils.clearAll();
                            return;
                        }

                        SignService.getInstance().signUp(username.getText().toString(), PasswordEncryptUtil.INSTANCE.encrypt(password1.getText().toString())).observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<GeneralResponse>() {
                                    @Override
                                    public void onSubscribe(Disposable d) {

                                    }

                                    @Override
                                    public void onNext(GeneralResponse value) {
                                        WaitingDialog.dismiss();
                                        // success
                                        if (value.isSuccess()) {
                                            // save to local db
                                            PreferenceUtils.setUserId(userIdString);
                                            PreferenceUtils.setNickname(userNickname);
                                            //PushUtils.registerPushHandler(new MyFirebaseMessagingService());
                                            Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                                            startActivity(intent);
                                            finish();
                                        } else {
                                            // toast
                                            Toast.makeText(getApplicationContext(), value.getErr(), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onError(Throwable e) {
                                        // fail
                                        WaitingDialog.dismiss();
                                        Log.e(TAG, e.toString());
                                        Toast.makeText(SignUpActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onComplete() {

                                    }
                                });


                    });



                }
            }
        });

        logIn = findViewById(R.id.loginin_button);
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
            }
        });

    }
}
