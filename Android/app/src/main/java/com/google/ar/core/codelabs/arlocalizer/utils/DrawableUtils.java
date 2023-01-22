package com.google.ar.core.codelabs.arlocalizer.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;


/**
 * This provides methods to draw icon and color.
 */
public class DrawableUtils {
    @Nullable
    public static Drawable setTintList(@NonNull Context context, int resId, int colorRes) {
        if (colorRes == 0) {
            return AppCompatResources.getDrawable(context, resId);
        }
        return setTintList(AppCompatResources.getDrawable(context, resId), AppCompatResources.getColorStateList(context, colorRes));
    }

    @Nullable
    public static Drawable setTintList(@Nullable Drawable drawable, @Nullable ColorStateList colorStateList) {
        if (drawable == null || colorStateList == null) {
            return drawable;
        }
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTintList(drawable, colorStateList);
        return drawable.mutate();
    }

}
