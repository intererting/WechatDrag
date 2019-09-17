package com.example.wechatdrag

import android.widget.ImageView
import java.lang.Exception

fun ImageView.checkHasImage(): Boolean {
    try {
        val innerDrawableField = ImageView::class.java.getDeclaredField("mDrawable")
        innerDrawableField.isAccessible = true
        return innerDrawableField.get(this) != null
    } catch (e: Exception) {
        return false
    }
}