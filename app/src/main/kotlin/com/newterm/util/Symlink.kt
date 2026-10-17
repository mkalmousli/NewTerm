package com.newterm.util

import android.os.Build

/** symlink(2) that works from API 14 (reflection into libcore before API 21). */
object Symlink {
    fun make(target: String, link: String) {
        if (Build.VERSION.SDK_INT >= 21) {
            android.system.Os.symlink(target, link)
        } else {
            val os = Class.forName("libcore.io.Libcore").getField("os").get(null)
            os!!.javaClass.getMethod("symlink", String::class.java, String::class.java)
                .invoke(os, target, link)
        }
    }
}
