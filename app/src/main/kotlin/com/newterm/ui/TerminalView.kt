package com.newterm.ui

import android.content.Context
import android.graphics.Typeface
import android.os.Handler
import android.os.Looper
import android.widget.ScrollView
import android.widget.TextView
import com.newterm.core.Terminal
import com.newterm.theme.Theme
import com.newterm.ui.dsl.dp
import com.newterm.ui.dsl.round
import com.newterm.util.Ansi

/** Scrollback view bound to one [Terminal]; redraws are throttled to ~20 fps. */
class TerminalView(ctx: Context, t: Theme, size: Int, private val term: Terminal) : ScrollView(ctx) {
    private val text = TextView(ctx)
    private val ui = Handler(Looper.getMainLooper())
    private var pending = false

    init {
        background = ctx.round(t.termBg, 12)
        text.setTextColor(t.termFg); text.textSize = size.toFloat()
        text.typeface = Typeface.MONOSPACE; text.setTextIsSelectable(true)
        text.setPadding(ctx.dp(10), ctx.dp(8), ctx.dp(10), ctx.dp(8))
        addView(text)
        term.listener = { schedule() }
        redraw()
    }

    private fun schedule() {
        if (pending) return
        pending = true
        ui.postDelayed({ pending = false; redraw() }, 50)
    }

    private fun redraw() {
        val raw = synchronized(term.output) { term.output.takeLast(30_000).toString() }
        text.text = Ansi.render(raw)
        post { fullScroll(FOCUS_DOWN) }
    }

    override fun onDetachedFromWindow() { term.listener = null; super.onDetachedFromWindow() }
}
