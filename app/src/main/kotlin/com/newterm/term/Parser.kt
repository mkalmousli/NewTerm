package com.newterm.term

/** Escape-sequence tokenizer feeding [Emulator]. Input is already UTF-8 decoded. */
class Parser(private val e: Emulator) {
    private enum class St { GROUND, ESC, CSI, OSC, STR, CHARSET0, CHARSET1, SKIP }
    private var st = St.GROUND
    private val p = IntArray(16)
    private var n = 0; private var cur = 0; private var have = false; private var mk = 0
    private val osc = StringBuilder()

    fun input(c: Int) {
        if (c == 0x1B) { if (st == St.OSC) endOsc(); st = St.ESC; return }
        if (c == 0x18 || c == 0x1A) { st = St.GROUND; return }
        if (c < 0x20 && st != St.OSC) { c0(c); return }
        when (st) {
            St.GROUND -> if (c >= 0x20 && c != 0x7F) e.print(c)
            St.ESC -> esc(c)
            St.CSI -> csi(c)
            St.OSC -> if (c == 7) { endOsc(); st = St.GROUND } else if (osc.length < 512) osc.appendCodePoint(c)
            St.STR -> if (c == 7) st = St.GROUND
            St.CHARSET0 -> { e.gfx0 = c == '0'.code; st = St.GROUND }
            St.CHARSET1 -> { e.gfx1 = c == '0'.code; st = St.GROUND }
            St.SKIP -> st = St.GROUND
        }
    }

    private fun c0(c: Int) {
        val s = e.s
        when (c) {
            8 -> s.setCursor(s.cx - 1, s.cy)
            9 -> s.setCursor(minOf((s.cx / 8 + 1) * 8, s.cols - 1), s.cy)
            10, 11, 12 -> { e.lf(); s.wrapNext = false }
            13 -> s.setCursor(0, s.cy)
            14 -> e.shifted = true
            15 -> e.shifted = false
        }
    }

    private fun esc(c: Int) {
        st = St.GROUND
        when (c.toChar()) {
            '[' -> { st = St.CSI; n = 0; cur = 0; have = false; mk = 0 }
            ']' -> { st = St.OSC; osc.setLength(0) }
            'P', 'X', '^', '_' -> st = St.STR
            '(' -> st = St.CHARSET0
            ')' -> st = St.CHARSET1
            '#', '%' -> st = St.SKIP
            '7' -> e.save()
            '8' -> e.restore()
            'D' -> e.lf()
            'E' -> { e.s.cx = 0; e.lf() }
            'M' -> e.ri()
            'c' -> e.reset()
        }
    }

    private fun csi(c: Int) {
        when {
            c in 0x30..0x39 -> { cur = minOf(cur * 10 + (c - 0x30), 99999); have = true }
            c == ';'.code || c == ':'.code -> { if (n < p.size) p[n++] = cur; cur = 0; have = false }
            c in 0x3C..0x3F -> mk = c
            c in 0x20..0x2F -> {}
            c in 0x40..0x7E -> {
                if (have || n > 0) { if (n < p.size) p[n++] = cur }
                st = St.GROUND
                Csi.run(e, mk, p, n, c)
            }
            else -> st = St.GROUND
        }
    }

    private fun endOsc() {
        val t = osc.toString()
        if (t.startsWith("0;") || t.startsWith("2;")) e.title = t.substring(2)
    }
}
