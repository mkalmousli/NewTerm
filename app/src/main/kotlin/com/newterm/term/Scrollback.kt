package com.newterm.term

/** History of lines scrolled off the top of the main screen. */
class Scrollback(private val max: Int) {
    class Line(val ch: IntArray, val fg: IntArray, val bg: IntArray, val at: IntArray)

    private val lines = ArrayDeque<Line>()
    val size get() = lines.size

    fun push(s: Screen, row: Int) {
        val a = row * s.cols
        val b = a + s.cols
        lines.addLast(Line(s.ch.copyOfRange(a, b), s.fg.copyOfRange(a, b), s.bg.copyOfRange(a, b), s.at.copyOfRange(a, b)))
        if (lines.size > max) lines.removeFirst()
    }

    /** [i] counts from the oldest line. */
    operator fun get(i: Int) = lines[i]
    fun clear() = lines.clear()
}
