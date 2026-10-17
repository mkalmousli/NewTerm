package com.newterm.core

import android.content.Context
import java.io.File

/** A isolated workspace. Everything it owns lives under [dir]. */
data class Session(val id: Long, val name: String, val kindId: Long, val distroId: Long?) {
    fun dir(ctx: Context) = File(ctx.filesDir, "sessions/$id").apply { mkdirs() }
    fun rootfs(ctx: Context) = File(dir(ctx), "rootfs").apply { mkdirs() }
    fun home(ctx: Context) = File(dir(ctx), "home").apply { mkdirs() }
}
