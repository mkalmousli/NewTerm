plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.newterm"
    compileSdk = 34
    defaultConfig {
        applicationId = "com.newterm"
        minSdk = 14
        targetSdk = 28
        versionCode = 3
        versionName = "0.3"
    }
    sourceSets["main"].java.srcDir("src/main/kotlin")
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions { jvmTarget = "1.8" }
    lint { abortOnError = false; checkReleaseBuilds = false }
}

apply(from = "check-loc.gradle.kts")
apply(from = "fetch-emuroot.gradle.kts")
