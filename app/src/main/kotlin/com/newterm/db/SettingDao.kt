package com.newterm.db

import android.content.Context

/** Typed access to `setting` falling back to `setting_def`. */
class SettingDao(ctx: Context) {
    private val db = Db.get(ctx).writableDatabase

    fun get(key: String): String {
        db.rawQuery("SELECT value FROM setting WHERE key=?", arrayOf(key)).use {
            if (it.moveToFirst()) return it.getString(0)
        }
        db.rawQuery("SELECT default_value FROM setting_def WHERE key=?", arrayOf(key)).use {
            return if (it.moveToFirst()) it.getString(0) else ""
        }
    }

    fun set(key: String, value: String) =
        db.execSQL("INSERT OR REPLACE INTO setting VALUES(?,?)", arrayOf(key, value))

    fun int(key: String) = get(key).toIntOrNull() ?: 0
}
