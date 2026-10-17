package com.newterm.ui

import android.content.Context
import android.view.inputmethod.EditorInfo
import android.widget.LinearLayout
import com.newterm.theme.Theme
import com.newterm.ui.dsl.*

/** Command line with ↑/↓ history. [onSend] gets the line + "\n". */
class InputBar(ctx: Context, t: Theme, onSend: (String) -> Unit) : LinearLayout(ctx) {
    private val history = mutableListOf<String>()
    private var at = 0

    init {
        gravity = android.view.Gravity.CENTER_VERTICAL
        val field = ctx.input(t, "command")
        field.typeface = android.graphics.Typeface.MONOSPACE
        field.imeOptions = EditorInfo.IME_ACTION_SEND
        fun send() {
            val s = field.text.toString()
            history += s; at = history.size
            field.setText(""); onSend(s + "\n")
        }
        fun go(d: Int) {
            at = (at + d).coerceIn(0, history.size)
            field.setText(history.getOrElse(at) { "" }); field.setSelection(field.text.length)
        }
        field.setOnEditorActionListener { _, _, _ -> send(); true }
        add(field, 1f)
        add(ctx.button(t, "↑") { go(-1) }, m = 2, width = WRAP)
        add(ctx.button(t, "↓") { go(1) }, m = 2, width = WRAP)
        add(ctx.button(t, "⏎", true) { send() }, m = 2, width = WRAP)
    }
}
