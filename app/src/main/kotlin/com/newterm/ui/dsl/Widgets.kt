package com.newterm.ui.dsl

import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.newterm.theme.Theme

/** Themed building blocks. Every screen composes these. */
fun Context.button(t: Theme, text: String, primary: Boolean = false, onClick: () -> Unit) =
    TextView(this).apply {
        this.text = text; gravity = Gravity.CENTER; textSize = 14f
        setTextColor(if (primary) t.onAccent else t.text)
        setPadding(dp(16), dp(11), dp(16), dp(11))
        background = if (primary) pressable(t.accent, t.accent and 0xCCFFFFFF.toInt(), 12)
        else pressable(t.surface, t.border, 12, t.border)
        setOnClickListener { onClick() }
    }

fun Context.chip(t: Theme, text: String, selected: Boolean, onClick: () -> Unit) =
    TextView(this).apply {
        this.text = text; textSize = 13f; setPadding(dp(14), dp(8), dp(14), dp(8))
        setTextColor(if (selected) t.onAccent else t.text)
        background = round(if (selected) t.accent else t.surface, 20, t.border)
        setOnClickListener { onClick() }
    }

fun Context.card(t: Theme, onClick: (() -> Unit)? = null, block: LinearLayout.() -> Unit) =
    vbox(16) {
        background = pressable(t.surface, t.border, 16, t.border); block()
        onClick?.let { c -> setOnClickListener { c() } }
    }

fun Context.input(t: Theme, hint: String) = EditText(this).apply {
    this.hint = hint; setHintTextColor(t.subtext); setTextColor(t.text); textSize = 15f
    setSingleLine(); setPadding(dp(14), dp(10), dp(14), dp(10))
    background = round(t.bg, 12, t.border)
}

fun Context.chipRow(t: Theme, items: List<String>, selected: Int, onPick: (Int) -> Unit): View {
    val row = hbox { }
    items.forEachIndexed { i, s ->
        row.add(chip(t, s, i == selected) { onPick(i) }, m = 3, width = WRAP)
    }
    return android.widget.HorizontalScrollView(this).apply { isHorizontalScrollBarEnabled = false; addView(row) }
}
