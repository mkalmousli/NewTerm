package com.newterm.term

/** DECSET/DECRST and SM/RM mode switches. */
object Modes {
    fun set(e: Emulator, priv: Boolean, p: IntArray, n: Int, on: Boolean) {
        for (i in 0 until n) {
            if (!priv) { if (p[i] == 4) e.insert = on; continue }
            when (p[i]) {
                1 -> e.appCursor = on
                6 -> { e.origin = on; e.cup(0, 0) }
                7 -> e.autowrap = on
                25 -> e.cursorVisible = on
                47, 1047 -> e.useAlt(on, false)
                1049 -> e.useAlt(on, true)
                2004 -> e.bracketed = on
            }
        }
    }
}
