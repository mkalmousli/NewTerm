package com.newterm.backend

import android.content.Context
import com.newterm.core.Session
import com.newterm.db.DistroDao
import com.newterm.db.Seeds
import com.newterm.distro.DistroInstaller
import com.newterm.emuroot.EmurootRunner

/** Linux distro rootfs (downloaded on first run) under emuroot. */
class DistroBackend : Backend {
    override val kindId = Seeds.DISTRO
    private val binds = listOf("/proc" to "/proc", "/dev" to "/dev")

    override fun prepare(ctx: Context, s: Session, log: (String) -> Unit) {
        val d = DistroDao(ctx).get(s.distroId ?: return) ?: return
        DistroInstaller.ensure(ctx, s, d, log)
    }

    override fun command(ctx: Context, s: Session) =
        EmurootRunner.wrap(ctx, s, binds, listOf("/bin/sh", "-l"))

    override fun env(ctx: Context, s: Session) = super.env(ctx, s) + ("HOME" to "/root")
}
