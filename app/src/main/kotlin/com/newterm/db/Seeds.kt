package com.newterm.db

import android.database.sqlite.SQLiteDatabase

/** Initial rows. Add a distro = one `distro(...)` line. */
object Seeds {
    const val BLANK = 1L
    const val EMUROOT = 2L
    const val DISTRO = 3L
    private const val ALP = "https://dl-cdn.alpinelinux.org/alpine/v3.20/releases"

    fun apply(db: SQLiteDatabase) {
        listOf(BLANK to "blank", EMUROOT to "emuroot", DISTRO to "distro").forEach { (id, n) ->
            db.execSQL("INSERT INTO backend_kind VALUES(?,?)", arrayOf(id, n))
        }
        distro(db, "Alpine", "$ALP/armv7/alpine-minirootfs-3.20.3-armv7.tar.gz",
            "$ALP/aarch64/alpine-minirootfs-3.20.3-aarch64.tar.gz",
            "$ALP/x86/alpine-minirootfs-3.20.3-x86.tar.gz",
            "$ALP/x86_64/alpine-minirootfs-3.20.3-x86_64.tar.gz")
        listOf("theme_mode" to "system", "font_size" to "13").forEach { (k, v) ->
            db.execSQL("INSERT INTO setting_def VALUES(?,?)", arrayOf(k, v))
        }
    }

    private fun distro(db: SQLiteDatabase, name: String, vararg urls: String) =
        db.execSQL("INSERT INTO distro(name,url_arm,url_arm64,url_x86,url_x86_64) VALUES(?,?,?,?,?)",
            arrayOf(name, *urls))
}
