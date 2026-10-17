package com.newterm.backend

import android.content.Context
import com.newterm.core.Session
import com.newterm.db.Seeds
import com.newterm.emuroot.EmurootRunner

/** Fake-root over an (empty) rootfs with the Android system bound in. */
class EmurootBackend : Backend {
    override val kindId = Seeds.EMUROOT
    private val binds = listOf("/system" to "/system", "/proc" to "/proc", "/dev" to "/dev")

    override fun command(ctx: Context, s: Session) =
        EmurootRunner.wrap(ctx, s, binds, listOf("/system/bin/sh"))
}
