package com.newterm.distro

import android.content.Context
import com.newterm.core.Session
import java.io.File

/** Downloads + extracts a distro into the session rootfs once (marker file). */
object DistroInstaller {
    fun ensure(ctx: Context, s: Session, d: Distro, log: (String) -> Unit) {
        val root = s.rootfs(ctx)
        val marker = File(s.dir(ctx), ".installed")
        if (marker.exists()) return
        val url = d.url() ?: throw IllegalStateException("${d.name}: no image for this ABI")
        val tmp = File(ctx.cacheDir, "${d.name}.tar.gz")
        var last = -10
        log("Downloading ${d.name}…\n")
        Downloader.fetch(url, tmp) { if (it >= last + 10) { last = it; log("  $it%\n") } }
        log("Extracting…\n")
        TarGzExtractor.extract(tmp, root)
        tmp.delete()
        File(root, "etc").mkdirs()
        File(root, "etc/resolv.conf").writeText("nameserver 8.8.8.8\n")
        marker.writeText("ok")
        log("Ready.\n")
    }
}
