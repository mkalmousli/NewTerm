package com.newterm.term

/** xterm-style terminal state machine. Thread-safe via `synchronized(emu)`. */
class Emulator(cols: Int, rows: Int, val reply: (String) -> Unit) {
    val sb = Scrollback(3000)
    private val main = Screen(cols, rows).also { it.sb = sb }
    private val alt = Screen(cols, rows)
    var s = main
    val pen = Pen()
    var appCursor = false; var autowrap = true; var origin = false
    var bracketed = false; var cursorVisible = true; var insert = false
    var title = ""
    private val parser = Parser(this)
    private val saved = IntArray(6)
    private var cp = 0; private var pending = 0
    var gfx0 = false; var gfx1 = false; var shifted = false

    val inAlt get() = s === alt

    @Synchronized fun feed(b: ByteArray, n: Int) {
        for (i in 0 until n) {
            val v = b[i].toInt() and 0xFF
            when {
                v < 0x80 -> { pending = 0; parser.input(v) }
                v >= 0xC0 -> { pending = if (v >= 0xF0) 3 else if (v >= 0xE0) 2 else 1; cp = v and (0x3F shr pending) }
                pending > 0 -> { cp = (cp shl 6) or (v and 0x3F); if (--pending == 0) parser.input(cp) }
            }
        }
    }

    @Synchronized fun resize(c: Int, r: Int) {
        if (c < 1 || r < 1) return
        main.resize(c, r); alt.resize(c, r)
    }

    fun print(c: Int) {
        if (s.wrapNext) { s.cx = 0; lf(); s.wrapNext = false }
        if (insert) s.insChars(1)
        s.set(s.cx, s.cy, if (gfx(c)) LINES[c - 0x60] else c, pen)
        if (s.cx == s.cols - 1) s.wrapNext = autowrap else s.cx++
    }

    private fun gfx(c: Int) = c in 0x60..0x7E && (if (shifted) gfx1 else gfx0)

    fun lf() { if (s.cy == s.bot) s.scrollUp(1) else if (s.cy < s.rows - 1) s.cy++ }
    fun ri() { if (s.cy == s.top) s.scrollDown(1) else if (s.cy > 0) s.cy-- }

    fun cup(x: Int, y: Int) =
        if (origin) s.setCursor(x, (s.top + y).coerceIn(s.top, s.bot)) else s.setCursor(x, y)

    fun save() { saved[0] = s.cx; saved[1] = s.cy; saved[2] = pen.fg; saved[3] = pen.bg; saved[4] = pen.at; saved[5] = if (origin) 1 else 0 }
    fun restore() {
        s.setCursor(saved[0], saved[1]); pen.fg = saved[2]; pen.bg = saved[3]; pen.at = saved[4]; origin = saved[5] == 1
    }

    fun useAlt(on: Boolean, saveCursor: Boolean) {
        if (on == inAlt) return
        if (on) { if (saveCursor) save(); s = alt; alt.clear(0, alt.cols * alt.rows); alt.top = 0; alt.bot = alt.rows - 1 }
        else { s = main; if (saveCursor) restore() }
    }

    fun reset() {
        pen.reset(); appCursor = false; autowrap = true; origin = false; insert = false
        bracketed = false; cursorVisible = true; gfx0 = false; gfx1 = false; shifted = false
        s = main; for (sc in listOf(main, alt)) { sc.clear(0, sc.cols * sc.rows); sc.top = 0; sc.bot = sc.rows - 1; sc.setCursor(0, 0) }
    }

    /** Copies virtual row [vy] (negative = scrollback) into the scratch arrays. */
    @Synchronized fun copyRow(vy: Int, ch: IntArray, fg: IntArray, bg: IntArray, at: IntArray) {
        val c = s.cols
        if (vy >= 0) {
            for ((k, d) in arrayOf(ch, fg, bg, at).withIndex()) System.arraycopy(s.arrays()[k], vy * c, d, 0, c)
            return
        }
        val i = sb.size + vy
        if (i < 0) { ch.fill(32, 0, c); fg.fill(0, 0, c); bg.fill(0, 0, c); at.fill(0, 0, c); return }
        val l = sb[i]; val n = minOf(c, l.ch.size)
        ch.fill(32, 0, c); fg.fill(0, 0, c); bg.fill(0, 0, c); at.fill(0, 0, c)
        System.arraycopy(l.ch, 0, ch, 0, n); System.arraycopy(l.fg, 0, fg, 0, n)
        System.arraycopy(l.bg, 0, bg, 0, n); System.arraycopy(l.at, 0, at, 0, n)
    }

    companion object {
        private val LINES = "◆▒␉␌␍␊°±␤␋┘┐┌└┼⎺⎻─⎼⎽├┤┴┬│≤≥π≠£·".map { it.code }
    }
}
