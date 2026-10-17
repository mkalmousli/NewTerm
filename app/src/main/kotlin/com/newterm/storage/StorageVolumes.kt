package com.newterm.storage

import android.content.Context
import android.os.Build
import android.os.Environment

object StorageVolumes {
    fun list(ctx: Context): List<Volume> {
        val out = mutableListOf(Volume("Internal storage",
            Environment.getExternalStorageDirectory().path, "/sdcard"))
        if (Build.VERSION.SDK_INT >= 19) {
            ctx.getExternalFilesDirs(null).filterNotNull().drop(1).forEachIndexed { i, f ->
                out += Volume("SD card ${i + 1}", f.path.substringBefore("/Android"), "/ext${i + 1}")
            }
        }
        out += Volume("Full access (entire device)", "/", "/host")
        return out
    }
}
