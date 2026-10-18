package com.newterm.term

/** Character grid + cursor + scroll region. Mutations live here and in ScreenOps. */
class Screen(var cols: Int, var rows: Int) {
    var ch = IntArray(cols * rows) { 32 }
    var fg = IntArray(cols * rows)
    var bg = IntArray(cols * rows)
    var at = IntArray(cols * rows)
    var cx = 0
    var cy = 0
    var top = 0
    var bot = rows - 1
    var wrapNext = false
    var sb: Scrollback? = null

    fun clear(from: Int, to: Int, bgColor: Int = 0) {
        ch.fill(32, from, to); fg.fill(0, from, to); bg.fill(bgColor, from, to); at.fill(0, from, to)
    }

    fun set(x: Int, y: Int, cp: Int, pen: Pen) {
        val i = y * cols + x
        ch[i] = cp; fg[i] = pen.fg; bg[i] = pen.bg; at[i] = pen.at
    }

    fun setCursor(x: Int, y: Int) {
        cx = x.coerceIn(0, cols - 1); cy = y.coerceIn(0, rows - 1); wrapNext = false
    }

    fun arrays() = arrayOf(ch, fg, bg, at)

    fun resize(nc: Int, nr: Int) {
        val old = arrays()
        val oc = cols
        val orows = rows
        cols = nc; rows = nr
        ch = IntArray(nc * nr) { 32 }; fg = IntArray(nc * nr); bg = IntArray(nc * nr); at = IntArray(nc * nr)
        val shift = (cy - nr + 1).coerceAtLeast(0)   // keep cursor line visible when shrinking
        for (y in 0 until minOf(orows - shift, nr)) for ((k, a) in arrays().withIndex()) {
            System.arraycopy(old[k], (y + shift) * oc, a, y * nc, minOf(oc, nc))
        }
        top = 0; bot = nr - 1
        setCursor(cx, cy - shift)
    }
}
