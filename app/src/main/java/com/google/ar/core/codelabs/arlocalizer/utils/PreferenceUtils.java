package com.google.ar.core.codelabs.arlocalizer.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

/**
 * This provides methods to manage preferences data.
 */
public class PreferenceUtils {

    private static final String PREFERENCE_KEY_USER_ID = "PREFERENCE_KEY_USER_ID";
    private static final String PREFERENCE_KEY_NICKNAME = "PREFERENCE_KEY_NICKNAME";
    private static final String PREFERENCE_KEY_PROFILE_URL = "PREFERENCE_KEY_PROFILE_URL";
    private static final String PREFERENCE_KEY_DO_NOT_DISTURB = "PREFERENCE_KEY_DO_NOT_DISTURB";

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    // Prevent instantiation
    private PreferenceUtils() {
    }

    public static void init(@NonNull Context appContext) {
        context = appContext.getApplicationContext();
    }

    private static SharedPreferences getSharedPreferences() {
        return context.getSharedPreferences("sendbird", Context.MODE_PRIVATE);
    }

    public static void setUserId(@NonNull String userId) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREFERENCE_KEY_USER_ID, userId).apply();
    }

    @NonNull
    public static String getUserId() {
        final String result = getSharedPreferences().getString(PREFERENCE_KEY_USER_ID, "");
        return result == null ? "" : result;
    }

    public static void setNickname(@NonNull String nickname) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREFERENCE_KEY_NICKNAME, nickname).apply();
    }

    @NonNull
    public static String getNickname() {
        final String result = getSharedPreferences().getString(PREFERENCE_KEY_NICKNAME, "");
        return result == null ? "" : result;
    }

    public static void setProfileUrl(@NonNull String profileUrl) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(PREFERENCE_KEY_PROFILE_URL, profileUrl).apply();
    }

    @NonNull
    public static String getProfileUrl() {
        final String result = getSharedPreferences().getString(PREFERENCE_KEY_PROFILE_URL, "");
        return result == null ? "" : result;
    }

    public static void setDoNotDisturb(boolean doNotDisturb) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(PREFERENCE_KEY_DO_NOT_DISTURB, doNotDisturb).apply();
    }

    public static boolean getDoNotDisturb() {
        return getSharedPreferences().getBoolean(PREFERENCE_KEY_DO_NOT_DISTURB, false);
    }

    public static void clearAll() {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.clear().apply();
    }
}
