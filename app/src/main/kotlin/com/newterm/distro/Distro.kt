package com.newterm.distro

import com.newterm.emuroot.EmurootInstaller

/** [urls] order: arm, arm64, x86, x86_64 (matches [EmurootInstaller.abi]). */
data class Distro(val id: Long, val name: String, val urls: List<String?>) {
    fun url(): String? = urls[listOf("arm", "arm64", "x86", "x86_64").indexOf(EmurootInstaller.abi())]
}
