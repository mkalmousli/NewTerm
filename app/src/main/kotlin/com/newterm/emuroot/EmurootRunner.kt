package com.newterm.emuroot

import android.content.Context
import com.newterm.core.Session
import com.newterm.db.BindDao
import java.io.File

/** Builds `emuroot -r ROOT -b ... -u 0:0 -- CMD` for a session. */
object EmurootRunner {
    /** Android system dirs the platform shell and its linker/libc need (those that exist). */
    fun androidBinds() = listOf("/system", "/apex", "/linkerconfig", "/vendor", "/odm",
        "/product", "/system_ext", "/proc", "/dev").filter { File(it).exists() }.map { it to it }

    fun wrap(ctx: Context, s: Session, base: List<Pair<String, String>>, cmd: List<String>): List<String> {
        val bin = EmurootInstaller.binary(ctx)
            ?: return listOf("/system/bin/sh", "-c", "echo 'emuroot binary missing for ${EmurootInstaller.abi()}'; exec sh")
        val binds = (base + BindDao(ctx).all(s.id)).flatMap { (h, g) -> listOf("-b", "$h:$g") }
        return listOf(bin.path, "-r", s.rootfs(ctx).path) + binds + listOf("-u", "0:0", "--") + cmd
    }

    /** One-line summary of what this device lets emuroot do (`emuroot -D`). */
    fun diagnose(ctx: Context): String = try {
        val bin = EmurootInstaller.binary(ctx) ?: return "emuroot: no binary for ${EmurootInstaller.abi()}"
        val p = ProcessBuilder(bin.path, "-D").redirectErrorStream(true).start()
        val lines = p.inputStream.bufferedReader().readLines()
        "emuroot[${EmurootInstaller.abi()}]: " + (lines.lastOrNull { it.startsWith("=>") } ?: lines.joinToString(" | "))
    } catch (e: Exception) { "emuroot: diagnose failed: ${e.message}" }
}
