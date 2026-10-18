package com.newterm.core

import android.content.Context
import com.newterm.emuroot.EmurootInstaller
import java.io.File

/** A command on a real pty, via the bundled static `ptyhelper` (no JNI, works on API 14). */
class Pty(
    private val ctx: Context, private val cmd: List<String>, private val env: Map<String, String>,
    private val dir: File, private val cols: Int, private val rows: Int,
    private val onData: (ByteArray, Int) -> Unit, private val onExit: (Int) -> Unit,
) {
    private val tag = System.nanoTime()
    private val pidFile = File(ctx.cacheDir, "pty-$tag.pid")
    private val sizeFile = File(ctx.cacheDir, "pty-$tag.size")
    private var proc: Process? = null

    fun start() {
        val helper = EmurootInstaller.asset(ctx, "ptyhelper") ?: throw IllegalStateException("ptyhelper missing")
        val pb = ProcessBuilder(listOf(helper.path, pidFile.path, sizeFile.path, "$cols", "$rows", "--") + cmd)
        pb.directory(dir).redirectErrorStream(true).environment().putAll(env)
        val p = pb.start()
        proc = p
        Thread {
            val buf = ByteArray(8192)
            while (true) {
                val n = try { p.inputStream.read(buf) } catch (e: Exception) { -1 }
                if (n < 0) break
                onData(buf, n)
            }
            pidFile.delete(); sizeFile.delete()
            onExit(p.waitFor())
        }.start()
    }

    @Synchronized fun write(b: ByteArray) {
        try { proc?.outputStream?.run { write(b); flush() } } catch (_: Exception) {}
    }

    fun resize(c: Int, r: Int) {
        Thread {
            sizeFile.writeText("$c $r")
            repeat(20) {
                val pid = pidFile.takeIf { it.exists() }?.readText()?.trim()?.toIntOrNull()
                if (pid != null) { android.os.Process.sendSignal(pid, 10); return@Thread }
                Thread.sleep(50)
            }
        }.start()
    }

    fun kill() { proc?.destroy() }
}
