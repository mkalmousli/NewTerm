package com.newterm.term

/** xterm 256-colour palette. Colour ints are ARGB; 0 means "theme default". */
object Palette {
    private val ANSI = intArrayOf(0x000000, 0xCD3131, 0x0DBC79, 0xE5E510, 0x2472C8, 0xBC3FBC,
        0x11A8CD, 0xE5E5E5, 0x666666, 0xF14C4C, 0x23D18B, 0xF5F543, 0x3B8EEA, 0xD670D6,
        0x29B8DB, 0xFFFFFF)

    fun color(n: Int): Int = 0xFF000000.toInt() or when {
        n < 16 -> ANSI[n]
        n < 232 -> {
            val i = n - 16
            fun lvl(v: Int) = if (v == 0) 0 else 55 + v * 40
            (lvl(i / 36) shl 16) or (lvl(i / 6 % 6) shl 8) or lvl(i % 6)
        }
        else -> (8 + (n - 232) * 10) * 0x010101
    }

    fun rgb(r: Int, g: Int, b: Int) = 0xFF000000.toInt() or ((r and 255) shl 16) or ((g and 255) shl 8) or (b and 255)
}
