package com.newterm.db

import android.content.Context
import com.newterm.distro.Distro

class DistroDao(ctx: Context) {
    private val db = Db.get(ctx).readableDatabase

    fun all(): List<Distro> = db.rawQuery(
        "SELECT id,name,url_arm,url_arm64,url_x86,url_x86_64 FROM distro ORDER BY name", null
    ).use { c ->
        generateSequence { if (c.moveToNext()) c else null }.map {
            Distro(c.getLong(0), c.getString(1), listOf(2, 3, 4, 5).map(c::getString))
        }.toList()
    }

    fun get(id: Long): Distro? = all().firstOrNull { it.id == id }
}
