package com.newterm.backend

import android.content.Context
import com.newterm.core.Session

/** How a session's shell is launched. Implement + register in [BackendRegistry]. */
interface Backend {
    val kindId: Long
    /** One-time setup (download, extract…); report progress via [log]. */
    fun prepare(ctx: Context, s: Session, log: (String) -> Unit) {}
    fun command(ctx: Context, s: Session): List<String>
    fun env(ctx: Context, s: Session): Map<String, String> = mapOf(
        "HOME" to s.home(ctx).path, "TERM" to "xterm-256color", "COLORTERM" to "truecolor", "LANG" to "en_US.UTF-8",
        "PATH" to "/sbin:/usr/sbin:/usr/bin:/bin:/system/bin:/system/xbin")
}
