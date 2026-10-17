package com.newterm.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/** Single app database. Use `Db.get(ctx).writableDatabase` from DAOs. */
class Db private constructor(ctx: Context) : SQLiteOpenHelper(ctx, "newterm.db", null, 1) {
    override fun onCreate(db: SQLiteDatabase) {
        Schema.TABLES.forEach(db::execSQL)
        Seeds.apply(db)
    }

    override fun onOpen(db: SQLiteDatabase) {
        if (!db.isReadOnly) db.execSQL("PRAGMA foreign_keys=ON")
    }

    override fun onUpgrade(db: SQLiteDatabase, from: Int, to: Int) {}

    companion object {
        @Volatile private var inst: Db? = null
        fun get(ctx: Context): Db = inst ?: synchronized(this) {
            inst ?: Db(ctx.applicationContext).also { inst = it }
        }
    }
}
