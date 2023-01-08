package com.google.ar.core.codelabs.arlocalizer.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class FileUtil {

    @SuppressLint("Range")
    public static String getPath(Context context, Uri uri) {
        String path = null;
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            return null;
        }
        if (cursor.moveToFirst()) {
            try {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cursor.close();
        return path;
    }

    public static void saveSPString(Context context, String key, String value) {
        SharedPreferences sharedPreferences= context.getSharedPreferences("EasyDrug", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getSPString(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("EasyDrug", Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }

    public static void deleteSPString(Context context, String key) {
        SharedPreferences sharedPreferences= context.getSharedPreferences("EasyDrug", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }

    public static void saveSPBool(Context context, String key, boolean value) {
        SharedPreferences sharedPreferences= context.getSharedPreferences("EasyDrug", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public static boolean getSPBool(Context context, String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("EasyDrug", Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, false);
    }
    public static void deleteSPBool(Context context, String key) {
        SharedPreferences sharedPreferences= context.getSharedPreferences("EasyDrug", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
    }
}
