package com.newterm.ui

import android.content.Intent
import android.view.View
import com.newterm.backend.BackendRegistry
import com.newterm.db.SessionDao
import com.newterm.theme.Theme
import com.newterm.ui.dsl.*

/** Session list. Tap a card to open it. */
class MainActivity : BaseActivity() {
    override fun build(t: Theme): View {
        val list = vbox()
        list.add(hbox {
            add(label("NewTerm", t.text, 26f, true), 1f)
            add(button(t, "⚙") { startActivity(Intent(this@MainActivity, SettingsActivity::class.java)) }, width = WRAP)
        }, m = 4)
        val sessions = SessionDao(this).all()
        if (sessions.isEmpty()) list.add(label("No sessions yet. Create one below.", t.subtext), m = 12)
        sessions.forEach { s ->
            val kind = listOf("Blank", "Emuroot", "Distro")[BackendRegistry.of(s).kindId.toInt() - 1]
            list.add(card(t, { open(s.id) }) {
                add(label(s.name, t.text, 18f, true))
                add(label(kind, t.subtext, 13f), m = 2)
            }, m = 6)
        }
        list.add(button(t, "＋  New session", true) { NewSessionDialog.show(this, t) { open(it) } }, m = 6)
        return page(list)
    }

    private fun open(id: Long) = startActivity(
        Intent(this, SessionActivity::class.java).putExtra("session", id))
}
