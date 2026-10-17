// Builds the static emuroot binaries into assets when missing (needs git + python3 + network).
val assets = file("src/main/assets/emuroot")
val targets = mapOf("arm" to "armv5", "arm64" to "aarch64", "x86" to "x86", "x86_64" to "x86_64")

tasks.register("fetchEmuroot") {
    onlyIf { targets.keys.any { !File(assets, "$it/emuroot").exists() } }
    doLast {
        val src = File(layout.buildDirectory.get().asFile, "emuroot-src")
        if (!src.exists()) exec { commandLine("git", "clone", "--depth", "1", "https://github.com/mkalmousli/emuroot", src.path) }
        exec { workingDir(src); commandLine("python3", "build.py", "--musl") }
        targets.forEach { (abi, t) ->
            File(src, "build/$t-musl/bin/emuroot").copyTo(File(assets, "$abi/emuroot"), true)
        }
    }
}
tasks.matching { it.name == "preBuild" }.configureEach { dependsOn("fetchEmuroot") }
