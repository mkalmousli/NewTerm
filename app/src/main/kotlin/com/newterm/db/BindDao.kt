package com.newterm.db

import android.content.Context

/** Per-session storage exposure: host path -> guest path. */
class BindDao(ctx: Context) {
    private val db = Db.get(ctx).writableDatabase

    fun all(session: Long): List<Pair<String, String>> = db.rawQuery(
        "SELECT host_path,guest_path FROM bind_mount WHERE session_id=? ORDER BY guest_path",
        arrayOf("$session")
    ).use { c ->
        generateSequence { if (c.moveToNext()) c.getString(0) to c.getString(1) else null }.toList()
    }

    fun add(session: Long, host: String, guest: String) = db.execSQL(
        "INSERT OR REPLACE INTO bind_mount(session_id,host_path,guest_path) VALUES(?,?,?)",
        arrayOf(session, host, guest))

    fun remove(session: Long, guest: String) = db.execSQL(
        "DELETE FROM bind_mount WHERE session_id=? AND guest_path=?", arrayOf(session, guest))
}
