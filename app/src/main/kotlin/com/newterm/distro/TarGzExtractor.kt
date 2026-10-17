package com.newterm.distro

import com.newterm.util.Symlink
import java.io.File
import java.io.InputStream
import java.util.zip.GZIPInputStream

/** Minimal ustar/GNU/pax tar.gz extractor (files, dirs, symlinks, hardlinks). */
object TarGzExtractor {
    fun extract(src: File, dest: File) {
        GZIPInputStream(src.inputStream().buffered(65536)).use { read(it, dest) }
    }

    private fun read(i: InputStream, dest: File) {
        val h = ByteArray(512)
        var longName: String? = null
        while (readFull(i, h) == 512 && h[0].toInt() != 0) {
            val size = octal(h, 124, 12)
            val name = longName ?: cstr(h, 0, 100).let { p ->
                val pre = cstr(h, 345, 155); if (pre.isEmpty()) p else "$pre/$p"
            }
            longName = null
            val type = h[156].toInt().toChar()
            if (type == 'L') { longName = String(readBytes(i, size)).trimEnd('\u0000'); continue }
            if (type == 'x' || type == 'g') { readBytes(i, size); continue }
            val f = File(dest, name.trimStart('/', '.').ifEmpty { "." })
            if (!f.canonicalPath.startsWith(dest.canonicalPath)) { readBytes(i, size); continue }
            when (type) {
                '5' -> f.mkdirs()
                '2' -> { f.parentFile?.mkdirs(); f.delete(); Symlink.make(cstr(h, 157, 100), f.path) }
                '1' -> { f.parentFile?.mkdirs(); File(dest, cstr(h, 157, 100)).copyTo(f, true) }
                '0', '\u0000' -> writeFile(i, f, size, octal(h, 100, 8).toInt())
                else -> readBytes(i, size)
            }
        }
    }

    private fun writeFile(i: InputStream, f: File, size: Long, mode: Int) {
        f.parentFile?.mkdirs()
        f.outputStream().use { o ->
            var left = size
            val buf = ByteArray(16384)
            while (left > 0) {
                val n = i.read(buf, 0, minOf(left, buf.size.toLong()).toInt())
                if (n < 0) break
                o.write(buf, 0, n); left -= n
            }
        }
        f.setExecutable(mode and 0x49 != 0, false); f.setReadable(true, false); f.setWritable(true, true)
        skip(i, (512 - size % 512) % 512)
    }

    private fun readBytes(i: InputStream, size: Long): ByteArray {
        val b = ByteArray(size.toInt()); readFull(i, b); skip(i, (512 - size % 512) % 512); return b
    }

    private fun skip(i: InputStream, n: Long) { var l = n; while (l > 0) { val k = i.skip(l); if (k <= 0) break; l -= k } }
    private fun readFull(i: InputStream, b: ByteArray): Int {
        var n = 0
        while (n < b.size) { val r = i.read(b, n, b.size - n); if (r < 0) break; n += r }
        return n
    }
    private fun cstr(b: ByteArray, off: Int, len: Int) =
        String(b, off, len).substringBefore('\u0000')
    private fun octal(b: ByteArray, off: Int, len: Int) =
        cstr(b, off, len).trim().ifEmpty { "0" }.toLong(8)
}
