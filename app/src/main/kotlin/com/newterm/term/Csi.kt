package com.newterm.term

/** CSI final-byte dispatch. [mk] is the private marker ('?', '>', …) or 0. */
object Csi {
    fun run(e: Emulator, mk: Int, p: IntArray, n: Int, f: Int) {
        val s = e.s
        fun a(i: Int, d: Int = 1) = if (i < n && p[i] > 0) p[i] else d
        when (f.toChar()) {
            'A' -> s.setCursor(s.cx, s.cy - a(0))
            'B', 'e' -> s.setCursor(s.cx, s.cy + a(0))
            'C', 'a' -> s.setCursor(s.cx + a(0), s.cy)
            'D' -> s.setCursor(s.cx - a(0), s.cy)
            'E' -> s.setCursor(0, s.cy + a(0))
            'F' -> s.setCursor(0, s.cy - a(0))
            'G', '`' -> s.setCursor(a(0) - 1, s.cy)
            'H', 'f' -> e.cup(a(1) - 1, a(0) - 1)
            'd' -> e.cup(s.cx, a(0) - 1)
            'J' -> s.eraseDisplay(a(0, 0))
            'K' -> s.eraseLine(a(0, 0))
            'L' -> s.insLines(a(0))
            'M' -> s.delLines(a(0))
            'P' -> s.delChars(a(0))
            '@' -> s.insChars(a(0))
            'X' -> { val i = s.cy * s.cols + s.cx; s.clear(i, minOf(i + a(0), (s.cy + 1) * s.cols)) }
            'S' -> s.scrollUp(a(0))
            'T' -> s.scrollDown(a(0))
            'm' -> if (mk == 0) e.pen.sgr(p, n)
            'r' -> {
                val t = a(0) - 1; val b = a(1, s.rows) - 1
                if (t < b && b < s.rows) { s.top = t; s.bot = b; e.cup(0, 0) }
            }
            's' -> e.save()
            'u' -> e.restore()
            'h' -> Modes.set(e, mk == '?'.code, p, n, true)
            'l' -> Modes.set(e, mk == '?'.code, p, n, false)
            'n' -> if (a(0, 0) == 5) e.reply("\u001B[0n") else if (a(0, 0) == 6)
                e.reply("\u001B[${s.cy + 1 - (if (e.origin) s.top else 0)};${s.cx + 1}R")
            'c' -> e.reply(if (mk == '>'.code) "\u001B[>0;115;0c" else "\u001B[?62;c")
        }
    }
}
