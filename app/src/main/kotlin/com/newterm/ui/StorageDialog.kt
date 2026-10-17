package com.newterm.ui

import android.app.Activity
import android.app.AlertDialog
import com.newterm.perm.Permission
import com.newterm.perm.PermissionManager
import com.newterm.storage.SessionStorage
import com.newterm.storage.StorageVolumes
import com.newterm.theme.Theme
import com.newterm.ui.dsl.*

/** Per-session storage access toggles; asks for needed permissions inline. */
object StorageDialog {
    fun show(a: Activity, t: Theme, session: Long) {
        val body = a.vbox(18)
        val dialog = AlertDialog.Builder(a).setView(body).create()
        fun draw() {
            body.removeAllViews()
            body.add(a.label("Storage access", t.text, 20f, true), m = 4)
            body.add(a.label("Takes effect for new terminals.", t.subtext, 13f), m = 4)
            StorageVolumes.list(a).forEach { v ->
                val on = SessionStorage.isMounted(a, session, v)
                val perm = if (v.host == "/") Permission.ALL_FILES else Permission.STORAGE
                body.add(a.chip(t, (if (on) "✓  " else "") + "${v.label}  →  ${v.guest}", on) {
                    if (on || PermissionManager.ensure(a, perm)) {
                        SessionStorage.toggle(a, session, v); draw()
                    }
                }, m = 4)
            }
        }
        draw()
        dialog.window?.setBackgroundDrawable(a.round(t.surface, 20))
        dialog.show()
    }
}
