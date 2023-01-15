package com.google.ar.core.codelabs.arlocalizer.widget

import android.content.Context
import android.util.AttributeSet
import android.widget.ProgressBar
import com.google.ar.core.codelabs.arlocalizer.R
import com.google.ar.core.codelabs.arlocalizer.utils.DrawableUtils

class ProgressView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
) : ProgressBar(context, attrs) {
    init {
        val loadingTint = R.color.theme_color
        val loading = DrawableUtils.setTintList(context, R.drawable.progress, loadingTint)
        this.indeterminateDrawable = loading
    }
}
