package com.google.ar.core.codelabs.arlocalizer;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.multidex.MultiDexApplication;

import com.google.ar.core.codelabs.arlocalizer.consts.InitState;
import com.google.ar.core.codelabs.arlocalizer.consts.StringSet;
import com.google.ar.core.codelabs.arlocalizer.utils.PreferenceUtils;
import com.google.ar.core.codelabs.arlocalizer.utils.PushUtils;
import com.sendbird.android.exception.SendbirdException;
import com.sendbird.android.handler.InitResultHandler;
import com.sendbird.android.params.OpenChannelCreateParams;
import com.sendbird.uikit.SendbirdUIKit;
import com.sendbird.uikit.adapter.SendbirdUIKitAdapter;
import com.sendbird.uikit.consts.ReplyType;
import com.sendbird.uikit.consts.ThreadReplySelectType;
import com.sendbird.uikit.interfaces.CustomParamsHandler;
import com.sendbird.uikit.interfaces.UserInfo;

public class BaseApplication extends MultiDexApplication {

    private static Context context;
    private static final String APP_ID = "36F2BDF9-4885-4631-BD81-4B3F57A41933";
    private static final MutableLiveData<InitState> initState = new MutableLiveData<>();

    public static Context getContext(){
        return context;
    }

    /**
     * Initializes Sendbird UIKit
     */
    @Override
    public void onCreate() {
        super.onCreate();

        context=getApplicationContext();
        PreferenceUtils.init(getApplicationContext());

        SendbirdUIKit.init(new SendbirdUIKitAdapter() {
            @Override
            @NonNull
            public String getAppId() {
                return APP_ID;
            }

            @Override
            @NonNull
            public String getAccessToken() {
                return "";
            }

            @Override
            @NonNull
            public UserInfo getUserInfo() {
                return new UserInfo() {
                    @NonNull
                    @Override
                    public String getUserId() {
                        return PreferenceUtils.getUserId();
                    }

                    @NonNull
                    @Override
                    public String getNickname() {
                        return PreferenceUtils.getNickname();
                    }

                    @NonNull
                    @Override
                    public String getProfileUrl() {
                        return PreferenceUtils.getProfileUrl();
                    }
                };
            }

            @Override
            @NonNull
            public InitResultHandler getInitResultHandler() {
                return new InitResultHandler() {
                    @Override
                    public void onMigrationStarted() {
                        initState.setValue(InitState.MIGRATING);
                    }

                    @Override
                    public void onInitFailed(@NonNull SendbirdException e) {
                        initState.setValue(InitState.FAILED);
                    }

                    @Override
                    public void onInitSucceed() {
                        initState.setValue(InitState.SUCCEED);
                    }
                };
            }
        }, this);

        SendbirdUIKit.setDefaultThemeMode(SendbirdUIKit.ThemeMode.Light);
        // register push notification
        //PushUtils.registerPushHandler(new MyFirebaseMessagingService());
        // set logger
        SendbirdUIKit.setLogLevel(SendbirdUIKit.LogLevel.ALL);
        // set whether to use user profile
        SendbirdUIKit.setUseDefaultUserProfile(true);
        // set whether to use typing indicators in channel list
        SendbirdUIKit.setUseChannelListTypingIndicators(true);
        // set whether to use read/delivery receipt in channel list
        SendbirdUIKit.setUseChannelListMessageReceiptStatus(true);
        // set whether to use user mention
        SendbirdUIKit.setUseUserMention(true);
        // set reply type
        SendbirdUIKit.setReplyType(ReplyType.THREAD);
        SendbirdUIKit.setThreadReplySelectType(ThreadReplySelectType.THREAD);

        // set custom params
        SendbirdUIKit.setCustomParamsHandler(new CustomParamsHandler() {
            @Override
            public void onBeforeCreateOpenChannel(@NonNull OpenChannelCreateParams params) {
                // You can set OpenChannelCreateParams globally before creating a open channel.
                params.setCustomType(StringSet.SB_COMMUNITY_TYPE);
            }
        });
    }

    /**
     * Returns the state of the result from initialization of Sendbird UIKit.
     *
     * @return the {@link InitState} instance
     */
    @NonNull
    public static LiveData<InitState> initStateChanges() {
        return initState;
    }
}
