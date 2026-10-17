package com.newterm.perm

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings

/** One place to check and request permissions. Re-check in `onResume`. */
object PermissionManager {
    private val RUNTIME = arrayOf("android.permission.READ_EXTERNAL_STORAGE",
        "android.permission.WRITE_EXTERNAL_STORAGE")

    fun granted(ctx: Context, p: Permission): Boolean = when (p) {
        Permission.STORAGE -> Build.VERSION.SDK_INT < 23 ||
            RUNTIME.all { ctx.checkSelfPermission(it) == PackageManager.PERMISSION_GRANTED }
        Permission.ALL_FILES -> if (Build.VERSION.SDK_INT >= 30) Environment.isExternalStorageManager()
            else granted(ctx, Permission.STORAGE)
    }

    /** Returns true if already granted; otherwise opens the right prompt. */
    fun ensure(a: Activity, p: Permission): Boolean {
        if (granted(a, p)) return true
        when {
            p == Permission.ALL_FILES && Build.VERSION.SDK_INT >= 30 -> a.startActivity(
                Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
                    Uri.parse("package:${a.packageName}")))
            Build.VERSION.SDK_INT >= 23 -> a.requestPermissions(RUNTIME, 1)
        }
        return false
    }
}
