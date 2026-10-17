package com.newterm.ui.dsl

import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView

fun Context.dp(v: Int) = (v * resources.displayMetrics.density + .5f).toInt()

val MATCH = ViewGroup.LayoutParams.MATCH_PARENT
val WRAP = ViewGroup.LayoutParams.WRAP_CONTENT

fun Context.vbox(pad: Int = 0, block: LinearLayout.() -> Unit = {}) = LinearLayout(this).apply {
    orientation = LinearLayout.VERTICAL; setPadding(dp(pad), dp(pad), dp(pad), dp(pad)); block()
}

fun Context.hbox(pad: Int = 0, block: LinearLayout.() -> Unit = {}) = LinearLayout(this).apply {
    orientation = LinearLayout.HORIZONTAL; setPadding(dp(pad), dp(pad), dp(pad), dp(pad))
    gravity = android.view.Gravity.CENTER_VERTICAL; block()
}

fun Context.label(text: String, color: Int, sp: Float = 15f, bold: Boolean = false) = TextView(this).apply {
    this.text = text; setTextColor(color); textSize = sp
    if (bold) setTypeface(typeface, Typeface.BOLD)
}

/** Add [v] with weight [w] (0 = wrap) and margins in dp. */
fun LinearLayout.add(v: View, w: Float = 0f, m: Int = 0, width: Int = MATCH, height: Int = WRAP): View {
    val lp = LinearLayout.LayoutParams(if (w > 0 && orientation == LinearLayout.HORIZONTAL) 0 else width,
        if (w > 0 && orientation == LinearLayout.VERTICAL) 0 else height, w)
    lp.setMargins(context.dp(m), context.dp(m), context.dp(m), context.dp(m))
    addView(v, lp); return v
}
