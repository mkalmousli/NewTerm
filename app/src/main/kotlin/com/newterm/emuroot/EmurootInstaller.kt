package com.newterm.emuroot

import android.content.Context
import android.os.Build
import java.io.File

/** Copies the bundled emuroot binary for this device's ABI into app storage. */
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

    /** Returns the executable, or null when no binary is bundled for this ABI. */
    fun binary(ctx: Context): File? {
        val out = File(ctx.filesDir, "emuroot-${abi()}")
        if (out.canExecute()) return out
        return try {
            ctx.assets.open("emuroot/${abi()}/emuroot").use { i ->
                out.outputStream().use { i.copyTo(it) }
            }
            out.setExecutable(true, false)
            out
        } catch (e: Exception) { null }
    }
}
