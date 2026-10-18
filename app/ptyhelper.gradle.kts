// Compiles native/ptyhelper.c (static musl) per ABI into assets, using emuroot's musl toolchains.
val outDir = file("src/main/assets/emuroot")
val triples = mapOf("arm" to "arm-linux-musleabi", "arm64" to "aarch64-linux-musl",
    "x86" to "i686-linux-musl", "x86_64" to "x86_64-linux-musl")

tasks.register("buildPtyHelper") {
    dependsOn("fetchEmuroot")
    onlyIf { triples.keys.any { !File(outDir, "$it/ptyhelper").exists() } }
    doLast {
        val tc = File(System.getProperty("user.home"), ".cache/emuroot/musl")
        triples.forEach { (abi, t) ->
            val gcc = File(tc, "$t-cross/bin/$t-gcc")
            if (!gcc.exists()) throw GradleException("missing toolchain $gcc (run build.py --musl in emuroot)")
            exec { commandLine(gcc.path, "-static", "-Os", "-o", File(outDir, "$abi/ptyhelper").path,
                file("../native/ptyhelper.c").path) }
        }
    }
}
tasks.matching { it.name == "preBuild" }.configureEach { dependsOn("buildPtyHelper") }
