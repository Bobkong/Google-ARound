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
import com.google.ar.core.codelabs.arlocalizer.widgets.WaitingDialog;
import com.sendbird.uikit.SendbirdUIKit;
import com.sendbird.uikit.log.Logger;
import com.sendbird.uikit.utils.TextUtils;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import qiu.niorgai.StatusBarCompat;


public class LoginActivity extends Activity {
    private String TAG = "LoginActivity";
    private ConstraintLayout logIn;
    private LinearLayout signUp;
    EditText username, password;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        StatusBarCompat.setStatusBarColor(this, this.getResources().getColor(R.color.bg_color));
        username = findViewById(R.id.editText1);
        password = findViewById(R.id.editText2);
        logIn = findViewById(R.id.login_button);
        logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String userId = username.getText().toString();
                // Remove all spaces from userID
                String userIdString = userId.replaceAll("\\s", "");

                String userNickname = username.getText().toString();
                if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(userNickname)) {
                    return;
                }

                PreferenceUtils.setUserId(userIdString);
                PreferenceUtils.setNickname(userNickname);

                WaitingDialog.show(LoginActivity.this);
                SendbirdUIKit.connect((user, e) -> {
                    if (e != null) {
                        Logger.e(e);
                        WaitingDialog.dismiss();
                        PreferenceUtils.clearAll();
                        return;
                    }

                    SignService.getInstance().signIn(username.getText().toString(), PasswordEncryptUtil.INSTANCE.encrypt(password.getText().toString())).observeOn(AndroidSchedulers.mainThread())
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
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
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
                                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onComplete() {

                                }
                            });


                });
            }
        });

        signUp = findViewById(R.id.signup_button);
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });
    }

}
