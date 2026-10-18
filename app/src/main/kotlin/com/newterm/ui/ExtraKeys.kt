package com.newterm.ui

import android.content.Context
import android.view.KeyEvent
import android.widget.HorizontalScrollView
import com.newterm.term.TermView
import com.newterm.theme.Theme
import com.newterm.ui.dsl.*

/** Scrollable row of keys a phone keyboard lacks. CTRL/ALT are sticky for one key. */
class ExtraKeys(ctx: Context, t: Theme, v: TermView) : HorizontalScrollView(ctx) {
    init {
        isHorizontalScrollBarEnabled = false
        val row = ctx.hbox()
        fun key(label: String, code: Int) = row.add(ctx.chip(t, label, false) { v.sendKey(code) }, m = 3, width = WRAP)
        key("ESC", KeyEvent.KEYCODE_ESCAPE); key("TAB", KeyEvent.KEYCODE_TAB)
        val ctrl = ctx.chip(t, "CTRL", false) { v.ctrl = !v.ctrl; v.onMods?.invoke() }
        val alt = ctx.chip(t, "ALT", false) { v.alt = !v.alt; v.onMods?.invoke() }
        row.add(ctrl, m = 3, width = WRAP); row.add(alt, m = 3, width = WRAP)
        v.onMods = { ctx.setChipSelected(ctrl, t, v.ctrl); ctx.setChipSelected(alt, t, v.alt) }
        key("←", KeyEvent.KEYCODE_DPAD_LEFT); key("↓", KeyEvent.KEYCODE_DPAD_DOWN)
        key("↑", KeyEvent.KEYCODE_DPAD_UP); key("→", KeyEvent.KEYCODE_DPAD_RIGHT)
        key("HOME", KeyEvent.KEYCODE_MOVE_HOME); key("END", KeyEvent.KEYCODE_MOVE_END)
        key("PGUP", KeyEvent.KEYCODE_PAGE_UP); key("PGDN", KeyEvent.KEYCODE_PAGE_DOWN)
        for (s in listOf("|", "/", "-", "~", "\\")) row.add(ctx.chip(t, s, false) { v.sendText(s) }, m = 3, width = WRAP)
        row.add(ctx.chip(t, "PASTE", false) { v.paste() }, m = 3, width = WRAP)
        addView(row)
    }
}
