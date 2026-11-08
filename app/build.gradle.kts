plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.nyxcorp.nyxpet"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.nyxcorp.nyxpet"
        minSdk = 26
        targetSdk = 34
        // GitHub builds number themselves
        val build = System.getenv("GITHUB_RUN_NUMBER")?.toIntOrNull() ?: 0
        versionCode = 100 + build
        versionName = "1.0 (build $build)"
    }

    // A fixed signing key, so each new build installs over the old one (and keeps Nyx). Personal sideload use only.
    signingConfigs {
        create("sideload") {
            storeFile = file("nyxpet.keystore")
            storePassword = "nyxpet"
            keyAlias = "nyxpet"
            keyPassword = "nyxpet"
        }
    }

    buildTypes {
        getByName("debug") { signingConfig = signingConfigs.getByName("sideload") }
        getByName("release") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("sideload")
        }
    }

    lint {
        checkReleaseBuilds = false
        abortOnError = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.webkit:webkit:1.12.1")
}
