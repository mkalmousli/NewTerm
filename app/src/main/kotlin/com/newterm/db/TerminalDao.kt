package com.newterm.db

import android.content.ContentValues
import android.content.Context

/** Persisted terminal tabs (title + order) of a session. */
class TerminalDao(ctx: Context) {
    private val db = Db.get(ctx).writableDatabase

    fun ids(session: Long): List<Pair<Long, String>> = db.rawQuery(
        "SELECT id,title FROM terminal WHERE session_id=? ORDER BY position", arrayOf("$session")
    ).use { c ->
        generateSequence { if (c.moveToNext()) c.getLong(0) to c.getString(1) else null }.toList()
    }

    fun insert(session: Long, title: String): Long = db.insertOrThrow("terminal", null,
        ContentValues().apply {
            put("session_id", session); put("title", title)
            put("position", ids(session).size)
        })

    fun delete(id: Long) = db.execSQL("DELETE FROM terminal WHERE id=?", arrayOf(id))
}
