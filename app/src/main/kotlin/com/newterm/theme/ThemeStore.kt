package com.newterm.theme

import android.content.Context
import android.content.res.Configuration
import com.newterm.db.SettingDao

/** Resolves the active [Theme] from the `theme_mode` setting (system/light/dark). */
object ThemeStore {
    val MODES = listOf("system", "light", "dark")

    fun current(ctx: Context): Theme = when (SettingDao(ctx).get("theme_mode")) {
        "light" -> Themes.LIGHT
        "dark" -> Themes.DARK
        else -> {
            val night = ctx.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
            if (night == Configuration.UI_MODE_NIGHT_YES) Themes.DARK else Themes.LIGHT
        }
    }
}
