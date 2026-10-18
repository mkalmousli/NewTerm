package com.newterm.emuroot

import android.content.Context
import android.os.Build
import java.io.File

/** Copies bundled native helpers (emuroot, ptyhelper) for this device's ABI into app storage. */
object EmurootInstaller {
    fun abi(): String {
        val a = Build.CPU_ABI.lowercase()
        return when {
            "arm64" in a -> "arm64"
            "armeabi" in a || "arm" in a -> "arm"
            "x86_64" in a -> "x86_64"
            else -> "x86"
        }
    }

    fun binary(ctx: Context): File? = asset(ctx, "emuroot")

    /** Returns the executable, or null when none is bundled for this ABI. Re-copied per app update. */
    fun asset(ctx: Context, name: String): File? {
        val stamp = ctx.packageManager.getPackageInfo(ctx.packageName, 0).lastUpdateTime
        val out = File(ctx.filesDir, "$name-${abi()}-$stamp")
        if (out.canExecute()) return out
        ctx.filesDir.listFiles()?.filter { it.name.startsWith("$name-${abi()}-") }?.forEach { it.delete() }
        return try {
            ctx.assets.open("emuroot/${abi()}/$name").use { i -> out.outputStream().use { i.copyTo(it) } }
            out.setExecutable(true, false)
            out
        } catch (e: Exception) { null }
    }
}
