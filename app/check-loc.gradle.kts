// Fails the build when any Kotlin source exceeds 100 lines.
tasks.register("checkLoc") {
    doLast {
        val bad = (fileTree("src").matching { include("**/*.kt") } + fileTree("../native")).filter { it.readLines().size > 100 }
        if (!bad.isEmpty) throw GradleException("Files over 100 LoC: " + bad.files.joinToString { it.name })
    }
}
tasks.matching { it.name == "preBuild" }.configureEach { dependsOn("checkLoc") }
