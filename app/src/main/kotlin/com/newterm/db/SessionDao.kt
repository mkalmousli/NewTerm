package com.newterm.db

import android.content.ContentValues
import android.content.Context
import com.newterm.core.Session

class SessionDao(ctx: Context) {
    private val db = Db.get(ctx).writableDatabase

    fun all(): List<Session> = db.rawQuery(
        "SELECT id,name,kind_id,distro_id FROM session ORDER BY id DESC", null
    ).use { c ->
        generateSequence { if (c.moveToNext()) c else null }.map {
            Session(c.getLong(0), c.getString(1), c.getLong(2), if (c.isNull(3)) null else c.getLong(3))
        }.toList()
    }

    fun get(id: Long) = all().first { it.id == id }

    fun insert(name: String, kind: Long, distro: Long?): Long = db.insertOrThrow("session", null,
        ContentValues().apply {
            put("name", name); put("kind_id", kind); put("distro_id", distro)
            put("created_at", System.currentTimeMillis())
        })

    fun rename(id: Long, name: String) =
        db.execSQL("UPDATE session SET name=? WHERE id=?", arrayOf(name, id))

    fun delete(id: Long) = db.execSQL("DELETE FROM session WHERE id=?", arrayOf(id))
}
