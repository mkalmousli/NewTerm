package com.newterm.theme

/** Colors are ARGB ints. Add a field here, set it in [Themes]. */
data class Theme(
    val bg: Int, val surface: Int, val text: Int, val subtext: Int,
    val accent: Int, val onAccent: Int, val border: Int,
    val termBg: Int, val termFg: Int, val danger: Int,
)
