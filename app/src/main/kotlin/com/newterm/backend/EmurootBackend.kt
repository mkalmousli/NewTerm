package com.newterm.backend

import android.content.Context
import com.newterm.core.Session
import com.newterm.db.Seeds
import com.newterm.emuroot.EmurootRunner

/** Fake-root over an (empty) rootfs with the Android system bound in. */
class EmurootBackend : Backend {
    override val kindId = Seeds.EMUROOT

    override fun command(ctx: Context, s: Session) =
        EmurootRunner.wrap(ctx, s, EmurootRunner.androidBinds(), listOf("/system/bin/sh"))

    override fun prepare(ctx: Context, s: Session, log: (String) -> Unit) =
        log(EmurootRunner.diagnose(ctx) + "\n")
}
