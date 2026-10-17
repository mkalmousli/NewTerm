package com.newterm.core

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.newterm.backend.BackendRegistry
import java.io.OutputStream

/** A running shell. Output is buffered so UIs can re-attach after rotation. */
class Terminal(val id: Long, val session: Session, private val ctx: Context) {
    val output = StringBuilder()
    var listener: (() -> Unit)? = null
    private var stdin: OutputStream? = null
    private var proc: Process? = null
    private val ui = Handler(Looper.getMainLooper())

    fun start() {
        Thread {
            try {
                val backend = BackendRegistry.of(session)
                backend.prepare(ctx, session) { append(it) }
                val pb = ProcessBuilder(backend.command(ctx, session))
                pb.directory(session.home(ctx)).redirectErrorStream(true)
                pb.environment().putAll(backend.env(ctx, session))
                val p = pb.start()
                proc = p; stdin = p.outputStream
                val buf = ByteArray(4096)
                while (true) {
                    val n = p.inputStream.read(buf)
                    if (n < 0) break
                    append(String(buf, 0, n))
                }
                append("\n[exited ${p.waitFor()}]\n")
            } catch (e: Exception) {
                append("\n[error: ${e.message}]\n")
            }
        }.start()
    }

    fun send(text: String) {
        Thread { try { stdin?.run { write(text.toByteArray()); flush() } } catch (_: Exception) {} }.start()
    }

    fun kill() { proc?.destroy() }

    private fun append(s: String) {
        synchronized(output) {
            output.append(s)
            if (output.length > 200_000) output.delete(0, output.length - 150_000)
        }
        ui.post { listener?.invoke() }
    }
}
