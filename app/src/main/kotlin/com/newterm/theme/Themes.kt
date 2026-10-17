package com.newterm.theme

object Themes {
    val LIGHT = Theme(
        bg = 0xFFF4F5F7.toInt(), surface = 0xFFFFFFFF.toInt(), text = 0xFF15171A.toInt(),
        subtext = 0xFF6B7280.toInt(), accent = 0xFF4F46E5.toInt(), onAccent = 0xFFFFFFFF.toInt(),
        border = 0xFFE3E5E8.toInt(), termBg = 0xFFFAFAFA.toInt(), termFg = 0xFF1F2328.toInt(),
        danger = 0xFFDC2626.toInt())
    val DARK = Theme(
        bg = 0xFF0E1013.toInt(), surface = 0xFF181B20.toInt(), text = 0xFFE8EAED.toInt(),
        subtext = 0xFF9AA0A6.toInt(), accent = 0xFF818CF8.toInt(), onAccent = 0xFF0E1013.toInt(),
        border = 0xFF2A2E35.toInt(), termBg = 0xFF0A0C0E.toInt(), termFg = 0xFFD7DAE0.toInt(),
        danger = 0xFFF87171.toInt())
}
