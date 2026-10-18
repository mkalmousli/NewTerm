package com.newterm.term

import android.view.KeyEvent

/** Android key codes -> terminal byte sequences. */
object Keys {
    private const val ESC = "\u001B"

    private fun csiKey(letter: Char, mod: Int, app: Boolean) = when {
        mod > 1 -> "$ESC[1;$mod$letter"
        app -> "${ESC}O$letter"
        else -> "$ESC[$letter"
    }

    private fun tilde(n: Int, mod: Int) = if (mod > 1) "$ESC[$n;$mod~" else "$ESC[$n~"

    /** [mod] is the xterm modifier value: 1 + shift(1) + alt(2) + ctrl(4). */
    fun special(code: Int, app: Boolean, mod: Int): String? = when (code) {
        KeyEvent.KEYCODE_DPAD_UP -> csiKey('A', mod, app)
        KeyEvent.KEYCODE_DPAD_DOWN -> csiKey('B', mod, app)
        KeyEvent.KEYCODE_DPAD_RIGHT -> csiKey('C', mod, app)
        KeyEvent.KEYCODE_DPAD_LEFT -> csiKey('D', mod, app)
        KeyEvent.KEYCODE_MOVE_HOME -> csiKey('H', mod, app)
        KeyEvent.KEYCODE_MOVE_END -> csiKey('F', mod, app)
        KeyEvent.KEYCODE_PAGE_UP -> tilde(5, mod)
        KeyEvent.KEYCODE_PAGE_DOWN -> tilde(6, mod)
        KeyEvent.KEYCODE_FORWARD_DEL -> tilde(3, mod)
        KeyEvent.KEYCODE_INSERT -> tilde(2, mod)
        KeyEvent.KEYCODE_ENTER -> "\r"
        KeyEvent.KEYCODE_TAB -> if (mod == 2) "$ESC[Z" else "\t"
        KeyEvent.KEYCODE_ESCAPE -> ESC
        KeyEvent.KEYCODE_DEL -> "\u007F"
        in KeyEvent.KEYCODE_F1..KeyEvent.KEYCODE_F4 -> "${ESC}O${"PQRS"[code - KeyEvent.KEYCODE_F1]}"
        in KeyEvent.KEYCODE_F5..KeyEvent.KEYCODE_F12 -> tilde(intArrayOf(15, 17, 18, 19, 20, 21, 23, 24)[code - KeyEvent.KEYCODE_F5], mod)
        else -> null
    }

    /** Applies ctrl/alt to typed text (sticky buttons or hardware modifiers). */
    fun text(s: String, ctrl: Boolean, alt: Boolean): String {
        var r = s
        if (ctrl && r.length == 1) {
            val c = r[0]
            r = when (c) { in 'a'..'z' -> (c.code - 96).toChar().toString(); in '@'..'_' -> (c.code - 64).toChar().toString()
                ' ' -> "\u0000"; else -> r }
        }
        return if (alt) ESC + r else r
    }
}
