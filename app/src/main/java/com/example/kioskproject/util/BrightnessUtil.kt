package com.example.kioskproject.util

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper

// Compose의 context에서 Activity 찾기
fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

// 앱 화면 밝기 적용
fun applyAppBrightness(context: Context, brightness: Float) {
    val activity = context.findActivity() ?: return
    val params = activity.window.attributes

    // 0.01f ~ 1f 사이로 제한
    params.screenBrightness = brightness.coerceIn(0.01f, 1f)

    activity.window.attributes = params
}