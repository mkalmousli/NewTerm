package com.newterm.term

import org.junit.Assert.assertEquals
import org.junit.Test

class EmulatorTest {
    private fun emu(cols: Int = 10, rows: Int = 4) = Emulator(cols, rows) { }
    private fun Emulator.type(s: String) { val b = s.toByteArray(); feed(b, b.size) }
    private fun Emulator.row(y: Int) = String(IntArray(s.cols) { s.ch[y * s.cols + it] }, 0, s.cols).trimEnd()

    @Test fun printsAndWraps() {
        val e = emu(); e.type("hello world!")
        assertEquals("hello worl", e.row(0)); assertEquals("d!", e.row(1))
    }

    @Test fun cursorMoveAndErase() {
        val e = emu(); e.type("abc\u001B[2;3Hx\u001B[1;2H\u001B[K")
        assertEquals("a", e.row(0)); assertEquals("  x", e.row(1))
    }

    @Test fun scrollsIntoHistory() {
        val e = emu(); e.type("1\r\n2\r\n3\r\n4\r\n5")
        assertEquals("2", e.row(0)); assertEquals(1, e.sb.size)
    }

    @Test fun sgrColors() {
        val e = emu(); e.type("\u001B[1;31mR\u001B[0mN")
        assertEquals(Pen.BOLD, e.s.at[0]); assertEquals(Palette.color(1), e.s.fg[0]); assertEquals(0, e.s.fg[1])
    }

    @Test fun altScreenRestoresMain() {
        val e = emu(); e.type("main\u001B[?1049h\u001B[Halt\u001B[?1049l")
        assertEquals("main", e.row(0))
    }

    @Test fun utf8AndReplies() {
        var out = ""; val e = Emulator(10, 4) { out += it }
        e.type("é\u001B[6n"); assertEquals("é", e.row(0)); assertEquals("\u001B[1;2R", out)
    }
}
