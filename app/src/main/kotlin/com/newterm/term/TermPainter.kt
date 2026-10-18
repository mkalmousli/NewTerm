package com.newterm.term

import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import com.newterm.theme.Theme

/** Draws an [Emulator] viewport. Cell metrics are exposed for sizing. */
class TermPainter(private val t: Theme, px: Float) {
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { typeface = Typeface.MONOSPACE; textSize = px }
    private val ascent = -paint.ascent()
    val cw = paint.measureText("M")
    val lh = Math.ceil((paint.descent() + ascent).toDouble()).toFloat()
    private var ch = IntArray(0); private var fg = IntArray(0); private var bg = IntArray(0); private var at = IntArray(0)
    private val run = StringBuilder()

    fun draw(c: Canvas, e: Emulator, off: Int, cursor: Boolean) {
        c.drawColor(t.termBg)
        synchronized(e) {
            val cols = e.s.cols
            if (ch.size < cols) { ch = IntArray(cols); fg = IntArray(cols); bg = IntArray(cols); at = IntArray(cols) }
            for (y in 0 until e.s.rows) { e.copyRow(y - off, ch, fg, bg, at); row(c, y, cols) }
            if (cursor && off == 0 && e.cursorVisible) {
                paint.color = (t.accent and 0xFFFFFF) or 0x99000000.toInt()
                c.drawRect(e.s.cx * cw, e.s.cy * lh, (e.s.cx + 1) * cw, (e.s.cy + 1) * lh, paint)
            }
        }
    }

    private fun row(c: Canvas, y: Int, cols: Int) {
        var x = 0
        while (x < cols) {
            var end = x + 1
            while (end < cols && fg[end] == fg[x] && bg[end] == bg[x] && at[end] == at[x]) end++
            var f = if (fg[x] == 0) t.termFg else fg[x]
            var b = if (bg[x] == 0) t.termBg else bg[x]
            if (at[x] and Pen.INV != 0) { val k = f; f = b; b = k }
            if (b != t.termBg) { paint.color = b; c.drawRect(x * cw, y * lh, end * cw, (y + 1) * lh, paint) }
            run.setLength(0)
            for (i in x until end) run.appendCodePoint(ch[i])
            if (run.isNotBlank() || at[x] and (Pen.UNDER or Pen.STRIKE) != 0) {
                paint.color = if (at[x] and Pen.DIM != 0) (f and 0xFFFFFF) or 0x99000000.toInt() else f
                paint.isFakeBoldText = at[x] and Pen.BOLD != 0
                paint.isUnderlineText = at[x] and Pen.UNDER != 0
                paint.isStrikeThruText = at[x] and Pen.STRIKE != 0
                paint.textSkewX = if (at[x] and Pen.ITALIC != 0) -0.2f else 0f
                c.drawText(run, 0, run.length, x * cw, y * lh + ascent, paint)
            }
            x = end
        }
    }
}
