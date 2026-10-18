package com.newterm.term

import kotlin.math.min

/** Scrolling and insert/delete operations on a [Screen]. */
private fun Screen.moveRows(from: Int, to: Int, count: Int) {
    if (count <= 0) return
    for (a in arrays()) System.arraycopy(a, from * cols, a, to * cols, count * cols)
}

fun Screen.scrollUp(n: Int) {
    val k = min(n, bot - top + 1)
    if (top == 0) sb?.let { for (i in 0 until k) it.push(this, i) }
    moveRows(top + k, top, bot - top + 1 - k)
    clear((bot - k + 1) * cols, (bot + 1) * cols)
}

fun Screen.scrollDown(n: Int) {
    val k = min(n, bot - top + 1)
    moveRows(top, top + k, bot - top + 1 - k)
    clear(top * cols, (top + k) * cols)
}

fun Screen.insLines(n: Int) {
    if (cy < top || cy > bot) return
    val k = min(n, bot - cy + 1)
    moveRows(cy, cy + k, bot - cy + 1 - k)
    clear(cy * cols, (cy + k) * cols)
}

fun Screen.delLines(n: Int) {
    if (cy < top || cy > bot) return
    val k = min(n, bot - cy + 1)
    moveRows(cy + k, cy, bot - cy + 1 - k)
    clear((bot - k + 1) * cols, (bot + 1) * cols)
}

fun Screen.insChars(n: Int) {
    val k = min(n, cols - cx); val base = cy * cols + cx
    for (a in arrays()) System.arraycopy(a, base, a, base + k, cols - cx - k)
    clear(base, base + k)
}

fun Screen.delChars(n: Int) {
    val k = min(n, cols - cx); val base = cy * cols + cx
    for (a in arrays()) System.arraycopy(a, base + k, a, base, cols - cx - k)
    clear(cy * cols + cols - k, cy * cols + cols)
}

fun Screen.eraseLine(mode: Int) {
    val r = cy * cols
    when (mode) { 0 -> clear(r + cx, r + cols); 1 -> clear(r, r + cx + 1); else -> clear(r, r + cols) }
}

fun Screen.eraseDisplay(mode: Int) {
    val r = cy * cols
    when (mode) {
        0 -> clear(r + cx, rows * cols)
        1 -> clear(0, r + cx + 1)
        else -> { clear(0, rows * cols); if (mode == 3) sb?.clear() }
    }
}
