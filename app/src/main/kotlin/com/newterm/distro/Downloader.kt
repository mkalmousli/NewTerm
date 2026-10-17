package com.newterm.distro

import java.io.File
import java.net.HttpURLConnection
import java.net.URL

object Downloader {
    fun fetch(url: String, to: File, progress: (Int) -> Unit) {
        val c = URL(url).openConnection() as HttpURLConnection
        c.connectTimeout = 15000; c.readTimeout = 30000
        val total = c.contentLength.toLong()
        c.inputStream.use { i ->
            to.outputStream().use { o ->
                val buf = ByteArray(16384)
                var done = 0L
                while (true) {
                    val n = i.read(buf)
                    if (n < 0) break
                    o.write(buf, 0, n); done += n
                    if (total > 0) progress((done * 100 / total).toInt())
                }
            }
        }
    }
}
