package com.google.ar.core.codelabs.arlocalizer.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.ar.core.codelabs.arlocalizer.R;
import com.google.ar.core.codelabs.arlocalizer.consts.Configs;
import com.google.ar.core.codelabs.arlocalizer.utils.PreferenceUtils;

public class ChatActivity extends Activity {

    private ImageView back;
    private ImageView friendAvatar, friendAvatar1, friendAvatar2;
    private TextView friendName, friendMsg1, navigationHint, navigationTitle;
    private LinearLayout shareLocationLl;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        friendAvatar = findViewById(R.id.chat_avatar);
        friendAvatar1 = findViewById(R.id.chat_avatar_1);
        friendAvatar2 = findViewById(R.id.chat_avatar_2);

        friendName = findViewById(R.id.chat_user_name);
        friendMsg1 = findViewById(R.id.msg1);
        navigationHint = findViewById(R.id.share_location_hint);
        navigationTitle = findViewById(R.id.share_location_title);

        String username = PreferenceUtils.getNickname();
        if (username.equals("Liz")) {
            friendAvatar.setImageResource(R.drawable.avatar_male);
            friendAvatar1.setImageResource(R.drawable.avatar_male);
            friendAvatar2.setImageResource(R.drawable.avatar_male);

            friendName.setText("Ryan");
            friendMsg1.setText("Hey Liz! Where is your apartment building? So many buildings and entrances...");
            navigationHint.setText("You wants to share you location with Ryan");
            navigationTitle.setText("Find Ryan");
        } else {
            friendAvatar.setImageResource(R.drawable.avatar_female);
            friendAvatar1.setImageResource(R.drawable.avatar_female);
            friendAvatar2.setImageResource(R.drawable.avatar_female);

            friendName.setText("Liz");
            friendMsg1.setText("Hey Ryan! Where is your apartment building? So many buildings and entrances...");
            navigationHint.setText("You wants to share your location with Liz");
            navigationTitle.setText("Find Liz");
        }

        shareLocationLl = findViewById(R.id.share_location_ll);
        shareLocationLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ChatActivity.this, LocalizeActivity.class);
                intent.putExtra("mode", Configs.chat_mode);
                startActivity(intent);
            }
        });

    }
}
