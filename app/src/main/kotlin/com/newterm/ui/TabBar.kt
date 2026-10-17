package com.newterm.ui

import android.content.Context
import android.widget.HorizontalScrollView
import com.newterm.core.Terminal
import com.newterm.theme.Theme
import com.newterm.ui.dsl.*

/** Pill tabs for terminals, plus “+” (new) and “✕” (close current). */
class TabBar(
    ctx: Context, t: Theme, tabs: List<Terminal>, selected: Int,
    onPick: (Int) -> Unit, onAdd: () -> Unit, onClose: () -> Unit,
) : HorizontalScrollView(ctx) {
    init {
        isHorizontalScrollBarEnabled = false
        val row = ctx.hbox()
        tabs.forEachIndexed { i, term ->
            row.add(ctx.chip(t, "shell ${i + 1}", i == selected) { onPick(i) }, m = 3, width = WRAP)
        }
        row.add(ctx.chip(t, "＋", false) { onAdd() }, m = 3, width = WRAP)
        if (tabs.size > 1) row.add(ctx.chip(t, "✕", false) { onClose() }, m = 3, width = WRAP)
        addView(row)
    }
}
