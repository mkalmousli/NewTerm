package com.newterm.ui

import android.app.AlertDialog
import android.content.Context
import com.newterm.db.DistroDao
import com.newterm.db.Seeds
import com.newterm.db.SessionDao
import com.newterm.theme.Theme
import com.newterm.ui.dsl.*

/** Name + type + distro picker. Calls [done] with the new session id. */
object NewSessionDialog {
    private val KINDS = listOf("Blank" to Seeds.BLANK, "Emuroot" to Seeds.EMUROOT, "Distro" to Seeds.DISTRO)

    fun show(ctx: Context, t: Theme, done: (Long) -> Unit) {
        val distros = DistroDao(ctx).all()
        var kind = 1; var distro = 0
        val name = ctx.input(t, "Session name")
        val body = ctx.vbox(18)
        lateinit var dialog: AlertDialog
        fun draw() {
            body.removeAllViews()
            body.add(ctx.label("New session", t.text, 20f, true), m = 4)
            body.add(name, m = 4)
            body.add(ctx.chipRow(t, KINDS.map { it.first }, kind) { kind = it; draw() }, m = 4)
            if (KINDS[kind].second == Seeds.DISTRO) {
                body.add(ctx.chipRow(t, distros.map { it.name }, distro) { distro = it; draw() }, m = 4)
            }
            body.add(ctx.button(t, "Create", true) {
                val n = name.text.toString().ifBlank { "Session" }
                val d = if (KINDS[kind].second == Seeds.DISTRO) distros[distro].id else null
                dialog.dismiss(); done(SessionDao(ctx).insert(n, KINDS[kind].second, d))
            }, m = 4)
        }
        draw()
        body.background = ctx.round(t.surface, 20)
        dialog = AlertDialog.Builder(ctx).setView(body).create()
        dialog.window?.setBackgroundDrawable(ctx.round(t.surface, 20))
        dialog.show()
    }
}
