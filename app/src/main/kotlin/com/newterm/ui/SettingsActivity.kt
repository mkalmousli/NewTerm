package com.newterm.ui

import android.content.Intent
import android.view.View
import com.newterm.db.SettingDao
import com.newterm.theme.Theme
import com.newterm.theme.ThemeStore
import com.newterm.ui.dsl.*

class SettingsActivity : BaseActivity() {
    override fun build(t: Theme): View {
        val s = SettingDao(this)
        val col = vbox()
        col.add(hbox {
            add(button(t, "‹") { finish() }, width = WRAP)
            add(label("Settings", t.text, 22f, true), m = 8)
        }, m = 4)
        col.add(card(t) {
            add(label("Appearance", t.text, 16f, true))
            val i = ThemeStore.MODES.indexOf(s.get("theme_mode"))
            add(chipRow(t, ThemeStore.MODES.map { it.replaceFirstChar(Char::uppercase) }, i) {
                s.set("theme_mode", ThemeStore.MODES[it]); onResume()
            }, m = 4)
            add(label("Terminal font size: ${s.int("font_size")}", t.subtext, 13f), m = 4)
            add(hbox {
                add(button(t, "−") { s.set("font_size", "${maxOf(8, s.int("font_size") - 1)}"); onResume() }, width = WRAP)
                add(button(t, "+") { s.set("font_size", "${minOf(28, s.int("font_size") + 1)}"); onResume() }, m = 6, width = WRAP)
            })
        }, m = 6)
        col.add(card(t, { startActivity(Intent(this, PermissionsActivity::class.java)) }) {
            add(label("Permissions", t.text, 16f, true))
            add(label("Storage and all-files access", t.subtext, 13f))
        }, m = 6)
        return page(col)
    }
}
