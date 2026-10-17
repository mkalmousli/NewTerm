package com.newterm.util

import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.ForegroundColorSpan

/** Converts text with ANSI SGR colour codes to spans; strips other escapes. */
object Ansi {
    private val CSI = Regex("\u001B\\[([0-9;?]*)([A-Za-z])")
    private val PALETTE = intArrayOf(0xFF6E7681.toInt(), 0xFFE5534B.toInt(), 0xFF57AB5A.toInt(),
        0xFFC69026.toInt(), 0xFF539BF5.toInt(), 0xFFB083F0.toInt(), 0xFF39C5CF.toInt(), 0xFFADBAC7.toInt())

    fun render(raw: String): CharSequence {
        val out = SpannableStringBuilder()
        var color = 0
        var start = 0
        var last = 0
        fun flush() {
            if (color != 0 && out.length > start) {
                out.setSpan(ForegroundColorSpan(color), start, out.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
            }
            start = out.length
        }
        for (m in CSI.findAll(raw)) {
            out.append(raw, last, m.range.first); last = m.range.last + 1
            if (m.groupValues[2] != "m") continue
            flush()
            for (c in m.groupValues[1].split(';')) {
                val n = c.toIntOrNull() ?: 0
                color = when (n) { 0, 39 -> 0; in 30..37 -> PALETTE[n - 30]; in 90..97 -> PALETTE[n - 90]; else -> color }
            }
        }
        out.append(raw, last, raw.length); flush()
        return out
    }
}
