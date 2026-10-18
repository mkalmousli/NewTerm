package com.newterm.core

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.newterm.backend.BackendRegistry
import com.newterm.term.Emulator
import java.util.concurrent.atomic.AtomicBoolean

/** A running shell on a pty, rendered by [emu]. Survives activity recreation. */
class Terminal(val id: Long, val session: Session, private val ctx: Context) {
    val emu = Emulator(80, 24) { write(it) }
    var listener: (() -> Unit)? = null
    private var pty: Pty? = null
    private var cols = 80
    private var rows = 24
    private val ui = Handler(Looper.getMainLooper())
    private val dirty = AtomicBoolean()

    fun start() {
        Thread {
            try {
                val backend = BackendRegistry.of(session)
                backend.prepare(ctx, session) { log(it) }
                pty = Pty(ctx, backend.command(ctx, session), backend.env(ctx, session), session.home(ctx),
                    cols, rows, { b, n -> emu.feed(b, n); changed() }, { log("\n[process exited $it]\n") })
                    .also { it.start() }
            } catch (e: Exception) { log("\n[error: ${e.message}]\n") }
        }.start()
    }

    fun write(s: String) { pty?.write(s.toByteArray()) }

    fun resize(c: Int, r: Int) {
        if (c == cols && r == rows) return
        cols = c; rows = r
        emu.resize(c, r); pty?.resize(c, r); changed()
    }

    fun kill() { pty?.kill() }

    private fun log(s: String) {
        val b = s.replace("\n", "\r\n").toByteArray()
        emu.feed(b, b.size); changed()
    }

    private fun changed() {
        if (dirty.compareAndSet(false, true)) ui.postDelayed({ dirty.set(false); listener?.invoke() }, 16)
    }
}
