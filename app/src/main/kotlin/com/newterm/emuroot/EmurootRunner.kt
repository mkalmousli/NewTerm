package com.newterm.emuroot

import android.content.Context
import com.newterm.core.Session
import com.newterm.db.BindDao

/** Builds `emuroot -r ROOT -b ... -u 0:0 -- CMD` for a session. */
object EmurootRunner {
    fun wrap(ctx: Context, s: Session, base: List<Pair<String, String>>, cmd: List<String>): List<String> {
        val bin = EmurootInstaller.binary(ctx)
            ?: return listOf("/system/bin/sh", "-c", "echo 'emuroot binary missing for ${EmurootInstaller.abi()}'; exec sh")
        val binds = (base + BindDao(ctx).all(s.id)).flatMap { (h, g) -> listOf("-b", "$h:$g") }
        return listOf(bin.path, "-r", s.rootfs(ctx).path) + binds + listOf("-u", "0:0", "--") + cmd
    }
}
