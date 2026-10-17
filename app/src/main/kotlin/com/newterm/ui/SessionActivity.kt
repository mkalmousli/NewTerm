package com.newterm.ui

import android.view.View
import com.newterm.core.SessionManager
import com.newterm.db.SessionDao
import com.newterm.theme.Theme
import com.newterm.ui.dsl.*
import com.newterm.db.SettingDao

/** One session: tab bar of terminals, output view, input line. */
class SessionActivity : BaseActivity() {
    private var selected = 0

    override fun build(t: Theme): View {
        val session = SessionDao(this).get(intent.getLongExtra("session", 0))
        val tabs = SessionManager.terminals(this, session)
        selected = selected.coerceIn(0, tabs.size - 1)
        val term = tabs[selected]
        return vbox(12) {
            add(hbox {
                add(button(t, "‹") { finish() }, width = WRAP)
                add(label(session.name, t.text, 20f, true), 1f, 8)
                add(button(t, "Storage") { StorageDialog.show(this@SessionActivity, t, session.id) }, width = WRAP)
                add(button(t, "Delete") {
                    SessionManager.deleteSession(this@SessionActivity, session); finish()
                }, m = 4, width = WRAP)
            }, m = 2)
            add(TabBar(this@SessionActivity, t, tabs, selected,
                { selected = it; onResume() },
                { selected = tabs.size; SessionManager.addTerminal(this@SessionActivity, session); onResume() },
                { SessionManager.closeTerminal(this@SessionActivity, term); selected = 0; onResume() }), m = 2)
            add(TerminalView(this@SessionActivity, t, SettingDao(this@SessionActivity).int("font_size"), term),
                1f, 2, height = MATCH)
            add(InputBar(this@SessionActivity, t) { term.send(it) }, m = 2)
        }
    }
}
