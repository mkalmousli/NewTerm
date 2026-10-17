package com.newterm.ui.dsl

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable

fun Context.round(fill: Int, radius: Int = 14, stroke: Int = 0) = GradientDrawable().apply {
    setColor(fill); cornerRadius = dp(radius).toFloat()
    if (stroke != 0) setStroke(dp(1), stroke)
}

/** Rounded background that dims on press (no ripple needed on API 14). */
fun Context.pressable(fill: Int, pressed: Int, radius: Int = 14, stroke: Int = 0) =
    StateListDrawable().apply {
        addState(intArrayOf(android.R.attr.state_pressed), round(pressed, radius, stroke))
        addState(intArrayOf(), round(fill, radius, stroke))
    }
