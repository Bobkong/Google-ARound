package com.google.ar.core.codelabs.arlocalizer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.ar.core.codelabs.arlocalizer.R;
import com.google.ar.core.codelabs.arlocalizer.model.GeneralResponse;
import com.google.ar.core.codelabs.arlocalizer.netservice.Api.SignService;
import com.google.ar.core.codelabs.arlocalizer.utils.PasswordEncryptUtil;
import com.google.ar.core.codelabs.arlocalizer.utils.PreferenceUtils;
import com.google.ar.core.codelabs.arlocalizer.widgets.WaitingDialog;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import qiu.niorgai.StatusBarCompat;

public class MainActivity extends AppCompatActivity {

    private ConstraintLayout tryStatic, tryCloud, signOut;
    private ImageView avatar;
    private TextView helloText;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        StatusBarCompat.setStatusBarColor(this, this.getResources().getColor(R.color.bg_color));

        tryStatic = findViewById(R.id.try_static_cl);
        tryCloud = findViewById(R.id.try_cloud_cl);
        signOut = findViewById(R.id.sign_out_button);

        avatar = findViewById(R.id.user_avatar);
        helloText = findViewById(R.id.hello_text);

        String username = PreferenceUtils.getNickname();
        if (username.equals("Liz")) {
            avatar.setImageResource(R.drawable.avatar_female);
            helloText.setText("Good Morning, Liz!");
        } else {
            avatar.setImageResource(R.drawable.avatar_male);
            helloText.setText("Good Morning, Ryan!");
        }

        tryStatic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LocalizeActivity.class);
                startActivity(intent);
            }
        });

        tryCloud.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                WaitingDialog.show(MainActivity.this);
                SignService.getInstance().signOut(username).observeOn(AndroidSchedulers.mainThread())
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
                                    PreferenceUtils.setNickname("");
                                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
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
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onComplete() {

                            }
                        });


            }
        });
    }


}
