package com.newterm.ui

import android.view.View
import com.newterm.perm.Permission
import com.newterm.perm.PermissionManager
import com.newterm.theme.Theme
import com.newterm.ui.dsl.*

/** Lists each [Permission] with a one-tap grant. */
class PermissionsActivity : BaseActivity() {
    override fun build(t: Theme): View {
        val col = vbox()
        col.add(hbox {
            add(button(t, "‹") { finish() }, width = WRAP)
            add(label("Permissions", t.text, 22f, true), m = 8)
        }, m = 4)
        Permission.values().forEach { p ->
            val ok = PermissionManager.granted(this, p)
            col.add(card(t) {
                add(hbox {
                    add(vbox {
                        add(label(p.title, t.text, 16f, true))
                        add(label(p.why, t.subtext, 13f))
                    }, 1f)
                    if (ok) add(label("Granted", t.accent, 14f, true), width = WRAP)
                    else add(button(t, "Grant", true) { PermissionManager.ensure(this@PermissionsActivity, p) }, width = WRAP)
                })
            }, m = 6)
        }
        return page(col)
    }

    override fun onRequestPermissionsResult(c: Int, p: Array<out String>, r: IntArray) = onResume()
}
