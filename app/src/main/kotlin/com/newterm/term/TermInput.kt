package com.newterm.term

import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.BaseInputConnection

/** Soft-keyboard bridge: committed text and key events go straight to the terminal. */
class TermInput(view: View, private val out: TermView) : BaseInputConnection(view, false) {
    override fun commitText(text: CharSequence, newCursorPosition: Int): Boolean {
        out.sendText(text.toString()); return true
    }

    override fun setComposingText(text: CharSequence, newCursorPosition: Int) = true

    override fun deleteSurroundingText(before: Int, after: Int): Boolean {
        repeat(before.coerceAtLeast(1)) { out.sendKey(KeyEvent.KEYCODE_DEL) }; return true
    }

    override fun sendKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) out.onKey(event.keyCode, event)
        return true
    }
}
