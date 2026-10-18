package com.newterm.term

/** Current drawing attributes (SGR state). */
class Pen {
    var fg = 0
    var bg = 0
    var at = 0

    fun reset() { fg = 0; bg = 0; at = 0 }

    fun sgr(p: IntArray, n: Int) {
        if (n == 0) return reset()
        var i = 0
        while (i < n) {
            val c = p[i]
            when {
                c == 0 -> reset()
                c in 1..9 && c != 5 && c != 6 && c != 8 -> at = at or BIT[c]
                c == 22 -> at = at and (BOLD or DIM).inv()
                c == 23 -> at = at and ITALIC.inv()
                c == 24 -> at = at and UNDER.inv()
                c == 27 -> at = at and INV.inv()
                c == 29 -> at = at and STRIKE.inv()
                c in 30..37 -> fg = Palette.color(c - 30)
                c in 90..97 -> fg = Palette.color(c - 90 + 8)
                c in 40..47 -> bg = Palette.color(c - 40)
                c in 100..107 -> bg = Palette.color(c - 100 + 8)
                c == 39 -> fg = 0
                c == 49 -> bg = 0
                c == 38 || c == 48 -> {
                    val mode = p.getOrElse(i + 1) { 0 }
                    val col = if (mode == 5) Palette.color(p.getOrElse(i + 2) { 0 })
                    else Palette.rgb(p.getOrElse(i + 2) { 0 }, p.getOrElse(i + 3) { 0 }, p.getOrElse(i + 4) { 0 })
                    if (c == 38) fg = col else bg = col
                    i += if (mode == 5) 2 else 4
                }
            }
            i++
        }
    }

    companion object {
        const val BOLD = 1; const val DIM = 2; const val ITALIC = 4
        const val UNDER = 8; const val INV = 16; const val STRIKE = 32
        // SGR code -> attribute bit (1 bold, 2 dim, 3 italic, 4 underline, 7 inverse, 9 strike)
        private val BIT = intArrayOf(0, BOLD, DIM, ITALIC, UNDER, 0, 0, INV, 0, STRIKE)
    }
}
