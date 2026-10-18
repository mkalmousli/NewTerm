package com.newterm.term

import android.content.ClipboardManager
import android.content.Context
import android.text.InputType
import android.util.TypedValue
import android.view.GestureDetector
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputMethodManager
import com.newterm.core.Terminal
import com.newterm.theme.Theme

/** Interactive terminal surface for one [Terminal]. */
class TermView(ctx: Context, t: Theme, sizeSp: Int, private val term: Terminal) : View(ctx) {
    private val painter = TermPainter(t, TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_SP, sizeSp.toFloat(), resources.displayMetrics))
    var ctrl = false
    var alt = false
    var onMods: (() -> Unit)? = null
    private var offset = 0
    private var acc = 0f

    private val gestures = GestureDetector(ctx, object : GestureDetector.SimpleOnGestureListener() {
        override fun onDown(e: MotionEvent) = true
        override fun onSingleTapUp(e: MotionEvent): Boolean { showKeyboard(); return true }
        override fun onLongPress(e: MotionEvent) { paste() }
        override fun onScroll(a: MotionEvent?, b: MotionEvent, dx: Float, dy: Float): Boolean {
            acc += dy
            val n = (acc / painter.lh).toInt()
            if (n != 0) { acc -= n * painter.lh; scrollLines(n) }
            return true
        }
    })

    init {
        isFocusable = true; isFocusableInTouchMode = true
        term.listener = { postInvalidate() }
    }

    private fun scrollLines(n: Int) {
        if (term.emu.inAlt) {
            repeat(kotlin.math.abs(n)) { sendKey(if (n < 0) KeyEvent.KEYCODE_DPAD_UP else KeyEvent.KEYCODE_DPAD_DOWN) }
        } else offset = (offset - n).coerceIn(0, term.emu.sb.size)
        invalidate()
    }

    fun showKeyboard() {
        requestFocus()
        (context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).showSoftInput(this, 0)
    }

    fun sendText(s: String) {
        term.write(Keys.text(s, ctrl, alt)); offset = 0
        if (ctrl || alt) { ctrl = false; alt = false; onMods?.invoke() }
    }

    fun sendKey(code: Int, mod: Int = 1) {
        Keys.special(code, term.emu.appCursor, mod)?.let { term.write(it); offset = 0 }
    }

    fun onKey(code: Int, ev: KeyEvent?): Boolean {
        val mod = 1 + (if (ev?.isShiftPressed == true) 1 else 0) + (if (alt || ev?.isAltPressed == true) 2 else 0) +
            (if (ctrl || ev?.isCtrlPressed == true) 4 else 0)
        val special = Keys.special(code, term.emu.appCursor, mod)
        if (special != null) { term.write(special); offset = 0; return true }
        val u = ev?.unicodeChar ?: 0
        if (u == 0) return false
        sendText(String(Character.toChars(u)).let { if (ev?.isCtrlPressed == true) Keys.text(it, true, false) else it })
        return true
    }

    fun paste() {
        val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val s = cm.primaryClip?.getItemAt(0)?.coerceToText(context)?.toString() ?: return
        term.write(if (term.emu.bracketed) "\u001B[200~$s\u001B[201~" else s)
    }

    override fun onKeyDown(k: Int, e: KeyEvent) = onKey(k, e) || super.onKeyDown(k, e)
    override fun onCheckIsTextEditor() = true
    override fun onCreateInputConnection(o: EditorInfo): InputConnection {
        o.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD or
            InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS
        o.imeOptions = EditorInfo.IME_FLAG_NO_FULLSCREEN or EditorInfo.IME_FLAG_NO_EXTRACT_UI
        return TermInput(this, this)
    }

    override fun onSizeChanged(w: Int, h: Int, ow: Int, oh: Int) {
        term.resize((w / painter.cw).toInt().coerceAtLeast(2), (h / painter.lh).toInt().coerceAtLeast(1))
    }

    override fun onTouchEvent(e: MotionEvent) = gestures.onTouchEvent(e) || super.onTouchEvent(e)
    override fun onDraw(c: android.graphics.Canvas) = painter.draw(c, term.emu, offset, hasFocus())
    override fun onDetachedFromWindow() { term.listener = null; super.onDetachedFromWindow() }
}
